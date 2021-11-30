package neu.lab.dependency;

import neu.lab.dependency.graph.MavenModuleGraph;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(
        name = "build",
        aggregator = true,
        defaultPhase = LifecyclePhase.NONE,
        threadSafe = true
)
public class BuildMojo extends DependencyMojo {

    @Override
    public void run() {
        MavenModuleGraph.i().graph();
    }
}
