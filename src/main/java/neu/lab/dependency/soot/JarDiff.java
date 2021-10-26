package neu.lab.dependency.soot;

import java.util.HashSet;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class JarDiff {

    private String usedJar;
    private String testJar;
    private String hostPath;

    public JarDiff(String usedJar, String testJar, String hostPath) {
        this.testJar = testJar;
        this.usedJar = usedJar;
        this.hostPath = hostPath;
    }

    public Set<String> getDeleteMethods() {
        Set<String> reachedMethods = SootRiskCg.i().cmpCg(usedJar, hostPath);
        Set<String> jarMthds = JarAna.i().deconstruct(testJar);
        Set<String> deleteMethods = new HashSet<>();
        for (String reachedMethod : reachedMethods) {
            if (!jarMthds.contains(reachedMethod)) {
                deleteMethods.add(reachedMethod);
            }
        }
        return deleteMethods;
    }

}
