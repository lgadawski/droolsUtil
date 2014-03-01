package test.com.gadawski.util.db.jdbc;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.gadawski.util.db.jdbc.JdbcManagerUtil;

/**
 * @author l.gadawski@gmail.com
 * 
 */
public class SaveLeftTuplePerformance {
    /**
     * 
     */
    private JdbcManagerUtil m_jdbcManager;

    @Before
    public void init() {
        m_jdbcManager = JdbcManagerUtil.getInstance();
    }

    @Test
    public void testSaveLT() {
        Integer parentId = null;
        Integer handleId = 0;
        Integer parentRightTupleId = null;
        int sinkId = 0;
        Object leftTuple = new BigDecimal(100);
        Object factHandle = new BigDecimal(111);
        for (int i = 0; i < 1000000; i++) {
            m_jdbcManager.saveLeftTuple(parentId, ++handleId,
                    parentRightTupleId, sinkId, leftTuple, factHandle);
        }
    }
}
