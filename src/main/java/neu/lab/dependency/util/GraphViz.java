package neu.lab.dependency.util;

import java.io.*;

public class GraphViz {

    private static String TEMP_DIR = "E:\\Graph";
    private static String DOT = "D:\\Program Files\\Graphviz\\bin\\dot.exe";

    private StringBuilder graph = new StringBuilder();

    public GraphViz() {

    }

    public String getDotSource() {
        return graph.toString();
    }

    public void add(String line) {
        graph.append(line);
    }

    public void addln(String line) {
        graph.append(line + "\n");
    }

    public void addLn() {
        graph.append('\n');
    }

    public byte[] getGraph(String dotSource, String type) {
        File dot;
        byte[] imgStream = null;

        try {
            dot = writeDotSourceToFile(dotSource);
            if (dot != null) {
                imgStream = getImgStream(dot, type);
                if (dot.delete() == false) {
                    System.err.println("Warning: " + dot.getAbsolutePath() + " could not be deleted!");
                }
                return imgStream;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public int writeGraphToFile(byte[] img, String file) {
        File to = new File(file);
        return writeGraphToFile(img, to);
    }

    public int writeGraphToFile(byte[] img, File to) {
        try {
            FileOutputStream fos = new FileOutputStream(to);
            fos.write(img);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    private byte[] getImgStream(File dot, String type) {
        File img;
        byte[] imgStream = null;

        try {
            img = File.createTempFile("graph_", "." + type, new File(TEMP_DIR));
            Runtime rt = Runtime.getRuntime();

            String[] args = {DOT, "-T" + type, dot.getAbsolutePath(), "-o", img.getAbsolutePath()};
            Process p = rt.exec(args);

            p.waitFor();

            FileInputStream in = new FileInputStream(img.getAbsolutePath());
            imgStream = new byte[in.available()];
            in.read(imgStream);
            if (in != null) {
                in.close();
            }
            if (img.delete() == false) {
                System.err.println("Warning: " + img.getAbsolutePath() + " could not be deleted!");
            }
        } catch (IOException e) {
            System.err.println("Error:    in I/O processing of tempfile in dir " + TEMP_DIR + "\n");
            System.err.println("       or in calling external command");
            e.printStackTrace();
        } catch (java.lang.InterruptedException e) {
            System.err.println("Error: the execution of the external program was interrupted");
            e.printStackTrace();
        }
        return imgStream;
    }

    public File writeDotSourceToFile(String str) throws IOException {
        File temp;
        try {
            temp = File.createTempFile("graph_", ".dot.tmp", new File(TEMP_DIR));
            FileWriter fout = new FileWriter(temp);
            fout.write(str);
            fout.close();
        } catch (Exception e) {
            System.err.println("Error: I/O error while writing the dot source to temp file!");
            return null;
        }
        return temp;
    }

    public String startGraph() {
        return "digraph G {";
    }

    public String endGraph() {
        return "}";
    }

    public void readSource(String input) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(input);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            dis.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        this.graph = stringBuilder;
    }
}
