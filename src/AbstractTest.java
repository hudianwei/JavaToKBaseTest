import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import kbase.struct.KSTM_ABSTRACT_EXTRACTOR;
import kbase.struct.TPI_RETURN_RESULT;

import com.kbase.jdbc.ConnectionImpl;

public class AbstractTest {
    public static final String url = "jdbc:kbase://192.168.103.106";// kbase服务器地址
    public static final String driver = "com.kbase.jdbc.Driver";
    public static final String username = "DBOWN";
    public static final String password = "";

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ConnectionImpl conn = (ConnectionImpl) AbstractTest.getConn();
        String text = "性对水文的作用 气候变化对水文的作用, 在特定下垫面, 主要通过降水、气温、 蒸发等气象因素起作用。表现在对径流的作用, 包括年、月, 洪峰流量在时间与空间的";

		/*KSTM_ABSTRACT_EXTRACTOR kae = new KSTM_ABSTRACT_EXTRACTOR();
        kae.nSentenceCount = 5;
        kae.bUsePercentage = false;*/
        TPI_RETURN_RESULT result = conn.KBase_STM_AbstractExtractor_Do(0, text,
                null, null, 0);
        System.out.println(result.rtnBuf);
    }

    private static Connection getConn() {
        Connection conn = null;
        try {
            Class.forName(driver); // classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username,
                    password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
