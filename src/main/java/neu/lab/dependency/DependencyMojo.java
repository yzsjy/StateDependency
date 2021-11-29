package neu.lab.dependency;

import neu.lab.dependency.container.DepJars;
import neu.lab.dependency.container.NodeAdapters;
import neu.lab.dependency.util.Conf;
import neu.lab.dependency.util.MavenUtil;
import neu.lab.dependency.vo.DepJar;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.path.PathTranslator;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

import java.io.File;
import java.util.List;

/**
 * @author SUNJUNYAN
 */
public abstract class DependencyMojo extends AbstractMojo {
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    public MavenSession session;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    public MavenProject project;

    @Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
    public List<MavenProject> reactorProjects;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
    public List<ArtifactRepository> remoteRepositories;

    @Parameter(defaultValue = "${localRepository}", readonly = true)
    public ArtifactRepository localRepository;

    @Component
    public DependencyTreeBuilder dependencyTreeBuilder;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    public File buildDir;

    @Parameter(defaultValue = "${project.build.testOutputDirectory}", readonly = true)
    public File testDir;

    @Component
    public ArtifactFactory factory;

    DependencyNode root;

    @Parameter(property = "ignoreProvidedScope", defaultValue = "false")
    public boolean ignoreProvidedScope;

    @Parameter(property = "ignoreTestScope", defaultValue = "true")
    public boolean ignoreTestScope;

    @Parameter(property = "ignoreRuntimeScope", defaultValue = "false")
    public boolean ignoreRuntimeScope;

    @Parameter(defaultValue = "${project.compileSourceRoots}", readonly = true, required = true)
    public List<String> compileSourceRoots;

    @Parameter(property = "append", defaultValue = "false")
    public boolean append;

    @Parameter(property = "resultPath")
    public String resultPath = "." + File.separator;

    @Parameter(property = "allowSnapshots", defaultValue = "true")
    public boolean allowSnapshots;

    @Parameter(property = "maven.version.rules")
    public String rulesUri;

    @Parameter(property = "maven.version.rules.serverId", defaultValue = "serverId")
    public String serverId;

    @Parameter(defaultValue = "${settings}", required = true)
    public Settings settings;

    @Component
    public WagonManager wagonManager;

    @Parameter( defaultValue = "${project.pluginArtifactRepositories}", readonly = true )
    public List remotePluginRepositories;

    @Parameter( defaultValue = "${project.remoteArtifactRepositories}", readonly = true )
    public List remoteArtifactRepositories;

    @Override
    public void execute() {
        this.getLog().info("detect start:");

        run();

        this.getLog().debug("detect end");
    }

    /**
     * 每个Mojo重写该方法
     */
    public abstract void run();
}
