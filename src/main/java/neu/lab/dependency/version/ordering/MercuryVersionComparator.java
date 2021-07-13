package neu.lab.dependency.version.ordering;

import neu.lab.dependency.version.ComparableVersion;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.math.BigInteger;
import java.util.StringTokenizer;

/**
 * A comparator which uses Mercury's version rules.
 *
 * @author SUNJUNYAN
 */
public class MercuryVersionComparator
    extends AbstractVersionComparator {
    private static final BigInteger BIG_INTEGER_ONE = new BigInteger("1");

    @Override
    public int compare(ArtifactVersion o1, ArtifactVersion o2) {
        return new ComparableVersion(o1.toString()).compareTo(new ComparableVersion(o2.toString()));
    }

    @Override
    protected int innerGetSegmentCount(ArtifactVersion v) {
        final String version = v.toString();
        StringTokenizer tok = new StringTokenizer(version, ".-");
        return tok.countTokens();
    }

    @Override
    protected ArtifactVersion innerIncrementSegment(ArtifactVersion v, int segment) {
        final int segmentCount = getSegmentCount(v);
        if (segment < 0 || segment > segmentCount) {
            throw new InvalidSegmentException(segment, segmentCount, v.toString());
        }
        final String version = v.toString();
        StringBuilder result = new StringBuilder(version.length() + 10);
        StringTokenizer tok = new StringTokenizer(version, ".-");
        int index = 0;
        while (tok.hasMoreTokens() && segment > 0) {
            String token = tok.nextToken();
            result.append(token);
            index += token.length();
            if (tok.hasMoreTokens()) {
                // grab the token separator
                result.append(version.substring(index, index + 1));
                index++;
            }
            segment--;
        }
        if (segment == 0) {
            if (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                String newToken;
                try {
                    BigInteger n = new BigInteger(token);
                    newToken = n.add(BIG_INTEGER_ONE).toString();
                } catch (NumberFormatException e) {
                    // ok, let's try some common tricks
                    if ("alpha".equalsIgnoreCase(token)) {
                        newToken = "beta";
                    } else if ("beta".equalsIgnoreCase(token)) {
                        newToken = "milestone";
                    } else if ("milestone".equalsIgnoreCase(token)) {
                        newToken = "rc";
                    } else if ("rc".equalsIgnoreCase(token) || "cr".equalsIgnoreCase(token)) {
                        newToken = "ga";
                    } else if ("final".equalsIgnoreCase(token) || "ga".equalsIgnoreCase(token)
                            || "".equalsIgnoreCase(token)) {
                        newToken = "sp";
                    } else {
                        newToken = VersionComparators.alphaNumIncrement(token);
                    }
                }

                result.append(newToken);
                index += token.length();
                if (tok.hasMoreTokens()) {
                    // grab the token separator
                    result.append(version.substring(index, index + 1));
                    index++;
                }

            } else {
                // an empty part is equivalent to 0 for mercury version comparator
                result.append("1");
            }
        }
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            result.append("0");
            index += token.length();
            if (tok.hasMoreTokens()) {
                // grab the token separator
                result.append(version.substring(index, index + 1));
                index++;
            }
        }
        return new DefaultArtifactVersion(result.toString());
    }
}
