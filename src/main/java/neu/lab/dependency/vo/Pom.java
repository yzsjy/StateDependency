package neu.lab.dependency.vo;

import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author SUNJUNYAN
 */
public class Pom {
    private String groupId;
    private String artifactId;
    private String version;
    private String packaging;
    private Model model;
    private Pom parent;
    private String filePath;
    private Map<String, String> properties;
    private List<String> dependencies;
    private List<String> dependencyManagement;
    private List<DepInfo> dependencyManagements;
    private List<DepInfo> inheritDepManagements;
    private List<DepInfo> ownDependencies;
    private List<DepInfo> inheritDependencies;

    public Pom() {

    }

    public Pom(String artifactId, String filePath, Model model) {
        this.artifactId = artifactId;
        this.filePath = filePath;
        this.model = model;
        init();
    }

    public Pom(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        init();
    }

    public Pom(String groupId, String artifactId, String version, Pom parent, String filePath) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.filePath = filePath;
        this.parent = parent;
        init();
    }

    public void init() {
        dependencies = new ArrayList<>();
        ownDependencies = new ArrayList<>();
        inheritDependencies = new ArrayList<>();
        dependencyManagement = new ArrayList<>();
        dependencyManagements = new ArrayList<>();
        inheritDepManagements = new ArrayList<>();

    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<String> getDependencies() {
        if (dependencies.isEmpty()) {
            for (DepInfo depInfo : getOwnDependencies()) {
                if (!dependencies.contains(depInfo.getSig())) {
                    dependencies.add(depInfo.getSig());
                }
            }
            for (DepInfo depInfo : getInheritDependencies()) {
                if (!dependencies.contains(depInfo.getSig())) {
                    dependencies.add(depInfo.getSig());
                }
            }
        }
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public List<String> getDependencyManagement() {
        return dependencyManagement;
    }

    public void setDependencyManagement(List<String> dependencyManagement) {
        this.dependencyManagement = dependencyManagement;
    }

    public List<DepInfo> getOwnDependencies() {
        return ownDependencies;
    }

    public void setOwnDependencies(List<DepInfo> ownDependencies) {
        this.ownDependencies = ownDependencies;
    }

    public List<DepInfo> getInheritDependencies() {
        return inheritDependencies;
    }

    public void setInheritDependencies(List<DepInfo> inheritDependencies) {
        this.inheritDependencies = inheritDependencies;
    }

    public List<DepInfo> getDependencyManagements() {
        return dependencyManagements;
    }

    public void setDependencyManagements(List<DepInfo> dependencyManagements) {
        this.dependencyManagements = dependencyManagements;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Pom getParent() {
        return parent;
    }

    public void setParent(Pom parent) {
        this.parent = parent;
    }

    public Model getModel() {
        return model;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<DepInfo> getInheritDepManagements() {
        return inheritDepManagements;
    }

    public void setInheritDepManagements(List<DepInfo> inheritDepManagements) {
        this.inheritDepManagements = inheritDepManagements;
    }

    public boolean equals(Pom pom) {
        if (pom.getGroupId().equals(groupId) && pom.getArtifactId().equals(artifactId) && pom.getVersion().equals(version)) {
            return true;
        }
        return false;
    }

    public boolean isSelf(String groupId, String artifactId, String version) {
        if (this.groupId.equals(groupId) && this.artifactId.equals(artifactId) && this.version.equals(version)) {
            return true;
        }
        return false;
    }

    public void addDependencies(String dep) {
        if (!dependencies.contains(dep)) {
            dependencies.add(dep);
        }
    }

    public String getSig() {
        return groupId + ":" + artifactId + ":" + version;
    }

    @Override
    public String toString() {
        return "Pom{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", packaging='" + packaging + '\'' +
                '}';
    }
}
