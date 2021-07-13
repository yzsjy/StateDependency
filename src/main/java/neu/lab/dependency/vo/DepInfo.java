package neu.lab.dependency.vo;

/**
 * @author SUNJUNYAN
 */
public class DepInfo {

    private String groupId;
    private String artifactId;
    private String version;
    private String propertiesName;
    private boolean isProperty;
    private Pom versionPom;
    private Pom propertyPom;
    private Pom declarePom;

    public DepInfo() {

    }

    public DepInfo(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
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

    public boolean isProperty() {
        return isProperty;
    }

    public void setProperty(boolean property) {
        isProperty = property;
    }

    public Pom getVersionPom() {
        return versionPom;
    }

    public void setVersionPom(Pom versionPom) {
        this.versionPom = versionPom;
    }

    public Pom getPropertyPom() {
        return propertyPom;
    }

    public void setPropertyPom(Pom propertyPom) {
        this.propertyPom = propertyPom;
    }

    public Pom getDeclarePom() {
        return declarePom;
    }

    public void setDeclarePom(Pom declarePom) {
        this.declarePom = declarePom;
    }

    public String getPropertiesName() {
        return propertiesName;
    }

    public void setPropertiesName(String propertiesName) {
        this.propertiesName = propertiesName;
    }

    public String getSig() {
        return groupId + ":" + artifactId + ":" + version;
    }
}
