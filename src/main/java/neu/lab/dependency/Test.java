package neu.lab.dependency;

import neu.lab.dependency.container.Conflicts;
import neu.lab.dependency.container.Poms;
import neu.lab.dependency.soot.SootRiskCg;
import neu.lab.dependency.vo.Conflict;
import neu.lab.dependency.vo.Pom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class Test {
    public static void main(String[] args) {
        String projPath = "D:\\githubProject\\obevo\\";
        Poms.init(projPath);
        for (Pom pom : Poms.i().getPoms()) {

            System.out.println(pom.getSig());
        }
//        Conflicts.init(projPath);
//        String[] splits = projPath.split("\\\\");
//        Conflicts.i().generateGraphs(splits[splits.length - 1]);
//        List<Conflict> conflicts = Conflicts.i().getRealConflicts();
//        for (Conflict conflict : conflicts) {
//            System.out.println(conflict.getSig());
//            Map<String, List<Pom>> versionToModule = conflict.getVersionToModule();
//            for (Map.Entry<String, List<Pom>> entry : versionToModule.entrySet()) {
//                System.out.println("Version: " + entry.getKey());
//                List<Pom> poms = entry.getValue();
//                for (Pom pom : poms) {
//                    System.out.println(pom.getSig());
//                }
//            }
//            System.out.println(conflict.getSig() + " safe version : " + conflict.getSafeVersion());
//            System.out.println();
//        }
//        getCallGraph();

    }

    public static void getCallGraph() {
        List<String> paths = new ArrayList<>();
        String jarPath = "C:\\Users\\SUNJUNYAN\\.m2\\repository\\org\\apache\\maven\\shared\\maven-dependency-tree\\2.1\\maven-dependency-tree-2.1.jar";
        String hostPath = "D:\\IdeaProjects\\Decca\\target\\classes";
        Set<String> mthds = SootRiskCg.i().cmpCg(jarPath, hostPath);
        for (String mthd : mthds) {
            System.out.println(mthd);
        }
    }


}
