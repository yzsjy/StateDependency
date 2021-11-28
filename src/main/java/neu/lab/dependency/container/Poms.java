package neu.lab.dependency.container;

import neu.lab.dependency.handler.PomFileIO;
import neu.lab.dependency.util.FileUtil;
import neu.lab.dependency.vo.Pom;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SUNJUNYAN
 */
public class Poms {
    private static Poms instance;
    private Set<Pom> container;
    private Map<String, Pom> info;
    private Set<String> visited;
    private static String PROJECT_PATH;

    public static Poms i() {
        return instance;
    }

    public static void init(String projPath) {
        if (instance == null) {
            instance = new Poms(projPath);
        }
    }

    private Poms(String projPath) {
        PROJECT_PATH = projPath;
        container = new HashSet<>();
        info = new HashMap<>();
        visited = new HashSet<>();
        String[] paths = FileUtil.i().getAllPomFiles(projPath);

//        System.out.println("Pom paths size : " + paths.length);

        for (String path : paths) {
            if (path.contains("src") || path.contains("test" + File.separator + "java") || path.contains("target") || path.contains("META-INF")) {
                continue;
            }
            Model model = PomFileIO.i().parsePomFileToModel(projPath + path);
            if (model != null) {
                String artifactId = model.getArtifactId();
                if (artifactId.contains("${")) {
                    artifactId = parseProperties(artifactId, model);
                }
                Pom pom = new Pom(artifactId, projPath + path, path, model);
                Properties properties = model.getProperties();
                Map<String, String> pomProperties = new HashMap<>();
                for (String key : properties.stringPropertyNames()) {
                    pomProperties.put(key, properties.getProperty(key));
                }
                pom.setProperties(pomProperties);
                info.put(artifactId, pom);
                container.add(pom);
            }
        }
        pomAnalyze();
    }

    public void pomAnalyze() {
        for (Pom pom : container) {
            Model model = pom.getModel();
            if (!visited.contains(pom.getArtifactId())) {
                parsePom(model, pom);
            }
        }
    }

    public void parsePom(Model model, Pom pom) {
        String artifactId = pom.getArtifactId();
        String groupId = model.getGroupId();
        String version = model.getVersion();
        String packaging = model.getPackaging();
        String name = model.getName();
        Parent parent = model.getParent();

        if (groupId == null && parent != null) {
            groupId = parent.getGroupId();
        }
        if (version == null && parent != null) {
            version = parent.getVersion();
        }
        if (groupId != null && groupId.contains("${")) {
            groupId = parseProperties(groupId, model, pom);
        }
        if (artifactId != null && artifactId.contains("${")) {
            artifactId = parseProperties(artifactId, model, pom);
        }
        if (version != null && version.contains("${")) {
            version = parseProperties(version, model, pom);
        }
        if (packaging != null && packaging.contains("${")) {
            packaging = parseProperties(packaging, model, pom);
        }
        pom.setGroupId(groupId);
        pom.setArtifactId(artifactId);
        pom.setVersion(version);
        pom.setPackaging(packaging);
        if (name != null) {
            pom.setName(name);
        }
        if (parent != null) {
            String parentArtifactId = parent.getArtifactId();
            if (parentArtifactId.contains("${")) {
                parentArtifactId = parseProperties(parentArtifactId, model);
            }
            if (info.containsKey(parentArtifactId)) {
                Pom parentPom = info.get(parentArtifactId);
                pom.setParent(parentPom);
            }
        }
        visited.add(artifactId);
    }

    public String parseProperties(String name, Model model, Pom pom) {
        Pattern el = Pattern.compile("\\$\\{(.*?)\\}");
        boolean matched = true;
        while (name != null && matched) {
            Matcher m = el.matcher(name);
            matched = false;
            while (m.find()) {
                matched = true;
                if ("project.groupId".equals(m.group(1)) || "pom.groupId".equals(m.group(1))) {
                    String projectGroupId = model.getGroupId();
                    if (projectGroupId == null && model.getParent() != null) {
                        projectGroupId = model.getParent().getGroupId();
                    }
                    if (projectGroupId != null) {
                        String newName = name.replace(m.group(0), projectGroupId);
                        if (name.equals(newName)) {
                            matched = false;
                        } else {
                            name = newName;
                        }
                    } else {
                        matched = false;
                    }
                } else if ("project.version".equals(m.group(1)) || "pom.version".equals(m.group(1))) {
                    String projectVersion = model.getVersion();
                    if (projectVersion == null && model.getParent() != null) {
                        projectVersion = model.getParent().getVersion();
                    }
                    if (projectVersion != null) {
                        String newName = name.replace(m.group(0), projectVersion);
                        if (name.equals(newName) || newName.contains(name)) {
                            matched = false;
                        } else {
                            name = newName;
                        }
                    } else {
                        matched = false;
                    }
                } else if ("project.artifactId".equals(m.group(1)) || "pom.artifactId".equals(m.group(1))) {
                    if (model.getArtifactId() != null) {
                        String newName = name.replace(m.group(0), model.getArtifactId());
                        if (name.equals(newName)) {
                            matched = false;
                        } else {
                            name = newName;
                        }
                    } else {
                        matched = false;
                    }
                } else if ("project.packaging".equals(m.group(1)) || "pom.packaging".equals(m.group(1))) {
                    if (model.getPackaging() != null) {
                        String newName = name.replace(m.group(0), model.getPackaging());
                        if (name.equals(newName)) {
                            matched = false;
                        } else {
                            name = newName;
                        }
                    } else {
                        name = "jar";
                        matched = false;
                    }
                } else if ("project.parent.version".equals(m.group(1)) || "parent.version".equals(m.group(1))) {
                    if (model.getParent() != null && model.getParent().getVersion() != null) {
                        name = name.replace(m.group(0), model.getParent().getVersion());
                    } else {
                        matched = false;
                    }
                } else if ("project.parent.groupId".equals(m.group(1)) || "parent.groupId".equals(m.group(1))) {
                    if (model.getParent() != null && model.getParent().getGroupId() != null) {
                        name = name.replace(m.group(0), model.getParent().getGroupId());
                    } else {
                        matched = false;
                    }
                } else if ("project.parent.artifactId".equals(m.group(1)) || "parent.artifactId".equals(m.group(1))) {
                    if (model.getParent() != null && model.getParent().getArtifactId() != null) {
                        name = name.replace(m.group(0), model.getParent().getArtifactId());
                    } else {
                        matched = false;
                    }
                } else if ("project.prerequisites.maven".equals(m.group(1))) {
                    if (model.getPrerequisites() != null && model.getPrerequisites().getMaven() != null) {
                        name = name.replace(m.group(0), model.getPrerequisites().getMaven());
                    } else {
                        matched = false;
                    }
                } else if (model.getProperties() != null && model.getProperties().getProperty(m.group(1)) != null) {
                    String newName = name.replace(m.group(0), model.getProperties().getProperty(m.group(1)));
                    if (name.equals(newName)) {
                        matched = false;
                    } else {
                        name = newName;
                    }
                } else if (model.getParent() != null) {
                    String newName = parseFromParent(model, name, m, pom);
                    if (name.equals(newName)) {
                        matched = false;
                    } else {
                        name = newName;
                    }
                } else {
                    matched = false;
                }
            }
        }
        return name;
    }

    public String parseProperties(String name, Model model) {
        Pattern el = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher m = el.matcher(name);
        if (m.find()) {
            if (model.getProperties() != null && model.getProperties().getProperty(m.group(1)) != null) {
                String newName = name.replace(m.group(0), model.getProperties().getProperty(m.group(1)));
                if (!name.equals(newName)) {
                    name = newName;
                }
            }
        }
        return name;
    }

    public String parseFromParent(Model model, String name, Matcher m, Pom pom) {
        String parentGroupId = model.getParent().getGroupId();
        String parentArtifactId = model.getParent().getArtifactId();
        String parentVersion = model.getParent().getVersion();

        String parentPath = getParentPath(model, pom.getModulePath());
        Pom parentPom = findPom(parentPath);
        if (parentPom != null) {
            Model parentModel = parentPom.getModel();
            if (!visited.contains(pom.getArtifactId())) {
                parsePom(parentModel, parentPom);
            }
            if (isParent(parentPath, parentGroupId, parentArtifactId, parentVersion)) {
                pom.setParent(parentPom);
                if (parentModel.getProperties() != null
                        && parentModel.getProperties().getProperty(m.group(1)) != null) {
                    String newName = name.replace(m.group(0), parentModel.getProperties().getProperty(m.group(1)));
                    return newName;
                } else {
                    if (parentModel.getParent() != null) {
                        return parseFromParent(parentModel, name, m, parentPom);
                    }
                }
            } else {
                pom.setParent(null);
            }
        }
        return name;
    }

    public boolean isParent(Model model, Pom parentPom) {
        String parentGroupId = model.getParent().getGroupId();
        String parentArtifactId = model.getParent().getArtifactId();
        String parentVersion = model.getParent().getVersion();
        if (parentGroupId != null && parentGroupId.contains("${")) {
            parentGroupId = parseProperties(parentGroupId, model);
        }
        if (parentArtifactId != null && parentArtifactId.contains("${")) {
            parentArtifactId = parseProperties(parentArtifactId, model);
        }
        if (parentVersion != null && parentVersion.contains("${")) {
            parentVersion = parseProperties(parentVersion, model);

        }
        if (isParent(parentPom, parentGroupId, parentArtifactId, parentVersion)) {
            return true;
        }
        return false;
    }

    public boolean isParent(String pomPath, String groupId, String artifactId, String version) {
        String completePath = pomPath;
        Model model = PomFileIO.i().parsePomFileToModel(completePath);
        if (model == null) {
            return false;
        }
        String parentGroupId = model.getGroupId();
        String parentArtifactId = model.getArtifactId();
        String parentVersion = model.getVersion();
        if (parentGroupId != null && groupId != null && !parentGroupId.equals(groupId)) {
            return false;
        }
        if (parentArtifactId != null && artifactId != null && !parentArtifactId.equals(artifactId)) {
            return false;
        }
        if (parentVersion != null && version != null && !parentVersion.equals(version) && !version.equals("@project.version@")) {
            return false;
        }
        return true;
    }

    public boolean isParent(Pom parentPom, String groupId, String artifactId, String version) {
        String parentGroupId = parentPom.getGroupId();
        String parentArtifactId = parentPom.getArtifactId();
        String parentVersion = parentPom.getVersion();
        if (parentGroupId != null && groupId != null && !parentGroupId.equals(groupId)) {
            return false;
        }
        if (parentArtifactId != null && artifactId != null && !parentArtifactId.equals(artifactId)) {
            return false;
        }
        if (parentVersion != null && version != null && !parentVersion.equals(version) && !version.equals("@project.version@")) {
            return false;
        }
        return true;
    }

    public Set<Pom> getPoms() {
        return container;
    }

    public Set<String> getModules() {
        Set<String> modules = new HashSet<>();
        for (Pom pom : container) {
            modules.add(pom.getSig());
        }
        return modules;
    }

    public Pom findPom(String path) {
        Pom target = null;
        for (Pom pom : container) {
            if (pom.getFilePath().equals(path)) {
                target = pom;
            }
        }
        return target;
    }

    public Pom getPomBySig(String sig) {
        Pom target = null;
        for (Pom pom : container) {
            if (pom.getSig().equals(sig)) {
                target = pom;
            }
        }
        return target;
    }

    public Pom getPomByArtifactId(String artifactId) {
        Pom target = null;
        for (Pom pom : container) {
            if (pom.getArtifactId().equals(artifactId)) {
                target = pom;
            }
        }
        return target;
    }

    public String getParentPath(Model model, String path) {
        String normalizePath = null;
        String parentPath = model.getParent().getRelativePath().replace("\\", File.separator).replace("/", File.separator);
        if (parentPath != null) {
            if (!(parentPath.endsWith("pom.xml") || parentPath.endsWith(".xml"))) {
                if (!parentPath.endsWith(File.separator)) {
                    parentPath += File.separator;
                }
                parentPath += "pom.xml";
            }

            if (parentPath.startsWith(File.separator)) {
                parentPath = parentPath.substring(1);
            }
            String prefix = path;
            if (prefix.endsWith(".xml")) {
                int lastPathSeparatorIndex = prefix.lastIndexOf(File.separator);
                if (lastPathSeparatorIndex < 0) {
                    prefix = "";
                } else {
                    prefix = prefix.substring(0, lastPathSeparatorIndex);
                }
                if (prefix.endsWith(File.separator)) {
                    prefix = prefix.substring(0, prefix.length() - 1);
                }
                String parentWholePath = prefix + File.separator + parentPath;
                if (prefix.equals("")) {
                    parentWholePath = parentPath;
                }
                normalizePath = FileUtil.i().normalizePath(PROJECT_PATH + parentWholePath);
            }
        }
        return normalizePath;
    }

    public boolean isExist(String groupId, String artifactId, String version) {
        for (Pom pom : container) {
            if (pom.getGroupId().equals(groupId) && pom.getArtifactId().equals(artifactId) && pom.getVersion().equals(version)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Poms.init("D:\\githubProject\\camel\\");
        System.out.println("Poms size : " + Poms.i().getPoms().size());
        Set<String> visit = new HashSet<>();
        for (Pom pom : Poms.i().getPoms()) {
            if (visit.contains(pom.getSig())) {
                System.out.println(pom.getSig());
            }
            visit.add(pom.getSig());
        }
    }
}
