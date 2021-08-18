package neu.lab.dependency;

import neu.lab.dependency.container.Conflicts;
import neu.lab.dependency.pom.ModuleReduce;
import neu.lab.dependency.pom.ModuleRelation;
import neu.lab.dependency.pom.PomParser;
import neu.lab.dependency.soot.SootRiskCg;
import neu.lab.dependency.vo.Conflict;
import neu.lab.dependency.vo.Pom;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class Test {
    public static void main(String[] args) {
        String projPath = "D:\\githubProjects\\newts\\";
        PomParser.init(projPath);
        ModuleRelation.i().generateGraph();

        Conflicts.init();
        String[] splits = projPath.split("\\\\");
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
        ModuleReduce.i().reduceDep();
        ModuleReduce.i().generateGraph(splits[splits.length - 1]);
        ModuleReduce.i().canReduce();

//        getCallGraph();
    }

    public static void getCallGraph() {
        String jarPath = "D:\\githubProjects\\incubator-nemo\\runtime\\common\\target\\classes";
        String hostPath = "D:\\githubProjects\\incubator-nemo\\runtime\\executor\\target\\classes";
        Set<String> mthds = SootRiskCg.i().cmpCg(hostPath, jarPath);
        for (String mthd : mthds) {
            System.out.println(mthd);
        }
    }
}
