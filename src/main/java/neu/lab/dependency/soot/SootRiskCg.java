package neu.lab.dependency.soot;

import neu.lab.dependency.vo.ArgsVO;
import soot.PackManager;
import soot.Transform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class SootRiskCg {
    private static SootRiskCg instance = new SootRiskCg();

    private SootRiskCg() {

    }

    public static SootRiskCg i() {
        return instance;
    }

    public Set<String> cmpCg(String jarPath, String hostPath) {
//        MavenUtil.i().getLog().info("Use soot to compute reach methods");
        System.out.println("Use soot to compute reach methods");
        Set<String> reachMethods = new HashSet<>();
        List<String> jarPaths = new ArrayList<>();
        jarPaths.add(hostPath);
        jarPaths.add(hostPath);
        RiskCgTf transformer = new RiskCgTf(hostPath, jarPath);
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));
        soot.Main.main(ArgsVO.i().getArgs(jarPaths.toArray(new String[0])).toArray(new String[0]));
        reachMethods.addAll(transformer.getReachMethods());
        soot.G.reset();

        return reachMethods;
    }
}
