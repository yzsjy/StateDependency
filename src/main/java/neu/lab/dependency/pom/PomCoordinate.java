package neu.lab.dependency.pom;

import neu.lab.dependency.handler.PomFileIO;
import neu.lab.dependency.util.FileUtil;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取一个项目所有模块的路径以及解析项目的groupId、artifactId、version和packaging
 * @author yzsjy
 */
public class PomCoordinate {

	private static String PROJECT_PATH = "D:\\githubProject\\carina\\";
	private static int projectId = 0;
	private static String OUTPUT_PATH = "C:\\Users\\SUNJUNYAN\\Desktop\\JarDiff\\PomCoordinate\\";

	public PomCoordinate() {

	}

	public PomCoordinate(String projectPath) {
		PROJECT_PATH = projectPath;
	}

	public static void main(String[] args) {
		PomCoordinate pomCoordinate = new PomCoordinate("D:\\githubProject\\obevo\\");
		Map<String, String> res = pomCoordinate.parsePomForOneProj();
		for (Map.Entry<String, String> entry : res.entrySet()) {
			System.out.println(entry.getKey() + "    " + entry.getValue());
		}
	}

	/**
	 * 获取一个依赖的groupId，artifactId，version和packaging
	 */
	public Map<String, String> parsePomForOneProj() {
		Map<String, String> pomMap = new HashMap<>();
		String[] pomFiles = FileUtil.i().getAllPomFiles(PROJECT_PATH);
		System.out.println("pom file size : " + pomFiles.length);
		if (pomFiles.length > 0) {
			for (String pomFile : pomFiles) {
				if (pomFile.contains("src" + File.separator + "main") || pomFile.contains("test" + File.separator + "java")) {
					continue;
				}
				if (pomFile.endsWith("pom.xml")) {
					Model model = PomFileIO.i().parsePomFileToModel(PROJECT_PATH + pomFile);
					if (model != null) {
						String groupId = model.getGroupId();
						String artifactId = model.getArtifactId();
						String version = model.getVersion();
						String packaging = model.getPackaging();

						if (groupId == null) {
							if (model.getParent() != null) {
								groupId = model.getParent().getGroupId();
							}
						}
						if (version == null) {
							if (model.getParent() != null) {
								version = model.getParent().getVersion();
							}
						}
						if (groupId != null && groupId.contains("${")) {
							groupId = parseProperty(groupId, model, pomFile);
						}
						if (artifactId != null && artifactId.contains("${")) {
							artifactId = parseProperty(artifactId, model, pomFile);
						}
						if (version != null && version.contains("${")) {
							version = parseProperty(version, model, pomFile);
						}
						if (packaging != null && packaging.contains("${")) {
							packaging = parseProperty(packaging, model, pomFile);
						}
						String key = groupId + ":" + artifactId + ":" + packaging + ":" + version;
						pomMap.put(key, PROJECT_PATH + pomFile);
					}
				}
			}
		}
		return pomMap;
	}
	
	public String parseProperty(String name, Model model, String path) {
		Pattern el = Pattern.compile("\\$\\{(.*?)\\}");
		boolean matched = true;
		while (name != null && matched) {
			Matcher m = el.matcher(name);
			matched = false;
			while (m.find()) {
				matched = true;
				if ("project.groupId".equals(m.group(1)) || "pom.groupId".equals(m.group(1))) {
					String projectGroupId = model.getGroupId();
					for (Parent parent = model.getParent(); projectGroupId == null && model.getParent() != null; parent = model.getParent()) {
						projectGroupId = parent.getGroupId();
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
					for (Parent parent = model.getParent(); projectVersion == null && model.getParent() != null; parent = model.getParent()) {
						projectVersion = parent.getVersion();
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
					String newName = parseFromParent(model, name, m, path);
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

	public String parseFromParent(Model model, String name, Matcher m, String path) {
		// 处理parent
		String parentGroupId = model.getParent().getGroupId();
		String parentArtifactId = model.getParent().getArtifactId();
		String parentVersion = model.getParent().getVersion();

		String parentPath = model.getParent().getRelativePath().replace("\\", File.separator).replace("/", File.separator);
		if (parentPath.equals("")) {
			return name;
		} else {
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
				System.out.println("parent : " + new File(PROJECT_PATH + parentWholePath).getPath());
				if (new File(PROJECT_PATH + parentWholePath).exists() && isParent(
						PROJECT_PATH + parentWholePath, parentGroupId, parentArtifactId, parentVersion)) {
					Model tempModel = PomFileIO.i().parsePomFileToModel(PROJECT_PATH + parentWholePath);
					if (tempModel != null) {
						if (tempModel.getProperties() != null
								&& tempModel.getProperties().getProperty(m.group(1)) != null) {
							String newName = name.replace(m.group(0), tempModel.getProperties().getProperty(m.group(1)));
							return newName;
						} else {
							if (tempModel.getParent() != null) {
								return parseFromParent(tempModel, name, m, parentWholePath);
							}
						}
					}
				}
			} else if (prefix.endsWith(".pom")) {
				return name;
			}
		}
		return name;
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
}
