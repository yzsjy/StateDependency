package neu.lab.dependency.pom;

import neu.lab.dependency.container.Poms;
import neu.lab.dependency.graph.ModuleGraph;
import neu.lab.dependency.vo.DepInfo;
import neu.lab.dependency.vo.Pom;

import java.util.*;

/**
 * @author SUNJUNYAN
 */
public class PomParser {

    private volatile static PomParser instance;

    private String projPath;
    private Set<String> visited;

    private PomParser() {

    }

    private PomParser(String projPath) {
        this.projPath = projPath;
        visited = new HashSet<>();
        parseProject();
    }

    public static void init(String projPath) {
            instance = new PomParser(projPath);
    }

    public static PomParser i() {
        if (instance == null) {
            synchronized (PomParser.class) {
                if (instance == null) {
                    instance = new PomParser();
                }
            }
        }
        return instance;
    }

    public void parseProject() {
        Poms.init(projPath);
        ModuleParser.i().parseDependencies();
        parseInheritance();
    }

    public void parseInheritance() {
        for (Pom pom : Poms.i().getPoms()) {
            if (visited.contains(pom.getSig())) {
                continue;
            }
            parsePom(pom);
        }
    }

    public void parsePom(Pom pom) {
        List<DepInfo> dependencies = new ArrayList<>();
        List<DepInfo> dependencyManagement = new ArrayList<>();
        if (pom.getParent() != null) {
            if (!visited.contains(pom.getParent().getSig())) {
                parsePom(pom.getParent());
            }
            Pom parent = pom.getParent();
            dependencies.addAll(parent.getOwnDependencies());
            dependencies.addAll(parent.getInheritDependencies());
            dependencyManagement.addAll(parent.getDependencyManagements());
            dependencyManagement.addAll(parent.getInheritDepManagements());
        }
        pom.setInheritDependencies(dependencies);
        pom.setInheritDepManagements(dependencyManagement);
        visited.add(pom.getSig());
    }

    public static void main(String[] args) {
        String projPath = "D:\\githubProject\\blueocean-plugin\\";
        PomParser pomParser = new PomParser(projPath);
        pomParser.parseProject();
        ModuleGraph.i().generateGraph();
        int[][] modules = ModuleGraph.i().getModules();
        int[][] inheritance = ModuleGraph.i().getInheritance();
        Map<String, Integer> indexes = ModuleGraph.i().getSigToIndex();
    }
}
