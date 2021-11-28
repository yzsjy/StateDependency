package neu.lab.dependency.pom;

import neu.lab.dependency.container.Poms;
import neu.lab.dependency.graph.TopologicalSorting;
import neu.lab.dependency.vo.Pom;

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
        indexes.putAll(ModuleRelation.i().getSigToIndex());
        levelSort = new ArrayList<>();
        levelSort.addAll(TopologicalSorting.i().getLevelSort(matrix));
        moduleSort = new ArrayList<>();
        getModuleSort();
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

    public void getModuleSort() {
        Set<Pom> poms = Poms.i().getPoms();
        Pom parent = null;
        String parentPath = projPath + "pom.xml";
        for (Pom pom : poms) {
            if (pom.getModulePath().equals(parentPath)) {
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
        for (List<Integer> levelSort : levelSort) {
            int[] num = new int[levelSort.size()];
            for (int i = 0; i < levelSort.size(); i++) {
                num[i] = moduleSort.indexOf(levelSort.get(i));
            }
            Arrays.sort(num);
            for (int i = 0; i < num.length; i++) {
                buildList.add(moduleSort.get(num[i]));
            }
        }
    }

    public List<Integer> getBuildList() {
        return buildList;
    }

}
