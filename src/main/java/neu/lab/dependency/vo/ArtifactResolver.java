package neu.lab.dependency.vo;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author SUNJUNYAN
 */
public class ArtifactResolver {

    private volatile static ArtifactResolver instance;

    private ArtifactResolver() {

    }

    public static ArtifactResolver i() {
        if (instance == null) {
            synchronized (ArtifactResolver.class) {
                if (instance == null) {
                    instance = new ArtifactResolver();
                }
            }
        }
        return instance;
    }

    public String resolver(String groupId, String artifactId, String version) {
        boolean canResolve = mvnGetArtifact(groupId, artifactId, version);
        String path = null;
        if (canResolve) {
            String repository = mvnGetRepository();
            path = repository
                    + File.separator + groupId.replace(".", File.separator) +File.separator
                    + artifactId.replace(".", File.separator) + File.separator
                    + version + File.separator + artifactId + "-" + version + ".jar";
        }
        return path;
    }

    public String getJarPath(String groupId, String artifactId, String version) {
        String path;
        String repository = mvnGetRepository();
        path = repository
                + File.separator + groupId.replace(".", File.separator) + File.separator
                + artifactId.replace(".", File.separator) + File.separator
                + version + File.separator + artifactId + "-" + version + ".jar";
        return path;
    }

    public boolean mvnGetArtifact(String groupId, String artifactId, String version) {
        System.out.println("Start to get target dependency...");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        CommandLine cmdLine = CommandLine.parse("cmd /k mvn dependency:get -DgroupId=" + groupId + " -DartifactId=" + artifactId + " -Dversion=" + version);
        DefaultExecutor executor = new DefaultExecutor();
        try {
            executor.setStreamHandler(streamHandler);
            executor.execute(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean bulidSuccess = false;
        for (String line : outputStream.toString().split("\\n")) {
            if (line.contains("BUILD SUCCESS")) {
                bulidSuccess = true;
                System.out.println("Successfully get target dependency...");
            }
        }
        return bulidSuccess;
    }

    public String mvnGetRepository() {
        System.out.println("Start to get repository path...");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        CommandLine cmdLine = CommandLine.parse("cmd /k mvn help:effective-settings");
        DefaultExecutor executor = new DefaultExecutor();
        try {
            executor.setStreamHandler(streamHandler);
            executor.execute(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String repository = "D:\\Maven\\Repositories";
        for (String line : outputStream.toString().split("\\n")) {
            if (line.contains("<localRepository>")) {
                repository = line.split("<localRepository>")[1].split("</localRepository>")[0];
                System.out.println("Successfully get repository path...");
            }
        }
        return repository;
    }

    public static void main(String[] args) {
        System.out.println(new ArtifactResolver().resolver("com.google.guava", "guava", "21.0"));
    }
}
