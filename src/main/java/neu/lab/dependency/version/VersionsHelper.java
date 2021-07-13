package neu.lab.dependency.version;

import neu.lab.dependency.version.ordering.VersionComparator;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public interface VersionsHelper {

    /**
     * Gets the logger used by this helper.
     *
     * @return the logger used by this helper.
     */
    Log getLog();

    /**
     * Returns the version comparator to use for the specified artifact.
     *
     * @param artifact the artifact.
     * @return the version comparator to use.
     * @since 1.0-alpha-3
     */
    VersionComparator getVersionComparator(Artifact artifact );

    /**
     * Returns the version comparator to use for the specified groupId and artifactId.
     *
     * @param groupId the groupId.
     * @param artifactId the artifactId.
     * @return the version comparator to use.
     * @since 1.0-alpha-3
     */
    VersionComparator getVersionComparator( String groupId, String artifactId );

    /**
     * Returns the artifact factory to use.
     *
     * @return the artifact factory to use.
     * @since 1.0-alpha-3
     */
    ArtifactFactory getArtifactFactory();

    /**
     * Shorthand method for <code>getArtifactFactory().createDependencyArtifact(...)</code>.
     *
     * @param groupId The group id.
     * @param artifactId The artifact id.
     * @param version The version (possibly a range)
     * @param type The type.
     * @param classifier The classifier.
     * @param scope The scope.
     * @param optional If optional or not.
     * @return The corresponding dependency artifact.
     * @since 1.0-alpha-3
     */
    Artifact createDependencyArtifact( String groupId, String artifactId, VersionRange version, String type,
                                       String classifier, String scope, boolean optional );

    /**
     * Shorthand method for <code>getArtifactFactory().createDependencyArtifact(...)</code>.
     *
     * @param groupId The group id.
     * @param artifactId The artifact id.
     * @param versionRange The version range.
     * @param type The type.
     * @param classifier The classifier.
     * @param scope The scope.
     * @return The corresponding dependency artifact.
     * @since 1.0-beta-1
     */
    Artifact createDependencyArtifact( String groupId, String artifactId, VersionRange versionRange, String type,
                                       String classifier, String scope );

    /**
     * Shorthand method for <code>getArtifactFactory().createDependencyArtifact(...)</code> which extracts the
     * parameters from the DepInfo instance.
     *
     * @param dependency The dependency to create the artifact for.
     * @return The corresponding dependency artifact.
     * @throws InvalidVersionSpecificationException if the version specified in the dependency is invalid.
     * @since 1.0-alpha-3
     */
    Artifact createDependencyArtifact( Dependency dependency )
            throws InvalidVersionSpecificationException;

    /**
     * Takes a {@link List} of {@link org.apache.maven.project.MavenProject} instances and converts it into a
     * {@link Set} of {@link Artifact} instances.
     *
     * @param mavenProjects the {@link List} of {@link org.apache.maven.project.MavenProject} instances.
     * @return a {@link Set} of {@link Artifact} instances.
     * @since 1.0-alpha-3
     */
    Set<Artifact> extractArtifacts( Collection<MavenProject> mavenProjects );

    /**
     * Creates an {@link ArtifactVersion} instance from a string.
     *
     * @param version the string representation of the version.
     * @return The artifact version.
     * @since 1.0-beta-1
     */
    ArtifactVersion createArtifactVersion( String version );

    /**
     * Looks up the versions of the specified artifact that are available in either the local repository, or the
     * appropriate remote repositories.
     *
     * @param artifact The artifact to look for versions of.
     * @param usePluginRepositories <code>true</code> will consult the pluginRepositories, while <code>false</code> will
     *            consult the repositories for normal dependencies.
     * @return The details of the available artifact versions.
     * @throws ArtifactMetadataRetrievalException When things go wrong.
     * @since 1.0-alpha-3
     */
    ArtifactVersions lookupArtifactVersions( Artifact artifact, boolean usePluginRepositories )
            throws ArtifactMetadataRetrievalException;

    /**
     * Looks up the updates of an artifact.
     *
     * @param artifact The artifact to look up
     * @param allowSnapshots Include snapshots in the list of updates.
     * @param usePluginRepositories Search the plugin repositories.
     * @return The artifact update details.
     * @throws ArtifactMetadataRetrievalException When things go wrong.
     */
    ArtifactVersions lookupArtifactUpdates( Artifact artifact, boolean allowSnapshots, boolean usePluginRepositories )
            throws ArtifactMetadataRetrievalException;

    /**
     * Creates an {@link ArtifactVersions} instance from a dependency.
     *
     * @param dependency The dependency.
     * @param usePluginRepositories Search the plugin repositories.
     * @return The details of updates to the dependency.
     * @throws ArtifactMetadataRetrievalException When things go wrong.
     * @throws InvalidVersionSpecificationException if something goes wrong.
     * @since 1.0-beta-1
     */
    ArtifactVersions lookupDependencyUpdates( Dependency dependency, boolean usePluginRepositories )
            throws ArtifactMetadataRetrievalException, InvalidVersionSpecificationException;

    /**
     * Attempts to resolve the artifact.
     *
     * @param artifact The artifact to resolve.
     * @param usePluginRepositories whether to resolve from the plugin repositories or the regular repositories.
     * @throws ArtifactResolutionException if something goes wrong.
     * @throws ArtifactNotFoundException if something goes wrong.
     * @since 1.3
     */
    void resolveArtifact( Artifact artifact, boolean usePluginRepositories )
            throws ArtifactResolutionException, ArtifactNotFoundException;
}
