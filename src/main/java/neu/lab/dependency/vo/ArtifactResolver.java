package neu.lab.dependency.vo;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.factory.DefaultArtifactFactory;
import org.apache.maven.artifact.handler.manager.DefaultArtifactHandlerManager;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 * @author SUNJUNYAN
 */
public class ArtifactResolver {

    public void resolver() throws InvalidVersionSpecificationException {
        Artifact artifact = getArtifact("com.google.guava", "guava", "30.1.1-jre", "jar", "", "compile");
        Artifact newArtifact = new DefaultArtifact("com.google.guava", "guava", VersionRange.createFromVersionSpec("30.1.1-jre"), "compile", "jar", "", new DefaultArtifactHandlerManager().getArtifactHandler("compile"));
        System.out.println(artifact.getDownloadUrl());
    }

    public Artifact getArtifact(String groupId, String artifactId, String versionRange, String type, String classifier,
                                String scope) {
        ArtifactFactory factory = new DefaultArtifactFactory();
        try {
            return factory.createDependencyArtifact(groupId, artifactId,
                    VersionRange.createFromVersionSpec(versionRange), type, classifier, scope);
        } catch (InvalidVersionSpecificationException e) {
            System.err.println("cant create Artifact!" + e);
            return null;
        }
    }

    public static void main(String[] args) throws InvalidVersionSpecificationException {
        new ArtifactResolver().resolver();
    }
}
