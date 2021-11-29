package neu.lab.dependency.container;

import neu.lab.dependency.graph.GenerateGraphviz;
import neu.lab.dependency.pom.ModuleRelation;
import neu.lab.dependency.vo.Conflict;
import neu.lab.dependency.vo.Pom;

import java.io.File;
import java.util.*;

/**
 * @author SUNJUNYAN
 */
public class Conflicts {

    private List<Conflict> container;
    private Set<String> leaves = new HashSet<>();
    private int[][] modules;
    private int[][] inheritance;
    private Map<String, Integer> sigToIndex;
    private Map<Integer, String> indexToSig;

    private volatile static Conflicts instance;

    public static void init() {
        instance = new Conflicts();
    }

    public static Conflicts i() {
        if (instance == null) {
            synchronized (Conflicts.class) {
                if (instance == null) {
                    instance = new Conflicts();
                }
            }
        }
        return instance;
    }

    private Conflicts() {
        container = new ArrayList<>();
        modules = ModuleRelation.i().getModules();
        inheritance = ModuleRelation.i().getInheritance();
        sigToIndex = ModuleRelation.i().getSigToIndex();
        indexToSig = ModuleRelation.i().getIndexToSig();
        detectConflicts();
    }

    public void detectConflicts() {
        List<Pom> leafPoms = getLeafPom();
        Map<String, Conflict> conflictMap = new HashMap<>();
        for (Pom pom : leafPoms) {
            List<String> dependencies = pom.getDependencies();
            for (String dependency : dependencies) {
                String groupId = dependency.split(":")[0];
                String artifactId = dependency.split(":")[1];
                String version = dependency.split(":")[2];
                String depInfo = groupId + ":" + artifactId;
                Conflict conflict;
                if (conflictMap.containsKey(depInfo)) {
                    conflict = conflictMap.get(depInfo);
                } else {
                    conflict = new Conflict(groupId, artifactId);
                }
                conflict.addPom(pom);
                conflict.addModuleNames(pom.getSig());
                conflict.addVersion(version);
                conflict.addToModules(version, pom);
                conflictMap.put(depInfo, conflict);
            }
        }
        for (Map.Entry<String, Conflict> entry : conflictMap.entrySet()) {
            Conflict conflict = entry.getValue();
            if (conflict.getVersions().size() > 1) {
                container.add(conflict);
            }
        }
    }

    public boolean isSingle(int index) {
        int len = modules.length;
        boolean single = true;
        for (int i = 0; i < len; i++) {
            if (modules[index][i] == 1 || modules[i][index] == 1) {
                single = false;
                break;
            }
        }
        return single;
    }

    public boolean isLeaf(Pom pom) {
        boolean single = false;
        String path = pom.getFilePath();
        path = path.substring(0, path.length() - 7);
        File file = new File(path);
        for (File folder : file.listFiles()) {
            if (folder.getName().equals("src")) {
                single = true;
                break;
            }
        }
        return single;
    }

    public List<Pom> getLeafPom() {
        int len = inheritance.length;

//        for (int i = 0; i < len; i++) {
//            boolean isLeaf = true;
//            for (int j = 0; j < len; j++) {
//                if (inheritance[j][i] == 1) {
//                    isLeaf = false;
//                    break;
//                }
//            }
//            if (isLeaf) {
//                leaves.add(sigToIndex.get(i));
//            }
//        }

        for (Pom pom : Poms.i().getPoms()) {
            if (isLeaf(pom)) {
                leaves.add(pom.getSig());
            }
        }
        List<Pom> poms = new ArrayList<>();
        for (Pom pom : Poms.i().getPoms()) {
            String name = pom.getSig();
            if (leaves.contains(name)) {
                if (isSingle(sigToIndex.get(pom.getSig()))) {
                    continue;
                }
                poms.add(pom);
            }
        }
        return poms;
    }

    public void addConflict(Conflict conflict) {
        container.add(conflict);
    }

    public List<Conflict> getConflicts() {
        return container;
    }

    public Set<String> getConflictModules() {
        Set<String> conflictModules = new HashSet<>();
        for (Conflict conflict : container) {
            List<Pom> poms = conflict.getModules();
            for (Pom pom : poms) {
                conflictModules.add(pom.getSig());
            }
        }
        return conflictModules;
    }

    public void generateGraphs(String projName) {
        Set<String> conflictModules = getConflictModules();
        GenerateGraphviz.i().inheritGraph(inheritance, sigToIndex, conflictModules, projName);
        GenerateGraphviz.i().moduleGraph(modules, sigToIndex, conflictModules, projName, "dependencies");
    }

    public List<Conflict> getRealConflicts() {
        List<Conflict> conflicts = new ArrayList<>();
        for (Conflict conflict : container) {
            if (isRealConflict(conflict)) {
                conflicts.add(conflict);
            }
        }
        return conflicts;
    }

    public boolean isRealConflict(Conflict conflict) {
        Map<String, List<Pom>> versionToModule = conflict.getVersionToModule();
        for (Map.Entry<String, List<Pom>> entry : versionToModule.entrySet()) {
            List<String> names = new ArrayList<>();
            for (Pom pom : entry.getValue()) {
                names.add(pom.getSig());
            }
            List<String> moduleNames = new ArrayList<>();
            moduleNames.addAll(conflict.getModuleNames());
            moduleNames.removeAll(names);
            for (String name : names) {
                List<Integer> visited = getReachModules(sigToIndex.get(name));
                for (int i : visited) {
                    if (moduleNames.contains(indexToSig.get(i))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Integer> getReachModules(int start) {
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> visited = new ArrayList<>();
        queue.offer(start);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int index = queue.poll();
                for (int j = 0; j < modules.length; j++) {
                    if (modules[index][j] == 1 && !visited.contains(j)) {
                        queue.offer(j);
                        visited.add(j);
                    }
                }
            }
        }
        return visited;
    }
}
