package neu.lab.dependency.pom;

import guru.nidi.graphviz.attribute.Arrow;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.Node;
import neu.lab.dependency.container.Poms;
import neu.lab.dependency.util.Conf;
import neu.lab.dependency.vo.DepInfo;
import neu.lab.dependency.vo.Pom;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.*;

/**
 * @author SUNJUNYAN
 */
public class PomParser {

    private String projPath;
    private Set<String> visited;

    public PomParser() {

    }

    public PomParser(String projPath) {
        this.projPath = projPath;
        visited = new HashSet<>();
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

    public void generateGraph(int[][] map, Map<String, Integer> indexs, Set<String> conflicts, String projName) {
        int size = indexs.size();
        Node[] nodes = new Node[size];
        for (Map.Entry<String, Integer> entry : indexs.entrySet()) {
            if (conflicts.contains(entry.getKey())) {
                nodes[entry.getValue()] = node(entry.getKey()).with(Color.RED.font());
            } else {
                nodes[entry.getValue()] = node(entry.getKey());
            }
        }
        List<Node> nodeList = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            List<Link> links = new ArrayList<>();
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 1) {
                    links.add(to(nodes[j]).with(Arrow.NORMAL.open()));
                }
            }
            nodeList.add(nodes[i].link(links));
        }

        Graph g = graph("example").directed().graphAttr().with(Rank.dir(LEFT_TO_RIGHT)).with(nodeList);
        try {
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(Conf.Dir + "graph" + File.separator + projName + File.separator + "inheritance.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateGraph(int[][] map, Map<String, Integer> indexs, Set<String> conflicts, String classify, String projName) {
        int size = indexs.size();
        Node[] nodes = new Node[size];
        for (Map.Entry<String, Integer> entry : indexs.entrySet()) {
            if (conflicts.contains(entry.getKey())) {
                nodes[entry.getValue()] = node(entry.getKey()).with(Color.RED.font());
            } else {
                nodes[entry.getValue()] = node(entry.getKey());
            }
        }
        List<Node> nodeList = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            List<Link> links = new ArrayList<>();
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 1) {
                    links.add(to(nodes[j]));
                }
            }
            nodeList.add(nodes[i].link(links));
        }

        Graph g = graph("example").directed().graphAttr().with(Rank.dir(LEFT_TO_RIGHT)).with(nodeList);
        try {
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(Conf.Dir + "graph" + File.separator + projName + File.separator + classify + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateGraph(int[][] map, Map<String, Integer> indexs, String classify, String projName) {
        int size = indexs.size();
        Node[] nodes = new Node[size];
        for (Map.Entry<String, Integer> entry : indexs.entrySet()) {
            nodes[entry.getValue()] = node(entry.getKey());
        }
        List<Node> nodeList = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            List<Link> links = new ArrayList<>();
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 1) {
                    links.add(to(nodes[j]));
                } else if (map[i][j] == 2) {
                    links.add(to(nodes[j]).with(Color.RED));
                }
            }
            nodeList.add(nodes[i].link(links));
        }

        Graph g = graph("example").directed().graphAttr().with(Rank.dir(LEFT_TO_RIGHT)).with(nodeList);
        try {
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(Conf.Dir + "graph" + File.separator + projName + File.separator + classify + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String projPath = "D:\\githubProject\\blueocean-plugin\\";
        PomParser pomParser = new PomParser(projPath);
        pomParser.parseProject();
        ModuleRelation.i().generateGraph();
        int[][] modules = ModuleRelation.i().getModules();
        int[][] inheritance = ModuleRelation.i().getInheritance();
        Map<String, Integer> indexs = ModuleRelation.i().getIndexs();
    }
}
