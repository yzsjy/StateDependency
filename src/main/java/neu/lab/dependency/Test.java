package neu.lab.dependency;

import neu.lab.dependency.container.Conflicts;
import neu.lab.dependency.container.Poms;
import neu.lab.dependency.handler.PomFileIO;
import neu.lab.dependency.pom.ModuleReduce;
import neu.lab.dependency.pom.ModuleRelation;
import neu.lab.dependency.pom.PomParser;
import neu.lab.dependency.smell.DetectDupDeclare;
import neu.lab.dependency.soot.SootRiskCg;
import neu.lab.dependency.vo.Conflict;
import neu.lab.dependency.vo.DepInfo;
import neu.lab.dependency.vo.Pom;
import org.apache.maven.model.Model;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class Test {
    public static void main(String[] args) {
        String projPath = "D:\\IdeaProjects\\ModuleOrderDetect\\";
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

        for (Pom pom : Poms.i().getPoms()) {
            System.out.println(pom.getSig());
            if (pom.getParent() != null) {
                System.out.println(pom.getParent().getSig());
            }
            System.out.println("Dependencies: ");
            for (String dep : pom.getDependencies()) {
                System.out.println(dep);
            }
            System.out.println();
            System.out.println("DependencyManagement: ");
            for (String dep : pom.getDependencyManagement()) {
                System.out.println(dep);
            }
            System.out.println();
            System.out.println();
        }

//        getCallGraph();

//        DetectDupDeclare detectDupDeclare = new DetectDupDeclare(projPath);
//        detectDupDeclare.init();
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
