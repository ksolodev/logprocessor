package ksolo.dev.logprocessor;

import ksolo.dev.logprocessor.model.UserTransaction;
import ksolo.dev.logprocessor.parser.LogParser;
import ksolo.dev.logprocessor.writer.TransactionWriter;
import ksolo.dev.logprocessor.utils.FileUtils;

import java.io.File;
import java.util.*;

public class LogProcessor {
    private final String logsDirectory;

    public LogProcessor(String logsDirectory) {
        this.logsDirectory = logsDirectory;
    }

    public void processLogs() {
        // Чтение всех файлов в директории
        File[] logFiles = FileUtils.listFilesInDirectory(logsDirectory);

        // Парсинг логов
        LogParser parser = new LogParser();
        Map<String, List<UserTransaction>> userTransactions = new HashMap<>();

        for (File file : logFiles) {
            List<UserTransaction> transactions = parser.parseLogFile(file);
            for (UserTransaction transaction : transactions) {
                userTransactions.computeIfAbsent(transaction.getUser(), k -> new ArrayList<>()).add(transaction);

                // Если это перевод, добавляем транзакцию для получателя
                if ("transferred".equals(transaction.getOperation())) {
                    UserTransaction receivedTransaction = new UserTransaction(
                            transaction.getTimestamp(),
                            transaction.getTargetUser(),
                            "received",
                            transaction.getAmount(),
                            transaction.getUser()
                    );
                    userTransactions.computeIfAbsent(transaction.getTargetUser(), k -> new ArrayList<>()).add(receivedTransaction);
                }
            }
        }

        // Запись результатов в файлы
        TransactionWriter writer = new TransactionWriter(logsDirectory + "/transactions_by_users");
        writer.writeTransactions(userTransactions);
    }
}