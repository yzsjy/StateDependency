package neu.lab.dependency;

import neu.lab.dependency.container.Conflicts;
import neu.lab.dependency.container.Poms;
import neu.lab.dependency.pom.DetectUselessDep;
import neu.lab.dependency.pom.ModuleReduce;
import neu.lab.dependency.pom.ModuleRelation;
import neu.lab.dependency.pom.PomParser;
import neu.lab.dependency.smell.DetectDupDeclare;
import neu.lab.dependency.soot.SootRiskCg;
import neu.lab.dependency.vo.Conflict;
import neu.lab.dependency.vo.Pom;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class Test {

    public static String separator = File.separator.equals("/") ? "/" : "\\\\";

    public static void main(String[] args) {
        String projPath = "D:\\githubProjects\\dolphinscheduler\\";
        PomParser.init(projPath);
        ModuleRelation.i().generateGraph();

        System.out.println("Module number : " + Poms.i().getPoms().size());

//        String jarPath = "";
//        String hostPath = "";
//        getReachMethod(hostPath, jarPath);

//        versionCheck(projPath);
        buildOptimize(projPath);
//        findUselessDep(projPath);
//        detectDupDeclare(projPath);
    }

    public static void getReachMethod(String hostPath, String jarPath) {
        Set<String> methods = SootRiskCg.i().cmpCg(hostPath, jarPath);
        for (String method : methods) {
            System.out.println(method);
        }
    }

    public static void versionCheck(String projPath) {
        Conflicts.init();
        String[] splits = projPath.split(separator);
        Conflicts.i().generateGraphs(splits[splits.length - 1]);
        List<Conflict> conflicts = Conflicts.i().getRealConflicts();
        for (Conflict conflict : conflicts) {
            System.out.println(conflict.getSig());
            Map<String, List<Pom>> versionToModule = conflict.getVersionToModule();
            for (Map.Entry<String, List<Pom>> entry : versionToModule.entrySet()) {
                System.out.println("Version: " + entry.getKey());
                List<Pom> poms = entry.getValue();
                for (Pom pom : poms) {
                    System.out.println(pom.getSig());
                }
            }
//            System.out.println(conflict.getSig() + " safe version : " + conflict.getSafeVersion());
            System.out.println();
        }
    }

    public static void buildOptimize(String projPath) {
        String[] splits = projPath.split(separator);
        ModuleReduce.i().reduceDep();
        ModuleReduce.i().relationReduce();
        ModuleReduce.i().generateGraph(splits[splits.length - 1]);
    }

    public static void findUselessDep(String projPath) {
        String[] splits = projPath.split(separator);
        DetectUselessDep.i().reduceDep();
        DetectUselessDep.i().generateGraph(splits[splits.length - 1]);
    }

    public static void detectDupDeclare(String projPath) {
        DetectDupDeclare detectDupDeclare = new DetectDupDeclare(projPath);
        detectDupDeclare.init();
    }
}
