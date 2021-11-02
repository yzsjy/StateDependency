package neu.lab.dependency.graph;

import org.jgrapht.Graph;
import org.jgrapht.alg.TransitiveReduction;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.Set;

public class TransitiveReduce {

    private int[][] map;

    public TransitiveReduce(int[][] map) {
        this.map = map;
    }

    public Graph<Integer, DefaultEdge> createGraph() {
        Graph<Integer, DefaultEdge> g = new SimpleDirectedGraph<>(DefaultEdge.class);
        for (int i = 0; i < map.length; i++) {
            g.addVertex(i);
        }
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 1) {
                    g.addEdge(i, j);
                }
            }
        }
        return g;
    }

    public void reduce(Graph<Integer, DefaultEdge> g) {
        TransitiveReduction.INSTANCE.reduce(g);
    }

    public int[][] revertMap(Graph<Integer, DefaultEdge> g) {
        Set<Integer> vertexes = g.vertexSet();
        Set<DefaultEdge> edges = g.edgeSet();
        int[][] res = new int[vertexes.size()][vertexes.size()];
        for (DefaultEdge edge : edges) {
            String s = edge.toString();
            s = s.substring(1, s.length() - 1);
            int source = Integer.parseInt(s.split(" : ")[0]);
            int target = Integer.parseInt(s.split(" : ")[1]);
            res[source][target] = 1;
        }
        return res;
    }

    public int[][] getRes() {
        Graph<Integer, DefaultEdge> g = createGraph();
        reduce(g);
        return revertMap(g);
    }

    public static void main(String[] args) {

    }
}
