package ksolo.dev.logprocessor.model;

import java.util.Date;
import java.util.Objects;

public class UserTransaction {
    private final Date timestamp;
    private final String user;
    private final String operation;
    private final double amount;
    private final String targetUser;

    public UserTransaction(Date timestamp, String user, String operation, double amount, String targetUser) {
        if (!"final_balance".equals(operation) && amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.user = Objects.requireNonNull(user, "User cannot be null");
        this.operation = Objects.requireNonNull(operation, "Operation cannot be null");
        this.amount = amount;
        this.targetUser = targetUser;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getUser() {
        return user;
    }

    public String getOperation() {
        return operation;
    }

    public double getAmount() {
        return amount;
    }

    public String getTargetUser() {
        return targetUser;
    }

    @Override
    public String toString() {
        return "UserTransaction{" +
                "timestamp=" + timestamp +
                ", user='" + user + '\'' +
                ", operation='" + operation + '\'' +
                ", amount=" + amount +
                ", targetUser='" + targetUser + '\'' +
                '}';
    }
}