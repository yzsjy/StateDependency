package neu.lab.dependency.util;

import neu.lab.dependency.vo.DependencyInfo;
import neu.lab.dependency.vo.NodeAdapter;
import neu.lab.dependency.vo.Pom;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
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
//    public String POM_PATH = MavenUtil.i().getProjectPom();
//    public String POM_PATH_COPY = MavenUtil.i().getBaseDir().getAbsolutePath() + "/pom-copy.xml";

    public String POM_PATH = "";
    public String POM_PATH_COPY = "";
    public File POM_FILE;
    public File POM_FILE_COPY;
    private Set<String> dependencyInPom;

    private static PomOperation instance;

    public static PomOperation i() {
        if (instance == null) {
            instance = new PomOperation();
        }
        return instance;
    }

    private PomOperation() {
//        POM_FILE = new File(POM_PATH);
//        POM_FILE_COPY = new File(POM_PATH_COPY);
    }

    /**
     * changeVersion is used to change version of dependency
     */
    public void changeVersion(String groupId, String artifactId, String version) {
        DependencyInfo dependencyInfo = new DependencyInfo(groupId, artifactId, version);
        readPom();
        if (hasInCurrentPom(dependencyInfo)) {
            PomOperation.i().updateDependencyVersion(dependencyInfo);
            MavenUtil.i().getLog().info("success update dependency version for " + dependencyInfo.getName());
        } else {
            PomOperation.i().addDependency(dependencyInfo);
            MavenUtil.i().getLog().info("success add dependency for " + dependencyInfo.getName());
        }
    }

    public void changeVersion(String preNode, String version) {
        DependencyInfo dependencyInfo = new DependencyInfo(preNode.split(":")[0], preNode.split(":")[1], version);
        readPom();
        if (hasInCurrentPom(dependencyInfo)) {
            PomOperation.i().updateDependencyVersion(dependencyInfo);
            MavenUtil.i().getLog().info("success update dependency version for " + dependencyInfo.getName());
        } else {
            PomOperation.i().addDependency(dependencyInfo);
            MavenUtil.i().getLog().info("success add dependency for " + dependencyInfo.getName());
        }
    }

    /**
     * changeVersions is used to change versions of more than one dependency
     */
    public void changeVersions(List<String> depInfos) {
        readPom();
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(POM_FILE);
            Element rootElement = document.getRootElement();
            Element dependencies = rootElement.element("dependencies");
            if (dependencies == null) {
                dependencies = rootElement.addElement("dependencies");
            }
            for (String depInfo : depInfos) {
                DependencyInfo dependencyInfo = new DependencyInfo(depInfo.split(":")[0], depInfo.split(":")[1], depInfo.split(":")[2]);
                if (hasInCurrentPom(dependencyInfo)) {
                    Iterator dependencyIterator = dependencies.elementIterator("dependency");
                    while (dependencyIterator.hasNext()) {
                        Element dependency = (Element) dependencyIterator.next();
                        if (dependency.element("groupId").getText().equals(dependencyInfo.getGroupId())
                                && dependency.element("artifactId").getText().equals(dependencyInfo.getArtifactId())) {
                            Element version = dependency.element("version");
                            if (version == null) {
                                version = dependency.addElement("version");
                            }
                            version.setText(dependencyInfo.getVersion());
                            break;
                        }
                    }
                } else {
                    Element dependency = dependencies.addElement("dependency");
                    dependencyInfo.addDependencyElement(dependency);
                }
            }
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileWriter(POM_PATH_COPY), outputFormat);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            MavenUtil.i().getLog().error(e.getMessage());
        }
    }

    /**
     * addDependency is used to add new dependency to pom file
     */
    public void addDependency(DependencyInfo dependencyInfo) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(POM_FILE);
            Element rootElement = document.getRootElement();
            Element dependencies = rootElement.element("dependencies");
            if (dependencies == null) {
                dependencies = rootElement.addElement("dependencies");
            }
            dependencies.addComment("Repair part: add a direct dependency");
            Element dependency = dependencies.addElement("dependency");
            dependencyInfo.addDependencyElement(dependency);
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileWriter(POM_PATH_COPY), outputFormat);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            MavenUtil.i().getLog().error(e.getMessage());
        }
    }

    /**
     * updateDependencyVersion is used to update version of dependency in pom file
     */
    public void updateDependencyVersion(DependencyInfo dependencyInfo) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(POM_FILE);
            Element rootElement =document.getRootElement();
            Element dependencies = rootElement.element("dependencies");
            Iterator dependencyIterator = dependencies.elementIterator("dependency");
            while (dependencyIterator.hasNext()) {
                Element dependency = (Element) dependencyIterator.next();
                if (dependency.element("groupId").getText().equals(dependencyInfo.getGroupId())
                        && dependency.element("artifactId").getText().equals(dependencyInfo.getArtifactId())) {
                    Element version = dependency.element("version");
                    if (version == null) {
                        version = dependency.addElement("version");
                    }
                    version.addComment("Repair part : Update the version to repair dependency conflict.");
                    version.setText(dependencyInfo.getVersion());
                    break;
                }
            }
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileWriter(POM_PATH_COPY), outputFormat);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            MavenUtil.i().getLog().error(e.getMessage());
        }
    }

    /**
     * excludeDependencyVersion is used to exclude dependency in pom file
     */
    public void excludeDependencyVersion(DependencyInfo superDependency, DependencyInfo subDependency) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(POM_FILE);
            Element rootElement = document.getRootElement();
            Element dependencies = rootElement.element("dependencies");
            Iterator dependencyIterator = dependencies.elementIterator("dependency");
            while (dependencyIterator.hasNext()) {
                Element dependency = (Element) dependencyIterator.next();
                if (dependency.element("groupId").getText().equals(superDependency.getGroupId())
                        && dependency.element("artifactId").getText().equals(superDependency.getArtifactId())) {
                    dependency.addComment("Repair part: Add <exclusion> to delete target dependency");
                    Element exclusions = dependency.addElement("exclusions");
                    Element exclusion = exclusions.addElement("exclusion");
                    exclusion.addElement("groupId").setText(subDependency.getGroupId());
                    exclusion.addElement("artifactId").setText(subDependency.getArtifactId());
                    break;
                }
            }
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileWriter(POM_PATH_COPY), outputFormat);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            MavenUtil.i().getLog().error(e.getMessage());
        }
    }

    public boolean excludeDependencyVersion(List<String> preNodes, String groupId, String artifactId){
        SAXReader reader = new SAXReader();
        int i = 0;
        try {
            Document document = reader.read(POM_FILE);
            Element rootElement = document.getRootElement();
            Element dependencies = rootElement.element("dependencies");
            if (dependencies == null) {
                dependencies = rootElement.addElement("dependencies");
            }
            Iterator dependencyIterator = dependencies.elementIterator("dependency");
            while (dependencyIterator.hasNext()) {
                Element dependency = (Element) dependencyIterator.next();
                for (String preNode : preNodes) {
                    if (preNode.split(":")[0].equals(dependency.element("groupId").getText()) && preNode.split(":")[1].equals(dependency.element("artifactId").getText())) {
                        dependency.addComment("Repair : exclude the dependency " + groupId + ":" + artifactId);
                        Element exclusions = dependency.addElement("exclusions");
                        Element exclusion = exclusions.addElement("exclusion");
                        exclusion.addElement("groupId").setText(groupId);
                        exclusion.addElement("artifactId").setText(artifactId);
                        i++;
                        break;
                    }
                }
            }
            Writer fileWriter = new FileWriter(POM_PATH_COPY);
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setNewlines(true);
            XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i == preNodes.size();
    }

    /**
     * dependencyManagementDepVersion is used to union version of a dependency
     * @param dependencyInfo
     */
    public void dependencyManagementDepVersion(DependencyInfo dependencyInfo) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(POM_FILE);
            Element rootElement = document.getRootElement();
            Element dependencyManagement = rootElement.element("dependencyManagement");
            if (dependencyManagement == null) {
                dependencyManagement = rootElement.addElement("dependencyManagement");
                Element dependencies = dependencyManagement.addElement("dependencies");
                Element dependency = dependencies.addElement("dependency");
                dependency.addElement("groupId").setText(dependencyInfo.getGroupId());
                dependency.addElement("artifactId").setText(dependencyInfo.getArtifactId());
                dependency.addElement("version").setText(dependencyInfo.getVersion());
            } else {
                Element dependencies = dependencyManagement.element("dependencies");
                Iterator dependencyIterator = dependencies.elementIterator("dependency");
                boolean isExist = false;
                while (dependencyIterator.hasNext()) {
                    Element dependency = (Element) dependencyIterator.next();
                    if (dependency.element("groupId").getText().equals(dependencyInfo.getGroupId())
                            && dependency.element("artifactId").getText().equals(dependencyInfo.getArtifactId())) {
                        Element version = dependency.element("version");
                        if (version == null) {
                            version = dependency.addElement("version");
                        }
                        version.setText(dependencyInfo.getVersion());
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    Element dependency = dependencies.addElement("dependency");
                    dependency.addElement("groupId").setText(dependencyInfo.getGroupId());
                    dependency.addElement("artifactId").setText(dependencyInfo.getArtifactId());
                    dependency.addElement("version").setText(dependencyInfo.getVersion());
                }
            }
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileWriter(POM_PATH_COPY), outputFormat);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            MavenUtil.i().getLog().error(e.getMessage());
        }
    }

    public void dependencyManagementDepVersion(String groupId, String artifactId, String changeVersion) {
        DependencyInfo dependencyInfo = new DependencyInfo(groupId, artifactId, changeVersion);
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(POM_FILE);
            Element rootElement = document.getRootElement();
            Element dependencyManagement = rootElement.element("dependencyManagement");
            if (dependencyManagement == null) {
                dependencyManagement = rootElement.addElement("dependencyManagement");
                Element dependencies = dependencyManagement.addElement("dependencies");
                Element dependency = dependencies.addElement("dependency");
                dependency.addElement("groupId").setText(dependencyInfo.getGroupId());
                dependency.addElement("artifactId").setText(dependencyInfo.getArtifactId());
                dependency.addElement("version").setText(dependencyInfo.getVersion());
            } else {
                Element dependencies = dependencyManagement.element("dependencies");
                Iterator dependencyIterator = dependencies.elementIterator("dependency");
                boolean isExist = false;
                while (dependencyIterator.hasNext()) {
                    Element dependency = (Element) dependencyIterator.next();
                    if (dependency.element("groupId").getText().equals(dependencyInfo.getGroupId())
                            && dependency.element("artifactId").getText().equals(dependencyInfo.getArtifactId())) {
                        Element version = dependency.element("version");
                        if (version == null) {
                            version = dependency.addElement("version");
                        }
                        version.setText(dependencyInfo.getVersion());
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    Element dependency = dependencies.addElement("dependency");
                    dependency.addElement("groupId").setText(dependencyInfo.getGroupId());
                    dependency.addElement("artifactId").setText(dependencyInfo.getArtifactId());
                    dependency.addElement("version").setText(dependencyInfo.getVersion());
                }
            }
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileWriter(POM_PATH_COPY), outputFormat);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            MavenUtil.i().getLog().error(e.getMessage());
        }
    }

    /**
     * read the dependencies that pom file declare
     */
    public List<Element> readPomDependencies() {
        SAXReader reader = new SAXReader();
        List<Element> dependencyList = new ArrayList<>();

        try {
            Document document = reader.read(POM_FILE);
            Element rootElement = document.getRootElement();
            Element dependencies = rootElement.element("dependencies");
            if (dependencies != null) {
                dependencyList = dependencies.elements("dependency");
            }
        } catch (Exception e) {
            MavenUtil.i().getLog().error(e.getMessage());
        }
        return dependencyList;
    }

    public void readPom() {
        dependencyInPom = new HashSet<String>();
        List<Element> dependencyList = readPomDependencies();
        for (Element element : dependencyList) {
            dependencyInPom.add(element.element("groupId").getText() + ":" + element.element("artifactId").getText());
        }
    }

    public boolean hasInCurrentPom(DependencyInfo dependencyInfo) {
        return dependencyInPom.contains(dependencyInfo.getGroupId() + ":" + dependencyInfo.getArtifactId());
    }

    /**
     * backup pom.xml or pom-copy.xml
     */
    public boolean backupPom() {
        if (new File(POM_PATH_COPY).exists()) {
            new File(POM_PATH_COPY).delete();
        }
        MavenUtil.i().getLog().info("Backup pom.xml to pom-copy.xml");
        try {
            Files.copy(new File(POM_PATH).toPath(), new File(POM_PATH_COPY).toPath());
        } catch (IOException e){
            MavenUtil.i().getLog().error("Backup pom.xml error");
            MavenUtil.i().getLog().error(e.getMessage());
            return false;
        }
        return true;
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

    public void backupPom(String fileName) {
        MavenUtil.i().getLog().info("Backup pom-copy.xml to " + fileName);
        File file = new File(Conf.outDir);
        if (!file.exists()){
            file.mkdir();
        }
        try {
            Files.copy(new File(POM_PATH_COPY).toPath(), new File(Conf.outDir + fileName).toPath());
        } catch (IOException e) {
            MavenUtil.i().getLog().error("Backup pom-copy.xml error");
            MavenUtil.i().getLog().error(e.getMessage());
        }
    }

    public void restorePom() {
        if (new File(POM_PATH).exists()) {
            new File(POM_PATH).delete();
        }
        try {
            Files.copy(new File(POM_PATH_COPY).toPath(), new File(POM_PATH).toPath());
        } catch (IOException e) {
            MavenUtil.i().getLog().error("Restore pom.xml error");
            MavenUtil.i().getLog().error(e.getMessage());
        }
        MavenUtil.i().getLog().info("Successfully restore pom.xml");
    }

    public boolean mvnTest() {
        MavenUtil.i().getLog().info("Start to mvn test ...");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        CommandLine cmdLine = CommandLine.parse("cmd /k mvn test -f=" + POM_PATH_COPY);
        DefaultExecutor executor = new DefaultExecutor();
        try {
            executor.setStreamHandler(streamHandler);
            executor.execute(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean buildSuccess = false;
        for (String line : outputStream.toString().split("\\n")) {
            if (line.contains("BUILD SUCCESS")) {
                buildSuccess = true;
            }
        }
        return buildSuccess;
    }

    /**
     * delete the test pom file
     */
    public void deletePomCopy() {
        if (new File(POM_PATH_COPY).exists()) {
            new File(POM_PATH_COPY).delete();
        }
        MavenUtil.i().getLog().info("Successfully delete pom-copy.xml");
    }

    public void deleteFile(String path) {
        if (new File(path).exists()) {
            new File(path).delete();
        }
        MavenUtil.i().getLog().info("delete " + path);
    }

    public List<String> removeDependency(Pom pom, List<String> removes) {
        String pomPath = pom.getFilePath();
        String savePath = pomPath.substring(0, pomPath.length() - 7) + "pom-copy.xml";
        backupPom(savePath, pomPath);
        List<String> canRemoves = new ArrayList<>();
        SAXReader reader = new SAXReader();
        boolean canReduce = false;
        try {
            Document document = reader.read(pomPath);
            Element rootElement =document.getRootElement();
            Element dependencies = rootElement.element("dependencies");
            Iterator dependencyIterator = dependencies.elementIterator("dependency");
            while (dependencyIterator.hasNext()) {
                Element dependency = (Element) dependencyIterator.next();
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
        }
        return canRemoves;
    }
}
