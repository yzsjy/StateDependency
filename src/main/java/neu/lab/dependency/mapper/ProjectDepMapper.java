package neu.lab.dependency.mapper;

import neu.lab.dependency.pojo.DepDetail;

/**
 * @author SUNJUNYAN
 */
public interface ProjectDepMapper {

    DepDetail selectProject(String groupId, String artifactId, String version);

    void insertProject(DepDetail depDetail);
}
