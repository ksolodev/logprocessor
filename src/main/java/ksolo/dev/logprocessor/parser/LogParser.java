package ksolo.dev.logprocessor.parser;

import ksolo.dev.logprocessor.model.UserTransaction;
import ksolo.dev.logprocessor.utils.DateUtils;
import ksolo.dev.logprocessor.utils.FileUtils;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\] (\\w+) (balance inquiry|transferred|withdrew) ([\\d.]+)(?: to (\\w+))?"
    );

    public List<UserTransaction> parseLogFile(File file) {
        List<UserTransaction> transactions = new ArrayList<>();
        List<String> lines = FileUtils.readLinesFromFile(file);

        for (String line : lines) {
            Matcher matcher = LOG_PATTERN.matcher(line);
            if (!matcher.matches()) {
                System.err.println("Invalid log line: " + line);
                continue;
            }

            try {
                Date timestamp = DateUtils.parseDate(matcher.group(1));
                String user = matcher.group(2);
                String operation = matcher.group(3);
                double amount = Double.parseDouble(matcher.group(4));
                String targetUser = matcher.group(5);

                transactions.add(new UserTransaction(timestamp, user, operation, amount, targetUser));
            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing log line: " + line);
                e.printStackTrace();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return transactions;
    }
}