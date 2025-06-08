package ksolo.dev.logprocessor.writer;

import ksolo.dev.logprocessor.model.UserTransaction;
import ksolo.dev.logprocessor.utils.DateUtils;
import ksolo.dev.logprocessor.utils.FileUtils;

import java.io.File;
import java.util.*;

public class TransactionWriter {
    private final String outputDirectory;

    public TransactionWriter(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        FileUtils.createDirectoryIfNotExists(outputDirectory);
    }

    public void writeTransactions(Map<String, List<UserTransaction>> userTransactions) {
        for (Map.Entry<String, List<UserTransaction>> entry : userTransactions.entrySet()) {
            String user = entry.getKey();
            List<UserTransaction> transactions = entry.getValue();

            // Сортировка транзакций по дате
            transactions.sort(Comparator.comparing(UserTransaction::getTimestamp));

            // Формирование содержимого файла
            StringBuilder content = new StringBuilder();
            for (UserTransaction transaction : transactions) {
                content.append(formatTransaction(transaction)).append("\n");
            }

            // Добавление финального баланса
            double finalBalance = calculateFinalBalance(transactions);
            content.append(formatFinalBalance(user, finalBalance));

            // Запись в файл
            FileUtils.writeToFile(new File(outputDirectory, user + ".log"), content.toString());
        }
    }

    private String formatTransaction(UserTransaction transaction) {
        String formattedDate = DateUtils.formatDate(transaction.getTimestamp());
        String base = String.format("[%s] %s %s", formattedDate, transaction.getUser(), transaction.getOperation());

        if ("transferred".equals(transaction.getOperation())) {
            base += String.format(" %.2f to %s", transaction.getAmount(), transaction.getTargetUser());
        } else if ("received".equals(transaction.getOperation())) {
            base += String.format(" %.2f from %s", transaction.getAmount(), transaction.getTargetUser());
        } else {
            base += String.format(" %.2f", transaction.getAmount());
        }

        return base;
    }

    private String formatFinalBalance(String user, double balance) {
        String formattedDate = DateUtils.formatDate(new Date());
        return String.format("[%s] %s final balance %.2f", formattedDate, user, balance);
    }

    private double calculateFinalBalance(List<UserTransaction> transactions) {
        double balance = 0.0;
        for (UserTransaction transaction : transactions) {
            switch (transaction.getOperation()) {
                case "balance inquiry":
                    balance = transaction.getAmount();
                    break;
                case "transferred":
                    balance -= transaction.getAmount();
                    break;
                case "withdrew":
                    balance -= transaction.getAmount();
                    break;
                case "received":
                    balance += transaction.getAmount();
                    break;
                default:
                    break;
            }
        }
        return balance;
    }
}