
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import kbase.struct.TPI_RETURN_RESULT;

import com.kbase.jdbc.ConnectionImpl;

public class GetDomainNameListTest {
    public static final String url = "jdbc:kbase://192.168.105.111";// kbase服务器地址
    public static final String driver = "com.kbase.jdbc.Driver";
    public static final String username = "DBOWN";
    public static final String password = "";

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ConnectionImpl conn = (ConnectionImpl) GetDomainNameListTest.getConn();
        TPI_RETURN_RESULT result = conn.KBase_STM_GetDomainNameList(0);
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
