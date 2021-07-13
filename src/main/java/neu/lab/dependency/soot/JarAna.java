package neu.lab.dependency.soot;

import neu.lab.dependency.util.SootUtil;
import neu.lab.dependency.vo.ArgsVO;
import soot.*;

import java.util.*;

/**
 * @author SUNJUNYAN
 */
public class JarAna {

    private static JarAna instance = new JarAna();

    private JarAna() {

    }

    public static JarAna i() {
        if (instance == null) {
            instance = new JarAna();
        }
        return instance;
    }

    public Set<String> deconstruct(String path) {
        List<String> jarFilePath = new ArrayList<>();
        jarFilePath.add(path);
        List<String> args = ArgsVO.i().getArgs(jarFilePath.toArray(new String[0]));

        if (args.size() == 0) {
            return new HashSet<>();
        } else {
            DsTransformer transformer = new DsTransformer(path);
            PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));

            SootUtil.modifyLogOut();

            soot.Main.main(args.toArray(new String[0]));
            Set<String> mthds = transformer.getAllMthds();
            soot.G.reset();

            return mthds;
        }
    }
}

class DsTransformer extends SceneTransformer {
    private Set<String> allMthds;
    private String jarPaths;

    public DsTransformer(String jarPaths) {
        this.jarPaths = jarPaths;
        allMthds = new HashSet<>();
    }

    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        Set<String> clsTb = SootUtil.getJarClses(jarPaths);
        for (String cls : clsTb) {
            SootClass sootClass = Scene.v().getSootClass(cls);
            if (sootClass.getMethods() != null) {
                for (SootMethod sootMethod : sootClass.getMethods()) {
                    allMthds.add(sootMethod.getSignature());
                }
            }
        }
    }

    public Set<String> getAllMthds() {
        return allMthds;
    }
}

