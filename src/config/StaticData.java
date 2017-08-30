package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class StaticData {
	// public static String
	// EXP_HOME="C:/My MSc/ThesisWorks/Crowdsource_Knowledge_Base/SOCommentStudy/exploratory";
	// public static String EXP_HOME
	// ="F:/MyWorks/Thesis Works/Crowdsource_Knowledge_Base/M4CPBugs/exploratory";
	public static String EXP_HOME = "C:/My MSc/ThesisWorks/Crowdsource_Knowledge_Base/M4CPBugs/exploratory";
	public static String RQ_HOME = EXP_HOME
			+ "/csv/java-post-all/dbresults/RQs";

	// public static String BRICK_EXP
	// ="F:/MyWorks/Thesis Works/Crowdsource_Knowledge_Base/M4CPBugs/experiment/replication-data";
	public static String BRICK_EXP = System.getProperty("user.dir");;// "C:/My MSc/ThesisWorks/Crowdsource_Knowledge_Base/M4CPBugs/experiment/replication-data";
	public static String JDK_SRC_PATH = "C:/Program Files/Java/jdk1.7.0_07/src";

	public static String SSYSTEMS = "C:/My MSc/ThesisWorks/Crowdsource_Knowledge_Base/SOTraceQData/ssystems";
	static String Database_name = "m4cpbugsv2";// "stackoverflow2014p3";//
												// "m4cpbugsv2";//"stackoverflow2014p7"
												// "m4cpbugs"//// "vendasta";
	public static String connectionString = "jdbc:sqlserver://localhost:1433;databaseName="
			+ Database_name + ";integratedSecurity=true";
	public static String Driver_name = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	public static double SIGNIFICANCE_THRESHOLD = 0.001;
	public static int DOI_TOPK_THRESHOLD = 5;
	public static int MAX_SUGGESTED_QUERY_LEN = 20;
	public static int MAX_NL_QUERY_LEN = 8;
	public static int MAX_ST_THRESHOLD = 10;
	public static int MAX_QUERY_LEN = 1024;

	public static boolean DISCARD_TOO_GOOD = false;

	public static Connection getDBConnection() {
		Connection connection = null;
		try {
			Class.forName(StaticData.Driver_name).newInstance();
			connection = DriverManager
					.getConnection(StaticData.connectionString);

		} catch (Exception exc) {
			// handle the exception
		}
		return connection;
	}

	public static int CROWD_API_OCC_THRESHOLD = 5;

}
