import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.kbase.jdbc.ResultSetImpl;
import com.kbase.jdbc.StatementImpl;

public class SetValueTest {
    public static final String url = "jdbc:kbase://192.168.25.210";//kbase服务器地址
    public static final String driver = "com.kbase.jdbc.Driver";
    public static final String username = "DBOWN";
    public static final String password = "";
    public static void main(String[] args) throws Exception {
        Connection conn = null;
        StatementImpl stmt =null;
        ResultSetImpl rs = null;
        conn = SetValueTest.getConn();
        String sql = "SELECT * FROM UU_STREAM  WHERE id = 2";
        stmt = (StatementImpl) conn.createStatement();
        rs=(ResultSetImpl) stmt.executeQuery(sql,false);
        //修改
        while (rs.next()) {
            System.out.println(rs.getString(1));
            /**
             * 设置为编辑记录模式
             *
             * @return 若函数执行成功，返回大于等于0，否则小于0
             **/
            int resultEdit = rs.KBaseEdit();
            /**
             * 设置字段的值(通过字段序号) 在设置了新增或修改模式后才可调用，新设置的值直到成功调用Update后才效
             *
             * @param nColIndex
             *            字段序号
             * @param pValue
             *            字段值
             * @param buflen
             *            缓冲区大小
             * @return 若函数执行成功，返回大于等于0，否则小于0
             **/
            long result = rs.KBaseSetFieldValue(1, "你好，aaa我是修改记录的adadas施工方三房", "你好，aaa我是修改记录的adadas施工方三房".length());
            /**
             * 执行增加、修改记录的写记录操作
             *
             * @param bUnicode
             *            为true时做Unicode和UTF8和之间的相应转换，为false做Unicode和ANSI之间的相应转换，
             *            默认为false。
             *
             * @return 若函数执行成功，返回大于等于0，否则小于0 >=0 表示函数执行成功
             *         如果是在添加记录时，并且此记录中有AUTOID字段，则返回值与AUTO字段的内容一值；
             *         如果此记录中有AUTOID字段，则其表示的是表内部的记录号； 如果是在修改记录时,只是表示执行成功。 <0 表示函数执行失败
             *         如果返回值是TPI_ERR_TABLE_MODE，则可能是因没有调用TPI_AddNew或TPI_Edit 注意，
             *         TPI_Update 执行之后，hSet所指向的记录集会发生改变
             **/
            rs.KBaseUpdate(true);
        }
        //新增
		/*while (rs.next()) {
			System.out.println(rs.getString("ID")+ " " +rs.getString("FULLTEXT"));
			int resultAddNew = rs.KBaseAddNew();
			//long result = rs.KBaseSetFieldValue(1, "你好，aaa我是修改记录的adadas施工方三房", "你好，aaa我是修改记录的adadas施工方三房".length());
			long result0 = rs.KBaseSetFieldValue(0, "5", "5".length());
			long result1 = rs.KBaseSetFieldValue(1, "你好，aaa我是修改记录的adadas施工方三房", "你好，aaa我是修改记录的adadas施工方三房".length());
			rs.KBaseUpdate(true);
			//System.out.println(result);
			//System.out.println(rs.getString("FULLTEXT"));

			//System.out.println(result);
		}*/



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
