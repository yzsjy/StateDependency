package neu.lab.dependency.pom;

import neu.lab.dependency.container.Poms;
import neu.lab.dependency.graph.GenerateGraphviz;
import neu.lab.dependency.graph.ModuleGraph;
import neu.lab.dependency.soot.SootRiskCg;
import neu.lab.dependency.util.PomOperation;
import neu.lab.dependency.vo.Pom;

import java.io.File;
import java.util.*;

public class DetectUselessDep {

    private volatile static DetectUselessDep instance;

    private int[][] modules;
    private int[][] temp;
    private Map<String, Integer> sigToIndex;
    private Map<Integer, String> indexToSig;
    private Map<Pom, Integer> pomToIndex;
    private Set<Integer> visit;
    private List<List<Integer>> reduceEdges;
    private List<List<Integer>> canReduce;
    private List<List<Integer>> notReduce;

    private DetectUselessDep() {

    }

    public static DetectUselessDep i() {
        if (instance == null) {
            synchronized (DetectUselessDep.class) {
                if (instance == null) {
                    instance = new DetectUselessDep();
                }
            }
        }
        return instance;
    }

    public void init() {
        modules = ModuleGraph.i().getModules();
        sigToIndex = ModuleGraph.i().getSigToIndex();
        pomToIndex = ModuleGraph.i().getPomToIndex();
        indexToSig = ModuleGraph.i().getIndexToSig();
        visit = new HashSet<>();
        reduceEdges = new ArrayList<>();
        canReduce = new ArrayList<>();
        notReduce = new ArrayList<>();
        temp = copyArray(modules);
    }

    public void reduceDep() {
        init();
        canReduce();
    }

    public int[][] copyArray(int[][] array) {
        int length = array.length;
        int[][] clone = new int[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                clone[i][j] = array[i][j];
            }
        }
        return clone;
    }

    public void canReduce() {
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp.length; j++) {
                if (temp[i][j] == 1) {
                    Pom startPom = Poms.i().getPomBySig(indexToSig.get(i));
                    Pom endPom = Poms.i().getPomBySig(indexToSig.get(j));
                    String startPath = startPom.getFilePath();
                    String endPath = endPom.getFilePath();
                    startPath = startPath.substring(0, startPath.length() - 7) + "target" + File.separator + "classes";
                    endPath = endPath.substring(0, endPath.length() - 7) + "target" + File.separator + "classes";
                    Set<String> mthds = SootRiskCg.i().cmpCg(startPath, endPath);
                    if (mthds.isEmpty()) {
                        temp[i][j] = 3;
                        List<Integer> pair = new ArrayList<>();
                        pair.add(i);
                        pair.add(j);
                        reduceEdges.add(pair);
                    }
                }
            }
        }
    }

    public void relationReduce() {

        for (int i = 0; i < temp.length; i++) {
            Pom startModule = Poms.i().getPomBySig(indexToSig.get(i));
            List<String> removes = new ArrayList<>();
            Map<String, Integer> tmpIndex = new HashMap<>();
            for (int j = 0; j < temp.length; j++) {
                if (temp[i][j] == 3) {
                    Pom endModule = Poms.i().getPomBySig(indexToSig.get(j));
                    String groupId = endModule.getGroupId();
                    String artifactId = endModule.getArtifactId();
                    removes.add(groupId + ":" + artifactId);
                    tmpIndex.put(groupId + ":" + artifactId, j);
                }
            }
            if (removes.size() == 0) {
                continue;
            }
            List<String> canReduces = PomOperation.i().removeDependency(startModule, removes);
            for (String c : canReduces) {
                temp[i][tmpIndex.get(c)] = 2;
                List<Integer> edge = new ArrayList<>();
                edge.add(i);
                edge.add(tmpIndex.get(c));
                canReduce.add(edge);
            }
        }
    }

    public List<List<Integer>> getReduceEdges() {
        return reduceEdges;
    }

    public void generateGraph(String projName) {
        GenerateGraphviz.i().reduceGraph(temp, sigToIndex, pomToIndex, projName, "uselessModule");
    }

}
