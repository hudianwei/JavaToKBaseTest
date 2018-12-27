/**
 * @Description:
 * @author: HU
 * @date: 2018/12/17 9:30
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.kbase.jdbc.ResultSetImpl;
import com.kbase.jdbc.StatementImpl;

import kbase.struct.TPI_RETURN_RESULT;

public class FieldTestCustomize {
    public static final String url = "jdbc:kbase://192.168.105.71";// kbase服务器地址
    public static final String driver = "com.kbase.jdbc.Driver";
    public static final String username = "DBOWN";
    public static final String password = "";

    public static void main(String[] args) throws Exception {
        Connection conn = null;
        StatementImpl stmt = null;
        ResultSetImpl rs = null;
        ResultSetImpl rs2 = null;
        conn = FieldTestCustomize.getConn();
        stmt = (StatementImpl) conn.createStatement();
        /**
         * 1、获取所有字段信息
         */
        String sql = "SELECT  * FROM CCND0005 where  文件名='ABRB20050101T001'";
        // 是否是utf-8的查询
        rs = (ResultSetImpl) stmt.executeQuery(sql, false);
        // 从ResultSetImpl结果集中映射到ResultSetMetaData
        com.kbase.jdbc.ResultSetMetaData metaData = (com.kbase.jdbc.ResultSetMetaData) rs
                .getMetaData();
        // STEP 5: Extract data from result set
        System.out.println("---------------------------");
        // 获得所有列的数目及实际列数
        int columnCount = metaData.getColumnCount();
        System.out.println("字段数：" + columnCount);
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            // 获得指定列的列名
            String columnName = metaData.getColumnName(i);
            // 获得指定列的数据类型名
            String columnTypeName = metaData.getColumnTypeName(i);
            System.out.println("列" + i + "字段名称:" + columnName + ",数据类型:"
                    + columnTypeName + "字段长度：" + metaData.getColumnLen(i));

        }
        // 读值
		/*while (rs.next()) {
			for (int i = 0; i < columnCount; i++) {
				System.out.println("[" + i + "]列名：" + metaData.getColumnName(i)
						+ " --值数据：" + rs.getString(i));
			}

		}*/

        System.out.println("---------------------------");

        /**
         * 2、获取部分字段信息字段信息
         */
        String sql2 = "SELECT count(*) as temp,题名 as test FROM CCNDtotal,cmfdtoal where 文件名='ABRB20050101T001' ";
        rs2 = (ResultSetImpl) stmt.executeQuery(sql2, false);
        /**
         * KBaseGetRecordSetFieldName 得到记录集对应的全部字段名称
         *
         * @param hSet
         *            记录集句柄
         * @param nFlag
         *            标识，0-不包括别名；1-包括别名，以":"分隔，即“字段名:别名,...”
         *
         * @return TPI_RETURN_RESULT rtnBuf属性保存记录集对应的全部字段名
         **/
        int fieldCount = rs2.KBaseGetFieldCount();
        System.out.println("字段数：" + fieldCount);

        TPI_RETURN_RESULT fieldResult = rs2.KBaseGetRecordSetFieldName(0);
        String recordSetField = fieldResult.rtnBuf;
        // 获取列名的集合[test, 光盘号, 报纸中文名, 邮发号, 报纸拼音名, 年, 日期, 版号, 期号, 文章位置, 栏目, 文件名, 分类号, 引题, 正标题, 副标题, 作者, 全文, 更新日期, 旧机标关键词,...]
        String[] selectColumns = recordSetField.substring(0,
                recordSetField.length() - 1).split(",");
        System.out.println("字段名："
                + recordSetField.substring(0, recordSetField.length() - 1));

        while (rs2.next()) {
            for (int i = 0; i < fieldCount; i++) {
                System.out.println("[" + i + "]列名：[" + selectColumns[i]
                        + "] --值数据：" + rs2.getString(i));
            }

        }

        System.out.println("---------------------------");
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
