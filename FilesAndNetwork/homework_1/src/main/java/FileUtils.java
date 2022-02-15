import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static long calculateFolderSize(String path)
    {
//        File folder = new File(path);
//        File[] files = folder.listFiles();
//        long folderSize = 0;
//
//        for(File file : files)
//        {
//            if (file.isDirectory())
//            {
//
//            }
//            folderSize += file.length();
//        }
//
//        return folderSize;

        try {
            Path folder = Paths.get(path);
            return Files.walk(folder)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .mapToLong(File::length)
                    .sum();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return 0L;
    }
}
