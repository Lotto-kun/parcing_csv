import java.math.BigDecimal;
import java.util.Map;

public class Main {

    private static final String PATH_MOVEMENTS_CSV = "src/main/resources/movementList.csv";
    public static void main(String[] args) {
        Movements movements = new Movements(PATH_MOVEMENTS_CSV);
        System.out.println("Сумма доходов: " + movements.getIncomeSum());
        System.out.println("Сумма расходов: " + movements.getExpenseSum());
        System.out.println("Сумма расходов по организациям:");
        Map<String, BigDecimal> expenseByOrganisations = movements.ExpenseByOrganisations();
        for (String key : expenseByOrganisations.keySet()) {
            System.out.println(key + "\t\t" + expenseByOrganisations.get(key));
        }
    }
}
