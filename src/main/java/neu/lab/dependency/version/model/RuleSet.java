package neu.lab.dependency.version.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SUNJUNYAN
 */
public class RuleSet implements Serializable {
    private List<IgnoreVersion> ignoreVersions;
    private List<Rule> rules;
    private String comparisonMethod;
    private String modelEncoding = "UTF-8";

    public void addIgnoreVersion(IgnoreVersion ignoreVersion) {
        getIgnoreVersions().add(ignoreVersion);
    }

    public void addRule(Rule rule) {
        getRules().add(rule);
    }

    public String getComparisonMethod() {
        return comparisonMethod;
    }

    public List<IgnoreVersion> getIgnoreVersions() {
        if (ignoreVersions == null) {
            ignoreVersions = new ArrayList<>();
        }
        return ignoreVersions;
    }

    public String getModelEncoding() {
        return modelEncoding;
    }

    public List<Rule> getRules() {
        if (rules == null) {
            rules = new ArrayList<>();
        }
        return rules;
    }

    public void removeIgnoreVersion(IgnoreVersion ignoreVersion) {
        getIgnoreVersions().remove(ignoreVersion);
    }

    public void removeRule(Rule rule) {
        getRules().remove(rule);
    }

    public void setComparisonMethod(String comparisonMethod) {
        this.comparisonMethod = comparisonMethod;
    }

    public void setIgnoreVersions(List<IgnoreVersion> ignoreVersions) {
        this.ignoreVersions = ignoreVersions;
    }

    public void setModelEncoding(String modelEncoding) {
        this.modelEncoding = modelEncoding;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public RuleSet() {
        comparisonMethod = "maven";
    }
}
