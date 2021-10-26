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
    private Map<String, Integer> indexes;
    private Map<Pom, Integer> pomIndexes;

    private static ModuleRelation instance;

    public static ModuleRelation i() {
        if (instance == null) {
            instance = new ModuleRelation();
        }
        return instance;
    }

    public int[][] getModules() {
        return modules;
    }

    public Map<String, Integer> getIndexes() {
        return indexes;
    }

    public int[][] getInheritance() {
        return inheritance;
    }

    public Map<Pom, Integer> getPomIndexes() {
        return pomIndexes;
    }

    public Map<Integer, String> revertIndexes() {
        Map<Integer, String> revertIndexes = new HashMap<>(indexes.size());
        for (Map.Entry<String, Integer> entry : indexes.entrySet()) {
            revertIndexes.put(entry.getValue(), entry.getKey());
        }
        return revertIndexes;
    }

    public void generateGraph() {
        Set<Pom> poms = Poms.i().getPoms();
        int size = poms.size();
        modules = new int[size][size];
        inheritance = new int[size][size];
        indexes = new HashMap<>(size);
        pomIndexes = new HashMap<>(size);
        int i = 0;
        for (Pom pom : poms) {
            indexes.put(pom.getSig(), i);
            pomIndexes.put(pom, i);
            i++;
        }
        for (Pom pom : poms) {
            int m = indexes.get(pom.getSig());
            List<String> dependencies = pom.getDependencies();
            for (String dep : dependencies) {
                if (indexes.containsKey(dep)) {
                    int n = indexes.get(dep);
                    modules[m][n] = 1;
                }
            }
            if (pom.getParent() != null) {
                int par = indexes.get(pom.getParent().getSig());
                inheritance[m][par] = 1;
            }
        }
    }
}
