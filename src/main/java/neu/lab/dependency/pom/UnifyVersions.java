package neu.lab.dependency.pom;

import neu.lab.dependency.vo.Conflict;
import neu.lab.dependency.vo.Pom;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, List<Pom>> versionToModule = conflict.getVersionToModule();

    }


}
