package neu.lab.dependency.vo;

import java.util.*;

/**
 * @author SUNJUNYAN
 */
public class Conflict {

    private String groupId;
    private String artifactId;
    private List<DepInfo> depJars;
    private List<Pom> modules;
    private Set<String> versions;
    private List<String> moduleNames;
    private Map<String, List<Pom>> versionToModule;


    public Conflict(String groupId, String artifactId) {
        depJars = new ArrayList<>();
        modules = new ArrayList<>();
        versions = new HashSet<>();
        moduleNames = new ArrayList<>();
        versionToModule = new HashMap<>();
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public List<DepInfo> getDepJars() {
        return depJars;
    }

    public void addDepJars(DepInfo depJar) {
        depJars.add(depJar);
    }

    public void addPom(Pom pom) {
        modules.add(pom);
    }

    public void addVersion(String version) {
        versions.add(version);
    }

    public Set<String> getVersions() {
        return versions;
    }

    public List<Pom> getModules() {
        return modules;
    }

    public Map<String, List<Pom>> getVersionToModule() {
        return versionToModule;
    }

    public List<String> getModuleNames() {
        return moduleNames;
    }

    public void addModuleNames(String moduleName) {
        moduleNames.add(moduleName);
    }

    public void addToModules(String version, Pom module) {
        List<Pom> poms;
        if (versionToModule.containsKey(version)) {
            poms = versionToModule.get(version);
        } else {
            poms = new ArrayList<>();
        }
        poms.add(module);
        versionToModule.put(version, poms);
    }

    public String getSig() {
        return groupId + ":" + artifactId;
    }

}
