package neu.lab.dependency.util;

import neu.lab.dependency.DependencyMojo;
import neu.lab.dependency.vo.NodeAdapter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SUNJUNYAN
 */
public class MavenUtil {
    private volatile static MavenUtil instance;

    public static MavenUtil i() {
        if (instance == null) {
            synchronized (MavenUtil.class) {
                if (instance == null) {
                    instance = new MavenUtil();
                }
            }
        }
        return instance;
    }

    private MavenUtil() {

    }

    private DependencyMojo mojo;

    public boolean isInner(NodeAdapter nodeAdapter) {
        return nodeAdapter.isSelf(mojo.project);
    }

    public MavenProject getMavenProject(NodeAdapter nodeAdapter) {
        for (MavenProject mavenProject : mojo.reactorProjects) {
            if (nodeAdapter.isSelf(mavenProject)) {
                return mavenProject;
            }
        }
        return null;
    }

    public String getName() {
        String path = getBaseDir().getAbsolutePath();
        String tempPath = path.split("/unzip/")[1];
        String name = tempPath.split("/pom.xml")[0];
        return name;
    }

    public void setMojo(DependencyMojo mojo) {
        this.mojo = mojo;
    }

    public Log getLog() {
        return mojo.getLog();
    }

    public Artifact getArtifact(String groupId, String artifactId, String versionRange, String type, String classifier,
                                String scope) {
        try {
            return mojo.factory.createDependencyArtifact(groupId, artifactId,
                    VersionRange.createFromVersionSpec(versionRange), type, classifier, scope);
        } catch (InvalidVersionSpecificationException e) {
            getLog().error("cant create Artifact!", e);
            return null;
        }
    }


    public String getProjectInfo() {
        return mojo.project.getGroupId() + ":" + mojo.project.getArtifactId() + ":" + mojo.project.getVersion() + "@"
                + mojo.project.getFile().getAbsolutePath();
    }

    /**
     * 得到项目pom.xml的位置
     *
     * @return
     */
    public String getProjectPom() {
        return mojo.project.getFile().getAbsolutePath();
    }

    public File getProjectFile() {
        return mojo.project.getFile();
    }

    public String getProjectCor() {
        return mojo.project.getGroupId() + ":" + mojo.project.getArtifactId() + ":" + mojo.project.getVersion();
    }

    public String getProjectGroupId() {
        return mojo.project.getGroupId();
    }

    public String getProjectArtifactId() {
        return mojo.project.getArtifactId();
    }

    public String getProjectVersion() {
        return mojo.project.getVersion();
    }

    public DependencyMojo getMojo() {
        return mojo;
    }

    /**
     * D:\cWS\eclipse1\testcase.top
     *
     * @return
     */
    public File getBaseDir() {
        return mojo.project.getBasedir();
    }

    public File getBuildDir() {
        return mojo.buildDir;
    }

    public File getTestBuildDir() {
        return mojo.testDir;
    }

    public List<String> getSrcPaths() {
        List<String> srcPaths = new ArrayList<String>();
        if (this.mojo == null) {
            return null;
        }
        for (String srcPath : this.mojo.compileSourceRoots) {
            if (new File(srcPath).exists()) {
                srcPaths.add(srcPath);
            }
        }
        return srcPaths;
    }

    public String getMvnRep() {
        return this.mojo.localRepository.getBasedir() + File.separator;
    }
}
