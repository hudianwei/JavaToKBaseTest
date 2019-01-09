/*
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;

import com.kbase.jdbc.Connection;
import com.kbase.jdbc.NonRegisteringDriver;
import kbase.KBaseClient;
import kbase.struct.BSM_Init_File;
import kbase.struct.CLA_Init_File;
import kbase.struct.InputDirect;
import kbase.struct.KSTM_ABSTRACT_EXTRACTOR;
import kbase.struct.KSTM_CLASSIFIER;
import kbase.struct.KSTM_CLUSTER;
import kbase.struct.KSTM_KEYWORD_EXTRACTOR;
import kbase.struct.KSTM_TEXTWEIGHT;
import kbase.struct.KSTM_VSM_GENERATOR;
import kbase.struct.KWFSInput;
import kbase.struct.KWFSOutput;
import kbase.struct.TPI_BACKUP_EX;
import kbase.struct.TPI_COPYFILE;
import kbase.struct.TPI_DIRINFO;
import kbase.struct.TPI_DISKINFO;
import kbase.struct.TPI_LOGIN_PARA;
import kbase.struct.TPI_RESTORE_EX;
import kbase.struct.TPI_RETURN_RESULT;
import kbase.struct.WordInfor;

*/
/**
 * 一个连接代表与特定数据库的一次会话 Statement执行SQL语句并返回结果
 * 
 * <P>
 * 一个数据库的连接能够提供描绘数据库中表的信息 同时还要提供数据库支持的语法、数据库的存储过程、本次连接的容量等等
 * 这些信息通过getMetaData方法获得
 * </p>
 * 
 * @author lkk7421@cnki.net
 * @version v1.0.0
 * @see java.sql.Connection
 *//*

public class ConnectionImpl implements Connection {

	*/
/* 用来连接KBase数据库的KBaseClient实例 *//*

	private KBaseClient kbaseClient = null;

	*/
/* 到KBase数据库连接的句柄 *//*

	private int connectionHset = 0;
	*/
/* 方法执行返回的错误码 *//*

	private int error = 0;

	*/
/** 我们向数据库发起连接的端口号（默认是4567） *//*

	private int origPortToConnectTo;
	*/
/** 我们当前使用的数据库（在JDBC术语中叫做Catalog） *//*

	private String origDatabaseToConnectTo;
	*/
/** 我们要连接的数据库的IP *//*

	private String host = null;
	*/
/* KBase默认的端口号 *//*

	private int port = 4567;
	*/
/* 经过getInstance()方法解析后的最终的数据库名称 *//*

	private String database = null;
	*/
/* 经过getInstance()方法解析后的最终的连接数据库的URL *//*

	private String myURL = null;
	*/
/* 经过getInstance()方法解析后的最终的连接数据库的用户名 *//*

	private String user = null;
	*/
/* 经过getInstance()方法解析后的最终的连接数据库的密码 *//*

	private String password = null;
	*/
/* 当前数据库连接的配置信息，包括用户名密码等信息 *//*

	protected Properties props = null;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public KBaseClient getKbaseClient() {
		return kbaseClient;
	}

	public void setKbaseClient(KBaseClient kbaseClient) {
		this.kbaseClient = kbaseClient;
	}

	public int getConnectionHset() {
		return connectionHset;
	}

	public void setConnectionHset(int connectionHset) {
		this.connectionHset = connectionHset;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	*/
/**
	 * 创建一个数据库连接的实例
	 * 
	 * @param hostToConnectTo
	 *            我们要连接的数据库IP
	 * @param portToConnectTo
	 *            我们要连接的数据端口号（默认是4567）
	 * @param info
	 *            当前数据库连接的配置文件，默认为null
	 * @param url
	 *            数据库JDBC驱动用来连接KBase的URL
	 * @return Connection 返回创建的Connection
	 *//*


	protected static Connection getInstance(String hostToConnectTo, int portToConnectTo, Properties info,
			String databaseToConnectTo, String url) throws SQLException {
		ConnectionImpl ci = new ConnectionImpl(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
		return (Connection) ci;
	}

	*/
/**
	 * 创建数据库实例方法的具体实现
	 * 
	 * @param hostToConnectTo
	 *            我们要连接的数据库IP
	 * @param portToConnectTo
	 *            我们要连接的数据端口号（默认是4567）
	 * @param info
	 *            当前数据库连接的配置文件，默认为null
	 * @param url
	 *            数据库JDBC驱动用来连接KBase的URL
	 * @return ConnectionImpl 返回创建的Connection，可以用来创建Statement
	 * @throws SQLException
	 *//*

	protected ConnectionImpl(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo,
			String url) throws SQLException {

		this.host = hostToConnectTo;
		this.origPortToConnectTo = portToConnectTo;
		this.origDatabaseToConnectTo = databaseToConnectTo;
		if (hostToConnectTo == null) {
			this.host = "localhost";
		} else if (hostToConnectTo.indexOf(',') != -1) {
			// 多个数据库服务器IP用逗号分隔
			StringTokenizer hostTokenizer = new StringTokenizer(hostToConnectTo, ",", false);
		} else {
			this.host = hostToConnectTo;
		}
		this.port = portToConnectTo;

		if (databaseToConnectTo == null) {
			databaseToConnectTo = "";
		}
		this.database = databaseToConnectTo;
		this.myURL = url;
		this.user = info.getProperty(NonRegisteringDriver.USER_PROPERTY_KEY);
		this.password = info.getProperty(NonRegisteringDriver.PASSWORD_PROPERTY_KEY);

		if ((this.user == null) || this.user.equals("")) {
			this.user = "";
		}

		if (this.password == null) {
			this.password = "";
		}

		this.props = info;
		kbaseClient = new KBaseClient();
		TPI_LOGIN_PARA loginPara = new TPI_LOGIN_PARA();
		loginPara.szUserName = this.user;
		loginPara.szPassWord = this.password;
		String ip = this.host;
		try {
			if (ip.equals("localhost")) {
				InetAddress addr = InetAddress.getLocalHost();
				ip = addr.getHostAddress();// 获得本机IP
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		loginPara.szIp = ip;
		TPI_RETURN_RESULT robj = kbaseClient.KBase_OpenCon(ip, this.port, loginPara, 0);
		if (robj.rtnInt == -6031) {
			throw new SQLException("TPI句柄无效,返回错误码-6031");
		}
		if (robj.rtnInt <= 0) {
			throw new SQLException("连接失败,返回错误码"+robj.iResult);
		}
		
		this.connectionHset = robj.rtnInt;
		this.error = robj.iResult;
	}

	@Override
	public void abort(Executor executor) throws SQLException {

	}

	@Override
	public void clearWarnings() throws SQLException {

	}

	*/
/**
	 * 关闭Connection的方法.
	 *//*

	public void close() throws SQLException {
		kbaseClient.KBase_CloseCon(connectionHset);
	}

	@Override
	public void commit() throws SQLException {

	}

	@Override
	public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		return null;
	}

	@Override
	public Clob createClob() throws SQLException {
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return null;
	}

	*/
/**
	 * 创建一个具体的Statement.
	 * 
	 * @return Statement 返回创建的Statement
	 * @throws SQLException
	 *             如果运行时出现错误
	 *//*

	@Override
	public Statement createStatement() throws SQLException {
		com.kbase.jdbc.StatementImpl stmt = null;
		try {
			stmt = new com.kbase.jdbc.StatementImpl(this, this.database);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stmt;
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		com.kbase.jdbc.StatementImpl stmt = null;
		try {
			stmt = new com.kbase.jdbc.StatementImpl(this, this.database);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stmt;
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		com.kbase.jdbc.StatementImpl stmt = null;
		try {
			stmt = new com.kbase.jdbc.StatementImpl(this, this.database);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stmt;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return null;
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return false;
	}

	@Override
	public String getCatalog() throws SQLException {
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return null;
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return 0;
	}

	@Override
	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return 0;
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return null;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	@Override
	public boolean isClosed() throws SQLException {
		return false;
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return false;
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return false;
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return null;
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return null;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		com.kbase.jdbc.PreparedStatement pstmt = null;
		try {
			pstmt = new com.kbase.jdbc.PreparedStatement(this, sql, this.database);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pstmt;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		com.kbase.jdbc.PreparedStatement pstmt = null;
		try {
			pstmt = new com.kbase.jdbc.PreparedStatement(this, sql, this.database);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (PreparedStatement) pstmt;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		com.kbase.jdbc.PreparedStatement pstmt = null;
		try {
			pstmt = new com.kbase.jdbc.PreparedStatement(this, this.database);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pstmt;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		com.kbase.jdbc.StatementImpl stmt = null;
		try {
			stmt = new com.kbase.jdbc.StatementImpl(this, this.database);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (PreparedStatement) stmt;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		com.kbase.jdbc.StatementImpl stmt = null;
		try {
			stmt = new com.kbase.jdbc.StatementImpl(this, this.database);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (PreparedStatement) stmt;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		com.kbase.jdbc.StatementImpl stmt = null;
		try {
			stmt = new com.kbase.jdbc.StatementImpl(this, this.database);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (PreparedStatement) stmt;
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {

	}

	@Override
	public void rollback() throws SQLException {

	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {

	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {

	}

	@Override
	public void setCatalog(String catalog) throws SQLException {

	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {

	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {

	}

	@Override
	public void setHoldability(int holdability) throws SQLException {

	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {

	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return null;
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return null;
	}

	@Override
	public void setSchema(String schema) throws SQLException {

	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {

	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
		return null;
	}

	@Override
	public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException {
		return false;
	}

	*/
/**
	 * 获取全部数据库名称，以逗号“,”分隔
	 * 
	 * @param hCon
	 *            用户连接句柄
	 * @param nDataBuffLen
	 *            输入时数组pDataBuff的大小,输出时,实际占用大小
	 * @return TPI_RETURN_RESULT.iResult保存错误码，>=0 表示操作成功,
	 *         返回数据库个数。TPI_RETURN_RESULT.rtnBuf保存数据库名字，以逗号“,”分隔
	 **//*

	@Override
	public TPI_RETURN_RESULT KBase_GetAllDatabaseName() {
		TPI_RETURN_RESULT tpiResult = kbaseClient.KBase_GetAllDatabaseName(this.connectionHset, 4096);
		return tpiResult;
	}

	*/
/**
	 * 取服务器的硬盘列表
	 * 
	 * @param hCon
	 *            与服务器的通信句柄
	 * @param diskList
	 *            用于返回硬盘信息列表的缓冲区 （其数组元素个数不小于26，以保证不会溢出）
	 * @param nDiskCnt
	 *            硬盘个数
	 * @return 若函数执行成功，返回TPI_RETURN_RESULT，iResult保存函数错误码
	 **//*

	@Override
	public TPI_DISKINFO[] KBase_GetServerDiskInfo(int nDiskCnt) {
		TPI_DISKINFO[] diskInfoArr = new TPI_DISKINFO[26];
		diskInfoArr = kbaseClient.KBase_GetServerDiskInfo(this.connectionHset, nDiskCnt, diskInfoArr);
		return diskInfoArr;
	}

	*/
/**
	 * 取服务器的目录信息
	 * 
	 * @param pPath
	 *            服务器的目录路径
	 * @param lStartPos
	 *            开始位置
	 * @param pCount
	 *            输入时：最多取多少条 输出时：返回的个数
	 * @param nDirFlag
	 *            0为文件和目录，1为只列目录 返回目录下的对应(文件,目录)信息, 调用者应保证 元素个数不小于*pCount
	 * @return 若函数执行成功，返回大于等于0，否则小于0
	 **//*

	@Override
	public TPI_DIRINFO[] KBase_GetServerDirList(String pPath, int lStartPos, int pCount, int nDirFlag) {
		TPI_DIRINFO[] dirInfoArr = new TPI_DIRINFO[pCount];
		dirInfoArr = kbaseClient.KBase_GetServerDirList(this.connectionHset, pPath, lStartPos, pCount, nDirFlag,
				dirInfoArr);
		return dirInfoArr;
	}

	*/
/**
	 * 上传文件 读取本地的一个文件，往服务器上写一个文件
	 * 
	 * @param hCon
	 *            与服务器的通信句柄
	 * @param strSourceFile
	 *            本地要读取的数据文件名
	 * @param strDestFile
	 *            写在服务器上的目标文件名
	 * @return 若函数执行成功，返回大于等于0，否则小于0
	 **//*

	@Override
	public int KBase_WriteFile(String strSourceFile, String strDestFile) {
		int result = 0;
		result = kbaseClient.KBase_WriteFile(this.connectionHset, strSourceFile, strDestFile);
		return result;
	}

	*/
/**
	 * 下载文件 从服务器上读取一个文件，然后写在本地的一个文件上
	 * 
	 * @param hCon
	 *            与服务器的通信句柄
	 * @param strSourceFile
	 *            服务器上的原文件名
	 * @param strDestFile
	 *            本地要生成的目标文件名
	 * 
	 * @return 若函数执行成功，返回大于等于0，否则小于0
	 **//*

	@Override
	public int KBase_ReadFile(String strSourceFile, String strDestFile) {
		int result = 0;
		result = kbaseClient.KBase_ReadFile(this.connectionHset, strSourceFile, strDestFile);
		return result;
	}

	*/
/**
	 * 拷贝目录数据
	 * 
	 * @param szSrcPath
	 *            源路径
	 * 
	 * @param szDesPath
	 *            目的路径
	 * 
	 * @return 若函数执行成功，返回大于等于0，否则小于0
	 **//*

	@Override
	public int KBase_CopyDir(String szSrcPath, String szDesPath) {
		int result = 0;
		TPI_COPYFILE copyFile = new TPI_COPYFILE();
		copyFile.szSrcPath = szSrcPath;
		copyFile.szDesPath = szDesPath;
		result = kbaseClient.KBase_CopyDir(this.connectionHset, copyFile);
		return result;
	}

	*/
/**
	 * 建立目录
	 * 
	 * @param pDir
	 *            目录信息，支持建多级目录
	 * 
	 * @return 若函数执行成功，返回大于等于0，否则小于0
	 **//*

	@Override
	public int KBase_MakeDir(String pDir) {
		int result = 0;
		result = kbaseClient.KBase_MakeDir(this.connectionHset, pDir);
		return result;
	}

	*/
/**
	 * 删除目录 此API将删除此目录及目录下所有文件和子目录。
	 * 
	 * @param pDir
	 *            目录信息，是服务器上一个完整路径
	 * 
	 * @return 若函数执行成功，返回大于等于0，否则小于0
	 **//*

	@Override
	public int KBase_RemoveDir(String pDir) {
		int result = 0;
		result = kbaseClient.KBase_RemoveDir(this.connectionHset, pDir);
		return result;
	}

	*/
/**
	 * 删除一个表 删除表后，表仍物理存在，可通过TPI_ImportTable重新引入到系统中
	 * 
	 * @param hCon
	 *            连接句柄
	 * @param tableName
	 *            表的名称
	 * 
	 * @return 若函数执行成功，返回大于等于0，否则小于0
	 **//*

	@Override
	public int KBase_DeleteTable(String tableName) {
		int result = 0;
		result = kbaseClient.KBase_DeleteTable(this.connectionHset, tableName);
		return result;
	}

	*/
/**
	 * 判断表名是否存在
	 * 
	 * @param hCon
	 *            与服务器的通信句柄
	 * @param tableName
	 *            表名
	 * 
	 * @return 若存在，返回1；不存在，返回0；否则, 是<0的值，为错误码
	 **//*

	@Override
	public int KBase_TableNameExists(String tableName) {
		int result = 0;
		result = kbaseClient.KBase_TableNameExists(this.connectionHset, tableName);
		return result;
	}

	*/
/**
	 * 创建一个数据库
	 * 
	 * @param hCon
	 *            用户连接句柄
	 * @param dbName
	 *            数据库的名字
	 * 
	 * @return 用一整数表明操作成功与否的状态
	 * @return =0 实际知识域数量
	 * @return <0 错误码
	 **//*

	@Override
	public int KBase_CreateDataBase(String dbName) {
		int result = 0;
		result = kbaseClient.KBase_CreateDataBase(this.connectionHset, dbName);
		return result;
	}

	*/
/**
	 * 引入一个表
	 * 
	 * @param hCon
	 *            与服务器的通信句柄
	 * @param tablePath
	 *            表的路径
	 * 
	 * @return 若函数执行成功，返回大于等于0，否则小于0
	 **//*

	@Override
	public int KBase_ImportTable(String tablePath) {
		int result = 0;
		result = kbaseClient.KBase_ImportTable(this.connectionHset, tablePath);
		return result;
	}

	*/
/**
	 * 将指定表导入到指定的数据库中
	 * 
	 * @param hCon
	 *            用户连接句柄
	 * @param path
	 *            表文件路径
	 * @param pdbName
	 *            指定的数据库
	 * 
	 * @return 用一整数表明操作成功与否的状态
	 * @return 返回大于等于0, 表示操作成功
	 * @return !0 错误码
	 **//*

	@Override
	public int KBase_ImportTable2(String path, String pdbName) {
		int result = 0;
		result = kbaseClient.KBase_ImportTable2(this.connectionHset, path, pdbName);
		return result;
	}

	*/
/**
	 * 得到指定数据库的表名称记录集
	 * 
	 * @param hCon
	 *            用户连接句柄
	 * @param dbName
	 *            数据库名称
	 * 
	 * @return 查询结果集,如果返回NULL
	 **//*

	@Override
	public TPI_RETURN_RESULT KBase_GetTablesQueryInDB(String dbName) {
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_GetTablesQueryInDB(this.connectionHset, dbName);
		return result;
	}

	*/
/**
	 * 得到数据库的表名称列表
	 * 
	 * @param hCon
	 *            用户连接句柄
	 * @param pdbName
	 *            数据库名称
	 * @param nLen
	 *            缓冲长度
	 * @param nListType
	 *            表示返回表的类型 0 返回 表名：表类型
	 * 
	 * @return TPI_RETURN_RESULT，表名存放在TPI_RETURN_RESULT.rtnBuf里。
	 * @return 0 表示操作成功
	 * @return !0 错误码
	 **//*

	@Override
	public TPI_RETURN_RESULT KBase_GetTablesListInDBEx(String pdbName, int nLen, int nListType) {
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_GetTablesListInDBEx(this.connectionHset, pdbName, nLen, nListType);
		return result;
	}

	*/
/**
	 * 检查服务器状态 不检查账号信息，只要数据库连接状态正常，即使此账号当前已经被剔除也可
	 * 
	 * @param hCon
	 *            用户连接句柄
	 * 
	 * @return 0 服务器运行正常， <0 错误码, 出错原因可能是数据库连接非法或服务器工作不正常
	 **//*

	@Override
	public int KBase_CheckServerState() {
		int result = 0;
		result = kbaseClient.KBase_CheckServerState(this.connectionHset);
		return result;
	}

	*/
/**
	 * 取可用的字典名
	 * 
	 * XML格式表示的一个所有词典名,其格式如下 <dics> <name>dic1</name> <name>dic2</name> </dics>
	 * 
	 * @param hCon
	 *            与服务器的通信句柄
	 * @param bufLen
	 *            输入时为xmlDicts的长度，输出时实际写入buf中的长度
	 * 
	 * @return TPI_RETURN_RESULT.iResult保存的是错误码,
	 *         TPI_RETURN_RESULT.rtnBuf保存的是词典名，
	 **//*

	@Override
	public TPI_RETURN_RESULT KBase_KEGetDicts(int bufLen) {
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_KEGetDicts(this.connectionHset, bufLen);
		return result;
	}

	
	// kbase 备份和恢复 start
	*/
/**
	 *  @brief 备份扩展
	 *  备份扩展, 立即事件
	 *  @param hCon 用户连接句柄
	 *  @param hEvent 用于返回事件句柄
	 *  @param pInfo  备份信息
	 *  @return 用一整数表明操作成功与否的状态
	 *  @retval 0   表示操作成功
	 *  @retval !0  错误码
	*//*

	@Override
	public TPI_RETURN_RESULT KBase_BackupEx(int hEvent, TPI_BACKUP_EX pInfo) {
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_BackupEx(this.connectionHset,hEvent,pInfo);
		return result;
	}
	*/
/**
	 *  @brief 恢复扩展
	 *  备份扩展, 立即事件
	 *  @param hCon 用户连接句柄
	 *  @param hEvent 用于返回事件句柄
	 *  @param pInfo  恢复信息
	 *  @return 用一整数表明操作成功与否的状态
	 *  @retval 0   表示操作成功
	 *  @retval !0  错误码
	*//*

	@Override
	public TPI_RETURN_RESULT KBase_RestoreEx( int hEvent, TPI_RESTORE_EX pInfo) {
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_RestoreEx(this.connectionHset,hEvent,pInfo);
		return result;
	}
	// kbase 备份和恢复 end
	*/
/**
	 * 查询事件是否完成
	 * 
	 * @param hCon
	 *            用户连接句柄
	 * @param hevent
	 *            事件句柄
	 * 
	 * @return 用一整数表明操作成功与否的状态
	 * @return >=0 事件成功
	 * @return <0 错误码 如果是 TPI_ERR_EVENTNOEND 值说明事件没有结束
	 **//*

	@Override
	public int KBase_QueryEvent( int hevent) {
		int result = 0;
		result = kbaseClient.KBase_QueryEvent(this.connectionHset,hevent);
		return result;
	}
	*/
/**
	 * 扩展接口start
	 * 
	 *//*

	*/
/***
	 * @brief 直接向某个扩展模块发送消息，这是扩展模块中最灵活的接口 用户可以自己和自己的扩展模块进行通讯
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] extname 扩展模块名称
	 * @param[in] version 扩展模块的版本，如果为NULL，则表示发送给当前运行的版本
	 * @param[in] buffer 发送给扩展模块的缓冲
	 * @param[in] size 发送给扩展模块的缓冲大小
	 * @param[out] outbuf 扩展模块返回的缓冲大小，用户分配其空间，注意是字符串空间，要多保留一个ZERO
	 * @param[in,out] outsize 接受缓冲长度，须先传入接受缓冲大小，接口会返回实际的大小
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	@Override
	public TPI_RETURN_RESULT KBase_SendMessage(String extname, String version, String buffer, 
			StringBuilder outbuf, int outsize) {
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		if(outbuf == null){
			outbuf = new StringBuilder(outsize);
		}
		result = kbaseClient.KBase_SendMessage(this.connectionHset, extname, version, buffer, outbuf.toString(), outsize);
		return result;
	}
	*/
/***
	 * 
	 * @brief 直接向某个扩展模块发送消息，这是扩展模块中最灵活的接口 用户可以自己和自己的扩展模块进行通讯
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] extname 扩展模块名称
	 * @param[in] version 扩展模块的版本，如果为NULL，则表示发送给当前运行的版本
	 * @param[in] buffer 发送给扩展模块的缓冲
	 * @param[in] size 发送给扩展模块的缓冲大小
	 * @param[out] outbuf 扩展模块返回的缓冲大小，用户分配其空间，注意是字符串空间，要多保留一个ZERO
	 * @param[in,out] outsize 接受缓冲长度，须先传入接受缓冲大小，接口会返回实际的大小
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	@Override
	public TPI_RETURN_RESULT KBase_SendData(String extname, String version, byte[] buffer, int size,
			StringBuilder outbuf, int outsize) {
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		if(outbuf == null){
			outbuf = new StringBuilder(outsize);
		}
		result = kbaseClient.KBase_SendData(this.connectionHset, extname, version, buffer, size, outbuf.toString(), outsize);
		return result;
	}
	*/
/***
	 * @brief 直接向某个扩展模块发送消息，这是扩展模块中最灵活的接口 用户可以自己和自己的扩展模块进行通讯
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] extname 扩展模块名称
	 * @param[in] version 扩展模块的版本，如果为NULL，则表示发送给当前运行的版本
	 * @param[in] buffer 发送给扩展模块的缓冲
	 * @param[in] size 发送给扩展模块的缓冲大小
	 * @param[out] outbuf 扩展模块返回的缓冲大小，用户分配其空间，注意是字符串空间，要多保留一个ZERO
	 * @param[in,out] outsize 接受缓冲长度，须先传入接受缓冲大小，接口会返回实际的大小
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	@Override
	public TPI_RETURN_RESULT KBase_SendMessageTimeout( String extname, String version, String buffer,
			StringBuilder outbuf, int outsize, int timeout, int flag) {
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		if(outbuf == null){
			outbuf = new StringBuilder(outsize);
		}
		result = kbaseClient.KBase_SendMessageTimeout(this.connectionHset, extname, version, buffer,outbuf.toString(), outsize,timeout,flag);
		return result;
	}
	*/
/***
	 * @brief 直接向某个扩展模块发送消息，这是扩展模块中最灵活的接口 用户可以自己和自己的扩展模块进行通讯
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] extname 扩展模块名称
	 * @param[in] version 扩展模块的版本，如果为NULL，则表示发送给当前运行的版本
	 * @param[in] buffer 发送给扩展模块的缓冲
	 * @param[in] size 发送给扩展模块的缓冲大小
	 * @param[out] outbuf 扩展模块返回的缓冲大小，用户分配其空间，注意是字符串空间，要多保留一个ZERO
	 * @param[in,out] outsize 接受缓冲长度，须先传入接受缓冲大小，接口会返回实际的大小
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	@Override
	public TPI_RETURN_RESULT KBase_SendDataTimeout(String extname, String version, byte[] buffer, int size,
			StringBuilder outbuf, int outsize, int timeout, int flag) {
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		if(outbuf == null){
			outbuf = new StringBuilder(outsize);
		}
		result = kbaseClient.KBase_SendDataTimeout(this.connectionHset, extname, version, buffer, size, outbuf.toString(), outsize,timeout,flag);
		return result;
	}
	*/
/***
	 * !
	 * 
	 * @brief 注册扩展模块 注意每一个新增加的扩展模块都需要注册，否则服务器端 将无法使用其扩展功能
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] extname 扩展模块名称，必须填写，以后需要使用扩展模块的名称进行通讯服务
	 * 
	 * @param[in] version 扩展模块版本，默认为1.0.0.0
	 * 
	 * @param[in] onstartup 扩展模块是否在服务器启动时加载，默认为FALSE
	 * 
	 * @param[in] srv_fpath 扩展模块文件在服务器端的路径，默认为<扩展模块名称>.dll
	 * 
	 * @param[in] start_param 扩展模块的启动参数，默认为NULL
	 * 
	 * @param[in] description 扩展模块的描述，默认为NULL
	 * 
	 * @param[in] dependfiles 扩展模块的依赖文件，多文件用'|'隔开，默认为NULL
	 * 
	 * @return 若函数执行成功，返回大于等于0，否则小于0
	 *//*

	public int KBase_RegExtMod( String extname, String version, boolean onstartup, String srv_fpath,
			String start_param, String description, String dependfiles){
		int result = 0;
		result = this.kbaseClient.KBase_RegExtMod(this.connectionHset, extname, version, onstartup, srv_fpath, start_param, description, dependfiles);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 注销一个扩展模块 注销后扩展模块功能不能再使用， 同时运行中的所有有关扩展服务也立即停止
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] extname 扩展模块名称
	 * 
	 * @param[in] version 扩展模块版本，如果为空则注销最高版本的扩展模块。
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_UnRegExtMod( String extname, String version){
		int result = 0;
		result = kbaseClient.KBase_UnRegExtMod(this.connectionHset, extname, version);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 加载扩展模块，注意可以不显示调用， 调用相应的扩展函数时相应的扩展模块会自动加载
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] extname 扩展模块名称
	 * 
	 * @param[in] version 扩展模块的版本，如果为NULL，则表示加载同名的最大版本
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_LoadExtMod( String extname, String version){
		int result = 0;
		result = kbaseClient.KBase_LoadExtMod(this.connectionHset, extname, version);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 卸载扩展模块，为了节省服务器内存，使用扩展后可以显示卸载！ 注意如果有其他用户正在使用其扩展功能的话，其动作将会立即中止。
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] extname 扩展模块名称
	 * 
	 * @param[in] version 扩展模块的版本，如果为NULL，则表示卸载同名的最大版本
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_UnloadExtMod( String extname, String version){
		int result = 0;
		result = kbaseClient.KBase_UnloadExtMod(this.connectionHset, extname, version);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 得到某一模块的扩展信息 其信息为类似XML的字符串
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] extname 扩展模块名称
	 * 
	 * @param[in] version 扩展模块的版本，如果为NULL，则表示获取当前运行的版本
	 * 
	 * @param[out] modinfo 返回的模块信息，用户分配其空间
	 * 
	 * @param[in,out] size 其缓冲的大小，须先传入接受缓冲大小，接口会返回实际的大小
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_GetModInfo( String extname, String version){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_GetModInfo(this.connectionHset, extname, version);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 得到所有模块的扩展信息 其信息为类似XML的字符串
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[out] modinfos 返回的模块信息，用户分配其空间
	 * 
	 * @param[in,out] size 其缓冲的大小，须先传入接受缓冲大小，接口会返回实际的大小
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_GetModInfos(){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_GetModInfos(this.connectionHset);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 得到当前扩展管理器的版本日期和其时间
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[out] version 返回的版本信息，用户分配其空间
	 * 
	 * @param[in,out] size 其缓冲的大小，须先传入接受缓冲大小，接口会返回实际的大小
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_GetExtVersion(){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_GetExtVersion(this.connectionHset);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 运行扩展的sql语句，使用短连接
	 * 
	 * @param[in] connstr
	 *            连接字符串，形式为："Server=192.168.100.118;Port=4567;User=DBOWN;Pass=;"
	 * 
	 * @param[in] sql KSQL字符串，形式为 use ext xxx ... 具体参考KSQL手册
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_ExecSql(String connstr, String sql){
		int result = 0;
		result = this.kbaseClient.KBase_ExecSql(connstr,sql);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 运行扩展的sql语句
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] sql KSQL字符串，形式为 use ext xxx ... 具体参考KSQL手册
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_ExecSql2( String sql){
		int result = 0;
		result = this.kbaseClient.KBase_ExecSql2(this.connectionHset,sql);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 加载词典
	 * 
	 * @param[in] hcon 连接句柄
	 * 
	 * @param[in] dicts 预加载的词典列表，多个词典之间用,分割
	 * 
	 * @return 成功返回>=0，否则失败
	 *//*

	public long KBase_NLPE_DictLoad( String dicts){
		long result = 0;
		result = this.kbaseClient.KBase_NLPE_DictLoad(this.connectionHset,dicts);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 卸载词典
	 * 
	 * @param[in] hcon 连接句柄
	 * 
	 * @param[in] hDict 要卸载的词典句柄
	 * 
	 * @return 成功返回>=0，否则失败
	 *//*

	public int KBase_NLPE_DictUnload( long hDict){
		int result = 0;
		result = this.kbaseClient.KBase_NLPE_DictUnload(this.connectionHset,hDict);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 导入词典
	 * 
	 * @param[in] hcon 连接句柄
	 * 
	 * @param[in] dictpath 导入的词典路径
	 * 
	 * @return 成功返回>=0，否则失败
	 *//*

	public int KBase_NLPE_DictImport( String dictpath){
		int result = 0;
		result = this.kbaseClient.KBase_NLPE_DictImport(this.connectionHset,dictpath);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 查看某个词典是否存在
	 * 
	 * @param[in] hcon 连接句柄
	 * 
	 * @param[in] dictpath 词典路径
	 * 
	 * @return 成功返回>=0，否则失败
	 *//*

	public int KBase_NLPE_DictSearch( String dictpath){
		int result = 0;
		result = this.kbaseClient.KBase_NLPE_DictSearch(this.connectionHset,dictpath);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 对文本进行自动分词，将结果放在数组中
	 * 
	 * @param[in] hcon 连接句柄
	 * 
	 * @param[in] srcBuf 需分词的文本
	 * 
	 * @param[out] EndPos 数组，存放每个词的结束位置
	 * 
	 * @param[in] size srcBuf 的字节数，也是 EndPos 数组的大小
	 * 
	 * @param[in] hDict 用户词典句柄
	 * 
	 * @param[in] sectMethod 分词所用的方法，0，正向最大长度匹配方法，1，全切分方法，2，反向最大长度匹配方法。默认为0
	 * 
	 * @param[in] POS 数组，存放每个词的词性，默认为空
	 * 
	 * @param[in] senMaxLen 只在全切分方法中使用
	 * 
	 * @param[in] recogPhrase 是否识别短语，默认为不识别，只对全切分有效
	 * 
	 * @param[in] all_as_sep
	 *            所有标点符号作为句子的分隔符（true时可大辐提高分词速度，但无法切分含有标点符号的词，如“《彷徨》”）
	 * 
	 * @return 成功返回>=0，否则失败
	 *//*

	public TPI_RETURN_RESULT KBase_NLPE_WordSect( String srcBuf, long size, long hDict, int sectMethod,
			int[] POS, int senMaxLen, boolean recogPhrase, boolean all_as_sep){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_NLPE_WordSect(this.connectionHset, srcBuf, size, hDict, sectMethod, POS, senMaxLen, recogPhrase, all_as_sep);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 得到词典句柄
	 * 
	 * @param[in] hcon 连接句柄
	 * 
	 * @param[in] dictpath 词典路径
	 * 
	 * @return 返回词典句柄, >=0句柄才有效。
	 *//*

	public long KBase_NLPE_GetHandle( String dictpath){
		long result = 0;
		result = this.kbaseClient.KBase_NLPE_GetHandle(this.connectionHset,dictpath);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 删除词典句柄
	 * 
	 * @param[in] hcon 连接句柄
	 * 
	 * @param[in] hDict 词典句柄
	 * 
	 * @return 成功返回>=0，否则失败
	 *//*

	public int KBase_NLPE_DeleteHandle( long hDict){
		int result = 0;
		result = this.kbaseClient.KBase_NLPE_DeleteHandle(this.connectionHset,hDict);
		return result;
	}

	*/
/******************************************************************//*

	*/
/***																*//*

	*/
/*** 主题标引器(Classifier)模块 *//*

	*/
/***																*//*

	*/
/******************************************************************//*

	*/
/***
	 * !
	 * 
	 * @defgroup classifier_group 主题标引器模块
	 * 
	 * @ingroup group_NLPE
	 * 
	 * @note 服务器端依赖的功能文件为Classifier.dll
	 * 
	 * @{
	 *//*


	*/
/***
	 * !
	 * 
	 * @brief 运行扩展的sql语句
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] path 系统路径，指\<kbase目录\>\\system
	 * 
	 * @return 设定路径返回1，否则返回0
	 *//*

	public int KBase_CLA_SetPath( String path){
		int result = 0;
		result = this.kbaseClient.KBase_CLA_SetPath(this.connectionHset,path);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 获得文章的分类号
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] mode 分类类别，1 - 期刊分类，2 - 报纸分类
	 * 
	 * @param[in] cif 输入的文章处理数据
	 * 
	 * @param[out] classno 返回的文章分类号
	 * 
	 * @param[in,out] size 返回的分类号长度
	 * 
	 * @param[out] degree 返回的置信度
	 * 
	 * @param[in,out] dsize 返回的置信度长度
	 * 
	 * @param[in,out] count 返回的分类号&置信度数目
	 * 
	 * @param[in] level 设定返回的分类号层次。共有四个值：3，4，5，0。默认为0，输出最详细的分类号
	 * 
	 * @param[out] hnum
	 *             返回的可信分类号数目。在high=true的结果中，表明count个数的分类号中，前hnum个是自动分类判断的结果；high=false,
	 *             则hnum = 0。
	 * 
	 * @param[out] high true表示该结果达到了高准确率要求（输出一个或两个分类号），否则没有达到（按照权重输出三个分类号）
	 * 
	 * 
	 * @return 成功返回1，失败返回0。
	 *//*

	public TPI_RETURN_RESULT KBase_CLA_GetClassifyResult( int mode, CLA_Init_File cif, 
			int count, boolean high, int hnum, int level){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_CLA_GetClassifyResult(this.connectionHset, mode, cif, count, high, hnum, level);
		return result;
	}

	*/
/***
	 * !
	 * 
	 * @brief 释放所有资源
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 成功返回0，否则失败
	 *//*

	public int KBase_CLA_FreeDict(){
		int result = 0;
		result = this.kbaseClient.KBase_CLA_FreeDict(this.connectionHset);
		return result;
	}

	*/
/*** ! @} 主题标引器(Classifier)模块接口结束 *//*


	*/
/******************************************************************//*

	*/
/***																*//*

	*/
/*** 智能检索 相关函数 *//*

	*/
/***																*//*

	*/
/******************************************************************//*

	*/
/***
	 * !
	 * 
	 * @defgroup group_ar 智能检索模块
	 * 
	 * @ingroup group_NLPE
	 * 
	 * @note 服务器端依赖的功能文件为AR.dll
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 智能检索初始化，建立初始化词典名索引
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_AR_Initial(){
		int result = 0;
		result = this.kbaseClient.KBase_AR_Initial(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 新建词典
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] dict_name 欲创建的词典名称
	 * 
	 * @param[in] dict_dir 词典保存的文件夹路径
	 * 
	 * @param[in] src_file 词典来源文件名，即词典数据文本
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_AR_Dict_Create( String dict_name, String dict_dir, String src_file){
		int result = 0;
		result = this.kbaseClient.KBase_AR_Dict_Create(this.connectionHset, dict_name, dict_dir, src_file);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 删除词典
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] dict_name 欲删除的词典名称
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_AR_Dict_Delete( String dict_name){
		int result = 0;
		result = this.kbaseClient.KBase_AR_Dict_Delete(this.connectionHset, dict_name);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 获得全部词典名
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[out] buf 返回的词典信息，用户分配其空间
	 * 
	 * @param[in,out] len 返回缓冲的长度，须先传入接受缓冲长度，接口会返回实际的长度
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_AR_GetAllDictName(){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_AR_GetAllDictName(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 判断词典是否已经存在
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] dict_path 词典的路经
	 * 
	 * @return 1表示存在，0表示不存在，小于0为错误码
	 *//*

	public int KBase_AR_IsDictExist( String dict_path){
		int result = 0;
		result = kbaseClient.KBase_AR_IsDictExist(this.connectionHset,dict_path);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 返回检索项的分词结果及每个词对应的范畴
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] text 预处理的文本信息
	 * 
	 * @param[out] word 分词结果
	 * 
	 * @param[in,out] wordlen 分词结果的缓冲区长度
	 * 
	 * @param[out] info 存放分词结果中每个词的范畴
	 * 
	 * @param[in,out] infolen 存放范畴的缓冲区长度
	 * 
	 * @return 1表示存在，0表示不存在，小于0为错误码
	 * 
	 * @note 分词结果中，词和词之间用"|$|"字符串隔开，最后一个词后面是"|$|"
	 *       分词的范畴中，范畴之间用"|$|"字符串隔开，最后一个范畴后面是"|$|" 注意一个词可能有多个范畴，这时范畴之间用空格隔开。
	 *       同时注意缓冲区的长度，其word的长度不小于text长度的4倍加上1，
	 *       info的长度不小于text长度的13倍加上一，否则可能出现缓冲区不够的情况。
	 *//*

	public TPI_RETURN_RESULT KBase_AR_Search( String text){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_AR_Search(this.connectionHset,text);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 加载词典
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] dict_path 词典的路经
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_AR_Dict_Load( String dict_path){
		int result = 0;
		result = kbaseClient.KBase_AR_Dict_Load(this.connectionHset,dict_path);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 卸载词典
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] dict_path 词典的路经
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_AR_Dict_Unload( String dict_path){
		int result = 0;
		result = kbaseClient.KBase_AR_Dict_Unload(this.connectionHset,dict_path);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 引入词典
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] dict_path 词典的路经
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_AR_Dict_Import( String dict_path){
		int result = 0;
		result = kbaseClient.KBase_AR_Dict_Import(this.connectionHset,dict_path);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 获取全部已经加载的词典名
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[out] buf 缓冲区
	 * @param[in,out] len 缓冲区大小
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_AR_GetAllDictNameLoaded(){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_AR_GetAllDictNameLoaded(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 获取最后一次执行结果的范畴列表
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[out] buf 缓冲区
	 * @param[in,out] len 缓冲区大小
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_AR_GetCategoryName(){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_AR_GetCategoryName(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 智能检索词接口 - 2.0
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] src 输入词
	 * @param[out] buf 返回词的性质
	 * @param[in,out] len 返回缓冲区大小
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_Aptitude_Search( String src){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_Aptitude_Search(this.connectionHset,src);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief Aptitude 初始化
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_Aptitude_Init(){
		int result = 0;
		result = kbaseClient.KBase_Aptitude_Init(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief Aptitude 结束
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_Aptitude_UnInit(){
		int result = 0;
		result = kbaseClient.KBase_Aptitude_UnInit(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 智能分词接口
	 * 
	 * @param[in] hcon 连接句柄
	 * 
	 * @param[in] srcBuf 需分词的文本
	 * 
	 * @param[out] EndPos 数组，存放每个词的结束位置
	 * 
	 * @param[in] size srcBuf 的字节数，也是 EndPos 数组的大小
	 * 
	 * @param[in] hDict 用户词典句柄
	 * 
	 * @param[in] sectMethod 分词所用的方法，0，正向最大长度匹配方法，1，全切分方法，2，反向最大长度匹配方法。默认为0
	 * 
	 * @param[in] POS 数组，存放每个词的词性，默认为空
	 * 
	 * @param[in] senMaxLen 只在全切分方法中使用
	 * 
	 * @param[in] recogPhrase 是否识别短语，默认为不识别，只对全切分有效
	 * 
	 * @return 成功返回>=0，否则失败
	 *//*

	public TPI_RETURN_RESULT KBase_Aptitude_WordSect( String srcBuf, long size, long hDict,
			int sectMethod, int[] POS, int senMaxLen, boolean recogPhrase){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_Aptitude_WordSect(this.connectionHset, srcBuf, size, hDict, sectMethod, POS, senMaxLen, recogPhrase);
		return result;
	}

	*/
/** ! @} 智能检索模块接口结束 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** 标注 相关函数 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_yidong 标注模块
	 * @ingroup group_NLPE
	 * @note 服务器端依赖的功能文件为ClassNoLabel.dll @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 初始化
	 * 
	 * @param[in] hcon 连接句柄
	 * @param[in] dictPath 训练好的分类词典完整路径和词典名
	 * 
	 * @return 成功返回>=0，否则失败
	 *//*

	public int KBase_ClassNoLabel_Init( String dictPath){
		int result = 0;
		result = this.kbaseClient.KBase_ClassNoLabel_Init(this.connectionHset,dictPath);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 待分类文本全文
	 * 
	 * @param[in] hcon 连接句柄
	 * @param[in] hDict 分类文本
	 * @param[out] result 文本的分类号
	 * @param[in,out] count 指定文本分类号的大小
	 * 
	 * @return 成功返回>=0，否则失败
	 *//*

	public TPI_RETURN_RESULT KBase_ClassNoLabel_GetClass( String text){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_ClassNoLabel_GetClass(this.connectionHset, text);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 卸载词典
	 * 
	 * @param[in] hcon 连接句柄
	 * 
	 * @return 成功返回>=0，否则失败
	 *//*

	public int KBase_ClassNoLabel_Free(){
		int result = 0;
		result = this.kbaseClient.KBase_ClassNoLabel_Free(this.connectionHset);
		return result;
	}

	*/
/** ! @} 标注接口结束 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** 文件抽词模块 相关函数 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_extract 文件抽词模块
	 * 
	 * @ingroup group_NLPE
	 * 
	 * @note 服务器端依赖的功能文件为ExtractWord.dll
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 文件抽词初始化
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] language 初始的语言库(1为中文，2为英文)
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_ExtractWord_Initial( int language){
		int result = 0;
		result = this.kbaseClient.KBase_ExtractWord_Initial(this.connectionHset,language);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 从文章抽词并进行归一化（KBASE数据库应该总是使用这个版本）
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] id 文章的输入信息
	 * 
	 * @param[in] word_buffer 返回排序后词语及其权重以及每个词语的分类号
	 * 
	 * @param[in,out] word_count 返回缓冲的大小，如果不确定可以设置为500
	 * 
	 * @param[in] language 分析的文本语言(1为中文，2为英文)
	 * 
	 * @param[in] average 确定取某些关键字时是否取正文临近关键词的平均权值，默认不取
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_ExtractWord_DirectNormal( InputDirect id, 
			WordInfor[] word_buffer, int word_count,int language, boolean average){
		int result = 0;
		result = this.kbaseClient.KBase_ExtractWord_DirectNormal(this.connectionHset, id, word_buffer, word_count, language, average);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 从文章抽词
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] id 文章的输入信息
	 * @param[in] word_buffer 返回排序后词语及其权重以及每个词语的分类号
	 * @param[in,out] word_count 返回缓冲的大小，如果不确定可以设置为500
	 * @param[in] language 分析的文本语言(1为中文，2为英文)
	 * @param[in] average 确定取某些关键字时是否取正文临近关键词的平均权值，默认不取
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_ExtractWord_Direct( InputDirect id, 
			WordInfor[] word_buffer, int word_count,int language, boolean average){
		int result = 0;
		result = this.kbaseClient.KBase_ExtractWord_Direct(this.connectionHset, id, word_buffer, word_count, language, average);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 文件抽词释放
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_ExtractWord_FreeDict(){
		int result = 0;
		result = this.kbaseClient.KBase_ExtractWord_FreeDict(this.connectionHset);
		return result;
	}

	*/
/** ! @} 文件抽词模块接口结束 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** 主题分类词表 相关函数 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_EZTC 主题分类词表
	 * 
	 * @ingroup group_NLPE
	 * 
	 * @note 服务器端依赖的功能文件为Extend_ZTC.dll
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 设定系统目录路径
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] path 服务器段系统目录路径
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_EZTC_SetPath(){
		int result = 0;
		result = this.kbaseClient.KBase_EZTC_SetPath(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 获得文章的分类号
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] classmode 1 - 期刊分类，2 - 报纸分类;
	 * 
	 * @param[in] language 1 - 中文，2 - 英文
	 * 
	 * @param[in] cif 文章的输入信息
	 * 
	 * @param[in] mode 输入模式。1 - 输出第一层的分类号，2 - 将输出第二层的分类号。
	 * 
	 * @param[out] classno 文章的分类号缓冲
	 * 
	 * @param[in,out] size 分类号缓冲的大小
	 * 
	 * @param[in] bselect true为输出经过阈值过滤的结果，false为不经过阈值过滤，返回前三个结果。
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_EZTC_GetTxtClassify( int classmode, 
			int language, CLA_Init_File cif, int mode, boolean bselect){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_EZTC_GetTxtClassify(this.connectionHset, classmode, language, cif, mode, bselect);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 释放主题分类词表
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_EZTC_FreeDict(){
		int result = 0;
		result = this.kbaseClient.KBase_EZTC_FreeDict(this.connectionHset);
		return result;
	}

	*/
/** ! @} 主题分类词表 模块接口结束 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** BSM 相关函数 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_EZTC 主题分类词表
	 * 
	 * @ingroup group_NLPE
	 * 
	 * @note 服务器端依赖的功能文件为Extend_ZTC.dll
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 设定系统目录路径
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_BSM_Word_Init(){
		int result = 0;
		result = this.kbaseClient.KBase_BSM_Word_Init(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 获得文章的分类号
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] bif 文章的输入信息
	 *            在博硕士标识码分类中输入以下字段：Title：标题，Key：关键词，Abs：摘要，classNo：中图分类号,其他为空
	 *            在期刊标识码分类中输入以下字段：必选:Title：标题，Key：关键词，Abs：摘要，Content：全文，可选:classNo：中图分类号，
	 *            qkBsm：期刊自带的标识码名称（汉字，不是数字），docucode：期刊docucode字段，Km：刊名。
	 * 
	 * @param[in] classmode 1 - 期刊标识码标注 2 - 博硕士标识码标注（常用）
	 * 
	 * @param[in] language 1 - 中文（常用） 2 - 英文
	 * 
	 * @param[out] classno 文章的分类号缓冲
	 * 
	 * @param[in,out] classno_size 字符串classno的大小，最小取5
	 * 
	 * @param[out] degree 文章的分类号缓冲
	 * 
	 * @param[in,out] degree_size 字符串degree的大小，最小取5
	 * 
	 * @param[in] high true表示该结果属于高准确结果集，否则属于待检查结果集
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_BSM_GetClassifyResult( int classmode, int language, BSM_Init_File bif, boolean high){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_BSM_GetClassifyResult(this.connectionHset, classmode, language, bif, high);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 释放主题分类词表
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_BSM_FreeDict(){
		int result = 0;
		result = this.kbaseClient.KBase_BSM_FreeDict(this.connectionHset);
		return result;
	}

	*/
/** ! @} 主题分类词表 模块接口结束 *//*


	*/
/**
	 * !
	 * 
	 * @defgroup group_STM 文本挖掘引擎扩展模块
	 * 
	 * @note 文本挖掘引擎主要提供一些基本的文本数据处理方案\n 服务器对应的扩展文件为SmartTextMiner.dll
	 *//*

	*/
/*****************************************************************//*


	*/
/**																*//*

	*/
/** 文本挖掘引擎 相关函数 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_STM_basic 文本挖掘引擎基本函数
	 * 
	 * @ingroup group_STM
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 初始化文本挖掘引擎
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_Initialize(){
		int result = 0;
		result = this.kbaseClient.KBase_STM_Initialize(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 释放文本挖掘引擎
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_ShutDown(){
		int result = 0;
		result = this.kbaseClient.KBase_STM_ShutDown(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 判断文本挖掘引擎是否启动
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 1表示启动，0表示不启动，小于0为错误码
	 *//*

	public int KBase_STM_IsInitialized(){
		int result = 0;
		result = this.kbaseClient.KBase_STM_IsInitialized(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 创建知识域
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] domain_name 欲创建知识域的名称
	 * 
	 * @param[in] domain_path 保存知识域的路径，默认为NULL
	 * 
	 * @param[in] dict_name 创建知识域的词典名称，默认为NULL
	 * 
	 * @param[in] dict_path 其词典的路径，默认为NULL
	 * 
	 * @param[in] language 知识域语言类型，0 - 中文 1 - 英文
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_CreateDomain( String domain_name, String domain_path, String dict_name,
			String dict_path, int language){
		int result = 0;
		result = this.kbaseClient.KBase_STM_CreateDomain(this.connectionHset, domain_name, domain_path, dict_name, dict_path, language);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 删除知识域
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] domain_list 要删除的知识域名称列表，用,分割
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_DeleteDomain( String domain_list){
		int result = 0;
		result = this.kbaseClient.KBase_STM_DeleteDomain(this.connectionHset,domain_list);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 得到当前的知识域列表
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[out] name_buf 输出缓冲，返回知识域名称列表，用','分割
	 * 
	 * @param[in,out] buf_len 输出缓冲大小
	 * 
	 * @param[in] flag 0表示列举所有的知识域， 1表示列举已经打开的知识域
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_STM_GetDomainNameList( int flag){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_STM_GetDomainNameList(this.connectionHset,flag);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 打开指定知识域
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] domain_list 欲打开的知识域名称列表，用,分割
	 * 
	 * @return 若返回值小于0表示出错，否则为列表中第一个知识域的句柄（HDOMAIN）
	 *//*

	public int KBase_STM_OpenDomain( String domain_list){
		int result = 0;
		result = kbaseClient.KBase_STM_OpenDomain(this.connectionHset,domain_list);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 关闭指定知识域
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] domain_list 欲关闭的知识域名称列表，用,分割
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_CloseDomain( String domain_list){
		int result = 0;
		result = kbaseClient.KBase_STM_CloseDomain(this.connectionHset,domain_list);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 根据知识域句柄得到知识域名称
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @param[out] domain_name 返回知识域名称缓冲
	 * 
	 * @param[in,out] size 返回知识域名称缓冲的大小
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_STM_GetDomainName( int hdomain){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_STM_GetDomainName(this.connectionHset,hdomain);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 根据知识域名称得到其句柄
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] domain_name 知识域名称
	 * 
	 * @return 若函数执行成功，返回知识域句柄，否则小于0
	 *//*

	public int KBase_STM_GetDomainHandle( String domain_name){
		int result = 0;
		result = this.kbaseClient.KBase_STM_GetDomainHandle(this.connectionHset,domain_name);
		return result;
	}

	*/
/** ! @} 文本挖掘引擎基本函数结束 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** 文本分类器相关函数 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_STM_TC 文本分类器相关函数
	 * 
	 * @ingroup group_STM
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 启动文本分类器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @param[in] nCType 分类器类型
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_TextClassifier_Init( int hdomain,int nCType*/
/** knn =0 *//*
){
		int result = 0;
		result = this.kbaseClient.KBase_STM_TextClassifier_Init(this.connectionHset,hdomain,nCType);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 得到文本分类结果
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @param[out] result 文本分类结果
	 * 
	 * @param[in,out] len 文本分类结果缓冲长度
	 * 
	 * @param[in] weight 文本权重信息结构数组
	 * 
	 * @param[in] itemcount 文本权重信息结构数组数目
	 * 
	 * @param[in] param 文本分类器的相关参数
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_STM_TextClassifier_Do( int hdomain, KSTM_TEXTWEIGHT[] weight,
			int itemcount, KSTM_CLASSIFIER param){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = this.kbaseClient.KBase_STM_TextClassifier_Do(this.connectionHset, hdomain, weight, itemcount, param);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 关闭文本分类器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_TextClassifier_ShutDown( int hdomain){
		int result = 0;
		result = this.kbaseClient.KBase_STM_TextClassifier_ShutDown(this.connectionHset,hdomain);
		return result;
	}

	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** 信息过滤器相关函数 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_STM_IF 信息过滤器相关函数
	 * 
	 * @ingroup group_STM
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 启动信息过滤器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @param[in] profile 信息过滤器的模板名称
	 * 
	 * @return 若函数执行成功，返回0，否则小于00
	 *//*

	public int KBase_STM_InfoFilter_Init( int hdomain, String profile){
		int result = 0;
		result = this.kbaseClient.KBase_STM_InfoFilter_Init(this.connectionHset,hdomain,profile);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 执行信息过滤
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @param[in] weight 文本权重信息结构数组
	 * 
	 * @param[in] itemcount 文本权重信息结构数组数目
	 * 
	 * @return 若函数执行失败返回小于0，否则返回当前文档和模板的相关度。
	 *//*

	public int KBase_STM_InfoFilter_Do( int hdomain, KSTM_TEXTWEIGHT[] weight, int itemcount){
		int result = 0;
		result = this.kbaseClient.KBase_STM_InfoFilter_Do(this.connectionHset,hdomain,weight,itemcount);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 关闭信息过滤器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_InfoFilter_ShutDown( int hdomain){
		int result = 0;
		result = this.kbaseClient.KBase_STM_InfoFilter_ShutDown(this.connectionHset,hdomain);
		return result;
	}

	*/
/** ! @} 信息过滤器相关函数结束 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** 文本聚类器相关函数 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_STM_Cluster 文本聚类器相关函数
	 * 
	 * @ingroup group_STM
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 初始文本聚类器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_TextCluster_Init( int hdomain){
		int result = 0;
		result = this.kbaseClient.KBase_STM_TextCluster_Init(this.connectionHset,hdomain);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 执行文本聚类
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @param[in] weight 文本权重信息结构数组
	 * 
	 * @param[in] itemcount 文本权重信息结构数组数目
	 * 
	 * @param[in] docid 对应的文本id字符串
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 * 
	 *         \note 文本id字符串说明：
	 *//*

	public int KBase_STM_TextCluster_Do( int hdomain, KSTM_TEXTWEIGHT[] weight, int itemcount,String docid){
		int result = 0;
		result = this.kbaseClient.KBase_STM_TextCluster_Do(this.connectionHset, hdomain, weight, itemcount, docid);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 关闭文本聚类器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] hdomain 知识域句柄
	 * @param[in] param 信息过滤器的参数结构
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_TextCluster_ShutDown( int hdomain, KSTM_CLUSTER param){
		int result = 0;
		result = this.kbaseClient.KBase_STM_TextCluster_ShutDown(this.connectionHset,hdomain,param);
		return result;
	}

	*/
/** ! @} 文本聚类器相关函数结束 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** 关键词提取器相关函数 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_STM_KWE 关键词提取器相关函数
	 * 
	 * @ingroup group_STM
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 启动关键词提取器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @param[in] flag 如果为1，表示在英文知识域环境下，同时输出中文关键词，否则为0。
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_KeyWordExtractor_Init( int hdomain, int flag){
		int result = 0;
		result = this.kbaseClient.KBase_STM_KeyWordExtractor_Init(this.connectionHset,hdomain,flag);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 执行关键词提取
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @param[out] result 关键词结果缓冲
	 * 
	 * @param[in,out] len 关键词结果缓冲长度
	 * 
	 * @param[in] weight 文本权重信息结构数组
	 * 
	 * @param[in] itemcount 文本权重信息结构数组数目
	 * 
	 * @param[in] param 关键词提取器的相关参数
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_STM_KeyWordExtractor_Do( int hdomain,
			KSTM_TEXTWEIGHT[] weight, int itemcount, KSTM_KEYWORD_EXTRACTOR param){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_STM_KeyWordExtractor_Do(this.connectionHset, hdomain, weight, itemcount, param);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 关闭关键词提取器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_KeyWordExtractor_ShutDown( int hdomain){
		int result = 0;
		result = this.kbaseClient.KBase_STM_KeyWordExtractor_ShutDown(this.connectionHset,hdomain);
		return result;
	}

	*/
/** ! @} 关键词提取器相关函数结束 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** 自动摘要提取器相关函数 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_STM_AE 自动摘要提取器相关函数
	 * 
	 * @ingroup group_STM
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 启动自动摘要提取器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_AbstractExtractor_Initialize( int hdomain){
		int result = 0;
		result = this.kbaseClient.KBase_STM_AbstractExtractor_Initialize(this.connectionHset,hdomain);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 执行自动摘要提取
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @param[in] src_text 欲进行自动摘要的文本
	 * 
	 * @param[out] result 自动摘要结果缓冲
	 * 
	 * @param[in,out] len 自动摘要结果缓冲长度
	 * 
	 * @param[in] param 自动摘要提取器的相关参数结构
	 * 
	 * @param[in] weight 影响自动摘要文本的权重信息结构数组
	 * 
	 * @param[in] itemcount 影响自动摘要文本的权重信息结构数组数目
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_STM_AbstractExtractor_Do( int hdomain, String src_text,
			KSTM_ABSTRACT_EXTRACTOR param, KSTM_TEXTWEIGHT[] weight, int itemcount){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_STM_AbstractExtractor_Do(this.connectionHset, hdomain, src_text, param, weight, itemcount);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 关闭自动摘要提取器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_AbstractExtractor_ShutDown( int hdomain){
		int result = 0;
		result = this.kbaseClient.KBase_STM_AbstractExtractor_ShutDown(this.connectionHset,hdomain);
		return result;
	}

	*/
/** ! @} 自动摘要提取器相关函数结束 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** VSM向量空间模型生成器 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_STM_VSM VSM向量空间模型生成器
	 * 
	 * @ingroup group_STM
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 启动VSM向量空间模型生成器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_VSMGenerator_Init( int hdomain){
		int result = 0;
		result = this.kbaseClient.KBase_STM_VSMGenerator_Init(this.connectionHset,hdomain);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 生成VSM向量空间模型
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @param[out] result VSM向量结果缓冲
	 * 
	 * @param[in,out] len VSM向量结果缓冲长度
	 * 
	 * @param[in] weight 文本权重信息结构数组
	 * 
	 * @param[in] itemcount 文本权重信息结构数组数目
	 * 
	 * @param[in] param VSM向量空间模型器的相关参数结构
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_STM_VSMGenerator_Do( int hdomain, KSTM_TEXTWEIGHT[] weight,
			int itemcount, KSTM_VSM_GENERATOR param){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_STM_VSMGenerator_Do(this.connectionHset, hdomain, weight, itemcount, param);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 简易生成VSM向量空间模型
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] hdomain 知识域句柄
	 * @param[out] result VSM向量结果缓冲
	 * @param[in,out] len VSM向量结果缓冲长度
	 * @param[in] src_text 需要VSM处理的文本缓冲
	 * @param[in] param VSM向量空间模型器的相关参数结构
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_STM_VSMGenerator_GetResult( int hdomain, String src_text,
			KSTM_VSM_GENERATOR param){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_STM_VSMGenerator_GetResult(this.connectionHset, hdomain, src_text, param);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 关闭VSM向量空间模型生成器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] hdomain 知识域句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_VSMGenerator_ShutDown( int hdomain){
		int result = 0;
		result = this.kbaseClient.KBase_STM_VSMGenerator_ShutDown(this.connectionHset,hdomain);
		return result;
	}

	*/
/** ! @} VSM向量空间模型生成器结束 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** 文本相似度模型 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_STM_SIMIL VSM文本相似度模型
	 * 
	 * @ingroup group_STM
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 启动文本相似度模型
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_Similarity_Initialize(){
		int result = 0;
		result = this.kbaseClient.KBase_STM_Similarity_Initialize(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 关闭文本相似度模型
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_Similarity_ShutDown(){
		int result = 0;
		result = this.kbaseClient.KBase_STM_Similarity_ShutDown(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 进行文本相似度比较
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] vsm1 进行比较的VSM文本1
	 * 
	 * @param[in] vsm2 进行比较的VSM文本2
	 * 
	 * @return 若函数返回0-100之间的值，表示2文本之间的相似度，若返回值小于0为错误码
	 *//*

	public int KBase_STM_Similarity_Analyst( String vsm1, String vsm2){
		int result = 0;
		result = this.kbaseClient.KBase_STM_Similarity_Analyst(this.connectionHset,vsm1,vsm2);
		return result;
	}

	*/
/** ! @} VSM文本相似度模型结束 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** 普通自动摘要提取器 *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_STM_ABSTR 普通自动摘要提取器
	 * 
	 * @ingroup group_STM
	 * 
	 * @note 注：此功能需要依赖于KAbstrart.dll
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 启动普通自动摘要提取器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_Abstractor_Init(){
		int result = 0;
		result = this.kbaseClient.KBase_STM_Abstractor_Init(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 执行自动摘要提取
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] src_text 欲进行普通自动摘要的文本
	 * 
	 * @param[out] result 普通自动摘要结果缓冲
	 * 
	 * @param[in,out] len 普通自动摘要结果缓冲长度
	 * 
	 * @param[in] param 普通自动摘要提取器的相关参数结构
	 * 
	 * @param[in] weight 影响普通自动摘要文本的权重信息结构数组
	 * 
	 * @param[in] itemcount 影响普通自动摘要文本的权重信息结构数组数目
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*


	public TPI_RETURN_RESULT KBase_STM_Abstractor_Do( String src_text,
			KSTM_ABSTRACT_EXTRACTOR param, KSTM_TEXTWEIGHT[] weight, int itemcount){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_STM_Abstractor_Do(this.connectionHset, src_text, param, weight, itemcount);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 关闭普通自动摘要提取器
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_STM_Abstractor_ShutDown(){
		int result = 0;
		result = this.kbaseClient.KBase_STM_Abstractor_ShutDown(this.connectionHset);
		return result;
	}

	*/
/** ! @} 普通自动摘要提取器结束 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** ARA *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_ARA ARA模块
	 * 
	 * @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 统计关联规则对同现频率开始
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] tmp_path 临时文件夹。即统计过程中产生的临时结果文件的存放文件夹
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_ARA_Stat_AssociatedFreq_Begin( String tmp_path){
		int result = 0;
		result = this.kbaseClient.KBase_ARA_Stat_AssociatedFreq_Begin(this.connectionHset,tmp_path);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 统计关联规则对同现频率
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] item1 规则一
	 * 
	 * @param[in] item2 规则二
	 * 
	 * @param[in] freq 频率
	 * 
	 * @param[in] ordered 规则对因果关系是否可逆(规则对是否有序)
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_ARA_Stat_AssociatedFreq( String item1, String item2, int freq, boolean ordered){
		int result = 0;
		result = this.kbaseClient.KBase_ARA_Stat_AssociatedFreq(this.connectionHset, item1, item2, freq, ordered);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 统计关联规则对同现频率结束
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_ARA_Stat_AssociatedFreq_End(){
		int result = 0;
		result = this.kbaseClient.KBase_ARA_Stat_AssociatedFreq_End(this.connectionHset);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 合并指定文件夹中的文件到指定名称的文件中
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] tmp_path 临时文件夹路径
	 * 
	 * @param[in] out_file 合并后的文件名
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 * 
	 *         \note 注意合并的文件必须是按照第一个规则从小到大排序的文件
	 *//*

	public int KBase_ARA_MulMergeFile( String tmp_path, String out_file){
		int result = 0;
		result = this.kbaseClient.KBase_ARA_MulMergeFile(this.connectionHset, tmp_path, out_file);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 过滤同现频率较小的规则对
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] filename 关联规则存放的文件名
	 * 
	 * @param[in] out_file 过滤后的文件名
	 * 
	 * @param[in] minfreq 过滤阙值
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 * 
	 *         \note 注意合并的文件格式为：规则A(char) 规则B(char) 同现频率 freq(int)
	 *//*

	public int KBase_ARA_Filtrate( String filename, String out_file, int minfreq){
		int result = 0;
		result = this.kbaseClient.KBase_ARA_Filtrate(this.connectionHset, filename, out_file, minfreq);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 计算同现关联规则的支持度及置信度
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] dict_name 关联规则存放的文件名
	 * 
	 * @param[in] out_file 计算结果的输出路径。
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 * 
	 *         \note 关联规则存放的文件格式为，规则A(char) 规则B(char) 同现频率 freq(int)
	 *         同时注意计算结果为一个REC文件，其格式为： \skip
	 *         <rec>\n<基准规则>=%s\n<关联规则>=%s\n<置信度>=%f\n<支持度>=%f\n
	 *//*

	public int KBase_ARA_GetWordSupportRec( String dict_name, String out_file){
		int result = 0;
		result = this.kbaseClient.KBase_ARA_GetWordSupportRec(this.connectionHset, dict_name, out_file);
		return result;
	}

	*/
/** ! @} ARA模块 *//*


	*/
/*****************************************************************//*

	*/
/**																*//*

	*/
/** KWFS *//*

	*/
/**																*//*

	*/
/*****************************************************************//*

	*/
/**
	 * !
	 * 
	 * @defgroup group_KWFS KWFS模块 @{
	 *//*


	*/
/**
	 * !
	 * 
	 * @brief 计算文本数据中的词频信息
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] input 欲计算的文本数据结构
	 * @param[in] count 欲计算的文本数据结构数目
	 * @param[in] out 计算结果的输出参数结构
	 * @param[in] threshold 最小的有效过滤阙值
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_KWFS_Frequency( KWFSInput input, int count, KWFSOutput out, int threshold){
		int result = 0;
		result = this.kbaseClient.KBase_KWFS_Frequency(this.connectionHset, input, count, out, threshold);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 计算文本数据中的共现信息
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] input 欲计算的文本数据结构
	 * @param[in] count 欲计算的文本数据结构数目
	 * @param[in] out 计算结果的输出参数结构
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_KWFS_Associated( KWFSInput input, int count, KWFSOutput out){
		int result = 0;
		result = this.kbaseClient.KBase_KWFS_Associated(this.connectionHset, input, count, out);
		return result;
	}

	*/
/** KWFS模块结束 *//*

	*/
/**
	 * !
	 * 
	 * @brief 列出目前可用的top数据列表
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[out] top_flag 可用的top词典列表返回，中间用,分割
	 * 
	 * @param[in] buf_size top_flag缓冲大小
	 * 
	 * @param[in] with_status 是否同时返回词典的状态，紧跟词典用:隔开，1表示已加载
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public TPI_RETURN_RESULT KBase_TOP_List( int with_status){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_TOP_List(this.connectionHset,with_status);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 初始化top结构
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] top_flag 词典名称
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_TOP_Init( String top_flag){
		int result = 0;
		result = this.kbaseClient.KBase_TOP_Init(this.connectionHset,top_flag);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief Top增加一个词典
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] dictname 词典名称
	 * 
	 * @param[in] dictpath 词典路径
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_Top_AddDict( String dictname, String dictpath){
		int result = 0;
		result = this.kbaseClient.KBase_Top_AddDict(this.connectionHset,dictname,dictpath);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief Top移除一个词典
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] dictname 词典名称
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_Top_RemoveDict( String dictname){
		int result = 0;
		result = this.kbaseClient.KBase_Top_RemoveDict(this.connectionHset,dictname);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 得到top10列表
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] top_flag 词典名称
	 * 
	 * @param[in] item 想得到的词语项
	 * 
	 * @param[out] topitem 返回相关词语的缓冲，中间用,隔开
	 * 
	 * @param[in,out] buflen 缓冲的大小
	 * 
	 * @return 若函数执行成功，返回0，否则小于0 注意返回TPI_ERR_DICTNOITEM表示词典中并没有相关条目
	 *//*

	public TPI_RETURN_RESULT KBase_TOP_GetItem( String top_flag, String item){
		TPI_RETURN_RESULT result = new TPI_RETURN_RESULT();
		result = kbaseClient.KBase_TOP_GetItem(this.connectionHset, top_flag, item);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 释放top表，防止内存占用太大
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] top_flag top结构的标示串，如要初始化多个，中间用,隔开
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_TOP_UnInit( String top_flag){
		int result = 0;
		result = this.kbaseClient.KBase_TOP_UnInit(this.connectionHset,top_flag);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 将sql产生的结果集导出到分隔符文件
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * 
	 * @param[in] sql top结构的标示串，如要初始化多个，中间用,隔开
	 * 
	 * @param[in] filename 文本文件名称
	 * 
	 * @param[in] sep 分隔符，默认为\t。
	 * 
	 * @param[in] code 编码格式，默认为"ansi"
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_ExportSqlToFile( String sql, String filename, int sep, String code){
		int result = 0;
		result = this.kbaseClient.KBase_ExportSqlToFile(this.connectionHset, sql, filename, sep, code);
		return result;
	}

	*/
/**
	 * !
	 * 
	 * @brief 将sql产生的结果集导出到分隔符文件
	 * 
	 * @param[in] hcon 连接服务器的句柄
	 * @param[in] sql top结构的标示串，如要初始化多个，中间用,隔开
	 * @param[in] filename 文本文件名称
	 * @param[in] sep 分隔符，默认为\t。
	 * @param[in] code 编码格式，默认为"ansi"
	 * 
	 * @return 若函数执行成功，返回0，否则小于0
	 *//*

	public int KBase_MakeTopDict( String filename, String ndxfile){
		int result = 0;
		result = this.kbaseClient.KBase_MakeTopDict(this.connectionHset, filename, ndxfile);
		return result;
	}
	
	
	*/
/**
	 * 扩展接口end
	 *//*

}
*/
