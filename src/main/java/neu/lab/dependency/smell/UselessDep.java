package neu.lab.dependency.smell;

import neu.lab.dependency.container.Poms;
import neu.lab.dependency.pom.*;
import neu.lab.dependency.util.Conf;
import neu.lab.dependency.util.PomOperation;
import neu.lab.dependency.vo.ExcelDataVO;
import neu.lab.dependency.writer.ReduceExcelWriter;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;

public class UselessDep {

    private String projPath;
    public static String separator = File.separator.equals("/") ? "/" : "\\\\";

    public UselessDep(String projPath) {
        this.projPath = projPath;
    }

    public void init() {
        PomParser.init(projPath);
        ModuleRelation.i().generateGraph();
        reduceModule();
    }

    public void reduceModule() {
        String path = projPath + "pom.xml";
        PomOperation.i().mvnClean(path);
        long preTime = PomOperation.i().mvnSerialBuildTime(path);
        PomOperation.i().mvnClean(path);
        long beforeTime = 0;
        long afterTime = 0;
        beforeTime = PomOperation.i().mvnParallelBuildTime(path, 4);
        String[] splits = projPath.split(separator);
        DetectUselessDep.i().reduceDep();
        DetectUselessDep.i().relationReduce();
        DetectUselessDep.i().generateGraph(splits[splits.length - 1]);
        PomOperation.i().mvnClean(path);
        afterTime = PomOperation.i().mvnParallelBuildTime(path, 4);
        RecoverPom rp = new RecoverPom(projPath);
        rp.recoverPom();
        writeReduceToExcelFile(preTime, beforeTime, afterTime);
    }

    public void writeReduceToExcelFile(long serialTime, long beforeTime, long afterTime) {
        String[] splits = projPath.split(separator);
        String projName = splits[splits.length - 1];
        int moduleNum = Poms.i().getModules().size();
        int reduceNum = DetectUselessDep.i().getReduceEdges().size();
        ExcelDataVO data = new ExcelDataVO(projName, moduleNum, reduceNum, serialTime, beforeTime, afterTime);
        String filePath = Conf.Dir + "UselessDep.xlsx";
        File file = new File(filePath);
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                Workbook workbook = ReduceExcelWriter.getWorkBook(inputStream);
                FileOutputStream outputStream = new FileOutputStream(file);
                ReduceExcelWriter.insertData(data, workbook);
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
                Workbook workbook = ReduceExcelWriter.exportData(data);
                FileOutputStream fileOut = new FileOutputStream(filePath);
                workbook.write(fileOut);
                fileOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
