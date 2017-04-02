package EnergyEfficient;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
//coding for java database connectivity
public class database_conn
{
 Connection con=null;
 Statement stat=null;
 Statement stat1=null;
 Statement stat2=null;
 Statement stat3=null;
 Statement stat4=null;
 public Statement stat5=null;
 public Statement stat6=null;
  public Statement stat7=null;
 Statement st=null;
 public database_conn()
 {
	 try
	 {
	   Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	   
	  con=DriverManager.getConnection("jdbc:odbc:Driver={SQL Server};Server=SUDHEESH-PC\\SQLEXPRESS;Database=gridsim1");
	  // con=DriverManager.getConnection("Password="";Persist Security Info=True;User ID=sa;Initial Catalog=aak;Data Source=ANANDH");
       st=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
	   stat=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
	   stat1=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
 	   stat2=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
 	   stat3=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
 	   stat4=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
 	   stat5=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
       stat6=con.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
       stat7=con.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
       System.out.println("In database_conn.java ");
     }
     catch(Exception e)
     {
		 System.out.println("Error in database_conn.java "+e);
	 }
 }

public static void main(String a[])
{
	database_conn db =new database_conn();
}
}

