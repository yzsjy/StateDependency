package neu.lab.dependency;

import neu.lab.dependency.pom.ModuleRelation;
import neu.lab.dependency.util.MavenUtil;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import java.util.List;

@Mojo(
        name = "build",
        aggregator = true,
        defaultPhase = LifecyclePhase.NONE,
        threadSafe = true
)
public class BuildMojo extends DependencyMojo {

    @Override
    public void run() {
        ModuleRelation.i().buildGraph();
    }
}
