package neu.lab.dependency;

import neu.lab.dependency.smell.VersionCheck;
import neu.lab.dependency.util.MavenUtil;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(
        name = "version",
        aggregator = true,
        defaultPhase = LifecyclePhase.NONE,
        threadSafe = true
)
public class VersionCheckMojo extends DependencyMojo {

    @Override
    public void run() {
        String projPath = MavenUtil.i().getBaseDir().getAbsolutePath();
        VersionCheck versionCheck = new VersionCheck(projPath);
        versionCheck.init();
    }
}
