package neu.lab.dependency;

import neu.lab.dependency.count.CountDepNum;
import neu.lab.dependency.soot.SootRiskCg;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SUNJUNYAN
 */
@Mojo(name = "count", defaultPhase = LifecyclePhase.VALIDATE)
public class CountMojo extends DependencyMojo {
    @Override
    public void run() {
        new CountDepNum().countDepNum();
    }
}
