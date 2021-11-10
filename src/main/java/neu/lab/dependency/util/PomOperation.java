package neu.lab.dependency.util;

import neu.lab.dependency.vo.DependencyInfo;
import neu.lab.dependency.vo.Pom;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * The operation of pom file
 * @author:yzsjy
 */

public class PomOperation {

    private Set<String> dependencyInPom;

    private static PomOperation instance;

    public static PomOperation i() {
        if (instance == null) {
            instance = new PomOperation();
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

    public long mvnSerialBuildTime(String projPath) {
        System.out.println("Start to build ...");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        CommandLine cmdLine = CommandLine.parse("cmd /k mvn -f=" + projPath + " install -DskipTests=true -Dmaven.test.skip=true");
        DefaultExecutor executor = new DefaultExecutor();
        long startTime = System.currentTimeMillis();
        try {
            executor.setStreamHandler(streamHandler);
            executor.execute(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        boolean buildSuccess = false;
        for (String line : outputStream.toString().split("\\n")) {
            if (line.contains("BUILD SUCCESS")) {
                buildSuccess = true;
            }
        }
        return buildSuccess ? (endTime - startTime) / 1000 : -1;
    }

    public long mvnParallelBuildTime(String projPath) {
        System.out.println("Start to build ...");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        CommandLine cmdLine = CommandLine.parse("mvn -f=" + projPath + " -T 1C install -DskipTests=true -Dmaven.test.skip=true");
        DefaultExecutor executor = new DefaultExecutor();
        long startTime = System.currentTimeMillis();
        try {
            executor.setStreamHandler(streamHandler);
            executor.execute(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        boolean buildSuccess = false;
        for (String line : outputStream.toString().split("\\n")) {
            if (line.contains("BUILD SUCCESS")) {
                buildSuccess = true;
            }
        }
        return buildSuccess ? (endTime - startTime) / 1000 : -1;
    }

    public boolean mvnClean(String projPath) {
        System.out.println("Start to clean ...");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        CommandLine cmdLine = CommandLine.parse("mvn -f=" + projPath + " clean");
        DefaultExecutor executor = new DefaultExecutor();
        long startTime = System.currentTimeMillis();
        try {
            executor.setStreamHandler(streamHandler);
            executor.execute(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        boolean buildSuccess = false;
        for (String line : outputStream.toString().split("\\n")) {
            if (line.contains("BUILD SUCCESS")) {
                buildSuccess = true;
            }
        }
        return buildSuccess;
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
