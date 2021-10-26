package neu.lab.dependency.pom;

import neu.lab.dependency.container.Poms;
import neu.lab.dependency.soot.SootRiskCg;
import neu.lab.dependency.vo.Pom;

import java.io.File;
import java.util.*;

public class DetectUselessDep {

    private static DetectUselessDep instance;

    private int[][] modules;
    private int[][] temp;
    private Map<String, Integer> indexs;
    private Map<Integer, String> revertIndexs;
    private Map<Pom, Integer> pomIndexs;
    private Set<Integer> visit;
    private List<List<Integer>> reduceEdge;
    private List<List<Integer>> canReduce;
    private List<List<Integer>> notReduce;

    private DetectUselessDep() {

    }

    public static DetectUselessDep i() {
        if (instance == null) {
            instance = new DetectUselessDep();
        }
        return instance;
    }

    public void init() {
        modules = ModuleRelation.i().getModules();
        indexs = ModuleRelation.i().getIndexes();
        pomIndexs = ModuleRelation.i().getPomIndexes();
        revertIndexs = ModuleRelation.i().revertIndexes();
        visit = new HashSet<>();
        reduceEdge = new ArrayList<>();
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
                    Pom startPom = Poms.i().getPom(revertIndexs.get(i));
                    Pom endPom = Poms.i().getPom(revertIndexs.get(j));
                    String startPath = startPom.getFilePath();
                    String endPath = endPom.getFilePath();
                    startPath = startPath.substring(0, startPath.length() - 7) + "target" + File.separator + "classes";
                    endPath = endPath.substring(0, endPath.length() - 7) + "target" + File.separator + "classes";
                    Set<String> mthds = SootRiskCg.i().cmpCg(startPath, endPath);
                    if (mthds.isEmpty()) {
                        temp[i][j] = 3;
                    }
                }
            }
        }
    }

    public void generateGraph(String projName) {
        PomParser.i().generateGraph(temp, indexs, pomIndexs, "newDependencies1", projName);
    }

}
