package neu.lab.dependency.version.ordering;

import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.util.Comparator;

/**
 * @author SUNJUNYAN
 */
public interface VersionComparator extends Comparator<ArtifactVersion> {

    /**
     * Returns the number of segments specified or specifiable in the supplied artifact version.
     *
     * @param artifactVersion The artifact version to count the segments of.
     * @return The number of segments.
     * @since 1.0-beta-1
     */
    int getSegmentCount( ArtifactVersion artifactVersion );

    /**
     * Increment the specified segment of the supplied version.
     *
     * @param artifactVersion The artifact version to increment.
     * @param segment The segment number to increment.
     * @return An artifact version with the specified segment incremented.
     * @since 1.0-beta-1
     */
    ArtifactVersion incrementSegment( ArtifactVersion artifactVersion, int segment );
}
