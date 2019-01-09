import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.kbase.jdbc.Blob;
import com.kbase.jdbc.ResultSetImpl;
import com.kbase.jdbc.StatementImpl;

public class GetBlobDataTest {
    public static final String url = "jdbc:kbase://192.168.25.210";//kbase服务器地址
    public static final String driver = "com.kbase.jdbc.Driver";
    public static final String username = "DBOWN";
    public static final String password = "";
    public static void main(String[] args) throws Exception {
        Connection conn = null;
        StatementImpl stmt =null;
        ResultSetImpl rs = null;
        conn = GetBlobDataTest.getConn();
        String sql = "SELECT * FROM UU_STREAM  WHERE pid = 5";
        stmt = (StatementImpl) conn.createStatement();
        rs=(ResultSetImpl) stmt.executeQuery(sql,true);
        int i=0;
        while (rs.next()) {
            String id = rs.getString(0);
            String fulltext = rs.getString(1);
            /**
             * 分段获得数据 按偏移获取大字段的字段值,每次只取从offset位置开始长度bufLen的部分数据 这里大字段是指最大长度超过 32K
             * 字节的字段，如 FIELD_TEXT,FIELD_TEXTCHAR,FIELD_LTEXT 因为返回的 buf
             * 最后会补'\0'，所以如果期望读取的数据长度是 bufLen，用户给 buf 分配的 长度至少为 bufLen+1，
             * 否则可能会发生不可预料的错误！！！ 对于小字段，此调用将忽略offset, 总是认为offset==0, 从记录的开始位置取值
             *
             * 注：参数 rec 没有实际意义，保留它是为了保持和旧版接口一致，实际上，它被忽略。记录的位置实际 由 hSet 信息决定。这样保持与 Move
             * 方法接口一致。
             *
             * @param columnIndex
             *            列句柄
             * @param offset
             *            偏移量，（小字段，此参数被忽略）
             * @param bufLen
             *            缓冲大小(注意必须是bufLen+1)
             *
             * @return TPI_RETURN_RESULT
             *         iResult属性：保存函数执行的错误码，若函数执行成功，返回大于等于0，否则小于0；rtnBuf属性：存放错误信息
             * @return >=0 实际获得数据多少
             * @return <0 错误码
             *
             * Blob getBlobBinary(int columnIndex, int offset, int bufLen) throws SQLException {
             **/
            Blob myBlob = (Blob) rs.getBlobBinary(2,0,1024*1024*2);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("E:/testtes2t"+i+".jpeg"));
            InputStream in = myBlob.getBinaryStream();
            byte[] b = new byte[1024];
            int len;
            while((len = in.read(b)) != -1){
                bos.write(b,0, len);
                bos.flush();
            }
            bos.close();
            in.close();
            System.out.println("id="+id+",fulltext:"+fulltext);
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
