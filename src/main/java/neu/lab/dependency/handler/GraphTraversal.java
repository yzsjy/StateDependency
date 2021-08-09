package neu.lab.dependency.handler;

import neu.lab.dependency.vo.ListGraph;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 图的广度优先搜索和深度优先搜索
 * @author SUNJUNYAN
 */
public class GraphTraversal {
    private ListGraph graph;
    /**
     * 被访问过的节点
     */
    private boolean[] visited;

    public GraphTraversal(ListGraph graph) {
        this.graph = graph;
        visited = new boolean[graph.getGraphs().size()];
    }

    public void DFS() {
        for (int i = 0; i < graph.getGraphs().size(); i++) {
            if (!visited[i]) {
                DFSTraversal(i);
            }
        }
    }

    public void BFS() {
        for (int i = 0; i < graph.getGraphs().size(); i++) {
            if (!visited[i]) {
                BFSTraversal(i);
            }
        }
    }

    public void DFSTraversal(int v) {
        if (visited[v]) {
            return;
        }
        visited[v] = true;
        System.out.print(v + " -> ");
        Iterator<Integer> near = graph.getGraphs().get(v).listIterator();
        while (near.hasNext()) {
            int next = near.next();
            if (!visited[next]) {
                DFSTraversal(next);
            }
        }
    }

    public void BFSTraversal(int v) {
        Deque<Integer> queue = new LinkedList<>();
        visited[v] = true;
        queue.offerFirst(v);
        while (!queue.isEmpty()) {
            Integer cur = queue.pollFirst();
            System.out.print(cur + " -> ");
            Iterator<Integer> near = graph.getGraphs().get(cur).listIterator();
            while (near.hasNext()) {
                int next = near.next();
                if (!visited[next]) {
                    visited[next] = true;
                    queue.offerLast(next);
                }
            }
        }
    }
}
