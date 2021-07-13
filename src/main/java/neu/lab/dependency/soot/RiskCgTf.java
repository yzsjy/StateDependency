package neu.lab.dependency.soot;

import neu.lab.dependency.container.DepJars;
import neu.lab.dependency.util.SootUtil;
import soot.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.util.queue.QueueReader;

import java.util.*;

/**
 * @author SUNJUNYAN
 */
public class RiskCgTf extends SceneTransformer {

    protected Set<String> entryClses;
    protected Set<String> reachMethods;
    protected Set<String> jarMethods;

    public RiskCgTf(String hostPath, String jarPath) {
        super();
        entryClses = SootUtil.getJarClses(hostPath);
        jarMethods = JarAna.i().deconstruct(jarPath);
        reachMethods = new HashSet<>();
    }

    @Override
    protected void internalTransform(String args0, Map<String, String> args1) {
        //        MavenUtil.i().getLog().info("RiskCgTf start...");
        System.out.println("RiskCgTf start...");
        Map<String, String> cgMap = new HashMap<>();
        cgMap.put("enabled", "true");
        cgMap.put("apponly", "true");
        cgMap.put("all-reachable", "true");

        List<SootMethod> entryMthds = new ArrayList<>();
        for (SootClass sootClass : Scene.v().getApplicationClasses()) {
            if (entryClses.contains(sootClass.getName())) {
                for (SootMethod method : sootClass.getMethods()) {
                    entryMthds.add(method);
                }
            }
        }
        Scene.v().setEntryPoints(entryMthds);
        CHATransformer.v().transform("wjtp", cgMap);

        QueueReader<MethodOrMethodContext> entryRchMthds = Scene.v().getReachableMethods().listener();
        while (entryRchMthds.hasNext()) {
            SootMethod method = entryRchMthds.next().method();
            if (entryMthds.contains(method)) {
                continue;
            }
            String mthdSig = method.getSignature();
            if (jarMethods.contains(mthdSig) && !mthdSig.startsWith("<java.") && !mthdSig.startsWith("<javax.") && !mthdSig.startsWith("<sun.") && !mthdSig.startsWith("<jdk.")) {
                reachMethods.add(mthdSig);
            }
        }
//        MavenUtil.i().getLog().info("RiskCgTf end...");
        System.out.println("RiskCgTf end...");
    }

    public Set<String> getReachMethods() {
        return reachMethods;
    }
}
