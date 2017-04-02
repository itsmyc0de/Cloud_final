/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EnergyEfficient;

/**
 *
 * @author User
 */
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

public class ReSchedulingPrediction {

    public ReSchedulingPrediction() {


        int a[] = null;
        int b[] = new int[6];
        int kk[] = new int[6];

        String strfaultidqry = "select faultid from tblfaultidcluster order by faultid asc";
        int faultvalue = 0, faultid = 0;


        database_conn db = new database_conn();
        try {

            ResultSet rs = db.st.executeQuery(strfaultidqry);


            String rowcountqry = "select count(*) from tblResourceBundling";

            ResultSet rss = db.stat.executeQuery(rowcountqry);
            int rowcount = 0;
            while (rss.next()) {
                rowcount = Integer.parseInt(rss.getString(1));
            }

            a = new int[rowcount];
            String strqry2 = "select * from tblResourceBundling";
            ResultSet rs2 = db.stat1.executeQuery(strqry2);
            int arri = 0;
            while (rs2.next()) {


                a[arri] = Integer.parseInt(rs2.getString(1));

                arri = arri + 1;
            }

            rs2.last();
            int rowCount1 = rs2.getRow();
            System.out.println("Number of Rows=" + rowCount1);

            int i = 0, m = 0;
            while (rs.next()) {

                faultid = Integer.parseInt(rs.getString(1).trim());
                b[i] = faultid;
                System.out.println("----" + i + "---" + b[i]);
                i = i + 1;
                String qrystr1 = "select * from tbltaskProcess where id='" + faultid + "'";
                ResultSet rs1 = db.stat2.executeQuery(qrystr1);
                while (rs1.next()) {
                    faultvalue = Integer.parseInt(rs1.getString(4).trim());
                    int gridletid = 0;
                    String strqry = "insert into tblfault values('" + faultid + "','" + faultvalue + "')";
                    db.stat3.executeUpdate(strqry);
                    System.out.println(faultvalue);
                }
            }
            int c[] = c = new int[20];
//            ;
            int l = 0;
            for (int k = 0; k < a.length; k++) {
                if (a[k] == b[0] || a[k] == b[1] || a[k] == b[2] || a[k] == b[3]) {
                    System.out.println("fault occured");
                } else {

                    //	ADOS.txtarea.append("******"+a[k]+'\n');
                    System.out.println("******" + a[k] + '\n');
                    c[l] = a[k];
                    l = l + 1;
                }
           }
            for (int ii = 0; ii < l; ii++) {
               System.out.println("" + c[ii] + "\n");
            }
            int getfaultid[] = new int[4];
            int n = 0;
            ResultSet rs3 = db.stat4.executeQuery("select distinct * from tblfaultidcluster");
            while (rs3.next()) {
                getfaultid[n] = Integer.parseInt(rs3.getString(1));
                n++;
            }
           for (int p = 0; p < n; p++) {
                String qrystr1 = "select * from tbltaskProcess where id='" + getfaultid[p] + "'";
                ResultSet rs6 = db.stat5.executeQuery(qrystr1);
                while (rs6.next()) {
                    faultvalue = Integer.parseInt(rs6.getString(4).trim());
                    kk[m] = faultvalue;
                    m++;
               }
           }
            ///////////////////////total work completion time

             int mb = 1024*1024;
            boolean flag = false;
            int ino = 1;
            for (int fori = 0; fori < m; fori++) {
                for (int jk = 0; jk < l; jk++) {
                    System.out.println("Cloudlet ID" + c[jk]);
                    System.out.println("withoutfaultexecution" + fori + "----" + kk[fori]);
                    Runtime runtime = Runtime.getRuntime();

                   double usedmemory= (runtime.totalMemory() - runtime.freeMemory()) / mb;
                   System.out.println("\nUsed Memory For Ados______"+ c[jk] + "\t" +usedmemory+ "\n"  );
                   System.out.println("Cloudlet ID" + c[jk] + "\n" + "Fault Occured Value" + kk[fori] + "\n");
                    String qrystr="insert into tblusedmemory values('"+c[jk]+"', 'ADOS','"+usedmemory+"')";
                   db.stat7.executeUpdate(qrystr);


                    Calendar cal = new GregorianCalendar();//
                    final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                    final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                    System.out.println(sdf.format(cal.getTime()));
                    String msecstarttime = sdf.format(cal.getTime());
                    int j = kk[fori], start = 0, end = 0;
                    int m1 = 100, m2 = 200, m3 = 300, m4 = 400, m5 = 500, m6 = 600, m7 = 700, m8 = 800, m9 = 900, m10 = 1000, m11 = 1100, m12 = 1200, m13 = 1300, m14 = 1400, m15 = 1500, m16 = 1600, m17 = 1700, m18 = 1800, m19 = 1900, m20 = 2000;
                   if ((m1 > j) && (j < (m1 + 100))) {
                        start = m1 - 100;
                        end = m1;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m2 > j) && (j < (m2 + 100))) {
                        start = m2 - 100;
                        end = m2;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m3 > j) && (j < (m3 + 100))) {
                        start = m3 - 100;
                        end = m3;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m4 > j) && (j < (m4 + 100))) {
                        start = m4 - 100;
                        end = m4;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m5 > j) && (j < (m5 + 100))) {
                        start = m5 - 100;
                        end = m5;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m6 > j) && (j < (m6 + 100))) {
                        start = m6 - 100;
                        end = m6;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m7 > j) && (j < (m7 + 100))) {
                        start = m7 - 100;
                        end = m7;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m8 > j) && (j < (m8 + 100))) {
                        start = m8 - 100;
                        end = m8;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m9 > j) && (j < (m9 + 100))) {
                        start = m9 - 100;
                        end = m9;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m10 > j) && (j < (m10 + 100))) {
                        start = m10 - 100;
                        end = m10;
                       System.out.println(start);
                        System.out.println(end);
                    } else if ((m11 > j) && (j < (m11 + 100))) {
                        start = m11 - 100;
                        end = m11;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m12 > j) && (j < (m12 + 100))) {
                        start = m12 - 100;
                        end = m12;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m13 > j) && (j < (m13 + 100))) {
                        start = m13 - 100;
                        end = m13;
                       System.out.println(start);
                        System.out.println(end);
                    } else if ((m13 > j) && (j < (m13 + 100))) {
                        start = m13 - 100;
                        end = m13;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m14 > j) && (j < (m14 + 100))) {
                        start = m14 - 100;
                        end = m14;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m15 > j) && (j < (m15 + 100))) {
                        start = m15 - 100;
                        end = m15;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m16 > j) && (j < (m16 + 100))) {
                        start = m16 - 100;
                        end = m16;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m17 > j) && (j < (m17 + 100))) {
                        start = m17 - 100;
                        end = m17;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m18 > j) && (j < (m18 + 100))) {
                        start = m18 - 100;
                        end = m18;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m19 > j) && (j < (m19 + 100))) {
                        start = m19 - 100;
                        end = m19;
                        System.out.println(start);
                        System.out.println(end);
                    } else if ((m20 > j) && (j < (m20 + 100))) {
                        start = m20 - 100;
                        end = m20;
                        System.out.println(start);
                        System.out.println(end);
                    }
                   System.out.println("Start Time" + msecstarttime + "\n");
                    n = end;
                    System.out.println("Start Time" + msecstarttime);
                    n = end;
                    for (int forii = start; forii <= end; forii++) {

                        for (int jj = 2; jj < forii; jj++) {
                            if (forii <= n) {
                                if (forii % jj == 0) {
                                    flag = true;
                                }
                            }
                        }
                        if (!flag) {
                            
                            System.out.print("_______" + forii + "\n");
                        }
                        flag = false;
                    }
                    Thread t = new Thread();
                    t.sleep(1001);
                    Calendar cal1 = new GregorianCalendar();//
                    final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                    final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                    System.out.println(sdf.format(cal1.getTime()));
                    String msecendtime = sdf.format(cal1.getTime());
                    System.out.println("End Time" + msecendtime);
                    
                    int ii = fori + 1;
                    String strqry = "insert into tbldualobjexecution1 values('" + fori + "','" + c[jk] + "','" + msecstarttime + "','" + msecendtime + "') ";
                    db.stat6.executeUpdate(strqry);
                }
            }
        } catch (Exception e) {
           // e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        ReSchedulingPrediction r = new ReSchedulingPrediction();
    }
}
