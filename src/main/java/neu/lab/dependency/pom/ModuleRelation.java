package neu.lab.dependency.pom;

import neu.lab.dependency.container.Poms;
import neu.lab.dependency.vo.Pom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class ModuleRelation {

    private int[][] modules;
    private int[][] inheritance;
    private Map<String, Integer> sigToIndex;
    private Map<Pom, Integer> pomToIndex;
    private Map<Integer, String> indexToSig;
    private Map<Integer, Pom> indexToPom;

    private volatile static ModuleRelation instance;

    public static ModuleRelation i() {
        if (instance == null) {
            synchronized (ModuleRelation.class) {
                instance = new ModuleRelation();
            }
        }
        return instance;
    }

    public int[][] getModules() {
        return modules;
    }

    public Map<String, Integer> getSigToIndex() {
        return sigToIndex;
    }

    public int[][] getInheritance() {
        return inheritance;
    }

    public Map<Pom, Integer> getPomToIndex() {
        return pomToIndex;
    }

    public Map<Integer, String> getIndexToSig() {
        return indexToSig;
    }

    public Map<Integer, Pom> getIndexToPom() {
        return indexToPom;
    }

    public static ModuleRelation getInstance() {
        return instance;
    }

    public void generateGraph() {
        Set<Pom> poms = Poms.i().getPoms();
        int size = poms.size();
        modules = new int[size][size];
        inheritance = new int[size][size];
        sigToIndex = new HashMap<>(size);
        pomToIndex = new HashMap<>(size);
        indexToSig = new HashMap<>(size);
        indexToPom = new HashMap<>(size);
        int i = 0;
        for (Pom pom : poms) {
            sigToIndex.put(pom.getSig(), i);
            pomToIndex.put(pom, i);
            indexToSig.put(i, pom.getSig());
            indexToPom.put(i, pom);
            i++;
        }
        for (Pom pom : poms) {
            int m = sigToIndex.get(pom.getSig());
            List<String> dependencies = pom.getDependencies();
            for (String dep : dependencies) {
                if (sigToIndex.containsKey(dep)) {
                    int n = sigToIndex.get(dep);
                    modules[m][n] = 1;
                }
            }
            if (pom.getParent() != null) {
                int par = sigToIndex.get(pom.getParent().getSig());
                inheritance[m][par] = 1;
            }
        }
    }
}
