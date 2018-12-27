import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.kbase.jdbc.ResultSetImpl;
import com.kbase.jdbc.StatementImpl;

public class SetBlobDataTest {
    public static final String url = "jdbc:kbase://192.168.25.210";//kbase服务器地址
    public static final String driver = "com.kbase.jdbc.Driver";
    public static final String username = "DBOWN";
    public static final String password = "";
    public static void main(String[] args) throws Exception {
        Connection conn = null;
        StatementImpl stmt =null;
        ResultSetImpl rs = null;
        conn = SetBlobDataTest.getConn();
        String sql = "SELECT * FROM UU_STREAM  WHERE id = 1";
        stmt = (StatementImpl) conn.createStatement();
        rs=(ResultSetImpl) stmt.executeQuery(sql,true);

        //updateBlobBinary测试
        while (rs.next()) {
            byte[] bytes = image2Bytes("D:/test.jpeg");
            /**
             * 修改字段值， 设置或追加超大字段内容 主要用于设置FIELD_DOB[数字对象字段]的字段值，注意，此调用一般用于设置FIELD_DOB的值，
             * 如果用于设置其它字段的值，则此时nType必须为0，只能覆盖式写入,否则此调用总是失败 注：参数 rec
             * 没有实际意义，保留它是为了保持和旧版接口一致，实际上，它被忽略。记录的位置实际 由 hSet 信息决定。这样保持与 Move 方法接口一致。
             * @param hcol
             *            列句柄
             * @param nType
             *            添加数据方法： 0 覆盖写入； 1 表示在原字段内容后上追加
             * @param buf
             *            数据缓冲
             * @param bytes
             *            缓冲大小
             *
             * @return 用一整数表明操作成功与否的状态
             * @return >=0 实际字段大小
             * @return <0 错误码
             * @throws SQLException
             * public int updateBlobBinary(int columnIndex, int nType, byte[] bytes, int bufLen)
             */
            int result = rs.updateBlobBinary(2, 0,  bytes, bytes.length +1);
            System.out.println(result);
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

    private static byte[] image2Bytes(String imgSrc) throws Exception
    {
        FileInputStream fin = new FileInputStream(new File(imgSrc));
        //可能溢出,简单起见就不考虑太多,如果太大就要另外想办法，比如一次传入固定长度byte[]
        byte[] bytes  = new byte[fin.available()];
        //将文件内容写入字节数组，提供测试的case
        fin.read(bytes);

        fin.close();
        return bytes;
    }
}
