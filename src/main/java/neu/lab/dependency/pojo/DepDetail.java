package neu.lab.dependency.pojo;

/**
 * @author SUNJUNYAN
 */
public class DepDetail {
    private int id;
    private String groupId;
    private String artifactId;
    private String version;
    private int depNum;
    private int usedDepNum;
    private String path;

    public DepDetail() {

    }

    public DepDetail(String groupId, String artifactId, String version, int usedDepNum, int depNum, String path) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.usedDepNum =usedDepNum;
        this.depNum = depNum;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getDepNum() {
        return depNum;
    }

    public void setDepNum(int depNum) {
        this.depNum = depNum;
    }

    public int getUsedDepNum() {
        return usedDepNum;
    }

    public void setUsedDepNum(int usedDepNum) {
        this.usedDepNum = usedDepNum;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
