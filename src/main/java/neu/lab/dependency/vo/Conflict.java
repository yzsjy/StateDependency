package neu.lab.dependency.vo;

import neu.lab.dependency.soot.JarAna;
import neu.lab.dependency.soot.SootRiskCg;
import neu.lab.dependency.util.MavenCrawler;

import java.io.File;
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

    public String getSafeVersion() {
        Set<String> localVersions = new HashSet<>();
        String safeVersion = null;
        Map<String, Set<String>> usedStore = new HashMap<>();
        Set<String> usedMethods = new HashSet<>();
        for (Map.Entry<String, List<Pom>> entry : versionToModule.entrySet()) {
            String version = entry.getKey();
            String jarPath = ArtifactResolver.i().resolver(groupId, artifactId, version);
            Set<String> reachMethods = new HashSet<>();
            for (Pom pom : entry.getValue()) {
                String pomPath = pom.getFilePath();
                String projPath = pomPath.substring(0, pomPath.length() - 7) + "target" + File.separator + "classes";
                reachMethods.addAll(SootRiskCg.i().cmpCg(jarPath, projPath));
            }
            usedMethods.addAll(reachMethods);
            usedStore.put(version, reachMethods);
        }
        for (String version : versions) {
            String jarPath = ArtifactResolver.i().getJarPath(groupId, artifactId, version);
            Set<String> jarMethods = JarAna.i().deconstruct(jarPath);
            boolean isSafe = true;
            for (String method : usedMethods) {
                if (!jarMethods.contains(method)) {
                    isSafe = false;
                    break;
                }
            }
            if (isSafe) {
                localVersions.add(version);
            }
        }
        if (localVersions.size() > 0) {
            int max = 0;
            for (String version : localVersions) {
                if (versionToModule.get(version).size() > max) {
                    safeVersion = version;
                    max = versionToModule.get(version).size();
                }
            }
        } else {
            List<String> testVersions = MavenCrawler.getVersionList(groupId + ":" + artifactId);
            int min = versions.size();
            for (String version : versions) {
                int index = testVersions.indexOf(version);
                if (index < min) {
                    min = index;
                }
            }
            for (int i = min + 1; i < testVersions.size(); i++) {
                String testVersion = testVersions.get(i);
                String jarPath = ArtifactResolver.i().resolver(groupId, artifactId, testVersion);
                Set<String> jarMethods = JarAna.i().deconstruct(jarPath);
                boolean isSafe = true;
                for (String method : usedMethods) {
                    if (!jarMethods.contains(method)) {
                        isSafe = false;
                        break;
                    }
                }
                if (isSafe) {
                    safeVersion = testVersion;
                    break;
                }
            }
        }
        return safeVersion;
    }

}
