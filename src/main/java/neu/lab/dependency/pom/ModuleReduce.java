package neu.lab.dependency.pom;

import neu.lab.dependency.container.Poms;
import neu.lab.dependency.soot.SootRiskCg;
import neu.lab.dependency.vo.Pom;

import java.io.File;
import java.util.*;

public class ModuleReduce {

    private static ModuleReduce instance;

    private int[][] modules;
    private int[][] temp;
    private Map<String, Integer> indexs;
    private Map<Integer, String> revertIndexs;
    private Set<Integer> visit;
    private List<List<Integer>> reduceEdge;

    private ModuleReduce() {

    }

    public static ModuleReduce i() {
        if (instance == null) {
            instance = new ModuleReduce();
        }
        return instance;
    }

    public void init() {
        modules = ModuleRelation.i().getModules();
        indexs = ModuleRelation.i().getIndexs();
        revertIndexs = ModuleRelation.i().revertIndexs();
        visit = new HashSet<>();
        reduceEdge = new ArrayList<>();
        temp = copyArray(modules);
    }

    public void reduceDep() {
        init();
//        int reduceIndex = findIndex(temp);
//        while (reduceIndex != -1) {
//            reduceDepModule(temp, reduceIndex);
//            reduceIndex = findIndex(temp);
//        }
        int len = temp.length;
        for (int i = 0; i < len; i++) {
            reduceDepModule(temp, i);
        }
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
                reduceEdge.add(pair);
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
                find.add(t);
                array.add(t);
                for (int j = 0; j < temp.length; j++) {
                    if (temp[t][j] == 1) {
                        if (!find.contains(j)) {
                            queue.offer(j);
                        }
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
        PomParser pomParser = new PomParser();
        pomParser.generateGraph(temp, indexs, "newDependencies", projName);
    }

    public void canReduce() {
        List<String> canReduce = new ArrayList<>();
        List<String> notReduce = new ArrayList<>();
        for (List<Integer> list : reduceEdge) {
            int start = list.get(0);
            int end = list.get(1);
            Pom startPom = Poms.i().getPom(revertIndexs.get(start));
            Pom endPom = Poms.i().getPom(revertIndexs.get(end));
            String startPath = startPom.getFilePath();
            String endPath = endPom.getFilePath();
            startPath = startPath.substring(0, startPath.length() - 7) + "target" + File.separator + "classes";
            endPath = endPath.substring(0, endPath.length() - 7) + "target" + File.separator + "classes";
            Set<String> mthds = SootRiskCg.i().cmpCg(startPath, endPath);
            if (mthds.isEmpty()) {
                canReduce.add(revertIndexs.get(start) + " to " + revertIndexs.get(end) + " can reduce");
            } else {
                notReduce.add(revertIndexs.get(start) + " to " + revertIndexs.get(end) + " can not reduce");
            }
        }
        for (String can : canReduce) {
            System.out.println(can);
        }

        for (String not : notReduce) {
            System.out.println(not);
        }
    }

    public List<List<Integer>> getReduceEdge() {
        return reduceEdge;
    }
}
