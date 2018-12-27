import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import kbase.struct.KSTM_TEXTWEIGHT;
import kbase.struct.KSTM_VSM_GENERATOR;
import kbase.struct.TPI_RETURN_RESULT;

import com.kbase.jdbc.ConnectionImpl;

public class VsmTest {
    public static final String url = "jdbc:kbase://192.168.103.106";// kbase服务器地址
    public static final String driver = "com.kbase.jdbc.Driver";
    public static final String username = "DBOWN";
    public static final String password = "";

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ConnectionImpl conn = (ConnectionImpl) VsmTest.getConn();
        KSTM_VSM_GENERATOR param = new KSTM_VSM_GENERATOR();
        param.bOutputWord = true;
        param.nMaxFeatureNum = 10;
        param.nSortItem = 1;

        String domainName = "CJFD";
        // conn.KBase_STM_ShutDown();关闭知识域
        // int hDomain = conn.KBase_STM_OpenDomain(domainName);启动知识域
        // int vsmInit = conn.KBase_STM_VSMGenerator_Init(hDomain);初始化知识域，一般没有
        KSTM_TEXTWEIGHT[] weight = GetKSTM_TEXTWEIGHT();
        TPI_RETURN_RESULT result =  conn
                .KBase_STM_VSMGenerator_Do(0, weight, weight.length, param);

        System.out.println(result.rtnBuf);
    }

    private static KSTM_TEXTWEIGHT[] GetKSTM_TEXTWEIGHT() {
        // TODO Auto-generated method stub
        KSTM_TEXTWEIGHT[] weightList = new KSTM_TEXTWEIGHT[1];

        KSTM_TEXTWEIGHT weight = new KSTM_TEXTWEIGHT();
        weight.pText = "知识是人类进步的阶梯";
        weight.nWeight = 5;
        weightList[0] = weight;
        return weightList;
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
