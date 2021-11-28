package neu.lab.dependency.pom;

import neu.lab.dependency.container.Poms;
import neu.lab.dependency.graph.TopologicalSorting;
import neu.lab.dependency.vo.Pom;
import org.apache.maven.model.Model;

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
    }

    public void init() {
        copyMatrix();
        indexes = new HashMap<>();
        indexes.putAll(ModuleRelation.i().getIndexes());
        levelSort = new ArrayList<>();
        levelSort.addAll(TopologicalSorting.i().getLevelSort(matrix));
        moduleSort = new ArrayList<>();
    }

    public void copyMatrix() {
        int[][] graph = ModuleRelation.i().getModules();
        int len = graph.length;
        matrix = new int[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                matrix[i][j] = graph[i][j];
            }
        }
    }

    public List<Integer> getModuleSort() {
        List<Integer> res = new ArrayList<>();
        Set<Pom> poms = Poms.i().getPoms();
        Pom parent = null;
        String parentPath = projPath + "pom.xml";
        for (Pom pom : poms) {
            if (pom.getModulePath().equals(parentPath)) {
                parent = pom;
                break;
            }
        }
        res.add(indexes.get(parent.getSig()));
        Queue<String> queue = new LinkedList<>();
        Model parentModel = parent.getModel();
        List modules = parentModel.getModules();
        for (Object s : modules) {
            queue.offer((String) s);
        }
        return res;
    }

    public int getNumber(Set<Pom> poms, String artifactId) {
        int num = 0;
        for (Pom pom : poms) {
            if (pom.getArtifactId().equals(artifactId)) {
                num = indexes.get(pom.getSig());
                break;
            }
        }
        return num;
    }
}
