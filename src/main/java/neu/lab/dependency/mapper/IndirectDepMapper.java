package neu.lab.dependency.mapper;

import neu.lab.dependency.pojo.IndirectDep;

/**
 * @author SUNJUNYAN
 */
public interface IndirectDepMapper {
    IndirectDep selectDepJar(String groupId, String artifactId);

    void insertDepJar(IndirectDep indirectDep);

    void updateDepJar(IndirectDep indirectDep);
}
