package neu.lab.dependency;

import neu.lab.dependency.smell.UselessDep;
import neu.lab.dependency.util.MavenUtil;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(
        name = "version",
        aggregator = true,
        defaultPhase = LifecyclePhase.NONE,
        threadSafe = true
)
public class UselessDepMojo extends DependencyMojo {
    @Override
    public void run() {
        String projPath = MavenUtil.i().getBaseDir().getAbsolutePath();
        UselessDep uselessDep = new UselessDep(projPath);
        uselessDep.init();
    }
}
