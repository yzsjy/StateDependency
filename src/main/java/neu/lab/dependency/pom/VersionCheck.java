package neu.lab.dependency.pom;

import neu.lab.dependency.container.Conflicts;
import neu.lab.dependency.container.Poms;
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

    public VersionCheck(String projPath) {
        this.projPath = projPath;
    }

    public void init() {
        Conflicts.init(projPath);
        generateGraph();
        printRisk();
        writeToExcelFile();
    }


    public void generateGraph() {
        String[] splits = projPath.split("\\\\");
        Conflicts.i().generateGraphs(splits[splits.length - 1]);
    }

    public void printRisk() {
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
            }
            stringBuffer.append(conflict.getSig() + " safe version : " + conflict.getSafeVersion() + "\n");
        }
        stringBuffer.append("\n\n");
        try {
            String outFile = Conf.Dir + "Conflicts.txt";
            File file = new File(outFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            printer.println(stringBuffer.toString());
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToExcelFile() {
        String[] splits = projPath.split("\\\\");
        String projName = splits[splits.length - 1];
        int depNum = getDepNum();
        int conflictNum = Conflicts.i().getConflicts().size();
        int moduleNum = Poms.i().getModules().size();
        int inheritDepth = getInheritDepth();
        ExcelDataVO data = new ExcelDataVO(projName, depNum, moduleNum, conflictNum, inheritDepth);
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


}
