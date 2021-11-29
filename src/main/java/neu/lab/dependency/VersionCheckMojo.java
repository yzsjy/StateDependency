package neu.lab.dependency;

import neu.lab.dependency.smell.VersionCheck;
import neu.lab.dependency.util.MavenUtil;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

@Mojo(
        name = "version",
        aggregator = true,
        defaultPhase = LifecyclePhase.NONE,
        threadSafe = true
)
public class VersionCheckMojo extends DependencyMojo {

    @Override
    public void run() {
        String projPath = MavenUtil.i().getBaseDir().getAbsolutePath() + File.separator;
        VersionCheck versionCheck = new VersionCheck(projPath);
        versionCheck.init();
    }
}
