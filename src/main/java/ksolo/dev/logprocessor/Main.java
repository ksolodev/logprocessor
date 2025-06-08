package ksolo.dev.logprocessor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java -jar LogProcessor.jar <path_to_logs_directory>");
            return;
        }

        String logsDirectory = args[0];
        File logsDir = new File(logsDirectory);

        if (!logsDir.exists() || !logsDir.isDirectory()) {
            System.err.println("Error: The provided path is not a valid directory.");
            return;
        }

        // Создаем временную директорию
        Path tempDir = Files.createTempDirectory("logs");
        System.out.println("Temporary directory created: " + tempDir);

        // Копируем файлы из указанной директории во временную директорию
        for (File logFile : logsDir.listFiles()) {
            if (logFile.isFile() && logFile.getName().endsWith(".log")) {
                Path destination = tempDir.resolve(logFile.getName());
                Files.copy(logFile.toPath(), destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Copied file: " + logFile.getName());
            }
        }

        // Запускаем обработку логов
        LogProcessor processor = new LogProcessor(tempDir.toString());
        processor.processLogs();
    }
}