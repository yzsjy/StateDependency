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

    public static Poms i() {
        return instance;
    }

    public static void init(String projPath) {
        if (instance == null) {
            instance = new Poms(projPath);
        }
    }

    private Poms(String projPath) {
        container = new HashSet<>();
        info = new HashMap<>();
        visited = new HashSet<>();
        String[] paths = FileUtil.i().getAllPomFiles(projPath);

        System.out.println("Pom paths size : " + paths.length);

        for (String path : paths) {
            if (path.contains("src") || path.contains("test" + File.separator + "java") || path.contains("target") || path.contains("META-INF")) {
                continue;
            }
            Model model = PomFileIO.i().parsePomFileToModel(projPath + path);
            if (model != null) {
                String artifactId = model.getArtifactId();
                System.out.println(artifactId);
                if (artifactId.contains("${")) {
                    artifactId = parseProperties(artifactId, model);
                }
                Pom pom = new Pom(artifactId, projPath + path, model);
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
        System.out.println(pom.getFilePath());
        String artifactId = pom.getArtifactId();
        String groupId = model.getGroupId();
        String version = model.getVersion();
        String packaging = model.getPackaging();
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
        if (parent != null) {
            String parentArtifactId = parent.getArtifactId();
            if (parentArtifactId.contains("${")) {
                parentArtifactId = parseProperties(parentArtifactId, model);
            }
            if (info.containsKey(parentArtifactId)) {
                Pom parentPom = info.get(parentArtifactId);
                if (isParent(model, parentPom)) {
                    pom.setParent(parentPom);
                }
            }
        }
        visited.add(artifactId);
    }

    public String parseFromParent(Model model, String name, Matcher m, Pom pom) {
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
        Pom parentPom = null;
        if (info.containsKey(parentArtifactId)) {
            parentPom = info.get(parentArtifactId);
        }
        if (parentPom != null) {
            Model parentModel = parentPom.getModel();
            if (!visited.contains(parentArtifactId)) {
                parsePom(parentModel, parentPom);
            }
            if (isParent(parentPom, parentGroupId, parentArtifactId, parentVersion)) {
                if (parentModel.getProperties() != null
                        && parentModel.getProperties().getProperty(m.group(1)) != null) {
                    String newName = name.replace(m.group(0), parentModel.getProperties().getProperty(m.group(1)));
                    return newName;
                } else {
                    if (parentModel.getParent() != null) {
                        return parseFromParent(parentModel, name, m, pom);
                    }
                }
            } else {
//                System.out.println(pom.getArtifactId() + " has no parent");
                pom.setParent(null);
            }
        }
        return name;
    }

    public String parseProperties(String name, Model model, Pom pom) {
        Pattern el = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher m = el.matcher(name);
        if (m.find()) {
            if ("project.parent.version".equals(m.group(1)) || "parent.version".equals(m.group(1))) {
                if (model.getParent() != null && model.getParent().getVersion() != null) {
                    name = name.replace(m.group(0), model.getParent().getVersion());
                }
            } else if ("project.parent.groupId".equals(m.group(1)) || "parent.groupId".equals(m.group(1))) {
                if (model.getParent() != null && model.getParent().getGroupId() != null) {
                    name = name.replace(m.group(0), model.getParent().getGroupId());
                }
            } else if ("project.parent.artifactId".equals(m.group(1)) || "parent.artifactId".equals(m.group(1))) {
                if (model.getParent() != null && model.getParent().getArtifactId() != null) {
                    name = name.replace(m.group(0), model.getParent().getArtifactId());
                }
            } else if (model.getProperties() != null && model.getProperties().getProperty(m.group(1)) != null) {
                String newName = name.replace(m.group(0), model.getProperties().getProperty(m.group(1)));
                if (!name.equals(newName)) {
                    name = newName;
                }
            } else if (model.getParent() != null) {
                String newName = parseFromParent(model, name, m, pom);
                if (!name.equals(newName)) {
                    name = newName;
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

    public Pom getPom(String module) {
        Pom target = null;
        for (Pom pom : container) {
            if (pom.getSig().equals(module)) {
                target = pom;
            }
        }
        return target;
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
