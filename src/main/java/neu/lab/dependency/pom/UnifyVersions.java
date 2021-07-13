package neu.lab.dependency.pom;

import neu.lab.dependency.vo.ArtifactResolver;
import neu.lab.dependency.vo.Conflict;

import java.util.List;

/**
 * @author SUNJUNYAN
 */
public class UnifyVersions {

    private List<Conflict> conflicts;

    public UnifyVersions(List<Conflict> conflicts) {
        this.conflicts = conflicts;
    }

}
