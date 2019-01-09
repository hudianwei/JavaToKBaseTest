import java.sql.DriverManager;
import java.sql.SQLException;

import com.kbase.jdbc.ConnectionImpl;

import kbase.struct.KSTM_ABSTRACT_EXTRACTOR;
import kbase.struct.TPI_RETURN_RESULT;

/**
 * @Description:
 * @author: HU
 * @date: 2018/12/29 8:20
 */
public class ExtentionInterfaceTest {

    /**
     * top dict相关测试方法 1、KBase_Top_AddDict Top增加一个词典 2、KBase_TOP_GetItem
     * 得到top10列表 3、 KBase_TOP_List 列出目前可用的top词典列表 4、KBase_Top_RemoveDict
     * Top移除一个词典
     */
    public static void test_TOP_Dict() {
        // 数据库连接信息
        String url = "jdbc:kbase://192.168.105.111";// kbase服务器地址
        String driver = "com.kbase.jdbc.Driver";
        String username = "DBOWN";
        String password = "";
        ConnectionImpl conn = null;
        try {
            Class.forName(driver); // classLoader,加载对应驱动
            conn = (ConnectionImpl) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 具体测试逻辑
        String strDictName = "test2_dic";
        String strDictPath = "NLPE_DICT\\test2_dic";
       int result = conn.KBase_Top_AddDict(strDictName, strDictPath);
        // conn.KBase_Top_AddDict("Class_Dictn", "NLPE_DICT\\Class_Dictn");
        // conn.KBase_Top_AddDict("classinfon", "NLPE_DICT\\classinfon");
        if (result < 0) {
            System.out.println("error KBase_Top_AddDict");
        }

        TPI_RETURN_RESULT itemResult = conn.KBase_TOP_GetItem("test2_dic", "计算机");
        if (itemResult.iResult == 0 && !itemResult.rtnBuf.equals("")) {
            String[] itemList = itemResult.rtnBuf.split(";");
            for (String str : itemList) {
                System.out.println(str);
            }
        }

        int status = 1;
        TPI_RETURN_RESULT topListResult = conn.KBase_TOP_List(status);
        if (topListResult.iResult == 0 && !topListResult.rtnBuf.equals("")) {
            String[] topList = topListResult.rtnBuf.split(",");
            for (String str : topList) {
                System.out.println(str);
            }
        }

        int removeInt = conn.KBase_Top_RemoveDict(strDictName);
        if (removeInt < 0) {
            System.out.println("error KBase_Top_RemoveDict...");
        }

        topListResult = conn.KBase_TOP_List(status);
        if (topListResult.iResult == 0 && !topListResult.rtnBuf.equals("")) {
            String[] topList = topListResult.rtnBuf.split(",");
            for (String str : topList) {
                System.out.println(str);
            }
        }

        String strUINT = "Class_Dictn,classinfon";
        int nRet = conn.KBase_TOP_UnInit(strUINT);
        if (nRet < 0) {
            System.out.println(nRet);
        }
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * STM domain 相关测试方法 1、KBase_STM_GetDomainNameList 得到当前的知识域列表 flag
     * 0表示列举所有的知识域， 1表示列举已经打开的知识域 2、KBase_STM_OpenDomain 打开指定知识域
     * 3、KBase_STM_GetDomainName 根据知识域句柄得到知识域名称 4、KBase_STM_CloseDomain 关闭指定知识域
     */
    public static void test_STM_Domain() {
        // 数据库连接信息
        String url = "jdbc:kbase://192.168.105.111";// kbase服务器地址
        String driver = "com.kbase.jdbc.Driver";
        String username = "DBOWN";
        String password = "";
        ConnectionImpl conn = null;
        try {
            Class.forName(driver); // classLoader,加载对应驱动
            conn = (ConnectionImpl) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 具体测试逻辑
        int flag = 0;
        TPI_RETURN_RESULT result = conn.KBase_STM_GetDomainNameList(flag);
        System.out.println(result);
        if (result.iResult == 0) {
            String domainName = result.rtnBuf.split(",")[0];
            System.out.println("启动知识域: " + domainName);
            int handle = conn.KBase_STM_OpenDomain(domainName);
            TPI_RETURN_RESULT domainNameResult = conn.KBase_STM_GetDomainName(handle);
            System.out.println("domainNameResult" + domainNameResult);
            System.out.println("关闭知识域: " + domainName + ", 其句柄为:" + handle);
            int closeInt = conn.KBase_STM_CloseDomain(domainName);
            System.out.println("closeInt:" + closeInt);
        } else {
            System.out.println("STM domain error " + result.iResult);
        }

        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * STM abstract 测试
     *
     * @throws SQLException
     */
    public static void test_STM_Abstractor_Do() throws SQLException {

        // 数据库连接信息
        String url = "jdbc:kbase://192.168.103.106";// kbase服务器地址
        String driver = "com.kbase.jdbc.Driver";
        String username = "DBOWN";
        String password = "";
        ConnectionImpl conn = null;
        try {
            Class.forName(driver); // classLoader,加载对应驱动
            conn = (ConnectionImpl) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 具体测试逻辑
        String text = "性对水文的作用 气候变化对水文的作用, 在特定下垫面, 主要通过降水、气温、 蒸发等气象因素起作用。表现在对径流的作用, 包括年、月, 洪峰流量在时间与空间的";
        int intResult = 0;
        System.out.println("启动自动摘要提取");
		/*intResult = conn.KBase_STM_Abstractor_Init();
		if (intResult < 0) {
			System.out.println("STM_Abstractor_Init 失败," + intResult);
			conn.close();
			return;
		}*/
        KSTM_ABSTRACT_EXTRACTOR kae = new KSTM_ABSTRACT_EXTRACTOR();
        kae.nSentenceCount = 5;
        kae.bUsePercentage = false;
        TPI_RETURN_RESULT result = conn.KBase_STM_AbstractExtractor_Do(0, text, kae, null, 0);
        System.out.println(result);

        System.out.println("关闭自动摘要提取");
        intResult = conn.KBase_STM_Abstractor_ShutDown();
        System.out.println("KBase_STM_Abstractor_ShutDown :" + intResult);
        conn.close();
    }

    public static void main(String[] args) throws SQLException {

        test_TOP_Dict();
        //test_STM_Domain();
        //test_STM_Abstractor_Do();
    }
}
