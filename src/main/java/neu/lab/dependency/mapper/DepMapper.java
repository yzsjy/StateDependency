package neu.lab.dependency.mapper;

import neu.lab.dependency.pojo.Dep;

/**
 * @author SUNJUNYAN
 */
public interface DepMapper {

    Dep selectDepJar(String groupId, String artifactId);

    void insertDepJar(Dep dep);

    void updateDepJar(Dep dep);

    Dep getDepJar(String groupId, String artifactId);
}
