package neu.lab.dependency;

import neu.lab.dependency.pom.VersionCheck;

/**
 * @author SUNJUNYAN
 */
public class MultiModule {
    public static void main(String[] args) {
        VersionCheck versionCheck = new VersionCheck(args[0]);
        versionCheck.init();
    }
}
