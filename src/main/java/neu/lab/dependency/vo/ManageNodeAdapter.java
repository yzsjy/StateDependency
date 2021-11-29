package neu.lab.dependency.vo;

import neu.lab.dependency.util.MavenUtil;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.shared.dependency.tree.DependencyNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SUNJUNYAN
 */
public class ManageNodeAdapter extends NodeAdapter {
    private String groupId;
    private String artifactId;
    private String version;
    private String classifier;
    private String type;
    private String scope;
    private Artifact artifact;

    public ManageNodeAdapter(NodeAdapter nodeAdapter) {
        super(null);
        groupId = nodeAdapter.getGroupId();
        artifactId = nodeAdapter.getArtifactId();
        version = nodeAdapter.getManagedVersion();
        classifier = nodeAdapter.getClassifier();
        type = nodeAdapter.getType();
        scope = nodeAdapter.getScope();

        artifact = MavenUtil.i().getArtifact(getGroupId(), getArtifactId(), getVersion(), getType(),
                getClassifier(), getScope());
        if (!artifact.isResolved()) {
//                MavenUtil.i().resolve(artifact);
        }

    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getClassifier() {
        return classifier;
    }

    @Override
    public boolean isNodeSelected() {
        return true;
    }

    public boolean isVersionSelected() {
        return true;
    }

    @Override
    public String getManagedVersion() {
        return version;
    }

    @Override
    public NodeAdapter getParent() {
        return null;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public boolean isVersionChanged() {
        return false;
    }

    @Override
    public List<String> getFilePath() {
        if (filePaths == null) {
            filePaths = new ArrayList<String>();
            if (isInnerProject()) {
                // inner project is target/classes
                filePaths.add(MavenUtil.i().getMavenProject(this).getBuild().getOutputDirectory());
            } else {// dependency is repository address
                String path = artifact.getFile().getAbsolutePath();
                filePaths.add(path);
            }
        }
        MavenUtil.i().getLog().debug("node filepath for " + toString() + " : " + filePaths);
        return filePaths;
    }

    @Override
    public boolean isSelf(DependencyNode node2) {
        return false;
    }
}
