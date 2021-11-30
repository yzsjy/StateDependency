package neu.lab.dependency.graph;

import neu.lab.dependency.util.MavenUtil;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

import java.util.List;

public class MavenModuleGraph {

    private volatile static MavenModuleGraph instance;

    private int[][] modules;
    private int[][] wholeModules;

    private MavenModuleGraph() {

    }

    public static MavenModuleGraph i() {
        if (instance == null) {
            synchronized (MavenModuleGraph.class) {
                if (instance == null) {
                    instance = new MavenModuleGraph();
                }
            }
        }
        return instance;
    }

    public void graph() {
        buildGraph();
        buildWholeGraph();
    }

    public void buildWholeGraph() {
        ProjectDependencyGraph graph = MavenUtil.i().getDependencyGraph();
        List<MavenProject> projects = graph.getSortedProjects();
        int size = projects.size();
        wholeModules = new int[size][size];
        for (MavenProject project : projects) {
            int i = projects.indexOf(project);
            List<MavenProject> childs = graph.getUpstreamProjects(project, false);
            if (!childs.isEmpty()) {
                for (MavenProject child : childs) {
                    int j = projects.indexOf(child);
                    wholeModules[i][j] = 1;
                }
            }
        }
        GenerateGraphviz.i().moduleGraph(wholeModules, projects, MavenUtil.i().getName(), "wholeModule");
    }

    public void buildGraph() {
        ProjectDependencyGraph graph = MavenUtil.i().getDependencyGraph();
        List<MavenProject> projects = graph.getSortedProjects();
        int size = projects.size();
        modules = new int[size][size];

        for (MavenProject project : projects) {
            List<Dependency> dependencies = project.getDependencies();
            List<Plugin> plugins = project.getBuildPlugins();
            List<Extension> extensions = project.getBuildExtensions();
            MavenProject parent = project.getParent();
            int i = projects.indexOf(project);

            int p = getParentIndex(parent, projects);
            if (p != -1) {
                modules[i][p] = 4;
            }

            for (Dependency dependency : dependencies) {
                int j = getDependencyIndex(dependency, projects);
                if (j != -1) {
                    modules[i][j] = 1;
                }
            }

            for (Plugin plugin : plugins) {
                int j = getPluginIndex(plugin, projects);
                if (j != -1) {
                    modules[i][j] = 2;
                }
            }

            for (Extension extension : extensions) {
                int j = getExtensionIndex(extension, projects);
                if (j != -1) {
                    modules[i][j] = 3;
                }
            }
        }
        generateGraph(projects);
    }

    public int getDependencyIndex(Dependency dependency, List<MavenProject> projects) {
        int index = -1;
        for (int i = 0; i < projects.size(); i++) {
            MavenProject project = projects.get(i);
            if (project.getGroupId().equals(dependency.getGroupId())
                    && project.getArtifactId().equals(dependency.getArtifactId())
                    && project.getVersion().equals(dependency.getVersion())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int getPluginIndex(Plugin plugin, List<MavenProject> projects) {
        int index = -1;
        for (int i = 0; i < projects.size(); i++) {
            MavenProject project = projects.get(i);
            if (project.getGroupId().equals(plugin.getGroupId())
                    && project.getArtifactId().equals(plugin.getArtifactId())
                    && project.getVersion().equals(plugin.getVersion())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int getExtensionIndex(Extension extension, List<MavenProject> projects) {
        int index = -1;
        for (int i = 0; i < projects.size(); i++) {
            MavenProject project = projects.get(i);
            if (project.getGroupId().equals(extension.getGroupId())
                    && project.getArtifactId().equals(extension.getArtifactId())
                    && project.getVersion().equals(extension.getVersion())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int getParentIndex(MavenProject parent, List<MavenProject> projects) {
        int index = -1;
        if (parent == null) {
            return index;
        }
        for (int i = 0; i < projects.size(); i++) {
            MavenProject project = projects.get(i);
            if (project.getGroupId().equals(parent.getGroupId())
                    && project.getArtifactId().equals(parent.getArtifactId())
                    && project.getVersion().equals(parent.getVersion())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void generateGraph(List<MavenProject> projects) {
        GenerateGraphviz.i().moduleDetailGraph(modules, projects, MavenUtil.i().getName(), "detailModule");
        for (int i = 1; i <= 4; i++) {
            GenerateGraphviz.i().moduleGraph(modules, projects, MavenUtil.i().getName(), i);
        }
    }
}
