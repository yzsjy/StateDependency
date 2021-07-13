package neu.lab.dependency.vo;

import neu.lab.dependency.container.DepJars;
import neu.lab.dependency.container.NodeAdapters;
import neu.lab.dependency.util.ClassifierUtil;
import neu.lab.dependency.util.MavenUtil;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;

import java.io.File;
import java.util.*;

/**
 * @author SUNJUNYAN
 */
public class NodeAdapter {
    protected DependencyNode node;
    protected DepJar depJar;
    protected List<String> filePaths;

    public NodeAdapter(DependencyNode node) {
        this.node = node;
        if (node != null) {
            resolve();
        }
    }

    private void resolve() {
        try {
            if (!isInnerProject()) {
                // inner project is target/classes 内部项目是target/classes
                if (null == node.getPremanagedVersion()) {
                    // artifact version of node is the version declared in pom. 节点的构件版本是POM中声明的版本。
                    if (!node.getArtifact().isResolved()) {
                        MavenUtil.i().resolve(node.getArtifact());
                    }
                } else {
                    Artifact artifact = MavenUtil.i().getArtifact(getGroupId(), getArtifactId(), getVersion(),
                            getType(), getClassifier(), getScope());
                    if (!artifact.isResolved()) {
                        MavenUtil.i().resolve(artifact);
                    }
                }
            }
        } catch (ArtifactResolutionException e) {
            MavenUtil.i().getLog().warn("cant resolve " + this.toString());
        } catch (ArtifactNotFoundException e) {
            MavenUtil.i().getLog().warn("cant resolve " + this.toString());
        }
    }

    public String getSelectedNodeWholeSig(){
        return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
    }

    public String getGroupId() {
        return node.getArtifact().getGroupId();
    }

    public String getScope() {
        return node.getArtifact().getScope();
    }

    public String getArtifactId() {
        return node.getArtifact().getArtifactId();
    }

    public String getVersion() {
        if (null != node.getPremanagedVersion()) {
            return node.getPremanagedVersion();
        } else {
            return node.getArtifact().getVersion();
        }
    }

    /**
     * version changes because of dependency management 被dependency management更改过版本
     *
     * @return
     */
    public boolean isVersionChanged() {
        return null != node.getPremanagedVersion();
    }

    public String getType() {
        return node.getArtifact().getType();
    }

    public String getClassifier() {
        return ClassifierUtil.transformClf(node.getArtifact().getClassifier());
    }

    /**
     * used version is select from this node,if version was from management ,this
     * node will return false. 这个版本的node是否被使用，如果被management更改过版本，将返回false
     *
     * @return
     */
    public boolean isNodeSelected() {
        if (isVersionChanged()) {
            return false;
        }
        return node.getState() == DependencyNode.INCLUDED;
    }

    public String getManagedVersion() {
        return node.getArtifact().getVersion();
    }


    /**
     * @param includeSelf :whether includes self
     * @return ancestors(from down to top) 从下至上
     */
    public LinkedList<NodeAdapter> getAncestors(boolean includeSelf) {
        LinkedList<NodeAdapter> ancestors = new LinkedList<NodeAdapter>();
        if (includeSelf) {
            ancestors.add(this);
        }
        NodeAdapter father = getParent();
        while (null != father) {
            ancestors.add(father);
            father = father.getParent();
        }
        return ancestors;
    }

    /**
     * jarClasspaths
     * 得到所有祖先节点的JarClassPath
     * @param includeSelf
     * @return
     */
    public Collection<String> getAncestorJarCps(boolean includeSelf) {
        List<String> jarCps = new ArrayList<String>();
        if (includeSelf) {
            jarCps.addAll(this.getFilePath());
        }
        NodeAdapter father = getParent();
        while (null != father) {
            jarCps.addAll(father.getFilePath());
            father = father.getParent();
        }
        return jarCps;
    }

    public String getOnlySelectedNodeSig(){
        return getGroupId() + ":" + getArtifactId();
    }

    /**
     * 得到父节点的jar classpath
     * 只得到一层
     * @param includeSelf
     */
    public Set<String> getParentJarClassPath(boolean includeSelf) {
        Set<String> jarClassPath = new HashSet<String>();
        if (includeSelf) {
            jarClassPath.addAll(this.getFilePath());
        }
        NodeAdapter father = getParent();
        jarClassPath.addAll(father.getFilePath());
        return jarClassPath;
    }

    /**
     * 得到父节点
     *
     * @return
     */
    public NodeAdapter getParent() {
        if (null == node.getParent()) {
            return null;
        }
        return NodeAdapters.i().getNodeAdapter(node.getParent());
    }

    /**
     * 得到文件路径
     *
     * @return
     */
    public List<String> getFilePath() {
        if (filePaths == null) {
            filePaths = new ArrayList<String>();
            if (isInnerProject()) {// inner project is target/classes
                filePaths.add(MavenUtil.i().getMavenProject(this).getBuild().getOutputDirectory());
                // filePaths = UtilGetter.i().getSrcPaths();
            } else {// dependency is repository address

                try {
                    if (null == node.getPremanagedVersion()) {
                        filePaths.add(node.getArtifact().getFile().getAbsolutePath());
                    } else {
                        Artifact artifact = MavenUtil.i().getArtifact(getGroupId(), getArtifactId(), getVersion(),
                                getType(), getClassifier(), getScope());
                        if (!artifact.isResolved()) {
                            MavenUtil.i().resolve(artifact);
                        }
                        filePaths.add(artifact.getFile().getAbsolutePath());
                    }
                } catch (ArtifactResolutionException e) {
                    MavenUtil.i().getLog().warn("cant resolve " + this.toString());
                } catch (ArtifactNotFoundException e) {
                    MavenUtil.i().getLog().warn("cant resolve " + this.toString());
                }

            }
        }
        MavenUtil.i().getLog().debug("node filepath for " + toString() + " : " + filePaths);
        return filePaths;

    }

    public boolean isInnerProject() {
        return MavenUtil.i().isInner(this);
    }

    public boolean isSelf(DependencyNode node2) {
        return node.equals(node2);
    }

    public boolean isSelf(MavenProject mavenProject) {
        return getGroupId().equals(mavenProject.getGroupId()) && getArtifactId().equals(mavenProject.getArtifactId())
                && getVersion().equals(mavenProject.getVersion())
                && getClassifier().equals(ClassifierUtil.transformClf(mavenProject.getArtifact().getClassifier()));
    }

    public boolean isSelf(NodeAdapter entryNodeAdapter) {
        return getGroupId().equals(entryNodeAdapter.getGroupId())
                && getArtifactId().equals(entryNodeAdapter.getArtifactId())
                && getVersion().equals(entryNodeAdapter.getVersion())
                && getClassifier().equals(entryNodeAdapter.getClassifier());
    }

    public MavenProject getSelfMavenProject() {
        return MavenUtil.i().getMavenProject(this);
    }

    public DepJar getDepJar() {
        if (depJar == null) {
            depJar = DepJars.i().getDep(this);
        }
        return depJar;
    }

    @Override
    public String toString() {
        String scope = getScope();
        if (null == scope) {
            scope = "";
        }
        return getGroupId() + ":" + getArtifactId() + ":" + getVersion() + ":" + getClassifier() + ":" + scope;
    }

    public String getWholePath() {
        StringBuilder sb = new StringBuilder(toString());
        NodeAdapter father = getParent();
        while (null != father) {
            sb.insert(0, father.toString() + " + ");
            father = father.getParent();
        }
        return sb.toString();
    }

    public int getNodeDepth() {
        int depth = 1;
        NodeAdapter father = getParent();
        while (null != father) {
            depth++;
            father = father.getParent();
        }
        return depth;
    }

    public String getNodePath() {
        StringBuilder sb = new StringBuilder();
        NodeAdapter father = getParent();
        while (null != father) {
            if (!father.getFilePath().iterator().next().endsWith(".pom")) {
                sb.insert(0, father.getFilePath().iterator().next() + File.pathSeparator);
            }
            father = father.getParent();
        }
        sb.insert(0, MavenUtil.i().getTestBuildDir().getAbsolutePath() + File.pathSeparator);
        return sb.toString();
    }
}
