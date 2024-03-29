package neu.lab.dependency.container;

import neu.lab.dependency.util.MavenUtil;
import neu.lab.dependency.util.NodeAdapterCollector;
import neu.lab.dependency.vo.DepJar;
import neu.lab.dependency.vo.ManageNodeAdapter;
import neu.lab.dependency.vo.NodeAdapter;
import org.apache.maven.shared.dependency.tree.DependencyNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class NodeAdapters {
    private static NodeAdapters instance;

    public static NodeAdapters i() {
        return instance;
    }

    public static void init(DependencyNode root) {
        // if (instance == null) {
        instance = new NodeAdapters();
        // add node in dependency tree
        NodeAdapterCollector visitor = new NodeAdapterCollector(instance);
        root.accept(visitor);
        // add management node
        List<NodeAdapter> manageNds = new ArrayList<NodeAdapter>();
        for (NodeAdapter nodeAdapter : instance.container) {
            if (nodeAdapter.isVersionChanged()) {// this node have management
                if (null == instance.getNodeAdapter(nodeAdapter)) {
                    // this managed-version doesn't have used node, we should create a virtual node to find conflict
                    NodeAdapter manageNd = null;
                    for (NodeAdapter existManageNd : manageNds) {
                        // find if manageNd exists
                        if (existManageNd.isSelf(nodeAdapter)) {
                            manageNd = existManageNd;
                            break;
                        }
                    }
                    if (null == manageNd) {
                        // don't exist manageNd,should new and add
                        manageNd = new ManageNodeAdapter(nodeAdapter);
                        manageNds.add(manageNd);
                    }
                }
            }
        }
        for (NodeAdapter manageNd : manageNds) {
            instance.addNodeAdapter(manageNd);
        }
    }

    private List<NodeAdapter> container;

    private NodeAdapters() {
        container = new ArrayList<NodeAdapter>();
    }

    public void addNodeAdapter(NodeAdapter nodeAdapter) {
        container.add(nodeAdapter);
    }

    /**
     * 根据node获得对应的adapter
     *
     * @param node
     */
    public NodeAdapter getNodeAdapter(DependencyNode node) {
        for (NodeAdapter nodeAdapter : container) {
            if (nodeAdapter.isSelf(node))
                return nodeAdapter;
        }
        MavenUtil.i().getLog().warn("cant find nodeAdapter for node:" + node.toNodeString());
        return null;
    }

    public NodeAdapter getNodeAdapter(NodeAdapter entryNodeAdapter) {
        for (NodeAdapter nodeAdapter : container) {
            if (nodeAdapter.isSelf(entryNodeAdapter))
                return nodeAdapter;
        }
        MavenUtil.i().getLog().warn("can not find nodeAdapter for management node:" + entryNodeAdapter.toString());
        return null;
    }

    /**
     * 得到使用depJar的所有NodeAdapters
     * @param depJar
     * @return
     */
    public Set<NodeAdapter> getNodeAdapters(DepJar depJar) {
        Set<NodeAdapter> result = new HashSet<NodeAdapter>();
        for (NodeAdapter nodeAdapter : container) {
            if (nodeAdapter.getDepJar() == depJar) {
                result.add(nodeAdapter);
            }
        }
        if (result.size() == 0)
            MavenUtil.i().getLog().warn("cant find nodeAdapter for depJar:" + depJar.toString());
        return result;
    }

    public List<NodeAdapter> getAllNodeAdapter() {
        return container;
    }

//    public String getNodeClassPath(String groupId, String artifactId, String version) {
//        StringBuilder sb = new StringBuilder();
//        for (NodeAdapter node : container) {
//            if (node.isNodeSelected() && node.getGroupId().equals(groupId) && node.getArtifactId().equals(artifactId)) {
//                sb.append(node.getNodePath());
//                Artifact artifact = MavenUtil.i().getArtifact(node.getGroupId(), node.getArtifactId(), version, node.getType(), node.getClassifier(), node.getClassifier());
//                try {
//                    MavenUtil.i().resolve(artifact);
//                } catch (ArtifactNotFoundException e) {
//                    e.printStackTrace();
//                } catch (ArtifactResolutionException e) {
//                    e.printStackTrace();
//                }
//                sb.append(artifact.getFile().getAbsolutePath());
//            }
//        }
//        return sb.toString();
//    }

    public NodeAdapter getNode(String groupId, String artifactId) {
        NodeAdapter targetNode = null;
        for (NodeAdapter node : container) {
            if (node.isNodeSelected()) {
                if (node.getGroupId().equals(groupId) && node.getArtifactId().equals(artifactId)) {
                    targetNode = node;
                    break;
                }
            }
        }
        return targetNode;
    }
}
