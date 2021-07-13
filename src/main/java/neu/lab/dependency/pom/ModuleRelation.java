package neu.lab.dependency.pom;

import com.google.javascript.jscomp.graph.DiGraph;
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
    private Map<String, Integer> indexs;

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

    public void setModules(int[][] modules) {
        this.modules = modules;
    }

    public Map<String, Integer> getIndexs() {
        return indexs;
    }

    public void setIndexs(Map<String, Integer> indexs) {
        this.indexs = indexs;
    }

    public int[][] getInheritance() {
        return inheritance;
    }

    public void setInheritance(int[][] inheritance) {
        this.inheritance = inheritance;
    }

    public Map<Integer, String> revertIndexs() {
        Map<Integer, String> revertIndexs = new HashMap<>(indexs.size());
        for (Map.Entry<String, Integer> entry : indexs.entrySet()) {
            revertIndexs.put(entry.getValue(), entry.getKey());
        }
        return revertIndexs;
    }

    public void generateGraph() {
        Set<Pom> poms = Poms.i().getPoms();
        int size = poms.size();
        modules = new int[size][size];
        inheritance = new int[size][size];
        indexs = new HashMap<>(size);
        int i = 0;
        for (Pom pom : poms) {
            indexs.put(pom.getSig(), i);
            i++;
        }
        for (Pom pom : poms) {
            int m = indexs.get(pom.getSig());
            List<String> dependencies = pom.getDependencies();
            for (String dep : dependencies) {
                if (indexs.containsKey(dep)) {
                    int n = indexs.get(dep);
                    modules[m][n] = 1;
                }
            }
            if (pom.getParent() != null) {
                int par = indexs.get(pom.getParent().getSig());
                inheritance[m][par] = 1;
            }
        }
    }
}
