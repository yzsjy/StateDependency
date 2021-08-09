package neu.lab.dependency.vo;

import java.util.ArrayList;

/**
 * 定义图的数据结构
 * @author SUNJUNYAN
 */
public class ListGraph {
    private ArrayList<ArrayList<Integer>> graphs;

    public ListGraph(int v) {
        graphs = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            graphs.add(new ArrayList<>());
        }
    }

    public ArrayList<ArrayList<Integer>> getGraphs() {
        return graphs;
    }

    public void addEdge(int start, int end) {
        graphs.get(start).add(end);
    }

    public void removeEdge(int start, int end) {
        graphs.get(start).remove((Integer) end);
    }
}
