package neu.lab.dependency.smell;

import neu.lab.dependency.pom.RecoverPom;
import neu.lab.dependency.container.Poms;
import neu.lab.dependency.pom.ModuleReduce;
import neu.lab.dependency.pom.ModuleRelation;
import neu.lab.dependency.pom.PomParser;
import neu.lab.dependency.util.Conf;
import neu.lab.dependency.util.PomOperation;
import neu.lab.dependency.vo.ExcelDataVO;
import neu.lab.dependency.writer.ReduceExcelWriter;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;

public class BuildOptimize {

    private String projPath;
    public static String separator = File.separator.equals("/") ? "/" : "\\\\";

    public BuildOptimize(String projPath) {
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
        String[] splits = projPath.split(separator);
        ModuleReduce.i().reduceDep();
        ModuleReduce.i().relationReduce();
        ModuleReduce.i().generateGraph(splits[splits.length - 1]);
        afterTime = PomOperation.i().mvnParallelBuildTime(path);
        RecoverPom rp = new RecoverPom(projPath);
        rp.recoverPom();
        PomOperation.i().mvnClean(path);
        beforeTime = PomOperation.i().mvnParallelBuildTime(path);
        writeReduceToExcelFile(preTime, beforeTime, afterTime);
    }

    public void writeReduceToExcelFile(long preTime, long beforeTime, long afterTime) {
        String[] splits = projPath.split(separator);
        String projName = splits[splits.length - 1];
        int moduleNum = Poms.i().getModules().size();
        int reduceNum = ModuleReduce.i().getReduceEdges().size();
        String success = preTime == -1 ? "failed" : "success";
        ExcelDataVO data = new ExcelDataVO(projName, moduleNum, reduceNum, success, beforeTime, afterTime);
        String filePath = Conf.Dir + "ReduceData.xlsx";
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
