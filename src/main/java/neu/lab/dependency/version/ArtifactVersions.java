package neu.lab.dependency.version;

import neu.lab.dependency.version.ordering.VersionComparator;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Holds the results of a search for versions of an artifact.
 * @author SUNJUNYAN
 */
public class ArtifactVersions extends AbstractVersionDetails {
    /**
     * The artifact that who's versions we hold details of.
     *
     * @since 1.0-alpha-3
     */
    private final Artifact artifact;

    /**
     * The available versions.
     *
     * @since 1.0-alpha-3
     */
    private final SortedSet<ArtifactVersion> versions;

    /**
     * The version comparison rule that is used for this artifact.
     *
     * @since 1.0-alpha-3
     */
    private final VersionComparator versionComparator;

    /**
     * Creates a new {@link ArtifactVersions} instance.
     *
     * @param artifact          The artifact.
     * @param versions          The versions.
     * @param versionComparator The version comparison rule.
     * @since 1.0-alpha-3
     */
    public ArtifactVersions(Artifact artifact, List<ArtifactVersion> versions, VersionComparator versionComparator) {
        this.artifact = artifact;
        this.versionComparator = versionComparator;
        this.versions = new TreeSet<ArtifactVersion>(versionComparator);
        this.versions.addAll(versions);
        if (artifact.getVersion() != null) {
            setCurrentVersion(artifact.getVersion());
        }
    }

    /**
     * Checks if the version is in the range (and ensures that the range respects the <code>-!</code> syntax to rule out
     * any qualifiers from range boundaries).
     *
     * @param version the version to check.
     * @param range   the range to check.
     * @return <code>true</code> if and only if the version is in the range.
     * @since 1.3
     */
    public static boolean isVersionInRange(ArtifactVersion version, VersionRange range) {
        if (!range.containsVersion(version)) {
            return false;
        }
        for (Restriction r : ((List<Restriction>) range.getRestrictions())) {
            if (r.containsVersion(version)) {
                // check for the -! syntax
                if (!r.isLowerBoundInclusive() && r.getLowerBound() != null) {
                    String s = r.getLowerBound().toString();
                    if (s.endsWith("-!") && version.toString().startsWith(s.substring(0, s.length() - 2))) {
                        return false;
                    }
                }
                if (!r.isUpperBoundInclusive() && r.getUpperBound() != null) {
                    String s = r.getUpperBound().toString();
                    if (s.endsWith("-!") && version.toString().startsWith(s.substring(0, s.length() - 2))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns the artifact who's version information we are holding.
     *
     * @return the artifact who's version information we are holding.
     * @since 1.0-alpha-3
     */
    public Artifact getArtifact() {
        return artifact;
    }

    /**
     * Returns the groupId of the artifact who's versions we are holding.
     *
     * @return the groupId.
     * @since 1.0-alpha-3
     */
    public String getGroupId() {
        return getArtifact().getGroupId();
    }

    /**
     * Returns the artifactId of the artifact who's versions we are holding.
     *
     * @return the artifactId.
     * @since 1.0-alpha-3
     */
    public String getArtifactId() {
        return getArtifact().getArtifactId();
    }

    @Override
    public ArtifactVersion[] getVersions(boolean includeSnapshots) {
        Set<ArtifactVersion> result;
        if (includeSnapshots) {
            result = versions;
        } else {
            result = new TreeSet<>(versionComparator);
            for (ArtifactVersion candidate : versions) {
                if (ArtifactUtils.isSnapshot(candidate.toString())) {
                    continue;
                }
                result.add(candidate);
            }
        }
        return result.toArray(new ArtifactVersion[result.size()]);
    }

    @Override
    public VersionComparator getVersionComparator() {
        return versionComparator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ArtifactVersions");
        sb.append("{artifact=").append(artifact);
        sb.append(", versions=").append(versions);
        sb.append(", versionComparator=").append(versionComparator);
        sb.append('}');
        return sb.toString();
    }
}
