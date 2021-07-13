package neu.lab.dependency.version.model;

import java.io.Serializable;

/**
 * @author SUNJUNYAN
 */
public class IgnoreVersion implements Serializable {
    private String version;

    private String type = "exact";

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(version);
        buf.append(" (");
        buf.append(type);
        buf.append(")");
        return buf.toString();
    }
}
