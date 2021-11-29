package neu.lab.dependency.pom;

import neu.lab.dependency.container.Poms;
import neu.lab.dependency.util.Conf;
import neu.lab.dependency.vo.DepInfo;
import neu.lab.dependency.vo.Pom;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SUNJUNYAN
 */
public class ModuleParser {
    private volatile static ModuleParser instance;

    public static ModuleParser i() {
        if (instance == null) {
            synchronized (ModuleParser.class) {
                if (instance == null) {
                    instance = new ModuleParser();
                }
            }
        }
        return instance;
    }

    public ModuleParser() {

    }

    public void parseDependencies() {
        Set<Pom> poms = getPoms();
        for (Pom pom : poms) {
            parsePom(pom);
        }
    }

    private Set<Pom> getPoms() {
        return Poms.i().getPoms();
    }

    public void parsePom(Pom pom) {
        if (Conf.visited.contains(pom.getSig())) {
            return;
        }
        if (pom.getParent() != null) {
            parsePom(pom.getParent());
        }
        Model model = pom.getModel();
        getDependencyManagements(pom, model);
        getDependnecies(pom, model);
        Conf.visited.add(pom.getSig());
    }

    public void getDependencyManagements(Pom pom, Model model) {
        if (model.getDependencyManagement() == null) {
            return;
        }
        List<DepInfo> dependencies = new ArrayList<>();
        List<Dependency> dependencyManagements = model.getDependencyManagement().getDependencies();
        for (Dependency dependencyManagement : dependencyManagements) {
            String groupId = dependencyManagement.getGroupId();
            String artifactId = dependencyManagement.getArtifactId();
            String version = dependencyManagement.getVersion();
            boolean isProperties = false;
            Pom propertiesPom = pom;
            String propertiesName = null;
            if (groupId != null && groupId.contains("${")) {
                groupId = parseProperties(groupId, pom).split(" ")[0];
            }
            if (artifactId != null && artifactId.contains("${")) {
                artifactId = parseProperties(artifactId, pom).split(" ")[0];
            }
            if (version != null && version.contains("${")) {
                String newVersion = parseProperties(version, pom);
                String[] versionInfos = newVersion.split(" ");
                if (versionInfos.length < 2) {
                    continue;
                }
                propertiesPom = parseVersion(version, pom);
                version = versionInfos[0];
                propertiesName = versionInfos[1];
                isProperties = true;
            }
            if (version == null) {
                continue;
            }

            if (dependencyManagement.getType().equals("pom")) {
                if (Poms.i().isExist(groupId, artifactId, version)) {
                    Pom bomPom = Poms.i().getPomBySig(groupId + ":" + artifactId + ":" + version);
                    if (!Conf.visited.contains(bomPom.getSig())) {
                        parsePom(bomPom);
                    }
                    dependencies.addAll(bomPom.getDependencyManagements());
                }
            }
            DepInfo depInfo = new DepInfo(groupId, artifactId, version);
            if (isProperties) {
                depInfo.setProperty(false);
                depInfo.setPropertyPom(propertiesPom);
                depInfo.setPropertiesName(propertiesName);
                depInfo.setVersionPom(propertiesPom);
            } else {
                depInfo.setVersionPom(pom);
            }
            depInfo.setDeclarePom(pom);
            dependencies.add(depInfo);
        }
        pom.setDependencyManagements(dependencies);
    }

    public void getDependnecies(Pom pom, Model model) {
        if (model.getDependencies() == null) {
            return;
        }
        List<DepInfo> deps = new ArrayList<>();
        List<Dependency> dependencies = model.getDependencies();
        for (Dependency dependency : dependencies) {
            String groupId = dependency.getGroupId();
            String artifactId = dependency.getArtifactId();
            String version = dependency.getVersion();
            boolean isProperties = false;
            Pom propertiesPom = pom;
            String propertiesName = null;
            if (groupId != null && groupId.contains("${")) {
                groupId = parseProperties(groupId, pom).split(" ")[0];
            }
            if (artifactId != null && artifactId.contains("${")) {
                artifactId = parseProperties(artifactId, pom).split(" ")[0];
            }
            if (version == null) {
                version = acquireVersion(groupId, artifactId, pom);
            }
            if (version != null && version.contains("${")) {
                String newVersion = parseProperties(version, pom);
                propertiesPom = parseVersion(version, pom);
//                String[] newVersions = newVersion.split(" ");
//                if (newVersions.length == 1) {
//                    continue;
//                }
                version = newVersion.split(" ")[0];
                propertiesName = newVersion.split(" ")[1];
                isProperties = true;
            }
            if (version == null) {
                continue;
            }
            DepInfo depInfo = new DepInfo(groupId, artifactId, version);
            if (isProperties) {
                depInfo.setProperty(false);
                depInfo.setPropertyPom(propertiesPom);
                depInfo.setPropertiesName(propertiesName);
                depInfo.setPropertyPom(propertiesPom);
            } else {
                depInfo.setVersionPom(pom);
            }
            depInfo.setDeclarePom(pom);
            deps.add(depInfo);
        }
        pom.setOwnDependencies(deps);
    }

    public String parseProperties(String name, Pom pom) {
        Pattern el = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher m = el.matcher(name);
        if (m.find()) {
            if (m.group(1).equals("project.groupId") || m.group(1).equals("pom.groupId") || m.group(1).equals("groupId")) {
                String projectGroupId = pom.getGroupId();
                String newName = name.replace(m.group(0), projectGroupId);
                if (!name.equals(newName)) {
                    name = newName + " " + m.group(1);
                }
            } else if (m.group(1).equals("project.version") || m.group(1).equals("pom.version") || m.group(1).equals("version")) {
                String projectVersion = pom.getVersion();

                String newName = name.replace(m.group(0), projectVersion);
                if (!name.equals(newName)) {
                    name = newName + " " + m.group(1);
                }
            } else if ("project.parent.version".equals(m.group(1)) || "parent.version".equals(m.group(1))) {
                if (pom.getParent() != null && pom.getParent().getVersion() != null) {
                    String newName = name.replace(m.group(0), pom.getParent().getVersion());
                    if (!name.equals(newName)) {
                        name = newName + " " + m.group(1);
                    }
                }
            } else if ("project.parent.groupId".equals(m.group(1)) || "parent.groupId".equals(m.group(1))) {
                if (pom.getParent() != null && pom.getParent().getGroupId() != null) {
                    String newName = name.replace(m.group(0), pom.getParent().getGroupId());
                    if (!name.equals(newName)) {
                        name = newName + " " + m.group(1);
                    }
                }
            } else if ("project.parent.artifactId".equals(m.group(1)) || "parent.artifactId".equals(m.group(1))) {
                if (pom.getParent() != null && pom.getParent().getArtifactId() != null) {
                    String newName = name.replace(m.group(0), pom.getParent().getArtifactId());
                    if (!name.equals(newName)) {
                        name = newName + " " + m.group(1);
                    }
                }
            } else if ("project.prerequisites.maven".equals(m.group(1))) {
                Model model = pom.getModel();
                if (model.getPrerequisites() != null && model.getPrerequisites().getMaven() != null) {
                    String newName = name.replace(m.group(0), model.getPrerequisites().getMaven());
                    if (!name.equals(newName)) {
                        name = newName + " " + m.group(1);
                    }
                }
            } else if (pom.getProperties().size() > 0 && pom.getProperties().containsKey(m.group(1))) {
                String newName = name.replace(m.group(0), pom.getProperties().get(m.group(1)));
                if (!name.equals(newName)) {
                    name = newName + " " + m.group(1);
                }
            } else if (pom.getParent() != null) {
                String newName = parseProperties(name, pom.getParent());
                if (!name.equals(newName)) {
                    name = newName + " " + m.group(1);
                }
            }
        }
        return name;
    }

    public Pom parseVersion(String name, Pom pom) {
        Pom newPom = pom;
        Pattern el = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher m = el.matcher(name);
        if (m.find()) {
            if (m.group(1).equals("project.groupId") || m.group(1).equals("pom.groupId") || m.group(1).equals("groupId")) {
                return newPom;
            } else if (m.group(1).equals("project.version") || m.group(1).equals("pom.version") || m.group(1).equals("version")) {
                return newPom;
            } else if (pom.getProperties().size() > 0 && pom.getProperties().containsKey(m.group(1))) {
                return newPom;
            } else if (pom.getParent() != null) {
                newPom = parseVersion(name, pom.getParent());
            }
        }
        return newPom;
    }

    public String acquireVersion(String groupId, String artifactId, Pom pom) {
        String version = null;
        if (pom.getDependencyManagements() != null) {
            List<DepInfo> dependencyManagements = pom.getDependencyManagements();
            for (DepInfo depInfo : dependencyManagements) {
                if (depInfo.getGroupId().equals(groupId) && depInfo.getArtifactId().equals(artifactId)) {
                    version = depInfo.getVersion();
                    return version;
                }
            }
        }
        if (pom.getParent() != null) {
            Pom parent = pom.getParent();
            if (!Conf.visited.contains(parent.getSig())) {
                parsePom(parent);
            }
            version = acquireVersion(groupId, artifactId, parent);
        }
        return version;
    }

    public static void main(String[] args) {
        Poms.init("D:\\githubProjects\\dubbo-dubbo-2.7.11\\");
        ModuleParser moduleParser = new ModuleParser();
        for (Pom pom : moduleParser.getPoms()) {
            moduleParser.parsePom(pom);
        }
        for (Pom pom : moduleParser.getPoms()) {
            System.out.println(pom.getSig());
            if (pom.getDependencies() != null && pom.getOwnDependencies().size() != 0) {
                System.out.println("dependency : ");
                List<DepInfo> dependencies = pom.getOwnDependencies();
                for (DepInfo depInfo : dependencies) {
                    System.out.println(depInfo.getSig());
                }
            }
            System.out.println();
            if (pom.getDependencyManagements() != null && pom.getDependencyManagements().size() != 0) {
                System.out.println("dependencyManagement : ");
                List<DepInfo> dependencyManagements = pom.getDependencyManagements();
                for (DepInfo depInfo : dependencyManagements) {
                    System.out.println(depInfo.getSig());
                }
            }

            System.out.println();
        }
    }
}
