package neu.lab.dependency.version.model.io.xpp3;

import neu.lab.dependency.version.model.IgnoreVersion;
import neu.lab.dependency.version.model.Rule;
import neu.lab.dependency.version.model.RuleSet;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.EntityReplacementMap;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;

/**
 * @author SUNJUNYAN
 */
public class RuleXpp3Reader {

    private boolean addDefaultEntities = true;

    /**
     * Field contentTransformer.
     */
    public final ContentTransformer contentTransformer;

    public RuleXpp3Reader() {
        this(new ContentTransformer() {
            @Override
            public String transform(String source, String fieldName) {
                return source;
            }
        });
    }

    public RuleXpp3Reader(ContentTransformer contentTransformer) {
        this.contentTransformer = contentTransformer;
    }

    private boolean checkFieldWithDuplicate(XmlPullParser parser, String tagName, String alias, Set parsed)
            throws XmlPullParserException {
        if (!(parser.getName().equals(tagName) || parser.getName().equals(alias))) {
            return false;
        }
        if (!parsed.add(tagName)) {
            throw new XmlPullParserException("Duplicated tag: '" + tagName + "'", parser, null);
        }
        return true;
    }

    private void checkUnknownAttribute(XmlPullParser parser, String attribute, String tagName, boolean strict)
            throws XmlPullParserException {
        if (strict) {
            throw new XmlPullParserException("Unknown attribute '" + attribute + "' for tag '" + tagName + "'", parser, null);
        }
    }

    private void checkUnknownElement(XmlPullParser parser, boolean strict)
            throws XmlPullParserException, IOException {
        if (strict) {
            throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
        }

        for (int unrecognizedTagCount = 1; unrecognizedTagCount > 0; ) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                unrecognizedTagCount++;
            } else if (eventType == XmlPullParser.END_TAG) {
                unrecognizedTagCount--;
            }
        }
    }

    private String getTrimmedValue(String s) {
        if (s != null) {
            s = s.trim();
        }
        return s;
    }

    private String interpolatedTrimmed(String value, String context) {
        return getTrimmedValue(contentTransformer.transform(value, context));
    }

    private int nextTag(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        int eventType = parser.next();
        if (eventType == XmlPullParser.TEXT) {
            eventType = parser.next();
        }
        if (eventType != XmlPullParser.START_TAG && eventType != XmlPullParser.END_TAG) {
            throw new XmlPullParserException("expected START_TAG or END_TAG not " + XmlPullParser.TYPES[eventType], parser, null);
        }
        return eventType;
    }

    public RuleSet read(Reader reader, boolean strict)
            throws IOException, XmlPullParserException {
        XmlPullParser parser = addDefaultEntities ? new MXParser(EntityReplacementMap.defaultEntityReplacementMap) : new MXParser();
        parser.setInput(reader);
        return read(parser, strict);
    }

    public RuleSet read(Reader reader)
            throws IOException, XmlPullParserException {
        return read(reader, true);
    }

    public RuleSet read(InputStream in)
            throws IOException, XmlPullParserException {
        return read(ReaderFactory.newXmlReader(in));
    }

    private IgnoreVersion parseIgnoreVersion(XmlPullParser parser, boolean strict)
            throws IOException, XmlPullParserException {
        String tagName = parser.getName();
        IgnoreVersion ignoreVersion = new IgnoreVersion();
        for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
            String name = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);

            if (name.indexOf(':') >= 0) {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            } else if ("type".equals(name)) {
                ignoreVersion.setType(interpolatedTrimmed(value, "type"));
            } else {
                checkUnknownAttribute(parser, name, tagName, strict);
            }
        }
        ignoreVersion.setVersion(interpolatedTrimmed(parser.nextText(), "version"));
        return ignoreVersion;
    }

    private Rule parseRule(XmlPullParser parser, boolean strict)
            throws IOException, XmlPullParserException {
        String tagName = parser.getName();
        Rule rule = new Rule();
        for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
            String name = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);

            if (name.indexOf(':') >= 0) {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            } else if ("groupId".equals(name)) {
                rule.setGroupId(interpolatedTrimmed(value, "groupId"));
            } else if ("artifactId".equals(name)) {
                rule.setArtifactId(interpolatedTrimmed(value, "artifactId"));
            } else if ("comparisonMethod".equals(name)) {
                rule.setComparisonMethod(interpolatedTrimmed(value, "comparisonMethod"));
            } else {
                checkUnknownAttribute(parser, name, tagName, strict);
            }
        }
        Set parsed = new HashSet();
        while ((strict ? parser.nextTag() : nextTag(parser)) == XmlPullParser.START_TAG) {
            if (checkFieldWithDuplicate(parser, "ignoreVersions", null, parsed)) {
                List<IgnoreVersion> ignoreVersions = new ArrayList<>();
                rule.setIgnoreVersions(ignoreVersions);
                while (parser.nextTag() == XmlPullParser.START_TAG) {
                    if ("ignoreVersion".equals(parser.getName())) {
                        ignoreVersions.add(parseIgnoreVersion(parser, strict));
                    } else {
                        checkUnknownElement(parser, strict);
                    }
                }
            } else {
                checkUnknownElement(parser, strict);
            }
        }
        return rule;
    }

    private RuleSet parseRuleSet(XmlPullParser parser, boolean strict)
            throws IOException, XmlPullParserException {
        String tagName = parser.getName();
        RuleSet ruleSet = new RuleSet();
        for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
            String name = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);

            if (name.indexOf(':') >= 0) {
                // just ignore attributes with non-default namespace (for example: xmlns:xsi)
            } else if ("xmlns".equals(name)) {
                // ignore xmlns attribute in root class, which is a reserved attribute name
            } else if ("comparisonMethod".equals(name)) {
                ruleSet.setComparisonMethod(interpolatedTrimmed(value, "comparisonMethod"));
            } else {
                checkUnknownAttribute(parser, name, tagName, strict);
            }
        }
        Set parsed = new HashSet();
        while ((strict ? parser.nextTag() : nextTag(parser)) == XmlPullParser.START_TAG) {
            if (checkFieldWithDuplicate(parser, "ignoreVersions", null, parsed)) {
                List<IgnoreVersion> ignoreVersions = new ArrayList<>();
                ruleSet.setIgnoreVersions(ignoreVersions);
                while (parser.nextTag() == XmlPullParser.START_TAG) {
                    if ("ignoreVersion".equals(parser.getName())) {
                        ignoreVersions.add(parseIgnoreVersion(parser, strict));
                    } else {
                        checkUnknownElement(parser, strict);
                    }
                }
            } else if (checkFieldWithDuplicate(parser, "rules", null, parsed)) {
                List<Rule> rules = new java.util.ArrayList<>();
                ruleSet.setRules(rules);
                while (parser.nextTag() == XmlPullParser.START_TAG) {
                    if ("rule".equals(parser.getName())) {
                        rules.add(parseRule(parser, strict));
                    } else {
                        checkUnknownElement(parser, strict);
                    }
                }
            } else {
                checkUnknownElement(parser, strict);
            }
        }
        return ruleSet;
    }

    private RuleSet read(XmlPullParser parser, boolean strict)
            throws IOException, XmlPullParserException {
        RuleSet ruleSet = null;
        int eventType = parser.getEventType();
        boolean parsed = false;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (strict && !"ruleset".equals(parser.getName())) {
                    throw new XmlPullParserException("Expected root element 'ruleset' but found '" + parser.getName() + "'", parser, null);
                } else if (parsed) {
                    // fallback, already expected a XmlPullParserException due to invalid XML
                    throw new XmlPullParserException("Duplicated tag: 'ruleset'", parser, null);
                }
                ruleSet = parseRuleSet(parser, strict);
                ruleSet.setModelEncoding(parser.getInputEncoding());
                parsed = true;
            }
            eventType = parser.next();
        }
        if (parsed) {
            return ruleSet;
        }
        throw new XmlPullParserException("Expected root element 'ruleset' but found no element at all: invalid XML document", parser, null);
    }

    public interface ContentTransformer {
        /**
         * Interpolate the value read from the xpp3 document
         *
         * @param source    The source value
         * @param fieldName A description of the field being interpolated. The implementation may use this to
         *                  log stuff.
         * @return The interpolated value.
         */
        String transform(String source, String fieldName);
    }
}