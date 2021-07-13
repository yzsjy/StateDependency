package neu.lab.dependency.version;

import neu.lab.dependency.version.ordering.VersionComparator;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 * @author SUNJUNYAN
 */
public interface VersionDetails {

    /**
     * Returns <code>true</code> if the specific version is in the list of versions.
     *
     * @param version the specific version.
     * @return <code>true</code> if the specific version is in the list of versions.
     * @since 1.0-beta-1
     */
    boolean containsVersion(String version);

    /**
     * Gets the rule for version comparison of this artifact.
     *
     * @return the rule for version comparison of this artifact.
     */
    VersionComparator getVersionComparator();

    /**
     * Returns all the available versions in increasing order.
     */
    ArtifactVersion[] getVersions();

    /**
     * Returns all available versions in increasing order.
     *
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return all available versions in increasing order.
     */
    ArtifactVersion[] getVersions(boolean includeSnapshots);

    /**
     * Returns all available versions within the specified version range.
     *
     * @param versionRange     The version range within which the version must exist.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return all available versions within the specified version range.
     */
    ArtifactVersion[] getVersions(VersionRange versionRange, boolean includeSnapshots);

    /**
     * Returns all available versions within the specified bounds.
     *
     * @param lowerBound the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param upperBound the upper bound or <code>null</code> if the upper limit is unbounded.
     * @return all available versions within the specified version range.
     */
    ArtifactVersion[] getVersions(ArtifactVersion lowerBound, ArtifactVersion upperBound);

    /**
     * Returns all available versions within the specified bounds.
     *
     * @param lowerBound       the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param upperBound       the upper bound or <code>null</code> if the upper limit is unbounded.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return all available versions within the specified version range.
     */
    ArtifactVersion[] getVersions(ArtifactVersion lowerBound, ArtifactVersion upperBound, boolean includeSnapshots);

    /**
     * Returns all available versions within the specified bounds.
     *
     * @param lowerBound       the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param upperBound       the upper bound or <code>null</code> if the upper limit is unbounded.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @param includeLower     <code>true</code> if the lower bound is inclusive.
     * @param includeUpper     <code>true</code> if the upper bound is inclusive.
     * @return all available versions within the specified version range.
     */
    ArtifactVersion[] getVersions(ArtifactVersion lowerBound, ArtifactVersion upperBound, boolean includeSnapshots,
                                  boolean includeLower, boolean includeUpper);

    /**
     * Returns all available versions within the specified bounds.
     *
     * @param versionRange     The version range within which the version must exist where <code>null</code> imples
     *                         <code>[,)</code>.
     * @param lowerBound       the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param upperBound       the upper bound or <code>null</code> if the upper limit is unbounded.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @param includeLower     <code>true</code> if the lower bound is inclusive.
     * @param includeUpper     <code>true</code> if the upper bound is inclusive.
     * @return all available versions within the specified version range.
     */
    ArtifactVersion[] getVersions(VersionRange versionRange, ArtifactVersion lowerBound, ArtifactVersion upperBound,
                                  boolean includeSnapshots, boolean includeLower, boolean includeUpper);

    /**
     * Returns the latest version newer than the specified lowerBound, but less than the specified upper bound or
     * <code>null</code> if no such version exists.
     *
     * @param lowerBound the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param upperBound the upper bound or <code>null</code> if the upper limit is unbounded.
     */
    ArtifactVersion getNewestVersion(ArtifactVersion lowerBound, ArtifactVersion upperBound);

    /**
     * Returns the latest version newer than the specified lowerBound, but less than the specified upper bound or
     * <code>null</code> if no such version exists.
     *
     * @param lowerBound       the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param upperBound       the upper bound or <code>null</code> if the upper limit is unbounded.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the latest version between currentVersion and upperBound or <code>null</code> if no version is available.
     */
    ArtifactVersion getNewestVersion(ArtifactVersion lowerBound, ArtifactVersion upperBound,
                                     boolean includeSnapshots);

    /**
     * Returns the latest version newer than the specified current version, but less than the specified upper bound or
     * <code>null</code> if no such version exists.
     *
     * @param lowerBound       the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param upperBound       the upper bound or <code>null</code> if the upper limit is unbounded.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @param includeLower     <code>true</code> if the lower bound is inclusive.
     * @param includeUpper     <code>true</code> if the upper bound is inclusive.
     * @return the latest version between lowerBound and upperBound or <code>null</code> if no version is available.
     */
    ArtifactVersion getNewestVersion(ArtifactVersion lowerBound, ArtifactVersion upperBound, boolean includeSnapshots,
                                     boolean includeLower, boolean includeUpper);

    /**
     * Returns the latest version newer than the specified current version, but less than the specified upper bound or
     * <code>null</code> if no such version exists.
     *
     * @param versionRange     The version range within which the version must exist where <code>null</code> imples
     *                         <code>[,)</code>.
     * @param lowerBound       the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param upperBound       the upper bound or <code>null</code> if the upper limit is unbounded.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @param includeLower     <code>true</code> if the lower bound is inclusive.
     * @param includeUpper     <code>true</code> if the upper bound is inclusive.
     * @return the latest version between lowerBound and upperBound or <code>null</code> if no version is available.
     */
    ArtifactVersion getNewestVersion(VersionRange versionRange, ArtifactVersion lowerBound, ArtifactVersion upperBound,
                                     boolean includeSnapshots, boolean includeLower, boolean includeUpper);

    /**
     * Returns the latest version within the specified version range or <code>null</code> if no such version exists.
     *
     * @param versionRange     The version range within which the version must exist.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the latest version within the version range or <code>null</code> if no version is available.
     */
    ArtifactVersion getNewestVersion(VersionRange versionRange, boolean includeSnapshots);

    /**
     * Returns the oldest version after the specified lowerBound, but less than the specified upper bound or
     * <code>null</code> if no such version exists.
     *
     * @param lowerBound the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param upperBound the upper bound or <code>null</code> if the upper limit is unbounded.
     * @return the next version between lowerBound and upperBound or <code>null</code> if no version is available.
     */
    ArtifactVersion getOldestVersion(ArtifactVersion lowerBound, ArtifactVersion upperBound);

    /**
     * Returns the oldest version within the specified version range or <code>null</code> if no such version exists.
     *
     * @param versionRange     The version range within which the version must exist.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the oldest version between currentVersion and upperBound or <code>null</code> if no version is available.
     */
    ArtifactVersion getOldestVersion(VersionRange versionRange, boolean includeSnapshots);

    /**
     * Returns the oldest version newer than the specified lower bound, but less than the specified upper bound or
     * <code>null</code> if no such version exists.
     *
     * @param lowerBound       the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param upperBound       the upper bound or <code>null</code> if the upper limit is unbounded.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the latest version between currentVersion and upperBound or <code>null</code> if no version is available.
     */
    ArtifactVersion getOldestVersion(ArtifactVersion lowerBound, ArtifactVersion upperBound,
                                     boolean includeSnapshots);

    /**
     * Returns the oldest version within the specified bounds or <code>null</code> if no such version exists.
     *
     * @param lowerBound       the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param upperBound       the upper bound or <code>null</code> if the upper limit is unbounded.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @param includeLower     <code>true</code> if the lower bound is inclusive.
     * @param includeUpper     <code>true</code> if the upper bound is inclusive.
     * @return the oldest version between lowerBound and upperBound or <code>null</code> if no version is available.
     */
    ArtifactVersion getOldestVersion(ArtifactVersion lowerBound, ArtifactVersion upperBound, boolean includeSnapshots,
                                     boolean includeLower, boolean includeUpper);

    /**
     * Returns the oldest version within the specified bounds or <code>null</code> if no such version exists.
     *
     * @param versionRange     The version range within which the version must exist where <code>null</code> imples
     *                         <code>[,)</code>.
     * @param lowerBound       the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param upperBound       the upper bound or <code>null</code> if the upper limit is unbounded.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @param includeLower     <code>true</code> if the lower bound is inclusive.
     * @param includeUpper     <code>true</code> if the upper bound is inclusive.
     * @return the oldest version between lowerBound and upperBound or <code>null</code> if no version is available.
     */
    ArtifactVersion getOldestVersion(VersionRange versionRange, ArtifactVersion lowerBound, ArtifactVersion upperBound,
                                     boolean includeSnapshots, boolean includeLower, boolean includeUpper);

    /**
     * Returns the oldest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param currentVersion the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param updateScope    the update scope to include.
     * @return the oldest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getOldestUpdate(ArtifactVersion currentVersion, UpdateScope updateScope);

    /**
     * Returns the newest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param currentVersion the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param updateScope    the update scope to include.
     * @return the newest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getNewestUpdate(ArtifactVersion currentVersion, UpdateScope updateScope);

    /**
     * Returns the all versions newer than the specified current version, but within the the specified update scope.
     *
     * @param currentVersion the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param updateScope    the update scope to include.
     * @return the all versions after currentVersion within the specified update scope.
     */
    ArtifactVersion[] getAllUpdates(ArtifactVersion currentVersion, UpdateScope updateScope);

    /**
     * Returns the oldest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param currentVersion   the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param updateScope      the update scope to include.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the oldest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getOldestUpdate(ArtifactVersion currentVersion, UpdateScope updateScope,
                                    boolean includeSnapshots);

    /**
     * Returns the newest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param currentVersion   the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param updateScope      the update scope to include.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the newest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getNewestUpdate(ArtifactVersion currentVersion, UpdateScope updateScope,
                                    boolean includeSnapshots);

    /**
     * Returns the all versions newer than the specified current version, but within the the specified update scope.
     *
     * @param currentVersion   the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param updateScope      the update scope to include.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the all versions after currentVersion within the specified update scope.
     */
    ArtifactVersion[] getAllUpdates(ArtifactVersion currentVersion, UpdateScope updateScope,
                                    boolean includeSnapshots);

    /**
     * Returns the oldest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param currentVersion the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param versionRange   the version range to include.
     * @return the oldest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getOldestUpdate(ArtifactVersion currentVersion, VersionRange versionRange);

    /**
     * Returns the newest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param currentVersion the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param versionRange   the version range to include.
     * @return the newest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getNewestUpdate(ArtifactVersion currentVersion, VersionRange versionRange);

    /**
     * Returns the all versions newer than the specified current version, but within the the specified update scope.
     *
     * @param currentVersion the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param versionRange   the version range to include.
     * @return the all versions after currentVersion within the specified update scope.
     */
    ArtifactVersion[] getAllUpdates(ArtifactVersion currentVersion, VersionRange versionRange);

    /**
     * Returns the oldest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param currentVersion   the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param versionRange     the version range to include.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the oldest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getOldestUpdate(ArtifactVersion currentVersion, VersionRange versionRange,
                                    boolean includeSnapshots);

    /**
     * Returns the newest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param currentVersion   the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param versionRange     the version range to include.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the newest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getNewestUpdate(ArtifactVersion currentVersion, VersionRange versionRange,
                                    boolean includeSnapshots);

    /**
     * Returns the all versions newer than the specified current version, but within the the specified update scope.
     *
     * @param currentVersion   the lower bound or <code>null</code> if the lower limit is unbounded.
     * @param versionRange     the version range to include.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the all versions after currentVersion within the specified update scope.
     */
    ArtifactVersion[] getAllUpdates(ArtifactVersion currentVersion, VersionRange versionRange,
                                    boolean includeSnapshots);

    /**
     * Returns <code>true</code> if and only if <code>getCurrentVersion() != null</code>.
     *
     * @return <code>true</code> if and only if <code>getCurrentVersion() != null</code>.
     */
    boolean isCurrentVersionDefined();

    /**
     * Sets the current version.
     *
     * @param currentVersion The new current version.
     */
    void setCurrentVersion(ArtifactVersion currentVersion);

    /**
     * Sets the current version.
     *
     * @param currentVersion The new current version.
     */
    void setCurrentVersion(String currentVersion);

    boolean isIncludeSnapshots();

    void setIncludeSnapshots(boolean includeSnapshots);

    /**
     * Retrieves the current version.
     *
     * @return The current version (may be <code>null</code>).
     */
    ArtifactVersion getCurrentVersion();

    /**
     * Returns the oldest version newer than the current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param updateScope the update scope to include.
     * @return the oldest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getOldestUpdate(UpdateScope updateScope);

    /**
     * Returns the newest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param updateScope the update scope to include.
     * @return the newest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getNewestUpdate(UpdateScope updateScope);

    /**
     * Returns the all versions newer than the specified current version, but within the the specified update scope.
     *
     * @param updateScope the update scope to include.
     * @return the all versions after currentVersion within the specified update scope.
     */
    ArtifactVersion[] getAllUpdates(UpdateScope updateScope);

    /**
     * Returns the oldest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param updateScope      the update scope to include.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the oldest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getOldestUpdate(UpdateScope updateScope, boolean includeSnapshots);

    /**
     * Returns the newest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param updateScope      the update scope to include.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the newest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getNewestUpdate(UpdateScope updateScope, boolean includeSnapshots);

    /**
     * Returns the all versions newer than the specified current version, but within the the specified update scope.
     *
     * @param updateScope      the update scope to include.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the all versions after currentVersion within the specified update scope.
     */
    ArtifactVersion[] getAllUpdates(UpdateScope updateScope, boolean includeSnapshots);

    /**
     * Returns the oldest version newer than the current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param versionRange the version range to include.
     * @return the oldest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getOldestUpdate(VersionRange versionRange);

    /**
     * Returns the newest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param versionRange the version range to include.
     * @return the newest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getNewestUpdate(VersionRange versionRange);

    /**
     * Returns the all versions newer than the specified current version, but within the the specified update scope.
     *
     * @param versionRange the version range to include.
     * @return the all versions after currentVersion within the specified update scope.
     */
    ArtifactVersion[] getAllUpdates(VersionRange versionRange);

    /**
     * Returns the oldest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param versionRange     the version range to include.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the oldest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getOldestUpdate(VersionRange versionRange, boolean includeSnapshots);

    /**
     * Returns the newest version newer than the specified current version, but within the the specified update scope or
     * <code>null</code> if no such version exists.
     *
     * @param versionRange     the version range to include.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the newest version after currentVersion within the specified update scope or <code>null</code> if no
     * version is available.
     */
    ArtifactVersion getNewestUpdate(VersionRange versionRange, boolean includeSnapshots);

    /**
     * Returns the all versions newer than the specified current version, but within the the specified update scope.
     *
     * @param versionRange     the version range to include.
     * @param includeSnapshots <code>true</code> if snapshots are to be included.
     * @return the all versions after currentVersion within the specified update scope.
     */
    ArtifactVersion[] getAllUpdates(VersionRange versionRange, boolean includeSnapshots);
}
