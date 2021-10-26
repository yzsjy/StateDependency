package neu.lab.dependency.util;

import neu.lab.dependency.container.NodeAdapters;
import neu.lab.dependency.vo.NodeAdapter;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class NodeAdapterCollector implements DependencyNodeVisitor {
    private static Set<String> longTimeLib;

    static {
        longTimeLib = new HashSet<String>();
        longTimeLib.add("org.scala-lang:scala-library");
        longTimeLib.add("org.clojure:clojure");
    }
    private NodeAdapters nodeAdapters;

    public NodeAdapterCollector(NodeAdapters nodeAdapters) {
        this.nodeAdapters = nodeAdapters;
    }

    @Override
    public boolean visit(DependencyNode node) {

        MavenUtil.i().getLog().debug(node.toNodeString() + " type:" + node.getArtifact().getType() + " version"
                + node.getArtifact().getVersionRange() + " selected:" + (node.getState() == DependencyNode.INCLUDED));

        if (Conf.DEL_LONGTIME) {
            if (longTimeLib.contains(node.getArtifact().getGroupId() + ":" + node.getArtifact().getArtifactId())) {
                return false;
            }
        }

        if (Conf.DEL_OPTIONAL) {
            if (node.getArtifact().isOptional()) {
                return false;
            }
        }

        if (MavenUtil.i().getMojo().ignoreProvidedScope) {
            if ("provided".equals(node.getArtifact().getScope())) {
                return false;
            }
        }

        if (MavenUtil.i().getMojo().ignoreTestScope) {
            if ("test".equals(node.getArtifact().getScope())) {
                return false;
            }
        }

        if (MavenUtil.i().getMojo().ignoreRuntimeScope) {
            if ("runtime".equals(node.getArtifact().getScope())) {
                return false;
            }
        }

        nodeAdapters.addNodeAdapter(new NodeAdapter(node));
        return true;
    }

    @Override
    public boolean endVisit(DependencyNode node) {
        return true;
    }
}
