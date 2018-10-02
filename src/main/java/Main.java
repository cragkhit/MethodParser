import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String inputFolder = args[0];
        String outputFolder = args[1];
        int minCloneSize = Integer.parseInt(args[2]);
        int maxCloneSize = Integer.parseInt(args[3]);
        String[] extensions = { "java" };
        File folder = new File(inputFolder);
        List<File> listOfFiles = (List<File>) FileUtils.listFiles(folder, extensions, true);

        /* copied from
        https://www.mkyong.com/java/how-to-create-directory-in-java/
         */
        File file = new File(outputFolder);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }

        File mappingf = new File("mapping.csv");
        // delete if mapping file exists
        if (mappingf.exists()) {
            mappingf.delete();
        }

        MethodParser mp = new MethodParser();
        mp.setInputDir(inputFolder);
        mp.setOutputDir(outputFolder);
        mp.setMinCloneSize(minCloneSize);
        mp.setMaxCloneSize(maxCloneSize);

        for (File f : listOfFiles) {
            System.out.println(f.getAbsolutePath());
            mp.setFilePath(f.getAbsolutePath());
            mp.parseMethods();
        }
    }
}

