package neu.lab.dependency.version;

import neu.lab.dependency.version.ordering.VersionComparator;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Base class for {@link neu.lab.dependency.version.VersionDetails}.
 * @author SUNJUNYAN
 */
public abstract class AbstractVersionDetails implements VersionDetails {
    /**
     * The current version. Guarded by {@link #currentVersionLock}.
     *
     * @since 1.0-beta-1
     */
    private ArtifactVersion currentVersion = null;

    /**
     * Do we want to include snapshots when snapshot inclusion is not specified. Guarded by {@link #currentVersionLock}.
     *
     * @since 1.0-beta-1
     */
    private boolean includeSnapshots = false;

    /**
     * Not sure if we need to be thread safe, but there's no harm being careful, after all we could be invoked from an
     * IDE.
     *
     * @since 1.0-beta-1
     */
    private final Object currentVersionLock = new Object();

    protected AbstractVersionDetails() {
    }

    protected AbstractVersionDetails(ArtifactVersion currentVersion, boolean includeSnapshots) {
        this.currentVersion = currentVersion;
        this.includeSnapshots = includeSnapshots;
    }

    @Override
    public final boolean isCurrentVersionDefined() {
        return getCurrentVersion() != null;
    }

    @Override
    public final ArtifactVersion getCurrentVersion() {
        synchronized (currentVersionLock) {
            return currentVersion;
        }
    }

    @Override
    public final void setCurrentVersion(ArtifactVersion currentVersion) {
        synchronized (currentVersionLock) {
            this.currentVersion = currentVersion;
        }
    }

    @Override
    public final void setCurrentVersion(String currentVersion) {
        setCurrentVersion(currentVersion == null ? null : new DefaultArtifactVersion(currentVersion));
    }

    @Override
    public final boolean isIncludeSnapshots() {
        synchronized (currentVersionLock) {
            return includeSnapshots;
        }
    }

    @Override
    public final void setIncludeSnapshots(boolean includeSnapshots) {
        synchronized (currentVersionLock) {
            this.includeSnapshots = includeSnapshots;
        }
    }

    @Override
    public final ArtifactVersion[] getVersions() {
        return getVersions(isIncludeSnapshots());
    }

    @Override
    public abstract ArtifactVersion[] getVersions(boolean includeSnapshots);

    @Override
    public final ArtifactVersion[] getVersions(VersionRange versionRange, boolean includeSnapshots) {
        return getVersions(versionRange, null, null, includeSnapshots, true, true);
    }

    @Override
    public final ArtifactVersion[] getVersions(ArtifactVersion currentVersion, ArtifactVersion upperBound) {
        return getVersions(currentVersion, upperBound, isIncludeSnapshots());
    }

    @Override
    public final ArtifactVersion[] getVersions(ArtifactVersion currentVersion, ArtifactVersion upperBound,
                                               boolean includeSnapshots) {
        return getVersions(currentVersion, upperBound, includeSnapshots, false, false);
    }

    /**
     * Gets newer versions of the specified artifact version.
     *
     * @param version                The current version of the artifact.
     * @param upperBoundFixedSegment Indicates the segment in the version number that cannot be changed. For example, a
     *                               value of 0 indicates that the major version number cannot be changed. A value of -1 indicates any
     *                               segment value can be changed.
     * @param includeSnapshots       Whether to include snapshot versions.
     * @return Returns the newer artifact versions.
     */
    private final ArtifactVersion[] getNewerVersions(ArtifactVersion version, int upperBoundFixedSegment,
                                                     boolean includeSnapshots) {
        ArtifactVersion lowerBound = version;
        ArtifactVersion upperBound = null;

        if (upperBoundFixedSegment != -1) {
            upperBound = getVersionComparator().incrementSegment(lowerBound, upperBoundFixedSegment);
        }

        return getVersions(version, upperBound, includeSnapshots, false, false);
    }

    private final ArtifactVersion[] getNewerVersions(ArtifactVersion version, boolean includeSnapshots) {
        return getVersions(version, null, includeSnapshots, false, true);
    }

    @Override
    public final ArtifactVersion getNewestVersion(ArtifactVersion lowerBound, ArtifactVersion upperBound) {
        return getNewestVersion(lowerBound, upperBound, isIncludeSnapshots());
    }

    @Override
    public final ArtifactVersion getNewestVersion(ArtifactVersion lowerBound, ArtifactVersion upperBound,
                                                  boolean includeSnapshots) {
        return getNewestVersion(lowerBound, upperBound, includeSnapshots, false, false);
    }

    @Override
    public final ArtifactVersion getNewestVersion(VersionRange versionRange, ArtifactVersion lowerBound,
                                                  ArtifactVersion upperBound, boolean includeSnapshots,
                                                  boolean includeLower, boolean includeUpper) {
        ArtifactVersion latest = null;
        final VersionComparator versionComparator = getVersionComparator();
        for (ArtifactVersion candidate : getVersions(includeSnapshots)) {
            if (versionRange != null && !ArtifactVersions.isVersionInRange(candidate, versionRange)) {
                continue;
            }
            int lower = lowerBound == null ? -1 : versionComparator.compare(lowerBound, candidate);
            int upper = upperBound == null ? +1 : versionComparator.compare(upperBound, candidate);
            if (lower > 0 || upper < 0) {
                continue;
            }
            if ((!includeLower && lower == 0) || (!includeUpper && upper == 0)) {
                continue;
            }
            if (!includeSnapshots && ArtifactUtils.isSnapshot(candidate.toString())) {
                continue;
            }
            if (latest == null) {
                latest = candidate;
            } else if (versionComparator.compare(latest, candidate) < 0) {
                latest = candidate;
            }

        }
        return latest;
    }

    @Override
    public final ArtifactVersion getNewestVersion(ArtifactVersion lowerBound, ArtifactVersion upperBound,
                                                  boolean includeSnapshots, boolean includeLower,
                                                  boolean includeUpper) {
        return getNewestVersion(null, lowerBound, upperBound, includeSnapshots, includeLower, includeUpper);
    }

    @Override
    public final ArtifactVersion getNewestVersion(VersionRange versionRange, boolean includeSnapshots) {
        return getNewestVersion(versionRange, null, null, includeSnapshots, true, true);
    }

    @Override
    public final boolean containsVersion(String version) {
        for (ArtifactVersion candidate : getVersions(true)) {
            if (version.equals(candidate.toString())) {
                return true;
            }
        }
        return false;
    }

    public final ArtifactVersion[] getNewerVersions(String version, boolean includeSnapshots) {
        return getNewerVersions(new DefaultArtifactVersion(version), includeSnapshots);
    }

    public final ArtifactVersion[] getNewerVersions(String version, int upperBoundSegment, boolean includeSnapshots) {
        return getNewerVersions(new DefaultArtifactVersion(version), upperBoundSegment, includeSnapshots);
    }

    @Override
    public final ArtifactVersion getOldestVersion(ArtifactVersion lowerBound, ArtifactVersion upperBound) {
        return getOldestVersion(lowerBound, upperBound, true);
    }

    @Override
    public final ArtifactVersion getOldestVersion(VersionRange versionRange, boolean includeSnapshots) {
        return getOldestVersion(versionRange, null, null, includeSnapshots, true, true);
    }

    @Override
    public final ArtifactVersion getOldestVersion(ArtifactVersion lowerBound, ArtifactVersion upperBound,
                                                  boolean includeSnapshots) {
        return getOldestVersion(lowerBound, upperBound, includeSnapshots, false, false);
    }

    @Override
    public final ArtifactVersion getOldestVersion(ArtifactVersion lowerBound, ArtifactVersion upperBound,
                                                  boolean includeSnapshots, boolean includeLower,
                                                  boolean includeUpper) {
        return getOldestVersion(null, lowerBound, upperBound, includeSnapshots, includeLower, includeUpper);
    }

    @Override
    public final ArtifactVersion getOldestVersion(VersionRange versionRange, ArtifactVersion lowerBound,
                                                  ArtifactVersion upperBound, boolean includeSnapshots,
                                                  boolean includeLower, boolean includeUpper) {
        ArtifactVersion oldest = null;
        final VersionComparator versionComparator = getVersionComparator();
        for (ArtifactVersion candidate : getVersions(includeSnapshots)) {
            if (versionRange != null && !ArtifactVersions.isVersionInRange(candidate, versionRange)) {
                continue;
            }
            int lower = lowerBound == null ? -1 : versionComparator.compare(lowerBound, candidate);
            int upper = upperBound == null ? +1 : versionComparator.compare(upperBound, candidate);
            if (lower > 0 || upper < 0) {
                continue;
            }
            if ((!includeLower && lower == 0) || (!includeUpper && upper == 0)) {
                continue;
            }
            if (!includeSnapshots && ArtifactUtils.isSnapshot(candidate.toString())) {
                continue;
            }
            if (oldest == null) {
                oldest = candidate;
            } else if (versionComparator.compare(oldest, candidate) > 0) {
                oldest = candidate;
            }
        }
        return oldest;
    }

    @Override
    public final ArtifactVersion[] getVersions(ArtifactVersion lowerBound, ArtifactVersion upperBound,
                                               boolean includeSnapshots, boolean includeLower, boolean includeUpper) {
        return getVersions(null, lowerBound, upperBound, includeSnapshots, includeLower, includeUpper);
    }

    @Override
    public final ArtifactVersion[] getVersions(VersionRange versionRange, ArtifactVersion lowerBound,
                                               ArtifactVersion upperBound, boolean includeSnapshots,
                                               boolean includeLower, boolean includeUpper) {
        final VersionComparator versionComparator = getVersionComparator();
        Set<ArtifactVersion> result = new TreeSet<>(versionComparator);
        for (ArtifactVersion candidate : Arrays.asList(getVersions(includeSnapshots))) {
            if (versionRange != null && !ArtifactVersions.isVersionInRange(candidate, versionRange)) {
                continue;
            }
            int lower = lowerBound == null ? -1 : versionComparator.compare(lowerBound, candidate);
            int upper = upperBound == null ? +1 : versionComparator.compare(upperBound, candidate);
            if (lower > 0 || upper < 0) {
                continue;
            }
            if ((!includeLower && lower == 0) || (!includeUpper && upper == 0)) {
                continue;
            }
            if (!includeSnapshots && ArtifactUtils.isSnapshot(candidate.toString())) {
                continue;
            }
            result.add(candidate);
        }
        return result.toArray(new ArtifactVersion[result.size()]);
    }

    @Override
    public final ArtifactVersion getOldestUpdate(ArtifactVersion currentVersion, UpdateScope updateScope) {
        return getOldestUpdate(currentVersion, updateScope, isIncludeSnapshots());
    }

    @Override
    public final ArtifactVersion getNewestUpdate(ArtifactVersion currentVersion, UpdateScope updateScope) {
        return getNewestUpdate(currentVersion, updateScope, isIncludeSnapshots());
    }

    @Override
    public final ArtifactVersion[] getAllUpdates(ArtifactVersion currentVersion, UpdateScope updateScope) {
        return getAllUpdates(currentVersion, updateScope, isIncludeSnapshots());
    }

    @Override
    public ArtifactVersion getOldestUpdate(ArtifactVersion currentVersion, VersionRange versionRange) {
        return getOldestUpdate(currentVersion, versionRange, isIncludeSnapshots());
    }

    @Override
    public ArtifactVersion getNewestUpdate(ArtifactVersion currentVersion, VersionRange versionRange) {
        return getNewestUpdate(currentVersion, versionRange, isIncludeSnapshots());
    }

    @Override
    public ArtifactVersion[] getAllUpdates(ArtifactVersion currentVersion, VersionRange versionRange) {
        return getAllUpdates(currentVersion, versionRange, isIncludeSnapshots());
    }

    @Override
    public final ArtifactVersion getOldestUpdate(ArtifactVersion currentVersion, UpdateScope updateScope,
                                                 boolean includeSnapshots) {
        return updateScope.getOldestUpdate(this, currentVersion, includeSnapshots);
    }

    @Override
    public final ArtifactVersion getNewestUpdate(ArtifactVersion currentVersion, UpdateScope updateScope,
                                                 boolean includeSnapshots) {
        return updateScope.getNewestUpdate(this, currentVersion, includeSnapshots);
    }

    @Override
    public final ArtifactVersion[] getAllUpdates(ArtifactVersion currentVersion, UpdateScope updateScope,
                                                 boolean includeSnapshots) {
        return updateScope.getAllUpdates(this, currentVersion, includeSnapshots);
    }

    @Override
    public ArtifactVersion getOldestUpdate(ArtifactVersion currentVersion, VersionRange versionRange,
                                           boolean includeSnapshots) {
        return getOldestVersion(versionRange, includeSnapshots);
    }

    @Override
    public ArtifactVersion getNewestUpdate(ArtifactVersion currentVersion, VersionRange versionRange,
                                           boolean includeSnapshots) {
        return getNewestVersion(versionRange, includeSnapshots);
    }

    @Override
    public ArtifactVersion[] getAllUpdates(ArtifactVersion currentVersion, VersionRange versionRange,
                                           boolean includeSnapshots) {
        return new ArtifactVersion[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public final ArtifactVersion getOldestUpdate(UpdateScope updateScope) {
        return getOldestUpdate(updateScope, isIncludeSnapshots());
    }

    @Override
    public final ArtifactVersion getNewestUpdate(UpdateScope updateScope) {
        return getNewestUpdate(updateScope, isIncludeSnapshots());
    }

    @Override
    public final ArtifactVersion[] getAllUpdates(UpdateScope updateScope) {
        return getAllUpdates(updateScope, isIncludeSnapshots());
    }

    @Override
    public final ArtifactVersion getOldestUpdate(UpdateScope updateScope, boolean includeSnapshots) {
        if (isCurrentVersionDefined()) {
            return getOldestUpdate(getCurrentVersion(), updateScope, includeSnapshots);
        }
        return null;
    }

    @Override
    public final ArtifactVersion getNewestUpdate(UpdateScope updateScope, boolean includeSnapshots) {
        if (isCurrentVersionDefined()) {
            return getNewestUpdate(getCurrentVersion(), updateScope, includeSnapshots);
        }
        return null;
    }

    @Override
    public final ArtifactVersion[] getAllUpdates(UpdateScope updateScope, boolean includeSnapshots) {
        if (isCurrentVersionDefined()) {
            return getAllUpdates(getCurrentVersion(), updateScope, includeSnapshots);
        }
        return null;
    }

    @Override
    public final ArtifactVersion getOldestUpdate(VersionRange versionRange) {
        return getOldestUpdate(versionRange, isIncludeSnapshots());
    }

    @Override
    public final ArtifactVersion getNewestUpdate(VersionRange versionRange) {
        return getNewestUpdate(versionRange, isIncludeSnapshots());
    }

    @Override
    public final ArtifactVersion[] getAllUpdates(VersionRange versionRange) {
        return getAllUpdates(versionRange, isIncludeSnapshots());
    }

    @Override
    public ArtifactVersion getOldestUpdate(VersionRange versionRange, boolean includeSnapshots) {
        return getOldestVersion(versionRange, getCurrentVersion(), null, includeSnapshots, false, true);
    }

    @Override
    public ArtifactVersion getNewestUpdate(VersionRange versionRange, boolean includeSnapshots) {
        return getNewestVersion(versionRange, getCurrentVersion(), null, includeSnapshots, false, true);
    }

    @Override
    public ArtifactVersion[] getAllUpdates(VersionRange versionRange, boolean includeSnapshots) {
        return getVersions(versionRange, getCurrentVersion(), null, includeSnapshots, false, true);
    }
}
