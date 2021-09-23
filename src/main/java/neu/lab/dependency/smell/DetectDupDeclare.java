package neu.lab.dependency.smell;

import neu.lab.dependency.container.Poms;
import neu.lab.dependency.pom.ModuleRelation;
import neu.lab.dependency.pom.PomParser;
import neu.lab.dependency.util.Conf;
import neu.lab.dependency.vo.DepInfo;
import neu.lab.dependency.vo.ExcelDataVO;
import neu.lab.dependency.vo.Pom;
import neu.lab.dependency.writer.DupDeclareExcelWriter;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.*;

public class DetectDupDeclare {
    private String projPath;
    Map<String, Set<String>> infos = new HashMap<>();

    public DetectDupDeclare(String projPath) {
        this.projPath = projPath;
    }

    public void init() {
        PomParser.init(projPath);
//        ModuleRelation.i().generateGraph();
        System.out.println(Poms.i().getPoms().size());

//        detect();
//        if (!infos.isEmpty()) {
//            writeToExcel();
//            writeToText();
//        }
    }

    public void detect() {
        PomParser.init(projPath);
        Set<Pom> poms = Poms.i().getPoms();
        for (Pom pom : poms) {
            String path = pom.getFilePath();
            List<String> dependencies = new ArrayList<>();
            Set<String> dup = new HashSet<>();
            for (DepInfo dep : pom.getOwnDependencies()) {
                String info = dep.getGroupId() + ":" + dep.getArtifactId();
                if (dependencies.contains(info)) {
                    dup.add(info);
                } else {
                    dependencies.add(info);
                }
            }
            if (!dup.isEmpty()) {
                System.out.println("Dup module : " + path);
                System.out.println("Dup dependency size : " + dup.size());
                infos.put(path, dup);
            } else {
                System.out.println(path + " has no dup dependency");
            }
        }
        System.out.println();
    }

    public void writeToExcel() {
        String[] splits = projPath.split("\\\\");
        String projName = splits[splits.length - 1];
        int moduleNum = Poms.i().getModules().size();
        int dupModuleNum = infos.keySet().size();
        int dupDepNum = getDupDepNum();
        ExcelDataVO data = new ExcelDataVO(projName, moduleNum, dupModuleNum, dupDepNum);
        String filePath = Conf.Dir + "DupData.xlsx";
        File file = new File(filePath);
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                Workbook workbook = DupDeclareExcelWriter.getWorkBook(inputStream);
                FileOutputStream outputStream = new FileOutputStream(file);
                DupDeclareExcelWriter.insertData(data, workbook);
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
                Workbook workbook = DupDeclareExcelWriter.exportData(data);
                FileOutputStream fileOut = new FileOutputStream(filePath);
                workbook.write(fileOut);
                fileOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeToText() {

        try {
            String txtPath = Conf.Dir + "DupDetect.txt";
            File file = new File(txtPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            printer.println(projPath);
            printer.println();
            for (Map.Entry<String, Set<String>> entry : infos.entrySet()) {
                printer.println(entry.getKey());
                printer.println("Dup Dependencies : ");
                for (String s : entry.getValue()) {
                    printer.println(s);
                }
                printer.println();
            }
            printer.println();
            printer.println();
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDupDepNum() {
        int count = 0;
        for (Map.Entry<String, Set<String>> entry : infos.entrySet()) {
            count += entry.getValue().size();
        }
        return count;
    }
}
