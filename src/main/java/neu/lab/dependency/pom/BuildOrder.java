package neu.lab.dependency.pom;

import neu.lab.dependency.container.Poms;
import neu.lab.dependency.graph.ModuleGraph;
import neu.lab.dependency.graph.TopologicalSorting;
import neu.lab.dependency.vo.Pom;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.codehaus.plexus.util.dag.DAG;
import org.codehaus.plexus.util.dag.TopologicalSorter;
import org.codehaus.plexus.util.dag.Vertex;

import java.util.*;

public class BuildOrder {

    private String projPath;
    private List<List<Integer>> levelSort;
    private List<Integer> moduleSort;
    private List<Integer> buildList;
    private int[][] matrix;
    private Map<String, Integer> indexes;

    public BuildOrder(String projPath) {
        this.projPath = projPath;
        init();
    }

    public void init() {
        copyMatrix();
        indexes = new HashMap<>();
        indexes.putAll(ModuleGraph.i().getSigToIndex());
        levelSort = new ArrayList<>();
        levelSort.addAll(TopologicalSorting.i().getLevelSort(matrix));
        moduleSort = new ArrayList<>();
        getModuleSort();
        buildList = new ArrayList<>();
    }

    public void copyMatrix() {
        int[][] graph = ModuleGraph.i().getModules();
        int len = graph.length;
        matrix = new int[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                matrix[i][j] = graph[i][j];
            }
        }
    }

    public void getModuleSort() {
        Set<Pom> poms = Poms.i().getPoms();
        Pom parent = null;
        String parentPath = projPath + "pom.xml";
        for (Pom pom : poms) {
            if (pom.getFilePath().equals(parentPath)) {
                parent = pom;
                break;
            }
        }
        DFSModuleSort(parent);
    }

    public void DFSModuleSort(Pom pom) {
        moduleSort.add(indexes.get(pom.getSig()));
        List modules = pom.getModel().getModules();
        if (modules != null && !modules.isEmpty()) {
            for (Object l : modules) {
                String artifactId = (String) l;
                Pom child = Poms.i().getPomByArtifactId(artifactId);
                DFSModuleSort(child);
            }
        }
    }

    public void calculateBuildList() {
        for (List<Integer> lSort : levelSort) {
            int[] num = new int[lSort.size()];
            for (int i = 0; i < lSort.size(); i++) {
                if (!moduleSort.contains(lSort.get(i))) {
                    num[i] = -1;
                    continue;
                }
                num[i] = moduleSort.indexOf(lSort.get(i));
            }
            Arrays.sort(num);
            for (int i = 0; i < num.length; i++) {
                if (num[i] == -1) {
                    continue;
                }
                buildList.add(moduleSort.get(num[i]));
            }
        }
    }

    public List<Integer> getBuildList() {
        return buildList;
    }

    public void build() {
        DAG dag = new DAG();
        Map<Integer, Pom> indexToPom = ModuleGraph.i().getIndexToPom();
        for (int i = 0; i < matrix.length; i++) {
            if (moduleSort.contains(i)) {
                dag.addVertex(Integer.toString(i));
            }
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (!moduleSort.contains(i) || !moduleSort.contains(j)) {
                    continue;
                }
                if (matrix[i][j] == 1) {
                    Vertex start = dag.getVertex(Integer.toString(i));
                    Vertex end = dag.getVertex(Integer.toString(j));
                    try {
                        dag.addEdge(start, end);
                    } catch (CycleDetectedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        List<String> sort = TopologicalSorter.sort(dag);
        for (String s : sort) {
            int index = Integer.parseInt(s);
            Pom pom = indexToPom.get(index);
            if (pom.getName() != null) {
                System.out.println(pom.getName());
            } else {
                System.out.println(pom.getArtifactId());
            }
        }

    }

    public void print() {
        Map<Integer, Pom> map = ModuleGraph.i().getIndexToPom();
        for (int m : moduleSort) {
            Pom pom = map.get(m);
            if (pom.getName() != null) {
                System.out.println(pom.getName());
            } else {
                System.out.println(pom.getArtifactId());
            }
        }
    }

}
