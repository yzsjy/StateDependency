package neu.lab.dependency.count;

import neu.lab.dependency.util.MavenUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class PomDirectories {

    private static PomDirectories instance;

    private Set<String> poms;

    public static PomDirectories i() {
        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new PomDirectories();
        }
    }

    private PomDirectories() {
        poms = new HashSet<>();
        getPomDirectories();
    }

    public void getPomDirectories() {
        File project = MavenUtil.i().getProjectFile();
        Set<String> directories = findPomPaths(project);
        poms.addAll(directories);
    }

    public Set<String> findPomPaths(File file) {
        File[] children = file.listFiles();
        Set<String> pomPaths = new HashSet<>();
        if (!file.getAbsolutePath().contains(File.separator + "target" + File.separator)
                && !file.getAbsolutePath().contains(File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator)
                && !file.getAbsolutePath().contains(File.separator + "src" + File.separator + "test" + File.separator)) {
            for (File child : children) {
                if (child.getName().equals("pom.xml")) {
                    pomPaths.add(file.getAbsolutePath());
                }
                if (child.isDirectory()) {
                    pomPaths.addAll(findPomPaths(child));
                }
            }
        }
        return pomPaths;
    }

    public Set<String> getPoms() {
        return poms;
    }
}
