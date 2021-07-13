package neu.lab.dependency.version.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SUNJUNYAN
 */
public class Rule {

    private List<IgnoreVersion> ignoreVersions;
    private String groupId;
    private String artifactId;
    private String comparisonMethod;

    public void addIgnoreVersion(IgnoreVersion ignoreVersion) {
        getIgnoreVersions().add(ignoreVersion);
    }

    /**
     * Get the artifactId to which this rule applies. Wildcards
     * with ? and * are valid.
     * A rule without wildcards will override a rule
     * with wildcards.
     * A rule with ? wildcards will override a rule
     * with * wildcards.
     *
     * @return String
     */
    public String getArtifactId() {
        return this.artifactId;
    }

    /**
     * Get the comparison method that this rule specifies.
     *
     * @return String
     */
    public String getComparisonMethod() {
        return this.comparisonMethod;
    }

    /**
     * Get the for groupId to which this rule applies. Wildcards
     * with ? and * are valid.
     * A rule applies to all child groupIds unless
     * overridden by a subsequent rule.
     * A rule without wildcards will override a rule
     * with wildcards.
     * A rule with ? wildcards will override a rule
     * with * wildcards.
     *
     * @return String
     */
    public String getGroupId() {
        return this.groupId;
    }

    public List<IgnoreVersion> getIgnoreVersions() {
        if (ignoreVersions == null) {
            ignoreVersions = new ArrayList<>();
        }
        return ignoreVersions;
    }

    public void removeIgnoreVersion(IgnoreVersion ignoreVersion) {
        getIgnoreVersions().remove(ignoreVersion);
    }

    /**
     * Set the artifactId to which this rule applies. Wildcards
     * with ? and * are valid.
     * A rule without wildcards will override a rule
     * with wildcards.
     * A rule with ? wildcards will override a rule
     * with * wildcards.
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * Set the comparison method that this rule specifies.
     */
    public void setComparisonMethod(String comparisonMethod) {
        this.comparisonMethod = comparisonMethod;
    }

    /**
     * Set the for groupId to which this rule applies. Wildcards
     * with ? and * are valid.
     * A rule applies to all child groupIds unless
     * overridden by a subsequent rule.
     * A rule without wildcards will override a rule
     * with wildcards.
     * A rule with ? wildcards will override a rule
     * with * wildcards.
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Set version patterns to ignore for this rule.
     */
    public void setIgnoreVersions(List<IgnoreVersion> ignoreVersions) {
        this.ignoreVersions = ignoreVersions;
    }

    /**
     * Creates a new empty rule.
     */
    public Rule() {
        // enables no-arg construction
        artifactId = "*";
        comparisonMethod = "maven";
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Rule[groupId = \"");
        buf.append(groupId);
        buf.append("\", artifactId = \"");
        buf.append(artifactId);
        buf.append("\", comparisonMethod = \"");
        buf.append(comparisonMethod);
        buf.append("\", ignoreVersions = \"");
        buf.append(ignoreVersions);
        buf.append("\"]");
        return buf.toString();
    }
}
