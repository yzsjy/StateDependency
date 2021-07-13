package neu.lab.dependency.pojo;

/**
 * @author SUNJUNYAN
 */
public class IndirectDep {
    private int id;
    private String groupId;
    private String artifactId;
    private int num;

    public IndirectDep() {

    }

    public IndirectDep(String groupId, String artifactId, int num) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.num = num;
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
