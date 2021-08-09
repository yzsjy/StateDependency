package neu.lab.dependency.util;

import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;

/**
 * @author SUNJUNYAN
 */
public class FileUtil {

    private static FileUtil instance;

    public static FileUtil i() {
        if (instance == null) {
            instance = new FileUtil();
        }
        return instance;
    }

    public FileUtil() {

    }

    /**
     * 获取一个项目所有POM文件路径
     * @param projectPath
     * @return
     */
    public String[] getAllPomFiles(String projectPath) {
        String[] INCLUDE_ALL_POMS = new String[]{"**" + File.separator + "pom.xml"};

        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(projectPath);
        scanner.setIncludes(INCLUDE_ALL_POMS);
        scanner.scan();
        return scanner.getIncludedFiles();
    }

    public String normalizePath(String path) {
        return new File(path).toPath().normalize().toString();
    }

}
