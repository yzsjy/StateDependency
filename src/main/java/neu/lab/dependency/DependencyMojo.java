package neu.lab.dependency;

import neu.lab.dependency.container.DepJars;
import neu.lab.dependency.container.NodeAdapters;
import neu.lab.dependency.util.Conf;
import neu.lab.dependency.util.MavenUtil;
import neu.lab.dependency.version.DefaultVersionsHelper;
import neu.lab.dependency.version.VersionsHelper;
import neu.lab.dependency.vo.DepJar;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
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

    @Component
    public ArtifactResolver resolver;
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

    @Component
    public PathTranslator pathTranslator;

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

    @Component
    public ArtifactMetadataSource artifactMetadataSource;

    public int systemSize = 0;

    public long systemFileSize = 0;

    private VersionsHelper helper;

    protected void initGlobalVar() throws Exception {
        MavenUtil.i().setMojo(this);
        Conf.outDir = resultPath;
        Conf.append = append;

        NodeAdapters.init(root);
        DepJars.init(NodeAdapters.i());
        validateSystemSize();
    }

    private void validateSystemSize() throws Exception {

        for (DepJar depJar : DepJars.i().getAllDepJar()) {
            if (depJar.isSelected()) {
                systemSize++;
                for (String filePath : depJar.getJarFilePaths(true)) {
                    systemFileSize = systemFileSize + new File(filePath).length();
                }
            }
        }

        MavenUtil.i().getLog().info("tree size:" + DepJars.i().getAllDepJar().size() + ", used size:" + systemSize
                + ", usedFile size:" + systemFileSize / 1000);

    }

    public VersionsHelper getHelper()
            throws MojoExecutionException {
        if (helper == null) {
            helper = new DefaultVersionsHelper(factory, resolver, artifactMetadataSource,
                    remoteArtifactRepositories, remotePluginRepositories, localRepository,
                    wagonManager, settings, serverId, rulesUri, getLog(), session,
                    pathTranslator);
        }
        return helper;
    }

    @Override
    public void execute() throws MojoExecutionException {
        this.getLog().info("method detect start:");
        long startTime = System.currentTimeMillis();
        String pckType = project.getPackaging();
        if ("jar".equals(pckType) || "war".equals(pckType) || "maven-plugin".equals(pckType)
                || "bundle".equals(pckType)) {
            try {
                // project.
                root = dependencyTreeBuilder.buildDependencyTree(project, localRepository, null);
            } catch (DependencyTreeBuilderException e) {
                throw new MojoExecutionException(e.getMessage());
            }
            try {
                initGlobalVar();
            } catch (Exception e) {
                MavenUtil.i().getLog().error(e);
                throw new MojoExecutionException("project size error!");
            }
            run();

        } else {
            this.getLog()
                    .info("this project fail because package type is neither jar nor war:" + project.getGroupId() + ":"
                            + project.getArtifactId() + ":" + project.getVersion() + "@"
                            + project.getFile().getAbsolutePath());
        }
        this.getLog().debug("method detect end");
    }

    /**
     * ??????Mojo???????????????
     */
    public abstract void run();
}
