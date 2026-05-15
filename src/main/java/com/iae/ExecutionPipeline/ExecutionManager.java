package com.iae.ExecutionPipeline;

import com.iae.model.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ExecutionManager {
    private static final int TIMEOUT_SECONDS = 20;


    public record CompileResult(boolean success, String log) {}

    public record RunResult(boolean success, String output, String errorLog) { }

    public CompileResult compile(Configuration config, File workingDir) {
        if (config.isInterpreted()) {
            return new CompileResult(true, "(Interpreted language — no compilation step)");
        }

        List<String> command = new ArrayList<>();
        command.add(config.getCompilerPath());
        command.addAll(tokenize(config.getCompilerArgs()));

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(workingDir);
            pb.redirectErrorStream(false);
            Process process = pb.start();

            Future<String> stderrFuture = readAsync(process.getErrorStream());

            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new CompileResult(false, "Compiler timed out after " + TIMEOUT_SECONDS + " seconds.");
            }

            String stderr = getQuietly(stderrFuture);
            int exitCode = process.exitValue();
            boolean success = exitCode == 0;

            return new CompileResult(success, stderr.isEmpty() ? "(no output)" : stderr);

        } catch (IOException e) {
            return new CompileResult(false,
                    "Could not launch compiler. Is the path correct?\n" + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new CompileResult(false, "Compilation interrupted.");
        }
    }

    public RunResult run(Configuration config, File workingDir, String binaryArgs) {
        List<String> command = new ArrayList<>();

        if (config.isInterpreted()) {
            command.add(config.getBinaryPath());
            if (config.getCompilerArgs() != null && !config.getCompilerArgs().isBlank()) {
                command.addAll(tokenize(config.getCompilerArgs()));
            }
        } else {
            command.add(workingDir.getAbsolutePath() + File.separator + config.getBinaryPath());
        }

        if (binaryArgs != null && !binaryArgs.isBlank()) {
            command.addAll(tokenize(binaryArgs));
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(workingDir);
            pb.redirectErrorStream(false);
            Process process = pb.start();

            Future<String> stdoutFuture = readAsync(process.getInputStream());
            Future<String> stderrFuture  = readAsync(process.getErrorStream());

            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new RunResult(false, "", "Program exceeded timeout (" + TIMEOUT_SECONDS + "s). Possible infinite loop.");
            }

            String stdout = getQuietly(stdoutFuture);
            String stderr = getQuietly(stderrFuture);
            int exitCode = process.exitValue();
            boolean success = exitCode == 0;

            return new RunResult(success, stdout, stderr.isEmpty() ? null : stderr);

        } catch (IOException e) {
            return new RunResult(false, "",
                    "Could not launch binary. Check binary path.\n" + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new RunResult(false, "", "Execution interrupted.");
        }
    }

    private Future<String> readAsync(InputStream inputStream) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString();
        });
        executor.shutdown();
        return future;
    }

    private String getQuietly(Future<String> future) {
        try {
            return future.get(TIMEOUT_SECONDS + 2L, TimeUnit.SECONDS);
        } catch (Exception e) {
            return "";
        }
    }

    private List<String> tokenize(String args) {
        List<String> tokens = new ArrayList<>();
        if (args == null || args.isBlank()) return tokens;

        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : args.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }

        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }

        return tokens;
    }
}
