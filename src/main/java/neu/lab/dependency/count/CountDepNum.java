package neu.lab.dependency.count;

import neu.lab.dependency.container.NodeAdapters;
import neu.lab.dependency.mapper.DepMapper;
import neu.lab.dependency.mapper.IndirectDepMapper;
import neu.lab.dependency.mapper.ProjectDepMapper;
import neu.lab.dependency.pojo.Dep;
import neu.lab.dependency.pojo.DepDetail;
import neu.lab.dependency.pojo.IndirectDep;
import neu.lab.dependency.util.MavenUtil;
import neu.lab.dependency.util.SqlSessionFactoryUtils;
import neu.lab.dependency.vo.NodeAdapter;
import org.apache.ibatis.session.SqlSession;

/**
 * @author SUNJUNYAN
 */
public class CountDepNum {
    public void countDepNum() {
        int size = getNodeSize();
        int usedSize = getUsedNodeSize();
        String groupId = MavenUtil.i().getProjectGroupId();
        String artifactId = MavenUtil.i().getProjectArtifactId();
        String version = MavenUtil.i().getProjectVersion();
        String path = MavenUtil.i().getBaseDir().getAbsolutePath();
        SqlSession sqlSession = SqlSessionFactoryUtils.openSqlSession();
        DepMapper depMapper = sqlSession.getMapper(DepMapper.class);
        IndirectDepMapper indirectDepMapper = sqlSession.getMapper(IndirectDepMapper.class);
        ProjectDepMapper projectDepMapper = sqlSession.getMapper(ProjectDepMapper.class);
        DepDetail depDetail = projectDepMapper.selectProject(groupId, artifactId, version);
        if (depDetail == null) {
            projectDepMapper.insertProject(new DepDetail(groupId, artifactId, version, usedSize, size, path));
            for (NodeAdapter nodeAdapter : NodeAdapters.i().getAllNodeAdapter()) {
                if (nodeAdapter.isNodeSelected()) {
                    if (nodeAdapter.getNodeDepth() <= 2) {
                        Dep dep = depMapper.selectDepJar(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId());
                        if (dep == null) {
                            depMapper.insertDepJar(new Dep(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId(), 1));
                        } else {
                            depMapper.updateDepJar(new Dep(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId(), dep.getNum() + 1));
                        }
                    } else {
                        IndirectDep indirectDep = indirectDepMapper.selectDepJar(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId());
                        if (indirectDep == null) {
                            indirectDepMapper.insertDepJar(new IndirectDep(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId(), 1));
                        } else {
                            indirectDepMapper.updateDepJar(new IndirectDep(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId(), indirectDep.getNum() + 1));
                        }
                    }

                }
            }
        } else {
            MavenUtil.i().getLog().info("This project has been tested...");
        }
        sqlSession.commit();
        SqlSessionFactoryUtils.closeSqlSession(sqlSession);

    }

    public int getNodeSize() {
        return NodeAdapters.i().getAllNodeAdapter().size();
    }

    public int getUsedNodeSize() {
        int size = 0;
        for (NodeAdapter nodeAdapter : NodeAdapters.i().getAllNodeAdapter()) {
            if (nodeAdapter.isNodeSelected()) {
                size++;
            }
        }
        return size;
    }
}
