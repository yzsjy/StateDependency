package neu.lab.dependency.graph;

import guru.nidi.graphviz.attribute.Arrow;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.Node;
import neu.lab.dependency.util.Conf;
import neu.lab.dependency.vo.Pom;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.*;

public class GenerateGraphviz {

    private volatile static GenerateGraphviz instance;

    private GenerateGraphviz() {

    }

    public static GenerateGraphviz i() {
        if (instance == null) {
            synchronized (GenerateGraphviz.class) {
                if (instance == null) {
                    instance = new GenerateGraphviz();
                }
            }
        }
        return instance;
    }

    /**
     * 模块继承关系图生成
     * @param map 继承关系图矩阵
     * @param sigToIndex 模块名与坐标名的对应
     * @param conflicts 出现不一致依赖的模块
     * @param projName 项目名称
     */
    public void inheritGraph(int[][] map, Map<String, Integer> sigToIndex, Set<String> conflicts, String projName) {
        int size = sigToIndex.size();
        Node[] nodes = new Node[size];
        for (Map.Entry<String, Integer> entry : sigToIndex.entrySet()) {
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

    /**
     * 模块依赖关系图生成
     * @param map 模块依赖关系图矩阵
     * @param sigToIndex 模块名与坐标名的对应
     * @param conflicts 出现不一致依赖的模块
     * @param projName 项目名称
     * @param fileName 生成的文件名
     */
    public void moduleGraph(int[][] map, Map<String, Integer> sigToIndex, Set<String> conflicts, String projName, String fileName) {
        int size = sigToIndex.size();
        Node[] nodes = new Node[size];
        for (Map.Entry<String, Integer> entry : sigToIndex.entrySet()) {
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
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(Conf.Dir + "graph" + File.separator + projName + File.separator + fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模块依赖关系图生成
     * @param map 模块依赖关系图矩阵
     * @param sigToIndex 模块名与坐标名的对应
     * @param pomToIndex 坐标名和模块别名的对应
     * @param projName 项目名称
     * @param fileName 生成的文件名
     */
    public void reduceGraph(int[][] map, Map<String, Integer> sigToIndex, Map<Pom, Integer> pomToIndex, String projName, String fileName) {
        int size = sigToIndex.size();
        Node[] nodes = new Node[size];
        for (Map.Entry<Pom, Integer> entry : pomToIndex.entrySet()) {
            if (entry.getKey().getName() != null) {
                nodes[entry.getValue()] = node(entry.getKey().getArtifactId());
            } else {
                nodes[entry.getValue()] = node(entry.getKey().getArtifactId());
            }

        }
        List<Node> nodeList = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            List<Link> links = new ArrayList<>();
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 1) {
                    links.add(to(nodes[j]));
                } else if (map[i][j] == 2) {
                    links.add(to(nodes[j]).with(Color.RED).with(Style.DOTTED));
                }
                else if (map[i][j] == 3) {
                    links.add(to(nodes[j]).with(Color.BLUE).with(Style.DOTTED));
                }
            }
            nodeList.add(nodes[i].link(links));
        }

        Graph g = graph("example").directed().graphAttr().with(Rank.dir(LEFT_TO_RIGHT)).with(nodeList);
        try {
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(Conf.Dir + "graph" + File.separator + projName + File.separator + fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moduleGraph(int[][] map, List<MavenProject> projects, String projName, String fileName) {
        int size = projects.size();
        Node[] nodes = new Node[size];
        for (int i = 0; i < size; i++) {
            MavenProject project = projects.get(i);
            if (project.getName() == null) {
                nodes[i] = node(project.getGroupId() + ":" + project.getArtifactId() + ":" + project.getVersion());
            } else {
                nodes[i] = node(project.getName());
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
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(Conf.Dir + "graph" + File.separator + projName + File.separator + fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
