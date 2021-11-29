package neu.lab.dependency.util;

import neu.lab.dependency.vo.Pom;
import org.apache.maven.shared.invoker.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The operation of pom file
 * @author:yzsjy
 */

public class PomOperation {

    private volatile static PomOperation instance;

    public static PomOperation i() {
        if (instance == null) {
            synchronized (PomOperation.class) {
                if (instance == null) {
                    instance = new PomOperation();
                }
            }
        }
        return instance;
    }

    private PomOperation() {
    }

    public void backupPom(String savePath, String filePath) {
        if (new File(savePath).exists()) {
            new File(savePath).delete();
        }
        try {
            Files.copy(new File(filePath).toPath(), new File(savePath).toPath());
        } catch (IOException e){
            System.err.println("Fail to backup pom.xml to " + savePath);
            e.printStackTrace();
        }
    }

    public void backupPom(File savePath, File filePath) {
        if (savePath.exists()) {
            savePath.delete();
        }
        try {
            Files.copy(filePath.toPath(), savePath.toPath());
        } catch (IOException e){
            System.err.println("Fail to backup pom.xml to " + savePath);
            e.printStackTrace();
        }
    }

    public long mvnParallelBuildTime(String path) {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(path));
        request.setGoals(Collections.singletonList("-T 1C install -Dmaven.test.skip=true -Dmaven.javadoc.skip=true"));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));
        boolean buildSuccess = false;
        long startTime = System.currentTimeMillis();
        try {
            invoker.setOutputHandler(null);
            InvocationResult invocationResult = invoker.execute(request);
            if (invocationResult.getExitCode() != 0) {
                System.out.println("Failed to build");
            } else {
                System.out.println("Successfully build");
                buildSuccess = true;
            }
        } catch (MavenInvocationException e) {
            System.out.println("Exception : Failed to build");
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        return buildSuccess ? (endTime - startTime) / 1000 : -1;
    }

    public long mvnParallelBuildTime(String path, int core) {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(path));
        request.setGoals(Collections.singletonList("-T " + core + " install -Dmaven.test.skip=true -Dmaven.javadoc.skip=true"));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));
        boolean buildSuccess = false;
        long startTime = System.currentTimeMillis();
        try {
            invoker.setOutputHandler(null);
            InvocationResult invocationResult = invoker.execute(request);
            if (invocationResult.getExitCode() != 0) {
                System.out.println("Failed to build");
            } else {
                System.out.println("Successfully build");
                buildSuccess = true;
            }
        } catch (MavenInvocationException e) {
            System.out.println("Exception : Failed to build");
        }
        long endTime = System.currentTimeMillis();
        return buildSuccess ? (endTime - startTime) / 1000 : -1;
    }

    public boolean mvnClean(String path) {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(path));
        request.setGoals(Collections.singletonList("clean"));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));
        boolean buildSuccess = false;
        try {
            invoker.setOutputHandler(null);
            InvocationResult invocationResult = invoker.execute(request);
            if (invocationResult.getExitCode() != 0) {
                System.out.println("Failed to clean");
            } else {
                System.out.println("Successfully clean");
                buildSuccess = true;
            }
        } catch (MavenInvocationException e) {
            System.out.println("Failed to clean");
            e.printStackTrace();
        }
        return buildSuccess;
    }

    public long mvnSerialBuildTime(String path) {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(path));
        request.setGoals(Collections.singletonList("install -Dmaven.test.skip=true -Dmaven.javadoc.skip=true"));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));
        boolean buildSuccess = false;
        long startTime = System.currentTimeMillis();
        try {
            invoker.setOutputHandler(null);
            InvocationResult invocationResult = invoker.execute(request);
            if (invocationResult.getExitCode() != 0) {
                System.out.println("Failed to build");
            } else {
                System.out.println("Successfully build");
                buildSuccess = true;
            }
        } catch (MavenInvocationException e) {
            System.out.println("Exception : Failed to build");
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        return buildSuccess ? (endTime - startTime) / 1000 : -1;
    }

    public List<String> removeDependency(Pom pom, List<String> removes) {
        String pomPath = pom.getFilePath();
        String savePath = pomPath.substring(0, pomPath.length() - 7) + "pom-copy.xml";
        backupPom(savePath, pomPath);
        List<String> canRemoves = new ArrayList<>();
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(pomPath);
            Element rootElement =document.getRootElement();
            Element dependencies = rootElement.element("dependencies");
            if (dependencies == null) {
                return canRemoves;
            }
            Iterator dependencyIterator = dependencies.elementIterator("dependency");
            while (dependencyIterator.hasNext()) {
                Element dependency = (Element) dependencyIterator.next();
                if (dependency.element("scope") != null && dependency.element("scope").equals("test")) {
                    continue;
                }
                if (dependency.element("type") != null && dependency.element("type").equals("test-jar")) {
                    continue;
                }
                String groupId = dependency.element("groupId").getText();
                String artifactId = dependency.element("artifactId").getText();
                if (groupId != null && artifactId != null && removes.contains(groupId + ":" + artifactId)) {
                    dependencies.remove(dependency);
                    canRemoves.add(groupId + ":" + artifactId);
                }
            }
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileWriter(pomPath), outputFormat);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(removes);
            System.out.println(pom.getFilePath());
        }
        return canRemoves;
    }
}
