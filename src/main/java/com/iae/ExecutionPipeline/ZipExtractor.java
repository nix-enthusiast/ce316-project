package com.iae.ExecutionPipeline;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

public class ZipExtractor {
    public static class ExtractionResult {
        private final String studentId;
        private final File extractedDirectory;
        private final boolean success;
        private final String errorMessage;

        public ExtractionResult(String studentId, File extractedDirectory) {
            this.studentId = studentId;
            this.extractedDirectory = extractedDirectory;
            this.success = true;
            this.errorMessage = null;
        }

        public ExtractionResult(String studentId, String errorMessage) {
            this.studentId = studentId;
            this.extractedDirectory = null;
            this.success = false;
            this.errorMessage = errorMessage;
        }

        public String getStudentId()            { return studentId; }
        public File getExtractedDirectory()     { return extractedDirectory; }
        public boolean isSuccess()              { return success; }
        public String getErrorMessage()         { return errorMessage; }
    }

    public List<ExtractionResult> extractAll(File zipDirectory) {
        List<ExtractionResult> results = new ArrayList<>();

        File[] zipFiles = zipDirectory.listFiles(
                (dir, name) -> name.toLowerCase().endsWith(".zip")
        );

        if (zipFiles == null || zipFiles.length == 0) {
            return results;
        }

        File extractionRoot = new File(zipDirectory, "extracted");
        extractionRoot.mkdirs();

        for (File zipFile : zipFiles) {
            String studentId = stripExtension(zipFile.getName());
            File studentDir = new File(extractionRoot, studentId);
            studentDir.mkdirs();

            try {
                extractZip(zipFile, studentDir);
                results.add(new ExtractionResult(studentId, studentDir));
            } catch (ZipException e) {
                results.add(new ExtractionResult(
                        studentId,
                        "Corrupted ZIP file: " + e.getMessage()
                ));
            } catch (IOException e) {
                results.add(new ExtractionResult(
                        studentId,
                        "Extraction failed: " + e.getMessage()
                ));
            }
        }

        return results;
    }

    private void extractZip(File zipFile, File outputDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File entryFile = new File(outputDir, entry.getName());

                if (!entryFile.getCanonicalPath().startsWith(outputDir.getCanonicalPath())) {
                    zis.closeEntry();
                    continue;
                }

                if (entry.isDirectory()) {
                    entryFile.mkdirs();
                } else {
                    entryFile.getParentFile().mkdirs();
                    writeEntry(zis, entryFile);
                }
                zis.closeEntry();
            }
        }
    }

    private void writeEntry(ZipInputStream zis, File target) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target))) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = zis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        }
    }

    private String stripExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(0, dot) : filename;
    }
}