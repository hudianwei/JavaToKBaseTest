
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import kbase.struct.KSTM_TEXTWEIGHT;
import kbase.struct.KSTM_VSM_GENERATOR;
import kbase.struct.TPI_RETURN_RESULT;

import com.kbase.jdbc.ConnectionImpl;

public class SmartReminder {
    public static final String url = "jdbc:kbase://192.168.25.210";// kbase服务器地址
    public static final String driver = "com.kbase.jdbc.Driver";
    public static final String username = "DBOWN";
    public static final String password = "";

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ConnectionImpl conn = (ConnectionImpl) SmartReminder.getConn();
        TPI_RETURN_RESULT result = conn.KBase_TOP_GetItem("test_dic", "农民");
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
