package neu.lab.dependency.vo;

import org.dom4j.Element;

/**
 * define some information of specify dependency
 * @author yzsjy
 */

public class DependencyInfo {
    private String groupId;
    private String artifactId;
    private String version;

    public DependencyInfo() {

    }

    public DependencyInfo(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public DependencyInfo(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId =artifactId;
        this.version = version;
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

    public void addDependencyElement(Element dependency) {
        dependency.addElement("artifactId").setText(artifactId);
        dependency.addElement("groupId").setText(groupId);
        dependency.addElement("version").setText(version);
    }

    public String getLogFileName() {
        return (groupId + artifactId + version).replaceAll("\\p{Punct}", "");
    }

    public String getName() {
        return groupId + ":" + artifactId + ":jar:" + version;
    }

    public String getSig() {
        return groupId + ":" + artifactId;
    }

    public String getDepInfo() {
        return groupId + ":" + artifactId + ":" + version;
    }

    @Override
    public String toString() {
        return "DependencyInfo{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
