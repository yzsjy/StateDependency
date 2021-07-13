package neu.lab.dependency.version;

import neu.lab.dependency.util.RegexUtils;
import neu.lab.dependency.util.WagonUtils;
import neu.lab.dependency.version.model.IgnoreVersion;
import neu.lab.dependency.version.model.Rule;
import neu.lab.dependency.version.model.RuleSet;
import neu.lab.dependency.version.model.io.xpp3.RuleXpp3Reader;
import neu.lab.dependency.version.ordering.VersionComparator;
import neu.lab.dependency.version.ordering.VersionComparators;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.path.PathTranslator;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.*;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author SUNJUNYAN
 */
public class DefaultVersionsHelper implements VersionsHelper {

    private static final String CLASSPATH_PROTOCOL = "classpath";
    private static final String TYPE_EXACT = "exact";
    private static final String TYPE_REGEX = "regex";
    private static final int LOOKUP_PARALLEL_THREADS = 5;

    /**
     * The artifact comparison rules to use.
     */
    private final RuleSet ruleSet;

    /**
     * The artifact metadata source to use.
     */
    private final ArtifactMetadataSource artifactMetadataSource;

    /**
     * The local repository to consult.
     */
    private final ArtifactRepository localRepository;

    /**
     * The remote artifact repositories to consult.
     */
    private final List remoteArtifactRepositories;

    /**
     * The remote plugin repositories to consult.
     */
    private final List remotePluginRepositories;

    /**
     * The artifact factory.
     */
    private final ArtifactFactory artifactFactory;

    /**
     * The {@link Log} to send log messages to.
     */
    private final Log log;

    /**
     * The path translator.
     */
    private final PathTranslator pathTranslator;

    /**
     * The maven session.
     */
    private final MavenSession mavenSession;

    /**
     * The artifact resolver.
     */
    private final ArtifactResolver artifactResolver;

    /**
     * Constructs a new {@link DefaultVersionsHelper}.
     *
     * @param artifactFactory            The artifact factory.
     * @param artifactResolver           Artifact resolver
     * @param artifactMetadataSource     The artifact metadata source to use.
     * @param remoteArtifactRepositories The remote artifact repositories to consult.
     * @param remotePluginRepositories   The remote plugin repositories to consult.
     * @param localRepository            The local repository to consult.
     * @param wagonManager               The wagon manager (used if rules need to be retrieved).
     * @param settings                   The settings (used to provide proxy information to the wagon manager).
     * @param serverId                   The serverId hint for the wagon manager.
     * @param rulesUri                   The URL to retrieve the versioning rules from.
     * @param log                        The {@link org.apache.maven.plugin.logging.Log} to send log messages to.
     * @param mavenSession               The maven session information.
     * @param pathTranslator             The path translator component. @throws org.apache.maven.plugin.MojoExecutionException If
     *                                   things go wrong.
     * @throws MojoExecutionException if something goes wrong.
     */
    public DefaultVersionsHelper(ArtifactFactory artifactFactory, ArtifactResolver artifactResolver,
                                 ArtifactMetadataSource artifactMetadataSource, List remoteArtifactRepositories,
                                 List remotePluginRepositories, ArtifactRepository localRepository,
                                 WagonManager wagonManager, Settings settings, String serverId, String rulesUri,
                                 Log log, MavenSession mavenSession, PathTranslator pathTranslator)
            throws MojoExecutionException {
        this.artifactFactory = artifactFactory;
        this.artifactResolver = artifactResolver;
        this.mavenSession = mavenSession;
        this.pathTranslator = pathTranslator;
        this.ruleSet = loadRuleSet(serverId, settings, wagonManager, rulesUri, log);
        this.artifactMetadataSource = artifactMetadataSource;
        this.localRepository = localRepository;
        this.remoteArtifactRepositories = remoteArtifactRepositories;
        this.remotePluginRepositories = remotePluginRepositories;
        this.log = log;
    }

    @Deprecated
    private static RuleSet getRuleSet(Wagon wagon, String remoteURI)
            throws IOException, AuthorizationException, TransferFailedException, ResourceDoesNotExistException {
        File tempFile = File.createTempFile("ruleset", ".xml");
        try {
            wagon.get(remoteURI, tempFile);
            InputStream is = new FileInputStream(tempFile);
            try {
                return readRulesFromStream(is);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        } finally {
            if (!tempFile.delete()) {
                // maybe we can delete this later
                tempFile.deleteOnExit();
            }
        }
    }

    private static RuleSet readRulesFromStream(InputStream stream)
            throws IOException {
        RuleXpp3Reader reader = new RuleXpp3Reader();
        BufferedInputStream bis = new BufferedInputStream(stream);

        try {
            return reader.read(bis);
        } catch (XmlPullParserException e) {
            final IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    static boolean exactMatch(String wildcardRule, String value) {
        Pattern p = Pattern.compile(RegexUtils.convertWildcardsToRegex(wildcardRule, true));
        return p.matcher(value).matches();
    }

    static boolean match(String wildcardRule, String value) {
        Pattern p = Pattern.compile(RegexUtils.convertWildcardsToRegex(wildcardRule, false));
        return p.matcher(value).matches();
    }

    private static RuleSet loadRuleSet(String serverId, Settings settings, WagonManager wagonManager, String rulesUri,
                                       Log logger)
            throws MojoExecutionException {
        RuleSet ruleSet = new RuleSet();
        boolean rulesUriGiven = isRulesUriNotBlank(rulesUri);

        if (rulesUriGiven) {
            RuleSet loadedRules;

            if (isClasspathUri(rulesUri)) {
                loadedRules = getRulesFromClasspath(rulesUri, logger);
            } else {
                loadedRules = getRulesViaWagon(rulesUri, logger, serverId, serverId, wagonManager,
                        settings);
            }

            ruleSet.setIgnoreVersions(loadedRules.getIgnoreVersions());
            ruleSet.setRules(loadedRules.getRules());
        }

        return ruleSet;
    }

    private static RuleSet getRulesFromClasspath(String uri, Log logger)
            throws MojoExecutionException {
        logger.debug("Going to load rules from \"" + uri + "\"");

        String choppedUrl = uri.substring(CLASSPATH_PROTOCOL.length() + 3);

        URL url = DefaultVersionsHelper.class.getResource(choppedUrl);

        if (null == url) {
            String message = "Resource \"" + uri + "\" not found in classpath.";

            throw new MojoExecutionException(message);
        }

        try {
            RuleSet rules = readRulesFromStream(url.openStream());
            logger.debug("Loaded rules from \"" + uri + "\" successfully");
            return rules;
        } catch (IOException e) {
            throw new MojoExecutionException("Could not load specified rules from " + uri, e);
        }
    }

    private static boolean isRulesUriNotBlank(String rulesUri) {
        return rulesUri != null && rulesUri.trim().length() != 0;
    }

    private static RuleSet getRulesViaWagon(String rulesUri, Log logger, String serverId, String id,
                                            WagonManager wagonManager, Settings settings)
            throws MojoExecutionException {
        RuleSet loadedRules = new RuleSet();

        int split = rulesUri.lastIndexOf('/');
        String baseUri = rulesUri;
        String fileUri = "";

        if (split != -1) {
            baseUri = rulesUri.substring(0, split) + '/';
            fileUri = split + 1 < rulesUri.length() ? rulesUri.substring(split + 1) : "";
        }

        try {
            Wagon wagon = WagonUtils.createWagon(serverId, baseUri, wagonManager, settings, logger);
            try {
                logger.debug("Trying to load ruleset from file \"" + fileUri + "\" in " + baseUri);
                loadedRules = getRuleSet(wagon, fileUri);
            } finally {
                logger.debug("Rule set loaded");

                if (wagon != null) {
                    try {
                        wagon.disconnect();
                    } catch (ConnectionException e) {
                        logger.warn("Could not disconnect wagon!", e);
                    }
                }
            }
        } catch (TransferFailedException e) {
            throw new MojoExecutionException("Could not transfer rules from " + rulesUri, e);
        } catch (AuthorizationException e) {
            throw new MojoExecutionException("Authorization failure trying to load rules from " + rulesUri, e);
        } catch (ResourceDoesNotExistException e) {
            throw new MojoExecutionException("Could not load specified rules from " + rulesUri, e);
        } catch (AuthenticationException e) {
            throw new MojoExecutionException("Authentication failure trying to load rules from " + rulesUri, e);
        } catch (UnsupportedProtocolException e) {
            throw new MojoExecutionException("Unsupported protocol for " + rulesUri, e);
        } catch (ConnectionException e) {
            throw new MojoExecutionException("Could not establish connection to " + rulesUri, e);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not load specified rules from " + rulesUri, e);
        }

        return loadedRules;
    }

    static boolean isClasspathUri(String uri) {
        boolean startsWithProtocol = null != uri && uri.startsWith(CLASSPATH_PROTOCOL);
        boolean hasColonNext = null != uri && uri.charAt(CLASSPATH_PROTOCOL.length()) == ':';

        return startsWithProtocol && hasColonNext;
    }

    @Override
    public ArtifactFactory getArtifactFactory() {
        return artifactFactory;
    }

    @Override
    public Log getLog() {
        return log;
    }

    @Override
    public ArtifactVersions lookupArtifactVersions(Artifact artifact, boolean usePluginRepositories)
            throws ArtifactMetadataRetrievalException {
        List remoteRepositories = usePluginRepositories ? remotePluginRepositories : remoteArtifactRepositories;
        final List<ArtifactVersion> versions =
                artifactMetadataSource.retrieveAvailableVersions(artifact, localRepository, remoteRepositories);
        final List<IgnoreVersion> ignoredVersions = getIgnoredVersions(artifact);
        if (!ignoredVersions.isEmpty()) {
            if (getLog().isDebugEnabled()) {
                getLog().debug("Found ignored versions: " + showIgnoredVersions(ignoredVersions));
            }

            final Iterator<ArtifactVersion> i = versions.iterator();
            while (i.hasNext()) {
                final String version = i.next().toString();
                for (final IgnoreVersion ignoreVersion : ignoredVersions) {
                    if (TYPE_REGEX.equals(ignoreVersion.getType())) {
                        Pattern p = Pattern.compile(ignoreVersion.getVersion());
                        if (p.matcher(version).matches()) {
                            if (getLog().isDebugEnabled()) {
                                getLog().debug("Version " + version + " for artifact "
                                        + ArtifactUtils.versionlessKey(artifact) + " found on ignore list: "
                                        + ignoreVersion);
                            }
                            i.remove();
                            break;
                        }
                    } else if (TYPE_EXACT.equals(ignoreVersion.getType())) {
                        if (version.equals(ignoreVersion.getVersion())) {
                            if (getLog().isDebugEnabled()) {
                                getLog().debug("Version " + version + " for artifact "
                                        + ArtifactUtils.versionlessKey(artifact) + " found on ignore list: "
                                        + ignoreVersion);
                            }
                            i.remove();
                            break;
                        }
                    }
                }
            }
        }
        return new ArtifactVersions(artifact, versions, getVersionComparator(artifact));
    }

    /**
     * Returns a list of versions which should not be considered when looking for updates.
     *
     * @param artifact The artifact
     * @return List of ignored version
     */
    private List<IgnoreVersion> getIgnoredVersions(Artifact artifact) {
        final List<IgnoreVersion> ret = new ArrayList<>();

        for (final IgnoreVersion ignoreVersion : ruleSet.getIgnoreVersions()) {
            if (!TYPE_EXACT.equals(ignoreVersion.getType()) && !TYPE_REGEX.equals(ignoreVersion.getType())) {
                getLog().warn("The type attribute '" + ignoreVersion.getType() + "' for global ignoreVersion["
                        + ignoreVersion + "] is not valid." + " Please use either '" + TYPE_EXACT + "' or '" + TYPE_REGEX
                        + "'.");
            } else {
                ret.add(ignoreVersion);
            }
        }

        final Rule rule = getBestFitRule(artifact.getGroupId(), artifact.getArtifactId());

        if (rule != null) {
            for (IgnoreVersion ignoreVersion : rule.getIgnoreVersions()) {
                if (!TYPE_EXACT.equals(ignoreVersion.getType()) && !TYPE_REGEX.equals(ignoreVersion.getType())) {
                    getLog().warn("The type attribute '" + ignoreVersion.getType() + "' for " + rule + " is not valid."
                            + " Please use either '" + TYPE_EXACT + "' or '" + TYPE_REGEX + "'.");
                } else {
                    ret.add(ignoreVersion);
                }
            }
        }

        return ret;
    }

    /**
     * Pretty print a list of ignored versions.
     *
     * @param ignoredVersions A list of ignored versions
     * @return A String representation of the list
     */
    private String showIgnoredVersions(List<IgnoreVersion> ignoredVersions) {
        StringBuilder buf = new StringBuilder();
        Iterator<IgnoreVersion> iterator = ignoredVersions.iterator();
        while (iterator.hasNext()) {
            IgnoreVersion ignoreVersion = iterator.next();
            buf.append(ignoreVersion);
            if (iterator.hasNext()) {
                buf.append(", ");
            }
        }
        return buf.toString();
    }

    @Override
    public void resolveArtifact(Artifact artifact, boolean usePluginRepositories)
            throws ArtifactResolutionException, ArtifactNotFoundException {
        List remoteRepositories = usePluginRepositories ? remotePluginRepositories : remoteArtifactRepositories;
        artifactResolver.resolve(artifact, remoteRepositories, localRepository);
    }

    @Override
    public VersionComparator getVersionComparator(Artifact artifact) {
        return getVersionComparator(artifact.getGroupId(), artifact.getArtifactId());
    }

    @Override
    public VersionComparator getVersionComparator(String groupId, String artifactId) {
        Rule rule = getBestFitRule(groupId, artifactId);
        final String comparisonMethod = rule == null ? ruleSet.getComparisonMethod() : rule.getComparisonMethod();
        return VersionComparators.getVersionComparator(comparisonMethod);
    }

    /**
     * Find the rule, if any, which best fits the artifact details given.
     *
     * @param groupId    Group id of the artifact
     * @param artifactId Artifact id of the artifact
     * @return Rule which best describes the given artifact
     */
    protected Rule getBestFitRule(String groupId, String artifactId) {
        Rule bestFit = null;
        final List<Rule> rules = ruleSet.getRules();
        int bestGroupIdScore = Integer.MAX_VALUE;
        int bestArtifactIdScore = Integer.MAX_VALUE;
        boolean exactGroupId = false;
        boolean exactArtifactId = false;
        for (Rule rule : rules) {
            int groupIdScore = RegexUtils.getWildcardScore(rule.getGroupId());
            if (groupIdScore > bestGroupIdScore) {
                continue;
            }
            boolean exactMatch = exactMatch(rule.getGroupId(), groupId);
            boolean match = exactMatch || match(rule.getGroupId(), groupId);
            if (!match || (exactGroupId && !exactMatch)) {
                continue;
            }
            if (bestGroupIdScore > groupIdScore) {
                bestArtifactIdScore = Integer.MAX_VALUE;
                exactArtifactId = false;
            }
            bestGroupIdScore = groupIdScore;
            if (exactMatch && !exactGroupId) {
                exactGroupId = true;
                bestArtifactIdScore = Integer.MAX_VALUE;
                exactArtifactId = false;
            }
            int artifactIdScore = RegexUtils.getWildcardScore(rule.getArtifactId());
            if (artifactIdScore > bestArtifactIdScore) {
                continue;
            }
            exactMatch = exactMatch(rule.getArtifactId(), artifactId);
            match = exactMatch || match(rule.getArtifactId(), artifactId);
            if (!match || (exactArtifactId && !exactMatch)) {
                continue;
            }
            bestArtifactIdScore = artifactIdScore;
            if (exactMatch && !exactArtifactId) {
                exactArtifactId = true;
            }
            bestFit = rule;
        }
        return bestFit;
    }

    @Override
    public Artifact createDependencyArtifact(String groupId, String artifactId, VersionRange versionRange, String type,
                                             String classifier, String scope, boolean optional) {
        return artifactFactory.createDependencyArtifact(groupId, artifactId, versionRange, type, classifier, scope,
                optional);
    }

    @Override
    public Artifact createDependencyArtifact(String groupId, String artifactId, VersionRange versionRange, String type,
                                             String classifier, String scope) {
        return artifactFactory.createDependencyArtifact(groupId, artifactId, versionRange, type, classifier, scope);
    }

    @Override
    public Artifact createDependencyArtifact(Dependency dependency)
            throws InvalidVersionSpecificationException {
        return createDependencyArtifact(dependency.getGroupId(), dependency.getArtifactId(),
                dependency.getVersion() == null ? VersionRange.createFromVersionSpec("[0,]")
                        : VersionRange.createFromVersionSpec(dependency.getVersion()),
                dependency.getType(), dependency.getClassifier(), dependency.getScope(),
                dependency.isOptional());
    }

    @Override
    public Set<Artifact> extractArtifacts(Collection<MavenProject> mavenProjects) {
        Set<Artifact> result = new HashSet<Artifact>();
        for (MavenProject project : mavenProjects) {
            result.add(project.getArtifact());
        }
        return result;
    }

    @Override
    public ArtifactVersion createArtifactVersion(String version) {
        return new DefaultArtifactVersion(version);
    }

    @Override
    public ArtifactVersions lookupArtifactUpdates(Artifact artifact, boolean allowSnapshots,
                                                  boolean usePluginRepositories)
            throws ArtifactMetadataRetrievalException {
        ArtifactVersions artifactVersions = lookupArtifactVersions(artifact, usePluginRepositories);
        artifactVersions.setIncludeSnapshots(allowSnapshots);
        return artifactVersions;
    }

    @Override
    public ArtifactVersions lookupDependencyUpdates(Dependency dependency, boolean usePluginRepositories)
            throws ArtifactMetadataRetrievalException, InvalidVersionSpecificationException {
        getLog().debug("Checking "
                + ArtifactUtils.versionlessKey(dependency.getGroupId(), dependency.getArtifactId())
                + " for updates newer than " + dependency.getVersion());
        VersionRange versionRange = VersionRange.createFromVersionSpec(dependency.getVersion());

        return lookupArtifactVersions(createDependencyArtifact(dependency.getGroupId(), dependency.getArtifactId(),
                versionRange, dependency.getType(),
                dependency.getClassifier(), dependency.getScope()),
                usePluginRepositories);
    }
}
