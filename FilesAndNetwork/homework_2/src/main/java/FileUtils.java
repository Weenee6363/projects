

import antlr.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtils {
    public static void copyFolder(String sourceDirectory, String destinationDirectory)
    {
        try {
            Path sourceFolder = Paths.get(sourceDirectory);
            Files.walk(sourceFolder)
                    .map(Path::toFile).forEach(file -> {
                        try {
                            if (file.isDirectory()) {
                                File folderFile = new File(destinationDirectory +
                                        "/" + file.getName());
                                folderFile.mkdir();
                            } else
                            {
                                Files.copy(file.toPath(),
                                        new File(destinationDirectory +
                                                "/" + getNameWithFolders(sourceDirectory, file)).toPath(),
                                        StandardCopyOption.REPLACE_EXISTING);
                            }
                        }catch (IOException ex)
                        {
                            ex.printStackTrace();
                        }

                    });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    //Метод сформировывает путь от директории, в которую копируем файлы
    //до файла, вместе со всеми вложенными папками.
    private static String getNameWithFolders(String path, File file)
    {
        StringBuilder pathWithParents = new StringBuilder();
        pathWithParents.insert(0, file.getName() + "/");
        File parentFile = file.getParentFile();
        if(!parentFile.equals(new File(path)))
        {
            pathWithParents.insert(0, getNameWithFolders(path, parentFile));
        }
        return pathWithParents.toString();
    }
}
