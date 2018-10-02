import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.*;

public class MethodParser {
    private String filePath;
    private String inputDir;
    private String outputDir;
    private int minCloneSize;
    private int maxCloneSize;
    private int count;
    private String mapping;

    public MethodParser() {
        super();
        count = 0;
        mapping = "id,file,start,end\n";
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public void setMinCloneSize(int minCloneSize) {
        this.minCloneSize = minCloneSize;
    }

    public void setMaxCloneSize(int maxCloneSize) {
        this.maxCloneSize = maxCloneSize;
    }

    /***
     * Extract both methods and constructors
     * @return a list of methods & constructors
     */
    public void parseMethods() {
        try {
            FileInputStream in = new FileInputStream(filePath);
            CompilationUnit cu;

            try {
                cu = JavaParser.parse(in);
                new MethodVisitor().visit(cu, null);
                new ConstructorVisitor().visit(cu, null);
                writeToFile(".", "mapping.csv", mapping, false);
            } catch (Throwable e) {
                System.out.println("Unparseable method (use whole fragment)");
            } finally {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Extract methods
     */
    private class MethodVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(MethodDeclaration n, Object arg) {
            File f = new File(filePath);
            int annotations = n.getAnnotations().size();
            if (n.getEnd().get().line - (n.getBegin().get().line + annotations) + 1 >= minCloneSize
                    && n.getEnd().get().line - (n.getBegin().get().line + annotations) + 1 <= maxCloneSize) {
                String filename = count + ".java";
                writeToFile(outputDir, filename, n.toString(), false);
                mapping += count + ","
                        + f.getAbsolutePath()
                        + "," + (n.getBegin().get().line + annotations)
                        + "," + n.getEnd().get().line + "\n";
                count++;
            }
            super.visit(n, arg);
        }
    }

    /***
     * Extract constructors
     */
    private class ConstructorVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(ConstructorDeclaration c, Object arg) {
            File f = new File(filePath);
            int annotations = c.getAnnotations().size();
            if (c.getEnd().get().line - (c.getBegin().get().line + annotations) + 1 >= minCloneSize
                    && c.getEnd().get().line - (c.getBegin().get().line + annotations) + 1 <= maxCloneSize) {
                String filename = count + ".java";
                writeToFile(outputDir, filename, c.toString(), false);
                mapping += count + ","
                        + f.getAbsolutePath()
                        + "," + c.getBegin().get().line + annotations
                        + "," + c.getEnd().get().line + "\n";
                count++;
            }
            super.visit(c, arg);
        }
    }

    public static void writeToFile(String location, String filename, String content, boolean isAppend) {
            /* copied from https://www.mkyong.com/java/how-to-write-to-file-in-java-bufferedwriter-example/ */
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(location + "/" + filename, isAppend);
            bw = new BufferedWriter(fw);
            bw.write(content);
            if (!isAppend)
                System.out.println("Saved as: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }
}