package neu.lab.dependency.mapper;

import neu.lab.dependency.pojo.IndirectDep;

/**
 * @author SUNJUNYAN
 */
public interface IndirectDepMapper {
    IndirectDep selectDepJar(String groupId, String artifactid);

    void insertDepJar(IndirectDep indirectDep);

    void updateDepJar(IndirectDep indirectDep);
}
