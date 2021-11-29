package neu.lab.dependency;

import neu.lab.dependency.smell.DetectDupDeclare;
import neu.lab.dependency.util.MavenUtil;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

@Mojo(
        name = "detectDup",
        aggregator = true,
        defaultPhase = LifecyclePhase.NONE,
        threadSafe = true
)
public class DetectDupMojo extends DependencyMojo {
    @Override
    public void run() {
        String projPath = MavenUtil.i().getBaseDir().getAbsolutePath() + File.separator;
        DetectDupDeclare detectDupDeclare = new DetectDupDeclare(projPath);
        detectDupDeclare.init();
    }
}
