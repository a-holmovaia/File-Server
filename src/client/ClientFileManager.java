package client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientFileManager {
    private static final String FILES_DIRECTORY = System.getProperty("user.dir") + File.separator + "src" + File.separator + "client" + File.separator + "data" + File.separator;


    public static byte[] getFileContent(String fileName) {
        try {
            return Files.readAllBytes(Path.of(FILES_DIRECTORY + fileName));
        } catch (IOException exception) {
            exception.printStackTrace();
            return new byte[0];
        }
    }

    public static void saveFile(String fileName, byte[] fileContent) {
        try {
            FileOutputStream outputStream = new FileOutputStream(FILES_DIRECTORY + fileName);
            outputStream.write(fileContent);
            outputStream.close();
        } catch (IOException ignored) {}
    }
}
