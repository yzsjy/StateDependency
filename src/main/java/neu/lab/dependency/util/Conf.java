package neu.lab.dependency.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SUNJUNYAN
 */
public class Conf {
    public static final boolean DEL_LONGTIME = true;
    public static final boolean DEL_OPTIONAL = true;
    public static String outDir;
    public static boolean append;
    public static List<String> visited = new ArrayList<>();
//    public static String Dir = "E:\\RunTest\\MultiModule\\";
    public static String Dir = "D:\\test\\";
//    public static String Dir = "/home/b406/sunjunyan/autoExecLinux/output/";


    public static void setDir(String dir) {
        Dir = dir;
    }
}
