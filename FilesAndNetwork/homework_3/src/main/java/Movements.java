import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Movements
{

    private List<Operation> operationList;

    public Movements(String pathMovementsCsv)
    {
        operationList = parseCsvFile(pathMovementsCsv);
    }

    public double getExpenseSum()
    {
        double sum = 0.0;

        for (Operation operation : operationList)
        {
            if(operation.getSum() < 0)
            {
                sum += operation.getSum();
            }
        }

        return sum * (-1);
    }

    public double getIncomeSum()
    {
        double sum = 0.0;

        for (Operation operation : operationList)
        {
            if(operation.getSum() > 0)
            {
                sum += operation.getSum();
            }
        }

        return sum;
    }

    private List<Operation> parseCsvFile(String path) {
        List<Operation> operationList = new ArrayList<>();
        List<String> movementList = new ArrayList<>();
        ArrayList<Charset> charsets = new ArrayList<>();
        charsets.add(StandardCharsets.UTF_8);
        charsets.add(StandardCharsets.US_ASCII);
        charsets.add(StandardCharsets.UTF_16);
        charsets.add(StandardCharsets.UTF_16BE);
        charsets.add(StandardCharsets.UTF_16LE);
        charsets.add(StandardCharsets.ISO_8859_1);

        for (int i=0 ;i<charsets.size(); i++ )
        {
            try {
                Charset charset = charsets.get(i);
                movementList = Files.readAllLines(Paths.get(path), charset);
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
    }
        if(movementList.size() > 0)
        {
            movementList.remove(0);
            for (String movement : movementList) {
                operationList.add(new Operation(getName(movement), getSum(movement)));
            }

            return operationList;
        }
        return null;
    }

    private String getName(String operation)
    {
       String[] fragments = operation.split(",");
       String[] operationDescription = fragments[5].split("\\\\|/|\\s{2,}");

       return operationDescription[operationDescription.length - 4];
    }

    private double getSum(String operation)
    {
        String[] fragments = operation.split(",(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))");
        fragments[6] = fragments[6].replaceAll("\"", "");
        fragments[6] = fragments[6].replaceAll(",", ".");
        fragments[7] = fragments[7].replaceAll("\"", "");
        fragments[7] = fragments[7].replaceAll(",", ".");
        double income = Double.parseDouble(fragments[6]);
        double expense = Double.parseDouble(fragments[7]);

        return income > 0? income : expense * (-1);
    }

    public List<Operation> gerAllExpenses()
    {
        List<Operation> expenses = new ArrayList<>();

        for(Operation operation : operationList)
        {
            if(operation.getSum() < 0)
            {
                expenses.add(operation);
            }
        }

        return expenses;
    }
}
