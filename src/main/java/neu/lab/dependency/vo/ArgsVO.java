package neu.lab.dependency.vo;

import neu.lab.dependency.util.MavenUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author SUNJUNYAN
 */
public class ArgsVO {

    public static ArgsVO instance = new ArgsVO();

    private ArgsVO() {

    }

    public static ArgsVO i() {
        return instance;
    }

    public List<String> getArgs(String[] jarFilePaths){
        List<String> argsList = new ArrayList<String>();
        addClassPath(argsList, jarFilePaths);
        if (argsList.size() == 0) {
            return argsList;
        }
        addGenArgs(argsList);
        addCgArgs(argsList);
        addIgrArgs(argsList);
        return argsList;
    }

    private void addGenArgs(List<String> argsList) {
        argsList.add("-ire");
        argsList.add("-app");
        argsList.add("-allow-phantom-refs");
        argsList.add("-w");
    }

    private void addCgArgs(List<String> argsList) {
        argsList.addAll(Arrays.asList(new String[] { "-p", "cg", "off", }));
    }

    private void addIgrArgs(List<String> argsList) {
        argsList.addAll(Arrays.asList(new String[] { "-p", "wjop", "off", }));
        argsList.addAll(Arrays.asList(new String[] { "-p", "wjap", "off", }));
        argsList.addAll(Arrays.asList(new String[] { "-p", "jtp", "off", }));
        argsList.addAll(Arrays.asList(new String[] { "-p", "jop", "off", }));
        argsList.addAll(Arrays.asList(new String[] { "-p", "jap", "off", }));
        argsList.addAll(Arrays.asList(new String[] { "-p", "bb", "off", }));
        argsList.addAll(Arrays.asList(new String[] { "-p", "tag", "off", }));
        argsList.addAll(Arrays.asList(new String[] { "-f", "n", }));
    }

    private void addClassPath(List<String> argsList, String[] jarFilePaths) {
        for (String jarFilePath : jarFilePaths) {
            if (new File(jarFilePath).exists()) {
                argsList.add("-process-dir");
                argsList.add(jarFilePath);
            } else {
//                MavenUtil.i().getLog().warn("add classpath error : can't analysis file " + jarFilePath);
                System.out.println("add classpath error : can't analysis file " + jarFilePath);
            }
        }
    }

    private boolean canAna(String jarFilePath) {
        if (jarFilePath.contains("\\asm\\") && jarFilePath.contains("6")) {
            return false;
        }
        return true;
    }
}
