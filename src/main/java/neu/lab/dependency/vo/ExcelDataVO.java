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

    public ExcelDataVO(String projName, int moduleNum, int depNum, int conflictNum, int inheritDepth, int conflictDepth) {
        this.projName = projName;
        this.moduleNum = moduleNum;
        this.depNum = depNum;
        this.conflictNum = conflictNum;
        this.inheritDepth = inheritDepth;
        this.conflictDepth = conflictDepth;
    }

    public ExcelDataVO(String projName, int moduleNum, int reduceNum, int usefulNum, int unusefulNum) {
        this.projName = projName;
        this.moduleNum = moduleNum;
        this.reduceNum = reduceNum;
        this.usefulNum = usefulNum;
        this.unusefulNum = unusefulNum;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public int getModuleNum() {
        return moduleNum;
    }

    public void setModuleNum(int moduleNum) {
        this.moduleNum = moduleNum;
    }

    public int getDepNum() {
        return depNum;
    }

    public void setDepNum(int depNum) {
        this.depNum = depNum;
    }

    public int getConflictNum() {
        return conflictNum;
    }

    public void setConflictNum(int conflictNum) {
        this.conflictNum = conflictNum;
    }

    public int getInheritDepth() {
        return inheritDepth;
    }

    public void setInheritDepth(int inheritDepth) {
        this.inheritDepth = inheritDepth;
    }

    public int getConflictDepth() {
        return conflictDepth;
    }

    public void setConflictDepth(int conflictDepth) {
        this.conflictDepth = conflictDepth;
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

    public void setReduceNum(int reduceNum) {
        this.reduceNum = reduceNum;
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
