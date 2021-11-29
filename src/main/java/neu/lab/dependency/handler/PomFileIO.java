package neu.lab.dependency.handler;

import neu.lab.dependency.util.Conf;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.*;

/**
 * @author SUNJUNYAN
 */
public class PomFileIO {
    private static String errorpath = Conf.outDir + "Error.txt";

    private volatile static PomFileIO instance;

    public static PomFileIO i() {
        if (instance == null) {
            synchronized (PomFileIO.class) {
                if (instance == null) {
                    instance = new PomFileIO();
                }
            }
        }
        return instance;
    }

    private PomFileIO() {

    }

    public Model parsePomFileToModel(String pomPath) {
        MavenXpp3Reader reader = new MavenXpp3Reader();

        Model model = null;
        try {
            FileReader fileReader = new FileReader(pomPath);
            model = reader.read(fileReader);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return model;
    }

    public void writeModelToPomFile(File pomFile, Model model) {
        Writer writer = null;
        try {
            writer = new FileWriter(pomFile);
            MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();
            mavenXpp3Writer.write(writer, model);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(writer);
        }
    }
}
