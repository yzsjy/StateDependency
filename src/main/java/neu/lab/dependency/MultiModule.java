package neu.lab.dependency;

import neu.lab.dependency.smell.BuildOptimize;
import neu.lab.dependency.smell.DetectDupDeclare;
import neu.lab.dependency.smell.VersionCheck;

/**
 * @author SUNJUNYAN
 */
public class MultiModule {
    public static void main(String[] args) {

        if (args[0].equals("versionCheck")) {
            VersionCheck versionCheck = new VersionCheck(args[1]);
            versionCheck.init();
        } else if (args[0].equals("buildOptimize")) {
            BuildOptimize buildOptimize = new BuildOptimize(args[1]);
            buildOptimize.init();
        } else if (args[0].equals("detectDupDeclare")) {
            DetectDupDeclare detectDupDeclare = new DetectDupDeclare(args[0]);
            detectDupDeclare.init();
        }
    }
}
