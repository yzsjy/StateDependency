package neu.lab.dependency.vo;

import neu.lab.dependency.container.DepJars;
import neu.lab.dependency.container.NodeAdapters;
import neu.lab.dependency.util.MavenUtil;
import neu.lab.dependency.util.SootUtil;

import java.util.*;

/**
 * @author SUNJUNYAN
 */
public class DepJar {
    private String groupId;
    private String artifactId;
    private String version;
    private String classifier;
    private List<String> jarFilePaths;
    private Set<NodeAdapter> nodeAdapters;

    public DepJar(String groupId, String artifactId, String version, String classifier, List<String> jarFilePaths) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.classifier = classifier;
        this.jarFilePaths = jarFilePaths;
    }

    public DepJar getUsedDepJar() {
        for (DepJar depJar : DepJars.i().getAllDepJar()) {
            if (isSameLib(depJar) && depJar.isSelected()) {
                return depJar;
            }
        }
        return this;
    }

    public Set<NodeAdapter> getNodeAdapters() {
        if (nodeAdapters == null) {
            nodeAdapters = NodeAdapters.i().getNodeAdapters(this);
        }
        return nodeAdapters;
    }

    public String getScope() {
        String scope = null;
        for (NodeAdapter node : nodeAdapters) {
            scope = node.getScope();
            if (scope != null) {
                break;
            }
        }
        return scope;
    }

    public String getAllDepPath() {
        StringBuilder sb = new StringBuilder(toString() + ":");
        for (NodeAdapter node : getNodeAdapters()) {
            sb.append("  [");
            sb.append(node.getWholePath());
            sb.append("]");
        }
        return sb.toString();

    }

    /**
     * @return the import path of depJar.
     */
    public String getValidDepPath() {
        StringBuilder sb = new StringBuilder(toString() + ":");
        for (NodeAdapter node : getNodeAdapters()) {
            if (node.isNodeSelected()) {
                sb.append("  [");
                sb.append(node.getWholePath());
                sb.append("]");
            }
        }
        return sb.toString();
    }

	public NodeAdapter getSelectedNode() {
		for (NodeAdapter node : getNodeAdapters()) {
			if (node.isNodeSelected()) {
				return node;
			}
		}
		return null;
	}

    public boolean isSelected() {
        for (NodeAdapter nodeAdapter : getNodeAdapters()) {
            if (nodeAdapter.isNodeSelected()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DepJar) {
            return isSelf((DepJar) obj);

        }
        return false;
    }

    @Override
    public int hashCode() {
        return groupId.hashCode() * 31 * 31 + artifactId.hashCode() * 31 + version.hashCode()
                + classifier.hashCode() * 31 * 31 * 31;
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getClassifier() {
        return classifier;
    }

    /**
     * 是否为同一个
     * @param depJar
     * @return
     */
    public boolean isSelf(DepJar depJar) {
        return isSame(depJar.getGroupId(), depJar.getArtifactId(), depJar.getVersion(), depJar.getClassifier());
    }

    public boolean isSame(String groupId2, String artifactId2, String version2, String classifier2) {
        return groupId.equals(groupId2) && artifactId.equals(artifactId2) && version.equals(version2)
                && classifier.equals(classifier2);
    }

    /**
     * 没有比较版本
     * @param depJar
     * @return
     */
    public boolean isSameLib(DepJar depJar) {
        return getGroupId().equals(depJar.getGroupId()) && getArtifactId().equals(depJar.getArtifactId());
    }

    /**
     * @param useTarget: host-class-name can get from source directory(false) or
     *                   target directory(true). using source directory: advantage: get class
     *                   before maven-package disadvantage:class can't deconstruct by soot;miss
     *                   class that generated.
     *                   主机类名称可以从源目录(False)或目标目录(True)获得.使用源目录。优点：获取类之前maven包的缺点：类不能被soot解构，错过类生成。
     *                   true:[C:\Users\Flipped\.m2\repository\neu\lab\testcase\TA\1.0\TA-1.0.jar]
     * @return
     */
    public List<String> getJarFilePaths(boolean useTarget) {
        if (!useTarget && isHost()) {
            return MavenUtil.i().getSrcPaths();
        }
        return jarFilePaths;
    }

    public boolean isHost() {
        if (getNodeAdapters().size() == 1) {
            NodeAdapter node = getNodeAdapters().iterator().next();
            if (MavenUtil.i().isInner(node))
                return true;
        }
        return false;
    }

    public String getDepJarName() {
        return (groupId + artifactId + version).replaceAll("\\p{Punct}", "");
    }

    public Set<String> getAllCls(boolean useTarget) {
        return SootUtil.getJarClasses(this.getJarFilePaths(useTarget));
    }
}
