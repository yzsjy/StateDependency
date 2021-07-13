package neu.lab.dependency.version.ordering;

import org.apache.maven.artifact.versioning.ArtifactVersion;

/**
 * Base class for version comparators.
 *
 * @author SUNJUNYAN
 */
public abstract class AbstractVersionComparator
    implements VersionComparator {
    @Override
    public abstract int compare(ArtifactVersion o1, ArtifactVersion o2);

    @Override
    public final int getSegmentCount(ArtifactVersion v) {
        if (v == null) {
            return 0;
        }
        if (VersionComparators.isSnapshot(v)) {
            return innerGetSegmentCount(VersionComparators.stripSnapshot(v));
        }
        return innerGetSegmentCount(v);

    }

    protected abstract int innerGetSegmentCount(ArtifactVersion v);

    @Override
    public final ArtifactVersion incrementSegment(ArtifactVersion v, int segment) {
        if (VersionComparators.isSnapshot(v)) {
            return VersionComparators.copySnapshot(v, innerIncrementSegment(VersionComparators.stripSnapshot(v),
                    segment));
        }
        return innerIncrementSegment(v, segment);
    }

    protected abstract ArtifactVersion innerIncrementSegment(ArtifactVersion v, int segment);

    /**
     * Returns a hash code value for the comparator class.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Returns true if this object is the same type of comparator as the parameter.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise.
     * @see #hashCode()
     * @see java.util.Hashtable
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj != null && getClass().equals(obj.getClass()));
    }
}
