import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        for (;;)
        {
            System.out.println("Введите путь копируемой папки");
            String pathOut = scanner.nextLine();
            if (!new File(pathOut).isDirectory())
            {
                errMessage();
                continue;
            }

            String pathIn;
            for (;;) {
                System.out.println("Введите путь папки, в которую копируем");
                pathIn = scanner.nextLine();
                if (new File(pathIn).isDirectory()) {
                    break;
                }
                errMessage();
            }

            FileUtils.copyFolder(pathOut, pathIn);
        }
    }

    private static void errMessage()
    {
        System.out.println("Указан неверный путь к папке");
    }

}
