package neu.lab.dependency.smell;

import neu.lab.dependency.container.Conflicts;
import neu.lab.dependency.container.Poms;
import neu.lab.dependency.pom.ModuleRelation;
import neu.lab.dependency.pom.PomParser;
import neu.lab.dependency.util.Conf;
import neu.lab.dependency.vo.Conflict;
import neu.lab.dependency.vo.ExcelDataVO;
import neu.lab.dependency.vo.Pom;
import neu.lab.dependency.writer.ExcelWriter;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author SUNJUNYAN
 */
public class VersionCheck {

    private String projPath;
    public static String separator = File.separator.equals("/") ? "/" : "\\\\";

    public VersionCheck(String projPath) {
        this.projPath = projPath;
    }

    public void init() {
        PomParser.init(projPath);
        ModuleRelation.i().generateGraph();
        detectConflict();
    }

    public void detectConflict() {
        Conflicts.init();
        if (Conflicts.i().getConflicts().size() == 0) {
            return;
        }
        generateGraph();
        printRisk();
        writeToExcelFile();
    }


    public void generateGraph() {
        String[] splits = projPath.split(separator);
        Conflicts.i().generateGraphs(splits[splits.length - 1]);
    }

    public void printRisk() {
        String[] splits = projPath.split(separator);
        String projName = splits[splits.length - 1];
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(projPath + "\n\n");
        for (Conflict conflict : Conflicts.i().getConflicts()) {
            stringBuffer.append(conflict.getSig() + "\n");
            Map<String, List<Pom>> versionToModule = conflict.getVersionToModule();
            for (Map.Entry<String, List<Pom>> entry : versionToModule.entrySet()) {
                stringBuffer.append("Version: " + entry.getKey() + "\n");
                List<Pom> poms = entry.getValue();
                for (Pom pom : poms) {
                    stringBuffer.append(pom.getSig() + "\n");
                }
                stringBuffer.append("\n");
            }
//            stringBuffer.append(conflict.getSig() + " safe version : " + conflict.getSafeVersion() + "\n");
            stringBuffer.append("\n\n");
        }
        stringBuffer.append("\n\n");
        try {
            if (!new File(Conf.Dir).exists()) {
                new File(Conf.Dir).mkdirs();
            }
            String outFile = Conf.Dir + "conflict" + File.separator + projName + ".txt";
            File file = new File(outFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            printer.println(stringBuffer);
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToExcelFile() {
        String[] splits = projPath.split(separator);
        String projName = splits[splits.length - 1];
        int depNum = getDepNum();
        int conflictNum = Conflicts.i().getConflicts().size();
        int moduleNum = Poms.i().getModules().size();
        int inheritDepth = getInheritDepth();
        int conflictDepth = getConflictDepth();
        ExcelDataVO data = new ExcelDataVO(projName, moduleNum, depNum, conflictNum, inheritDepth, conflictDepth);
        String filePath = Conf.Dir + "Data.xlsx";
        File file = new File(filePath);
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                Workbook workbook = ExcelWriter.getWorkBook(inputStream);
                FileOutputStream outputStream = new FileOutputStream(file);
                ExcelWriter.insertData(data, workbook);
                workbook.write(outputStream);
                outputStream.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                file.createNewFile();
                Workbook workbook = ExcelWriter.exportData(data);
                FileOutputStream fileOut = new FileOutputStream(filePath);
                workbook.write(fileOut);
                fileOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getDepNum() {
        Set<String> dep = new HashSet<>();
        for (Pom pom : Poms.i().getPoms()) {
            dep.addAll(pom.getDependencies());
        }
        return dep.size();
    }

    public int getInheritDepth() {
        int depth = 0;
        for (Pom pom : Poms.i().getPoms()) {
            int temp = 1;
            Pom parent = pom.getParent();
            while (parent != null) {
                temp++;
                parent = parent.getParent();
            }
            depth = Math.max(depth, temp);
        }
        return depth;
    }

    public int getConflictDepth() {
        int depth = 0;
        Set<String> visited = new HashSet<>();
        for (Conflict conflict : Conflicts.i().getConflicts()) {
            for (Pom pom : conflict.getModules()) {
                if (visited.contains(pom.getSig())) {
                    continue;
                }
                int temp = 1;
                Pom parent = pom.getParent();
                while (parent != null) {
                    temp++;
                    parent = parent.getParent();
                }
                depth = Math.max(depth, temp);
                visited.add(pom.getSig());
            }
        }
        return depth;
    }
}
