package neu.lab.dependency.util;

/**
 * Utility methods to help with regex manipulation.
 *
 * @author SUNJUNYAN
 */
public final class RegexUtils {
    /**
     * The end of a regex literal sequence.
     */
    public static final String REGEX_QUOTE_END = "\\E";

    /**
     * The start of a regex literal sequence.
     */
    public static final String REGEX_QUOTE_START = "\\Q";

    /**
     * Escape the escapes.
     */
    public static final String REGEX_QUOTE_END_ESCAPED = REGEX_QUOTE_END + '\\' + REGEX_QUOTE_END + REGEX_QUOTE_START;

    private RegexUtils() {
        throw new IllegalAccessError("Utility classes should never be instantiated");
    }

    /**
     * Takes a string and returns the regex that will match that string exactly.
     *
     * @param s The string to match.
     * @return The regex that will match the string exactly.
     */
    public static String quote(String s) {
        int i = s.indexOf(REGEX_QUOTE_END);
        if (i == -1) {
            // we're safe as nobody has a crazy \E in the string
            return REGEX_QUOTE_START + s + REGEX_QUOTE_END;
        }

        // damn there's at least one \E in the string
        StringBuilder sb = new StringBuilder(s.length() + 32);
        // each escape-escape takes 10 chars...
        // hope there's less than 4 of them

        sb.append(REGEX_QUOTE_START);
        int pos = 0;
        do {
            // we are safe from pos to i
            sb.append(s.substring(pos, i));
            // now escape-escape
            sb.append(REGEX_QUOTE_END_ESCAPED);
            // move the working start
            pos = i + REGEX_QUOTE_END.length();
            i = s.indexOf(REGEX_QUOTE_END, pos);
        }
        while (i != -1);

        sb.append(s.substring(pos, s.length()));
        sb.append(REGEX_QUOTE_END);

        return sb.toString();
    }

    public static int getWildcardScore(String wildcardRule) {
        int score = 0;
        for (int i = 0; i < wildcardRule.length(); i++) {
            char c = wildcardRule.charAt(i);
            if (c == '?') {
                score++;
            } else if (c == '*') {
                score += 1000;
            }
        }
        return score;
    }

    /**
     * Converts a wildcard rule to a regex rule.
     *
     * @param wildcardRule the wildcard rule.
     * @param exactMatch   <code>true</code> results in an regex that will match the entire string, while
     *                     <code>false</code> will match the start of the string.
     * @return The regex rule.
     */
    public static String convertWildcardsToRegex(String wildcardRule, boolean exactMatch) {
        StringBuilder regex = new StringBuilder();
        int index = 0;
        final int len = wildcardRule.length();
        while (index < len) {
            final int nextQ = wildcardRule.indexOf('?', index);
            final int nextS = wildcardRule.indexOf('*', index);
            if (nextQ == -1 && nextS == -1) {
                regex.append(quote(wildcardRule.substring(index)));
                break;
            }
            int nextIndex;
            if (nextQ == -1) {
                nextIndex = nextS;
            } else if (nextS == -1) {
                nextIndex = nextQ;
            } else {
                nextIndex = Math.min(nextQ, nextS);
            }
            if (index < nextIndex) {
                // we have some characters to match
                regex.append(quote(wildcardRule.substring(index, nextIndex)));
            }
            char c = wildcardRule.charAt(nextIndex);
            if (c == '?') {
                regex.append('.');
            } else {
                regex.append(".*");
            }
            index = nextIndex + 1;
        }
        if (!exactMatch) {
            regex.append(".*");
        }
        return regex.toString();
    }
}