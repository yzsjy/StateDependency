package neu.lab.dependency.util;

/**
 * @author SUNJUNYAN
 */
public class Graph {
    private String[] vertexes;
    private int numberOfVertex;
    private int numberOfEdges;
    private int[][] edges;

    public Graph(String[] vertexes) {
        numberOfVertex = vertexes.length;
        this.vertexes = vertexes;

    }
}
