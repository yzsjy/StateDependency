package neu.lab.dependency.pom;

import neu.lab.dependency.container.Poms;
import neu.lab.dependency.graph.GenerateGraphviz;
import neu.lab.dependency.graph.TransitiveReduce;
import neu.lab.dependency.soot.SootRiskCg;
import neu.lab.dependency.util.PomOperation;
import neu.lab.dependency.vo.Pom;

import java.io.File;
import java.util.*;

public class ModuleReduce {

    private volatile static ModuleReduce instance;

    private int[][] temp;
    private Map<String, Integer> sigToIndex;
    private Map<Integer, String> indexToSig;
    private Map<Pom, Integer> pomToIndex;
    private Set<Integer> visit;
    private List<List<Integer>> reduceEdges;
    private List<List<Integer>> canReduce;
    private List<List<Integer>> notReduce;

    private ModuleReduce() {

    }

    public static ModuleReduce i() {
        if (instance == null) {
            synchronized (ModuleReduce.class) {
                synchronized (ModuleReduce.class) {
                    instance = new ModuleReduce();
                }
            }
        }
        return instance;
    }

    public void init() {
        int[][] modules = ModuleRelation.i().getModules();
        sigToIndex = ModuleRelation.i().getSigToIndex();
        pomToIndex = ModuleRelation.i().getPomToIndex();
        indexToSig = ModuleRelation.i().getIndexToSig();
        visit = new HashSet<>();
        reduceEdges = new ArrayList<>();
        canReduce = new ArrayList<>();
        notReduce = new ArrayList<>();
        temp = copyArray(modules);
    }

    public void reduceDep() {
        init();
//        int reduceIndex = findIndex(temp);
//        while (reduceIndex != -1) {
//            reduceDepModule(temp, reduceIndex);
//            reduceIndex = findIndex(temp);
//        }

//        int len = temp.length;
//        for (int i = 0; i < len; i++) {
//            reduceDepModule(temp, i);
//        }

        TransitiveReduce transitiveReduce = new TransitiveReduce(temp);
        int[][] res = transitiveReduce.getRes();
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res.length; j++) {
                if (res[i][j] == 0 && temp[i][j] == 1) {
                    temp[i][j] = 2;
                    List<Integer> pair = new ArrayList<>();
                    pair.add(i);
                    pair.add(j);
                    reduceEdges.add(pair);
                }
            }
        }

//        canReduce();
    }

    public void reduceDepModule(int[][] temp, int index) {
        List<List<Integer>> reachNodes = getReachNodes(temp, index);
        Set<Integer> directNodes = new HashSet<>();
        Set<Integer> indirectNodes = new HashSet<>();
        for (int i = 0; i < reachNodes.size(); i++) {
            if (i == 0) {
                continue;
            } else if (i == 1) {
                directNodes.addAll(reachNodes.get(i));
            } else {
                indirectNodes.addAll(reachNodes.get(i));
            }
        }
        for (int directNode : directNodes) {
            if (indirectNodes.contains(directNode)) {
                temp[index][directNode] = 2;
                List<Integer> pair = new ArrayList<>();
                pair.add(index);
                pair.add(directNode);
                reduceEdges.add(pair);
            }
        }
    }

    public List<List<Integer>> getReachNodes(int[][] temp, int index) {
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(index);
        List<List<Integer>> level = new ArrayList<>();
        Set<Integer> find = new HashSet<>();
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> array = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                int t = queue.poll();
                if (find.contains(t)) {
                    continue;
                }
                find.add(t);
                array.add(t);
                for (int j = 0; j < temp.length; j++) {
                    if (temp[t][j] == 1 && !find.contains(j)) {
                        queue.offer(j);
                    }
                }
            }
            level.add(array);
        }
        return level;
    }

    public int findIndex(int[][] temp) {
        for (int i = 0; i < temp.length; i++) {
            boolean isTrue = true;
            for (int j = 0; j < temp.length; j++) {
                if (temp[j][i] == 1) {
                    isTrue = false;
                }
            }
            if (!isTrue) {
                continue;
            }
            if (!visit.contains(i)) {
                visit.add(i);
                return i;
            }
        }
        return -1;
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

    public void generateGraph(String projName) {
        GenerateGraphviz.i().reduceGraph(temp, sigToIndex, pomToIndex, projName, "reduceModule");
    }

    public void canReduce() {
        for (List<Integer> list : reduceEdges) {
            int start = list.get(0);
            int end = list.get(1);
            Pom startPom = Poms.i().getPomBySig(indexToSig.get(start));
            Pom endPom = Poms.i().getPomBySig(indexToSig.get(end));
            String startPath = startPom.getFilePath();
            String endPath = endPom.getFilePath();
            startPath = startPath.substring(0, startPath.length() - 7) + "target" + File.separator + "classes";
            endPath = endPath.substring(0, endPath.length() - 7) + "target" + File.separator + "classes";
            Set<String> methods = SootRiskCg.i().cmpCg(startPath, endPath);
            if (methods.isEmpty()) {
                canReduce.add(list);
            } else {
                notReduce.add(list);
            }
        }

        for (List<Integer> can : canReduce) {
            temp[can.get(0)][can.get(1)] = 3;
        }
    }

    public void relationReduce() {

        for (int i = 0; i < temp.length; i++) {
            Pom startModule = Poms.i().getPomBySig(indexToSig.get(i));
            List<String> removes = new ArrayList<>();
            Map<String, Integer> tmpIndex = new HashMap<>();
            for (int j = 0; j < temp.length; j++) {
                if (temp[i][j] == 2) {
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
                temp[i][tmpIndex.get(c)] = 3;
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

    public List<List<Integer>> getCanReduce() {
        return canReduce;
    }

    public List<List<Integer>> getNotReduce() {
        return notReduce;
    }
}
