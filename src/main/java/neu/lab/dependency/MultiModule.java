package neu.lab.dependency;

import neu.lab.dependency.handler.PomFileIO;
import neu.lab.dependency.pom.VersionCheck;
import org.apache.maven.model.Model;

import java.util.Scanner;

/**
 * @author SUNJUNYAN
 */
public class MultiModule {
    public static void main(String[] args) {
//        VersionCheck versionCheck = new VersionCheck(args[0]);
//        versionCheck.init();
//
        Model model = PomFileIO.i().parsePomFileToModel("D:\\githubProjects\\dubbo-dubbo-2.7.11\\pom.xml");
        System.out.println(model.getDependencyManagement().getDependencies());
    }
}
