import java.io.File;
import java.math.BigDecimal;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        for(;;)
        {
            System.out.println("Введите путь до папки:");
            String path = scanner.nextLine();
            if(checkFolder(path))
            {
                long folderSize = FileUtils.calculateFolderSize(path);
                System.out.println(setSizeFormat(folderSize, path));
            }
            else
            {
                System.out.println("Неверно указан путь! \n" );
            }
        }
    }

    private static boolean checkFolder(String path)
    {
        if(new File(path).isDirectory())
        {
            return true;
        }
        return false;
    }

    private static String setSizeFormat(long folderSize, String path) {
        String message = "Размер папки " + path + " cоставляет ";

        if (folderSize < 1024) {
            return message + folderSize + " байт";
        }

        double kbSize = folderSize / 1024;
        if (kbSize < 1024) {
            return message + String.format("%.1f", kbSize) + " кБ";
        }

        double mbSize = kbSize / 1024;
        if (mbSize < 1024)
        {
            return message + String.format("%.1f", mbSize) + " Мб";
        }

        double gbSize = mbSize / 1024;
        return message + String.format("%.1f", gbSize) + " Гб ";

    }
}
