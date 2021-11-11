package neu.lab.dependency.vo;

/**
 * @author SUNJUNYAN
 */
public class ExcelDataVO {

    private String projName;
    private int moduleNum;
    private int depNum;
    private int conflictNum;
    private int inheritDepth;
    private int conflictDepth;
    private int reduceNum;
    private int usefulNum;
    private int unusefulNum;
    private int dupModuleNum;
    private int dupDepNum;
    private long serialTime;
    private long beforeTime;
    private long afterTime;

    public ExcelDataVO(String projName, int moduleNum, int depNum, int conflictNum, int inheritDepth, int conflictDepth) {
        this.projName = projName;
        this.moduleNum = moduleNum;
        this.depNum = depNum;
        this.conflictNum = conflictNum;
        this.inheritDepth = inheritDepth;
        this.conflictDepth = conflictDepth;
    }

    public ExcelDataVO(String projName, int moduleNum, int reduceNum, long serialTime, long beforeTime, long afterTime) {
        this.projName = projName;
        this.moduleNum = moduleNum;
        this.reduceNum = reduceNum;
        this.serialTime = serialTime;
        this.beforeTime = beforeTime;
        this.afterTime = afterTime;
    }

    public ExcelDataVO(String projName, int moduleNum, int reduceNum, int usefulNum, int unusefulNum) {
        this.projName = projName;
        this.moduleNum = moduleNum;
        this.reduceNum = reduceNum;
        this.usefulNum = usefulNum;
        this.unusefulNum = unusefulNum;
    }

    public ExcelDataVO(String projName, int moduleNum, int dupModuleNum, int dupDepNum) {
        this.projName = projName;
        this.moduleNum = moduleNum;
        this.dupModuleNum = dupModuleNum;
        this.dupDepNum = dupDepNum;
    }

    public String getProjName() {
        return projName;
    }

    public int getModuleNum() {
        return moduleNum;
    }

    public int getDepNum() {
        return depNum;
    }

    public int getConflictNum() {
        return conflictNum;
    }

    public int getInheritDepth() {
        return inheritDepth;
    }

    public int getConflictDepth() {
        return conflictDepth;
    }

    public int getReduceNum() {
        return reduceNum;
    }

    public int getUsefulNum() {
        return usefulNum;
    }

    public int getUnusefulNum() {
        return unusefulNum;
    }

    public int getDupModuleNum() {
        return dupModuleNum;
    }

    public int getDupDepNum() {
        return dupDepNum;
    }

    public long getSerialTime() {
        return serialTime;
    }

    public long getBeforeTime() {
        return beforeTime;
    }

    public long getAfterTime() {
        return afterTime;
    }

    @Override
    public String toString() {
        return "ExcelDataVO{" +
                "projName='" + projName + '\'' +
                ", moduleNum=" + moduleNum +
                ", depNum=" + depNum +
                ", conflictNum=" + conflictNum +
                ", inheritDepth=" + inheritDepth +
                '}';
    }
}
