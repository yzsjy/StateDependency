package neu.lab.dependency;

import neu.lab.dependency.mapper.DepMapper;
import neu.lab.dependency.pojo.Dep;
import neu.lab.dependency.util.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

/**
 * @author SUNJUNYAN
 */
public class RunTest {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(RunTest.class);
        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionFactoryUtils.openSqlSession();
            DepMapper depMapper = sqlSession.getMapper(DepMapper.class);
            Dep dep = depMapper.getDepJar("com.google.guava", "guava");
            logger.info(dep.getGroupId());
        } finally {
            SqlSessionFactoryUtils.closeSqlSession(sqlSession);
        }
    }
}
