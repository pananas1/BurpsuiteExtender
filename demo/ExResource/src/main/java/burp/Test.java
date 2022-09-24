package burp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {
    public static void generateFile(){
        String fileSeparator = File.separator;

        String currentPath = System.getProperty("java.class.path");
        System.out.println(currentPath);
        String substring = currentPath.substring(currentPath.lastIndexOf(fileSeparator) + 1);
        System.out.println(substring);
//        Path path = Paths.get("D:\\data222\\test");
//        try {
//            Path pathCreate = Files.createDirectory(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public static void main(String[] args) throws IOException {
        String directories = "D:\\a\\b\\12.txt";
        File file = new File(directories);
        file.createNewFile();
//        boolean result = file.mkdirs();
//        System.out.println(result);
    }
}
