package neu.lab.dependency.version.ordering;

/**
 * Represents an invalid segment being identified within a version.
 * @author SUNJUNYAN
 */
public class InvalidSegmentException
    extends RuntimeException {
    /**
     * Constructs a new exception.
     *
     * @param segment      the invalid segment index.
     * @param segmentCount the number of segments.
     * @param version      the version string.
     */
    public InvalidSegmentException(int segment, int segmentCount, String version) {
        super(String.format("Invalid segment, %d, for the %d segment version: '%s'", segment, segmentCount,
                version));
    }
}
