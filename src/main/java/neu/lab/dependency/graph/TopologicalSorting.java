package neu.lab.dependency.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * 逆拓扑排序
 *
 */
public class TopologicalSorting {

    private static TopologicalSorting instance;

    private TopologicalSorting() {

    }

    public static TopologicalSorting i() {
        if (instance == null) {
            synchronized (TopologicalSorting.class) {
                if (instance == null) {
                    instance = new TopologicalSorting();
                }
            }
        }
        return instance;
    }

    public int[] getSourceSort(int[][] matrix) {
        int[] source = getSource(matrix);
        return getSourceSort(matrix, source);
    }

    public List<List<Integer>> getLevelSort(int[][] matrix) {
        int[] source = getSource(matrix);
        return getLevelSort(matrix, source);
    }

    /**
     * 返回有向图的逆拓扑排序
     * @param matrix 图的邻接矩阵表示
     * @param source 每个节点的出度值
     * @return 逆拓扑排序的顺序
     */
    public int[] getSourceSort(int[][] matrix, int[] source) {
        int size = source.length;
        int[] res = new int[size];
        int count = 0;
        boolean judge = true;
        while (judge) {
            for (int i = 0; i < size; i++) {
                if (source[i] == 0) {
                    res[count++] = i;
                    source[i] = -1;
                    for (int j = 0; j < size; j++) {
                        if (matrix[j][i] == 1) {
                            source[j] -= 1;
                        }
                    }
                }
            }
            if (count == size) {
                judge = false;
            }
        }
        return res;
    }

    public List<List<Integer>> getLevelSort(int[][] matrix, int[] source) {
        int size = source.length;
        List<List<Integer>> res = new ArrayList<>();
        int count = 0;
        boolean judge = true;
        while (judge) {
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                if (source[i] == 0) {
                    count++;
                    level.add(i);
                    source[i] = -1;
                }
            }
            for (int j = 0; j < size; j++) {
                for (int i : level) {
                    if (matrix[j][i] == 1) {
                        source[j] -= 1;
                    }
                }
            }
            res.add(level);
            if (count == size) {
                judge = false;
            }
        }
        return res;
    }

    /**
     * 返回图的每个节点的出度值
     * @param matrix 图的邻接矩阵表示
     * @return 每个节点的出度值
     */
    public int[] getSource(int[][] matrix) {
        int size = matrix.length;
        int[] source = new int[size];
        for (int i = 0; i < size; i++) {
            int count = 0;
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] == 1) {
                    count++;
                }
            }
            source[i] = count;
        }
        return source;
    }
}
