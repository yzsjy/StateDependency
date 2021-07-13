package neu.lab.dependency.util;

import soot.G;
import soot.SourceLocator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class SootUtil {
    public static void modifyLogOut() {
//        File outDir = MavenUtil.i().getBuildDir();
        File outDir = new File("D:\\IdeaProjects\\TestDemo\\target");
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        try {
            soot.G.v().out = new PrintStream(new File(outDir.getAbsolutePath() + File.separator + "soot.log"));
        } catch (FileNotFoundException e) {
            G.v().out = System.out;
        }
    }

    public static Set<String> getJarClasses(List<String> paths) {
        Set<String> allCls = new HashSet<>();
        for (String path : paths) {
            allCls.addAll(getJarClasses(path));
        }
        return allCls;
    }

    public static Set<String> getJarClses(String path) {
        Set<String> allCls = new HashSet<>();
        allCls.addAll(getJarClasses(path));
        return allCls;
    }

    public static List<String> getJarClasses(String path) {
        if (new File(path).exists()) {
            if (!path.endsWith("tar.gz") && !path.endsWith(".pom") && !path.endsWith(".war")) {
                return SourceLocator.v().getClassesUnder(path);
            } else {
//                MavenUtil.i().getLog().warn(path + " is illegal classpath");
                System.out.println(path + " is illegal classpath");
            }
        } else {
//            MavenUtil.i().getLog().warn(path + " doesn't exist in local");
            System.out.println(path + " doesn't exist in local");
        }
        return new ArrayList<>();
    }
}
