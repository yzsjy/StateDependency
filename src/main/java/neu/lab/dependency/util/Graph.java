package neu.lab.dependency.util;

/**
 * @author SUNJUNYAN
 */
public class Graph {
    private String[] vertexs;
    private int numberOfVertex;
    private int numberOfEdges;
    private int[][] edges;

    public Graph(String[] vertexs) {
        numberOfVertex = vertexs.length;
        this.vertexs = vertexs;

    }
}
