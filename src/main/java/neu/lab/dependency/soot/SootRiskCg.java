package neu.lab.dependency.soot;

import neu.lab.dependency.vo.ArgsVO;
import soot.PackManager;
import soot.Transform;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class SootRiskCg {
    private volatile static SootRiskCg instance;

    private SootRiskCg() {

    }

    public static SootRiskCg i() {
        if (instance == null) {
            synchronized (SootRiskCg.class) {
                if (instance == null) {
                    instance = new SootRiskCg();
                }
            }
        }
        return instance;
    }

    public Set<String> cmpCg(String hostPath, String jarPath) {
        Set<String> reachMethods = new HashSet<>();
        if (!new File(hostPath).exists() || !new File(jarPath).exists()) {
            return reachMethods;
        }
//        MavenUtil.i().getLog().info("Use soot to compute reach methods");
        System.out.println("Use soot to compute reach methods");
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
