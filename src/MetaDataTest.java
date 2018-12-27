import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.kbase.jdbc.ConnectionImpl;
import com.kbase.jdbc.ResultSetImpl;
import com.kbase.jdbc.StatementImpl;

import kbase.KBaseClient;
import kbase.struct.HS_TABLE_FIELD;
import kbase.struct.TPI_RETURN_RESULT;

public class MetaDataTest {
    public static final String url = "jdbc:kbase://192.168.25.210";//kbase服务器地址
    public static final String driver = "com.kbase.jdbc.Driver";
    public static final String username = "DBOWN";
    public static final String password = "";
    public static void main(String[] args) throws Exception {
        Connection conn = null;
        StatementImpl stmt =null;
        ResultSetImpl rs = null;
        conn = MetaDataTest.getConn();
        String sql = "SELECT * FROM UU_STREAM";
        stmt = (StatementImpl) conn.createStatement();
        rs=(ResultSetImpl) stmt.executeQuery(sql,true);
        com.kbase.jdbc.ResultSetMetaData data=(com.kbase.jdbc.ResultSetMetaData) rs.getMetaData();
        // STEP 5: Extract data from result set
        while (rs.next()) {
            System.out.println("---------------------------");
            //获得所有列的数目及实际列数
            int columnCount=data.getColumnCount();
            System.out.println(columnCount);
            for(int i = 0 ; i< data.getColumnCount() ; i++){
                //获得指定列的列名
                String columnName = data.getColumnName(i);
                //获得指定列的列值
                //String columnValue = rs.getString(i);
                //获得指定列的数据类型名
                String columnTypeName=data.getColumnTypeName(i);
                //System.out.println("获得列"+i+"的字段名称:"+columnName+",字段值:"+columnValue+",数据类型:"+columnTypeName+"字段长度："+data.getColumnLen(i));
                System.out.println("获得列"+i+"的字段名称:"+columnName+",数据类型:"+columnTypeName+"字段长度："+data.getColumnLen(i));

            }
        }

    }

    private static Connection getConn() {
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
