import java.util.List;

public class Main
{
    private static final String PATH = "src/main/data/movementList.csv";

    public static void main(String[] args)
    {
        Movements movements = new Movements(PATH);
        System.out.println("Сумма расходов: " + movements.getExpenseSum() + " руб.");
        System.out.println("Сумма доходов: " + movements.getIncomeSum() + " руб.\n");

        List<Operation> expenses = movements.gerAllExpenses();
        System.out.println("Суммы расходов по организациям:");
        for (Operation operation : expenses)
        {
            System.out.println(operation.getName() + "\t" + operation.getSum() * (-1));
        }

    }
}
