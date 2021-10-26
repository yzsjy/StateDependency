package neu.lab.dependency;

import neu.lab.dependency.handler.PomFileIO;
import neu.lab.dependency.pom.VersionCheck;
import neu.lab.dependency.smell.DetectDupDeclare;
import neu.lab.dependency.util.FileUtil;
import neu.lab.dependency.writer.DupDeclareExcelWriter;
import org.apache.maven.model.Model;

import java.util.Scanner;

/**
 * @author SUNJUNYAN
 */
public class MultiModule {
    public static void main(String[] args) {
//        VersionCheck versionCheck = new VersionCheck(args[0]);
//        versionCheck.init();

//        DetectDupDeclare detectDupDeclare = new DetectDupDeclare(args[0]);
//        detectDupDeclare.init();

        System.out.println(FileUtil.i().getAllPomFiles(args[0]));
    }
}
