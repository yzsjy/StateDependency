package neu.lab.dependency.pom;

import neu.lab.dependency.vo.Conflict;
import neu.lab.dependency.vo.Pom;

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
        if (isLocalVersion) {
            unifyLocalVersion(conflict, version);
        } else {
            unifyNewVersion(conflict, version);
        }

    }

    public void unifyLocalVersion(Conflict conflict, String version) {
        Map<String, List<Pom>> versionToModule = conflict.getVersionToModule();
        for (Map.Entry<String, List<Pom>> entry : versionToModule.entrySet()) {
            String curVersion = entry.getKey();
            if (curVersion.equals(version)) {
                continue;
            }
            
        }
    }

    public void unifyNewVersion(Conflict conflict, String version) {
        Map<String, List<Pom>> versionToModule = conflict.getVersionToModule();
        for (Map.Entry<String, List<Pom>> entry : versionToModule.entrySet()) {
            String curVersion = entry.getKey();


        }
    }


}
