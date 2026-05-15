package com.iae.ExecutionPipeline;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class EvaluationEngine {
    public boolean compareOutput(String actualOutput, File expectedOutputFile) throws IOException {
        String expectedContent = Files.readString(expectedOutputFile.toPath());
        List<String> actualLines   = normalizeLines(actualOutput);
        List<String> expectedLines = normalizeLines(expectedContent);
        return actualLines.equals(expectedLines);
    }

    public String getDiff(String actualOutput, File expectedOutputFile) throws IOException {
        String expectedContent = Files.readString(expectedOutputFile.toPath());
        List<String> actualLines   = normalizeLines(actualOutput);
        List<String> expectedLines = normalizeLines(expectedContent);

        if (actualLines.equals(expectedLines)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int maxLines = Math.max(actualLines.size(), expectedLines.size());

        sb.append("--- Expected (").append(expectedLines.size()).append(" lines) ")
                .append("vs Actual (").append(actualLines.size()).append(" lines) ---\n");

        for (int i = 0; i < maxLines; i++) {
            String expected = i < expectedLines.size() ? expectedLines.get(i) : "<missing>";
            String actual   = i < actualLines.size()   ? actualLines.get(i)   : "<missing>";

            if (!expected.equals(actual)) {
                sb.append("Line ").append(i + 1).append(":\n");
                sb.append("  Expected: ").append(expected).append("\n");
                sb.append("  Actual:   ").append(actual).append("\n");
            }
        }

        return sb.toString();
    }

    private List<String> normalizeLines(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String normalized = text.replace("\r\n", "\n").replace("\r", "\n");
        String[] rawLines = normalized.split("\n", -1);

        List<String> lines = new ArrayList<>();
        for (String line : rawLines) {
            lines.add(line.trim());
        }

        while (!lines.isEmpty() && lines.getLast().isEmpty()) {
            lines.removeLast();
        }

        return lines;
    }
}