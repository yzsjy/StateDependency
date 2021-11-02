package neu.lab.dependency;

import neu.lab.dependency.util.FileUtil;
import neu.lab.dependency.util.PomOperation;

import java.io.File;

public class RecoverPom {

    public static void main(String[] args) {
        String path = "D:\\githubProjects\\orientdb-3.1.13\\";
        recoverPom(path);
    }

    public static void recoverPom(String projPath) {
        String[] paths = FileUtil.i().getAllPomFiles(projPath);
        for (String path : paths) {
            String modulePath = projPath + path.substring(0, path.length() - 7);
            File moduleFile = new File(modulePath);
            File[] files = moduleFile.listFiles();
            File pomFile = null;
            File pomCopyFile = null;
            for (File file : files) {
                if (file.getName().equals("pom.xml")) {
                    pomFile = file;
                }
                if (file.getName().equals("pom-copy.xml")) {
                    pomCopyFile = file;
                }
            }
            if (pomFile != null && pomCopyFile != null) {
                PomOperation.i().backupPom(pomFile, pomCopyFile);
                pomCopyFile.delete();
            }
        }

    }
}
