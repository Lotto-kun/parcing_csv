import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Movements {
    private static final Logger LOGGER = LoggerFactory.getLogger(Movements.class);
    private static final Marker EXCEPTIONS_MARKER = MarkerFactory.getMarker("EXCEPTIONS");
    private static final int FIRST_LINE_INDEX = 0;
    private static final int ACCOUNT_TYPE_INDEX = 0;
    private static final int ACCOUNT_NUMBER_INDEX = 1;
    private static final int ACCOUNT_CURRENCY_INDEX = 2;
    private static final int OPERATION_DATE_INDEX = 3;
    private static final int OPERATION_DESCRIPTION = 5;
    private static final int OPERATION_VALUE_INCOME = 6;
    private static final int OPERATION_VALUE_EXPENSE = 7;
    private static final int YEAR_INDEX = 2;
    private static final int MONTH_INDEX = 1;
    private static final int DAY_INDEX = 0;
    private List<Operation> operationsList = new ArrayList<>();

    public Movements(String pathMovementsCsv) {
        parseMovements(pathMovementsCsv);
    }

    public void parseMovements(String pathMovementsCsv) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(pathMovementsCsv));
        } catch (IOException e) {
            LOGGER.error(EXCEPTIONS_MARKER, "не получилось прочитать файл movementList.csv", e);
            e.printStackTrace();
        }

        if (lines != null) {
            lines.remove(FIRST_LINE_INDEX);
            String quotedRegex = "\"[^\"]*\"";
            for (String line : lines) {
                try {
                    String[] tokens = line.split(",(?=([^\"]*" + quotedRegex + ")*[^\"]*$)");
                    if (tokens.length != 8) {
                        LOGGER.error(EXCEPTIONS_MARKER, "строка не поделилась корректно на 8 полей \n" + line);
                        continue;
                    }

                    LocalDate date = parseDate(tokens[OPERATION_DATE_INDEX]);
                    OperationType operationType = parseOperationType(tokens[OPERATION_VALUE_INCOME]);
                    String description = parseDescription(tokens[OPERATION_DESCRIPTION]);
                    String value;
                    if (tokens[OPERATION_VALUE_INCOME].trim().equals("0")) {
                        value = parseOperationValue(tokens[OPERATION_VALUE_EXPENSE]);
                    } else {
                        value = parseOperationValue(tokens[OPERATION_VALUE_INCOME]);
                    }

                    operationsList.add(new Operation(tokens[ACCOUNT_TYPE_INDEX],
                            tokens[ACCOUNT_NUMBER_INDEX],
                            tokens[ACCOUNT_CURRENCY_INDEX],
                            date,
                            description,
                            operationType,
                            new BigDecimal(value)));
                } catch (Exception e) {
                    LOGGER.error(EXCEPTIONS_MARKER, "Не смог спарсить правильно линию: \n" + line, e);
                    e.printStackTrace();
                }
            }
        }
    }

    private LocalDate parseDate(String date) {
        String[] dateTokens = date.split("\\.", 3);
        if (dateTokens.length == 3) {

            int year = 2000 + Integer.parseInt(dateTokens[YEAR_INDEX]);
            int month = Integer.parseInt(dateTokens[MONTH_INDEX]);
            int day = Integer.parseInt(dateTokens[DAY_INDEX]);
            return LocalDate.of(year, month, day);
        }
        return null;
    }

    private OperationType parseOperationType(String operationValue) {
        if (Double.parseDouble(operationValue.replaceAll("\"", "").replaceAll(",", ".").trim()) > 0) {
            return OperationType.income;
        }
        return OperationType.expense;
    }

    private String parseDescription(String description) {
        String descriptionRegex = "[/\\\\][/A-Z0-9a-z\\s_>\\\\]+[\\s]{2,}";
        String result = "";
        Pattern pattern = Pattern.compile(descriptionRegex);
        Matcher matcher = pattern.matcher(description);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            result = description.substring(start, end)
                    .replaceAll("[/_>\\\\]", " ").trim();
        }
        return result;
    }

    private String parseOperationValue(String operationValue) {
        operationValue = operationValue.replaceAll("\"", "").replaceAll(",", ".").trim();
        return operationValue;
    }

    public double getExpenseSum() {
        final BigDecimal[] sum = {new BigDecimal("0")};
        operationsList.stream().filter(o -> o.getOperationType().equals(OperationType.expense)).map(Operation::getOperationValue).forEach(value -> sum[0] = sum[0].add(value));
        return sum[0].doubleValue();
    }

    public double getIncomeSum() {
        final BigDecimal[] sum = {new BigDecimal("0")};
        operationsList.stream().filter(o -> o.getOperationType().equals(OperationType.income)).map(Operation::getOperationValue).forEach(value -> sum[0] = sum[0].add(value));
        return sum[0].doubleValue();
    }

    public Map<String, BigDecimal> ExpenseByOrganisations() {
        TreeMap<String, BigDecimal> result = new TreeMap<>();
        for (Operation operation : operationsList) {
            if (operation.getOperationType().equals(OperationType.income)) {
                continue;
            }
            if (result.containsKey(operation.getOperationDescription())) {
                result.put(operation.getOperationDescription(), result.get(operation.getOperationDescription()).add(operation.getOperationValue()));
            } else {
                result.put(operation.getOperationDescription(), operation.getOperationValue());
            }
        }
        return result;
    }
}
