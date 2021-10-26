package neu.lab.dependency.soot;

import neu.lab.dependency.util.SootUtil;
import soot.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.util.queue.QueueReader;

import java.util.*;

/**
 * @author SUNJUNYAN
 */
public class RiskCgTf extends SceneTransformer {

    protected Set<String> entryClasses;
    protected Set<String> reachMethods;
    protected Set<String> jarMethods;

    public RiskCgTf(String hostPath, String jarPath) {
        super();
        entryClasses = SootUtil.getJarClses(hostPath);
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

        List<SootMethod> entryMethods = new ArrayList<>();
        for (SootClass sootClass : Scene.v().getApplicationClasses()) {
            if (entryClasses.contains(sootClass.getName())) {
                for (SootMethod method : sootClass.getMethods()) {
                    entryMethods.add(method);
                }
            }
        }
        Scene.v().setEntryPoints(entryMethods);
        CHATransformer.v().transform("wjtp", cgMap);

        QueueReader<MethodOrMethodContext> entryRchMthds = Scene.v().getReachableMethods().listener();
        while (entryRchMthds.hasNext()) {
            SootMethod method = entryRchMthds.next().method();
            if (entryMethods.contains(method)) {
                continue;
            }
            String methodSig = method.getSignature();
            if (jarMethods.contains(methodSig) && !methodSig.startsWith("<java.") && !methodSig.startsWith("<javax.") && !methodSig.startsWith("<sun.") && !methodSig.startsWith("<jdk.")) {
                reachMethods.add(methodSig);
            }
        }
//        MavenUtil.i().getLog().info("RiskCgTf end...");
        System.out.println("RiskCgTf end...");
    }

    public Set<String> getReachMethods() {
        return reachMethods;
    }
}
