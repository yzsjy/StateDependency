package neu.lab.dependency.pom;

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

    public void repairConflicts() {
        for (Conflict conflict : conflicts) {
            repairConflict(conflict);
        }
    }

    public void repairConflict(Conflict conflict) {
        String version = conflict.getSafeVersion();
        boolean isLocalVersion = conflict.isLocalVersion();
    }


}
