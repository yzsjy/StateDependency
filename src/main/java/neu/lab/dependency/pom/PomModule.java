package neu.lab.dependency.pom;

import neu.lab.dependency.handler.PomFileIO;
import neu.lab.dependency.util.FileUtil;
import org.apache.maven.model.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取一个模块所有的模块
 * @author SUNJUNYAN
 */
public class PomModule {

    public static void getChildModule(String projPath, String pomFile, List<String> projModules) {
        String pomPath = projPath + pomFile;
        String prefix = pomFile;
        if (prefix.endsWith("pom.xml")) {
            prefix = prefix.substring(0, prefix.length() - 7);
        }
        if (new File(pomPath).exists()) {
            Model model = PomFileIO.i().parsePomFileToModel(pomPath);
            if (model!= null && model.getModules() != null) {
                List<String> temp = model.getModules();
                for(String t : temp) {
                    String module = prefix + t;
                    projModules.add(module);
                    getChildModule(projPath, module + File.separator + "pom.xml", projModules);
                }
            }
        }
    }

    public static List<String> getModulesFromPom(String pomPath) {
        List<String> modules = new ArrayList<>();
        String[] pomPaths = FileUtil.i().getAllPomFiles(pomPath);
        for (String file : pomPaths) {
            String path = pomPath + file;
            String prefix = file;
            if (prefix.endsWith("pom.xml")) {
                prefix = prefix.substring(0, prefix.length() - 7);
            }
            if (new File(path).exists()) {
                Model model = PomFileIO.i().parsePomFileToModel(path);
                if (model != null && model.getModules() != null) {
                    List<String> temp = model.getModules();
                    for (String t : temp) {
                        modules.add(prefix + t);
                    }
                }
            }
        }
        return modules;
    }

    public static void main(String[] args) {
        List<String> modules = new ArrayList<>();
        String projPath = "D:\\githubProject\\camel\\";
        String pomFile = "pom.xml";
        getChildModule(projPath, pomFile, modules);
        System.out.println("Modules : ");
        for (String module : modules) {
            System.out.println(module);
        }
    }
}
