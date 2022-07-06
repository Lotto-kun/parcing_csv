import java.math.BigDecimal;
import java.time.LocalDate;

public class Operation {
    private final String accountType;
    private final String accountNumber;
    private final String accountCurrency;
    private final LocalDate operationDate;
    private final String operationDescription;
    private final OperationType operationType;
    private final BigDecimal operationValue;

    public Operation(String accountType, String accountNumber, String accountCurrency, LocalDate operationDate, String operationDescription, OperationType operationType, BigDecimal operationValue) {
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.accountCurrency = accountCurrency;
        this.operationDate = operationDate;
        this.operationDescription = operationDescription;
        this.operationType = operationType;
        this.operationValue = operationValue;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountCurrency() {
        return accountCurrency;
    }

    public LocalDate getOperationDate() {
        return operationDate;
    }

    public String getOperationDescription() {
        return operationDescription;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public BigDecimal getOperationValue() {
        return operationValue;
    }
}
