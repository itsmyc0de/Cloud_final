package EnergyEfficient;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
//package org.cloudbus.cloudsim.examples;
import java.io.*;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;



import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCell;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
public class Cloudmainpack {

    /** The cloudlet list. */
	public static int cloudletnumber=0;
	public static int vmnumber=0;
	public static int dcent=0;
	public static int gggg=0;
    private static List<Cloudlet> cloudletList;
    /** The vmlist. */
    private static List<Vm> vmlist;

    private static List<Vm> createVM(int userId, int vms) {

        // Creates a container to store VMs. This list is passed to the broker
        // later
        LinkedList<Vm> list = new LinkedList<Vm>();

        // VM Parameters
        long size = 10000; // image size (MB)
        int ram = 512; // vm memory (MB)
        int mips = 250;
        long bw = 1000;
        int pesNumber = 1; // number of cpus
        String vmm = "Xen"; // VMM name

        // create VMs
        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
            vm[i] = new Vm(i, userId, mips, pesNumber, ram, bw, size, vmm,
                    new CloudletSchedulerTimeShared());
            // for creating a VM with a space shared scheduling policy for
            // cloudlets:
            // vm[i] = Vm(i, userId, mips, pesNumber, ram, bw, size, priority,
            // vmm, new CloudletSchedulerSpaceShared());

            list.add(vm[i]);
        }

        return list;
    }

    private static List<Cloudlet> createCloudlet(int userId, int cloudlets) {
        // Creates a container to store Cloudlets
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

        // cloudlet parameters
        long length = 4000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            cloudlet[i] = new Cloudlet(i, length, pesNumber, fileSize,
                    outputSize, utilizationModel, utilizationModel,
                    utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }

        return list;
    }

    // //////////////////////// STATIC METHODS ///////////////////////
    public static void main(String[] args) {
        Log.printLine("Starting CloudSim");

        try {
            // First step: Initialize the CloudSim package. It should be called
            // before creating any entities.
            int num_user = 1; // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false; // mean trace events

            // Initialize the CloudSim library
            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            // Datacenters are the resource providers in CloudSim. We need at
            // list one of them to run a CloudSim simulation

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter No of Data Centers");
                        
            dcent = Integer.parseInt(br.readLine());
            Datacenter datacenter0 = null;
            String datacenter = "";
            for (int i = 0; i < dcent; i++) {

                datacenter = datacenter + i;
                datacenter0 = createDatacenter(datacenter);

            }
            // Datacenter datacenter1 = createDatacenter("Datacenter_1");

            // Third step: Create Broker
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            // Fourth step: Create VMs and Cloudlets and send them to broker
            BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter No of Cloudlet");
            cloudletnumber = Integer.parseInt(br1.readLine());
            BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter No of Virtual Machine");
            vmnumber = Integer.parseInt(br2.readLine());
            vmlist = createVM(brokerId, vmnumber); // creating 20 vms
            cloudletList = createCloudlet(brokerId, cloudletnumber); // creating
            // 40
            // cloudlets

            broker.submitVmList(vmlist);
            broker.submitCloudletList(cloudletList);

            // Fifth step: Starts the simulation
            CloudSim.startSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            printCloudletList(newList);

            // Print the debt of each user to each datacenter
            datacenter0.printDebts();
            // datacenter1.printDebts();

            Log.printLine("CloudSim finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    public static Datacenter createDatacenter(String name) {

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store one or more
        // Machines
        List<Host> hostList = new ArrayList<Host>();

        // 2. A Machine contains one or more PEs or CPUs/Cores. Therefore,
        // should
        // create a list to store these PEs before creating
        // a Machine.
        List<Pe> peList1 = new ArrayList<Pe>();

        int mips = 1000;

        // 3. Create PEs and add these into the list.
        // for a quad-core machine, a list of 4 PEs is required:
        peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store
        // Pe id and
        // MIPS Rating
        peList1.add(new Pe(1, new PeProvisionerSimple(mips)));
        peList1.add(new Pe(2, new PeProvisionerSimple(mips)));
        peList1.add(new Pe(3, new PeProvisionerSimple(mips)));

        // Another list, for a dual-core machine
        List<Pe> peList2 = new ArrayList<Pe>();

        peList2.add(new Pe(0, new PeProvisionerSimple(mips)));
        peList2.add(new Pe(1, new PeProvisionerSimple(mips)));

        // 4. Create Hosts with its id and list of PEs and add them to the list
        // of machines
        int hostId = 0;
        int ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        int bw = 10000;

        hostList.add(new Host(hostId, new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw), storage, peList1,
                new VmSchedulerTimeShared(peList1))); // This is our first
        // machine

        hostId++;

        hostList.add(new Host(hostId, new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw), storage, peList2,
                new VmSchedulerTimeShared(peList2))); // Second machine

        // To create a host with a space-shared allocation policy for PEs to
        // VMs:
        // hostList.add(
        // new Host(
        // hostId,
        // new CpuProvisionerSimple(peList1),
        // new RamProvisionerSimple(ram),
        // new BwProvisionerSimple(bw),
        // storage,
        // new VmSchedulerSpaceShared(peList1)
        // )
        // );

        // To create a host with a oportunistic space-shared allocation policy
        // for PEs to VMs:
        // hostList.add(
        // new Host(
        // hostId,
        // new CpuProvisionerSimple(peList1),
        // new RamProvisionerSimple(ram),
        // new BwProvisionerSimple(bw),
        // storage,
        // new VmSchedulerOportunisticSpaceShared(peList1)
        // )
        // );

        // 5. Create a DatacenterCharacteristics object that stores the
        // properties of a data center: architecture, OS, list of
        // Machines, allocation policy: time- or space-shared, time zone
        // and its price (G$/Pe time unit).
        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.1; // the cost of using storage in this
        // resource
        double costPerBw = 0.1; // the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<Storage>(); // we
        // are
        // not
        // adding
        // SAN
        // devices
        // by
        // now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        // 6. Finally, we need to create a PowerDatacenter object.
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics,
                    new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    // We strongly encourage users to develop their own broker policies, to
    // submit vms and cloudlets according
    // to the specific rules of the simulated scenario
    public static DatacenterBroker createBroker() {

        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    /**
     * Prints the Cloudlet objects
     *
     * @param list
     *            list of Cloudlets
     * @throws IOException 
     */
    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;
        int Bandwidth[] = {1000, 10000};
        Random rnd = new Random();
        database_conn db = new database_conn();

///////////////////////////////////////////////////////////////////
        System.out.println("??????????????????????????????????????????????????");                      
          String filename = "d:\\Book1.xlsx";

          //
          // Create an ArrayList to store the data read from excel sheet.
          //
          List sheetData = new ArrayList();

          FileInputStream fis1 = null;
          try {
              //
              // Create a FileInputStream that will be use to read the excel file.
              //
              fis1 = new FileInputStream(filename);

              //
              // Create an excel workbook from the file system.
              //
              HSSFWorkbook workbook = new HSSFWorkbook(fis1);
              //
              // Get the first sheet on the workbook.
              //
              HSSFSheet sheet = workbook.getSheetAt(0);

              //
              // When we have a sheet object in hand we can iterator on each
              // sheet's rows and on each row's cells. We store the data read
              // on an ArrayList so that we can printed the content of the excel
              // to the console.
              //
              	            Iterator rows = sheet.rowIterator();
              	            while (rows.hasNext()) {
              	                HSSFRow row = (HSSFRow) rows.next();
              	                Iterator cells = row.cellIterator();

              	                List data1 = new ArrayList();
              	                while (cells.hasNext()) {
              	                    HSSFCell cell = (HSSFCell) cells.next();
              	                    data1.add(cell);
              	                }

              	                sheetData.add(data1);
              	            }
              	        } catch (IOException e) {
              	            e.printStackTrace();
              	        } finally {
              	            if (fis1 != null) {
              	                try {
      								fis1.close();
      							} catch (IOException e) {
      								// TODO Auto-generated catch block
      								e.printStackTrace();
      							}
              	            }
              	        }

              	        showExelData(sheetData);                 
                          
       System.out.println("/////////////////////////////////////////////////////////");                   
          
          
                              
                              

        double totalcost = 0;
        double totCputime = 0;
        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + indent
                + "Time" + indent + "Start Time" + indent + "Finish Time");

        
        DecimalFormat dft = new DecimalFormat("###.##");

        // /Fault Cluster Creation
        Random rand = new Random();
        int faultnum[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37, 38,39,40};
        int num1[] = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000,1100,1200,1300,1400,1500,1600,1700,1800,1900,2000};



        int r = rand.nextInt(size);

        int r1 = rand.nextInt(size);
        int r2 = rand.nextInt(size);
        int r3 = rand.nextInt(size);

        int NEWJOB = faultnum[r];

        int NEWJOB1 = faultnum[r1];

        int NEWJOB2 = faultnum[r2];

        int NEWJOB3 = faultnum[r3];

       System.out.println("NEW JOB num---" + NEWJOB);
       System.out.println("NEW JOB num---" + NEWJOB1);
       System.out.println("NEW JOB num---" + NEWJOB2);
       System.out.println("NEW JOB num---" + NEWJOB3);
        int faultidd[] = {NEWJOB, NEWJOB1, NEWJOB2, NEWJOB3};

        for (int i = 0; i < faultidd.length; i++) {
            String qryfaultid = "insert into tblfaultidcluster values('" + faultidd[i] + "')";
            //System.out.println("*********" + qryfaultid);
            try {
                db.stat4.executeUpdate(qryfaultid);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
 
              







            // cloudlet.getcloudlet
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);
            
            // cloudlet.
            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");
                
                Log.printLine(indent + indent + cloudlet.getResourceId()
                        + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent + indent
                        + dft.format(cloudlet.getActualCPUTime()) + indent
                        + indent + dft.format(cloudlet.getExecStartTime())
                        + indent + indent + indent
                        + dft.format(cloudlet.getFinishTime()));
                

                int priority[] = {2, 3, 4, 5, 6, 7, 8, 9};



                long totalMemory = Runtime.getRuntime().totalMemory();
             long freeMemory = Runtime.getRuntime().freeMemory();
             long memoryuse=totalMemory-freeMemory;

            int rr = rnd.nextInt(2);


            double calc[] = new double[size];
            int filesize = (int) cloudlet.getCloudletFileSize();
            int rno = rand.nextInt(9);
            int kk = num1[rno];
            double dij = 0;
            if (filesize > num1[rno]) {
                dij = ((kk - filesize) / kk) * 100;
                //System.out.println("dij value"+Math.abs(dij));
                calc[i] = Math.abs(dij);
            }
            if (filesize < num1[rno]) {
                dij = ((kk - filesize) / filesize) * 100;
                //System.out.println("dij filesize value"+Math.abs(dij));
                calc[i] = Math.abs(dij);
            }
 //changes
                double mem = memoryuse;

                ////////////////////
                String strcluster = "";
                if (mem < 3400000 && mem > 1400000) {
                	System.out.println("inside Cluster C1");
                    //	mainslanew.setTxt("inside Cluster C1");
                    strcluster = "Cluster C1";


                }
                if (mem < 8400000 && mem > 3400000) {
                	System.out.println("inside Cluster C2");
                    //mainslanew.setTxt("inside Cluster C2");

                    strcluster = "Cluster C2";
                }
                if (mem > 8400000) {
                	System.out.println("inside Cluster C2");
                    //mainslanew.setTxt("inside Cluster C3");

                    strcluster = "Cluster C3";
                }






  try {
                    String sqltblcluster = "insert into tblResourceBundling values('" + cloudlet.getCloudletId() + "','" + mem + "','" + strcluster + "')";
                    db.stat.executeUpdate(sqltblcluster);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }



                String inserttblgridlet = "insert into tblgridletdetails values('" + cloudlet.getCloudletId() + "','" + cloudlet.getCloudletLength() + "','" + cloudlet.getCloudletFileSize() + "','" + cloudlet.getCloudletOutputSize() + "','" + num1[r] + "','" + memoryuse + "','" + Bandwidth[rr] + "')";


                System.out.println("*******"+inserttblgridlet);
                try {
                    db.stat.executeUpdate(inserttblgridlet);
                } catch (SQLException ex) {
                    Logger.getLogger(Cloudmainpack.class.getName()).log(Level.SEVERE, null, ex);
                }
               

                if (cloudlet.getCloudletId() == 0) {

                    String msecendtime = "", msecstarttime = "";
                    System.out.println("\n" + "Task Processing start....");
                    System.out.println("Cloudlet ID 0**Job Submition\n prime numbers (1-100)");

long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);
                    double num[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99};
                    Random rand1 = new Random();
                    int r11 = rand1.nextInt(99);
                    System.out.println("New Job occured random value" + r11);

                    int n = gggg, ii = 1;
                    boolean flag = false;
                    if (ii == 1) {
                        System.out.print("_______" + "1\n");
                        
                    }

                    if (NEWJOB == 0 || NEWJOB1 == 0 || NEWJOB2 == 0 || NEWJOB3 == 0) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());
                        n = r11;

                        for (ii = 2; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                               
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;


                        }




                        System.out.println("\n" + "NEW JOB Occured By ........" + n + "--value");


                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf.format(cal1.getTime()));
                        msecendtime = sdf.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);


                        System.out.println("NEW JOB Occured By ........" + n + "--value");

                    } else {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 100;
                        for (ii = 2; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                            	System.out.println("\n" + "_______" + ii + "\n");



                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;


                        }
                        System.out.println("\n" + "Task Finished........");


                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf.format(cal1.getTime()));
                        msecendtime = sdf.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }

                    String strqry1 = "insert into tblTaskProcess values('0','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (cloudlet.getCloudletId() == 1) {

                	System.out.println("\n" + "Task Processing start........");
                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 1**Job Submition\n prime numbers (1-100)");
                    double num[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99};
                    Random rand1 = new Random();


                    int r11 = rand1.nextInt(99);
                    System.out.println("New Job occured Remove value" + r11);
      long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
      System.out.println(" used memory "+mem0);
      
      int n = gggg, ii = 1;
      boolean flag = false;
      if (NEWJOB == 1 || NEWJOB1 == 1 || NEWJOB2 == 1 || NEWJOB3 == 1) {
                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        n = r11;
                        for (ii = 1; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        
                        System.out.println("New Job Occured By ........" + n + "--value");
                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {

                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        n = 100;
                        for (ii = 1; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");
                    }

                    Calendar cal1 = new GregorianCalendar();//
                    final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                    final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                    System.out.println(sdf1.format(cal1.getTime()));
                    msecendtime = sdf1.format(cal1.getTime());
                    System.out.println("End Time" + msecendtime);


                    String strqry1 = "insert into tblTaskProcess values('1','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {

                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                if (cloudlet.getCloudletId() == 2) {
                	System.out.println("\n" + "Task Processing start........");

                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 2**Job Submition\n prime numbers (100-200)");
long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);
 System.out.println("\n" +" used memory "+mem0);
                    int START = 100;
                    int END = 200;
                    Random random = new Random();

                    int randfault = showRandomInteger(START, END, random);
                    System.out.println("Job occured Remove value" + randfault);
                    //System.out.println("fault occured random value"+r11);
                    int n = 200, ii = 1;
                    boolean flag = false;

                    if (NEWJOB == 2 || NEWJOB1 == 2 || NEWJOB2 == 2 || NEWJOB3 == 2) {



                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = randfault;
                        for (ii = 100; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        
                        System.out.println("New Job Occured By ........" + n + "--value");


                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {



                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 200;
                        for (ii = 100; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");


                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }
                    String strqry1 = "insert into tblTaskProcess values('2','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (cloudlet.getCloudletId() == 3) {
                	System.out.println("\n" + "Task Processing start........");
                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 3**Job Submition\n prime numbers (200-300)");
                    int n = 300, ii = 1;
                    boolean flag = false;

long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 //ainCloud.jTextArea1.append("\n" +" used memory "+mem0);
                    int START = 200;
                    int END = 300;
                    Random random = new Random();

                    int randfault = showRandomInteger(START, END, random);
                    System.out.println("Job occured Remove value" + randfault);





                    if (NEWJOB == 3 || NEWJOB1 == 3 || NEWJOB2 == 3 || NEWJOB3 == 3) {

                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        n = randfault;
                        for (ii = 200; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");


                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 300;
                        for (ii = 200; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");
                    }

                    Calendar cal1 = new GregorianCalendar();//
                    final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                    final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                    System.out.println(sdf1.format(cal1.getTime()));
                    msecendtime = sdf1.format(cal1.getTime());
                    System.out.println("End Time" + msecendtime);

                    String strqry1 = "insert into tblTaskProcess values('3','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 4) {
                    System.out.println("\n" + "Task Processing start........");
                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 4**Job Submition\n prime numbers (300-400)");

                    long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);
System.out.println("\n" +" used memory "+mem0);

                    int n = 400, ii = 1;
                    boolean flag = false;
                    if (NEWJOB == 4 || NEWJOB1 == 4 || NEWJOB2 == 4 || NEWJOB3 == 4) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());


                        int START = 300;
                        int END = 400;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured value" + randfault);

                        System.out.println("Start Time" + msecstarttime);
                        n = randfault;
                        for (ii = 300; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");


                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {

                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 400;

                        for (ii = 300; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");
                    }

                    Calendar cal1 = new GregorianCalendar();//
                    final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                    final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                    System.out.println(sdf1.format(cal1.getTime()));
                    msecendtime = sdf1.format(cal1.getTime());
                    System.out.println("End Time" + msecendtime);

                    String strqry1 = "insert into tblTaskProcess values('4','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 5) {

                    System.out.println("\n" + "Task Processing start........");

                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 5**Job Submition\n prime numbers (400-500)");
                    int n = 500, ii = 1;
                    boolean flag = false;
long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);
System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 5 || NEWJOB1 == 5 || NEWJOB2 == 5 || NEWJOB3 == 5) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        int START = 400;
                        int END = 500;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);

                        n = randfault;
                        for (ii = 400; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");
                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {

                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 500;
                        for (ii = 400; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }
                    String strqry1 = "insert into tblTaskProcess values('5','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 6) {
                    System.out.println("\n" + "Task Processing start........");

                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 6**Job Submition\n prime numbers (500-600)");
                    int n = 600, ii = 1;
                    boolean flag = false;

                    long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);
System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 6 || NEWJOB1 == 6 || NEWJOB2 == 6 || NEWJOB3 == 6) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);


                        int START = 500;
                        int END = 600;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        for (ii = 500; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    } else {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 600;

                        for (ii = 500; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");
                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }

                    String strqry1 = "insert into tblTaskProcess values('6','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 7) {
                    System.out.println("\n" + "Task Processing start........");

                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 7**Job Submition\n prime numbers (600-700)");
                    int n = 700, ii = 1;
                    boolean flag = false;


                    long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);
System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 7 || NEWJOB1 == 7 || NEWJOB2 == 7 || NEWJOB3 == 7) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 600;
                        int END = 700;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        for (ii = 600; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");
                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    } else {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 700;

                        for (ii = 600; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }

                    String strqry1 = "insert into tblTaskProcess values('7','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (cloudlet.getCloudletId() == 8) {
                    System.out.println("\n" + "Task Processing start........");


                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 8**Job Submition\n prime numbers (700-800)");
                    int n = 800, ii = 1;
                    boolean flag = false;




                    long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 8 || NEWJOB1 == 8 || NEWJOB2 == 8 || NEWJOB3 == 8) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 700;
                        int END = 800;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;

                        for (ii = 700; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");
                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {

                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 800;
                        for (ii = 700; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }



                    String strqry1 = "insert into tblTaskProcess values('8','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 9) {
                    System.out.println("\n" + "Task Processing start........");


                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 9**Job Submition\n prime numbers (800-900)");
                    int n = 900, ii = 1;
                    boolean flag = false;



                    long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);
System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 9 || NEWJOB1 == 9 || NEWJOB2 == 9 || NEWJOB3 == 9) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 800;
                        int END = 900;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        for (ii = 800; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");
                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        n = 900;
                        for (ii = 800; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }

                    String strqry1 = "insert into tblTaskProcess values('9','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                if (cloudlet.getCloudletId() == 10) {
                    System.out.println("\n" + "Task Processing start........");
                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 10**Job Submition\n prime numbers (900-1000)");
                    int n = 1000, ii = 1;
                    boolean flag = false;




                    long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 10 || NEWJOB1 == 10 || NEWJOB2 == 10 || NEWJOB3 == 10) {

                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 900;
                        int END = 1000;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;

                        for (ii = 900; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");
                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    } else {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 1000;

                        for (ii = 900; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }
                    String strqry1 = "insert into tblTaskProcess values('11','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                if (cloudlet.getCloudletId() == 11) {
                    System.out.println("\n" + "Task Processing start........");

                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 11**Job Submition\n prime numbers (1000-1100)");
                    int n = 1100, ii = 1;
                    boolean flag = false;




                    long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 11 || NEWJOB1 == 11 || NEWJOB2 == 11 || NEWJOB3 == 11) {

                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 1000;
                        int END = 1100;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        for (ii = 1000; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {

                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        n = 1100;
                        for (ii = 1000; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");


                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }

                    String strqry1 = "insert into tblTaskProcess values('11','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 12) {
                    System.out.println("\n" + "Task Processing start........");
                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 12**Job Submition\n prime numbers (1100-1200)");
                    int n = 1200, ii = 1;
                    boolean flag = false;



                    long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 12 || NEWJOB1 == 12 || NEWJOB2 == 12 || NEWJOB3 == 12) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 1100;
                        int END = 1200;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;

                        for (ii = 1100; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");


                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    } else {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 1200;
                        for (ii = 1100; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }

                    String strqry1 = "insert into tblTaskProcess values('12','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 13) {
                    System.out.println("\n" + "Task Processing start........");
                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 13**Job Submition\n prime numbers (1200-1300)");
                    int n = 1300, ii = 1;
                    boolean flag = false;


                             long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 13 || NEWJOB1 == 13 || NEWJOB2 == 13 || NEWJOB3 == 13) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 1200;
                        int END = 1300;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        for (ii = 1200; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {



                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 1300;
                        for (ii = 1200; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }

                    String strqry1 = "insert into tblTaskProcess values('13','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 14) {
                    System.out.println("\n" + "Task Processing start........");
                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 14**Job Submition\n prime numbers (1300-1400)");
                    int n = 1400, ii = 1;
                    boolean flag = false;


                             long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);

                    if (NEWJOB == 14 || NEWJOB1 == 14 || NEWJOB2 == 14 || NEWJOB3 == 14) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 1300;
                        int END = 1400;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        for (ii = 1300; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");
                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    } else {

                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        n = 1400;

                        for (ii = 1300; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }
                    String strqry1 = "insert into tblTaskProcess values('14','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 15) {
                    System.out.println("\n" + "Task Processing start........");
                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 15**Job Submition\n prime numbers (1400-1500)");
                    int n = 1500, ii = 1;
                    boolean flag = false;




                             long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 15 || NEWJOB1 == 15 || NEWJOB2 == 15 || NEWJOB3 == 15) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 1400;
                        int END = 1500;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        for (ii = 1400; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {

                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        n = 1500;
                        for (ii = 1400; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }
                    String strqry1 = "insert into tblTaskProcess values('15','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 16) {
                    System.out.println("\n" + "Task Processing start........");
                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 16**Job Submition\n prime numbers (1500-1600)");
                    int n = 1600, ii = 1;
                    boolean flag = false;


                             long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);

                    if (NEWJOB == 16 || NEWJOB1 == 16 || NEWJOB2 == 16 || NEWJOB3 == 16) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 1500;
                        int END = 1600;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        for (ii = 1500; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");
                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        n = 1600;

                        for (ii = 1500; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }

                    String strqry1 = "insert into tblTaskProcess values('16','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 17) {
                    System.out.println("\n" + "Task Processing start........");

                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 17**Job Submition\n prime numbers (1600-1700)");
                    int n = 1700, ii = 1;
                    boolean flag = false;

                            long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 17 || NEWJOB1 == 17 || NEWJOB2 == 17 || NEWJOB3 == 17) {



                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);



                        int START = 1600;
                        int END = 1700;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        for (ii = 1600; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {

                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        n = 1700;
                        for (ii = 1600; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }
                    String strqry1 = "insert into tblTaskProcess values('17','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 18) {
                    System.out.println("\n" + "Task Processing start........");
                    String msecendtime = "", msecstarttime = "";
                    System.out.println("Cloudlet ID 18**Job Submition\n prime numbers (1700-1800)");
                    int n = 1800, ii = 1;
                    boolean flag = false;
                             long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);

                    if (NEWJOB == 18 || NEWJOB1 == 18 || NEWJOB2 == 18 || NEWJOB3 == 18) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 1700;
                        int END = 1800;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        for (ii = 1700; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);

                    } else {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 1800;
                        for (ii = 1700; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }

                    String strqry1 = "insert into tblTaskProcess values('18','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 19) {
                    System.out.println("\n" + "Task Processing start........");
                    System.out.println("Cloudlet ID 19**Job Submition\n prime numbers (1800-1900)");
                    int n = 1900, ii = 1;
                    boolean flag = false;
                    String msecendtime = "", msecstarttime = "";


                             long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 19 || NEWJOB1 == 19 || NEWJOB2 == 19 || NEWJOB3 == 19) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 1800;
                        int END = 1900;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        for (ii = 1800; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);


                    } else {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        n = 1900;
                        for (ii = 1800; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }
                    String strqry1 = "insert into tblTaskProcess values('19','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 20) {
                    System.out.println("\n" + "Task Processing start........");
                    System.out.println("Cloudlet ID 20**Job Submition\n prime numbers (1900-2000)");
                    int n = 2000, ii = 1;
                    boolean flag = false;
                    String msecendtime = "", msecstarttime = "";


                             long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 20 || NEWJOB1 == 20 || NEWJOB2 == 20 || NEWJOB3 == 20) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        int START = 1900;
                        int END = 2000;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        for (ii = 1900; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);


                    } else {
                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);

                        n = 2000;

                        for (ii = 1900; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }
                    String strqry1 = "insert into tblTaskProcess values('20','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cloudlet.getCloudletId() == 21) {
                    System.out.println("\n" + "Task Processing start........");
                    System.out.println("Cloudlet ID 20**Job Submition\n prime numbers (2000-2100)");
                    int n = 2100, ii = 1;
                    boolean flag = false;
                    String msecendtime = "", msecstarttime = "";


                             long mem0 = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
 System.out.println(" used memory "+mem0);

 System.out.println("\n" +" used memory "+mem0);
                    if (NEWJOB == 21 || NEWJOB1 == 21 || NEWJOB2 == 21 || NEWJOB3 == 21) {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        int START = 2000;
                        int END = 2100;
                        Random random = new Random();

                        int randfault = showRandomInteger(START, END, random);
                        System.out.println("New Job occured random value" + randfault);
                        n = randfault;
                        System.out.println("Start Time" + msecstarttime);
                        for (ii = 2000; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                System.out.println("\n" + "_______" + ii + "\n");
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "New Job Occured By ........" + n + "--value");
                        System.out.println("New Job Occured By ........" + n + "--value");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    } else {


                        Calendar cal = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        System.out.println(sdf.format(cal.getTime()));
                        msecstarttime = sdf.format(cal.getTime());

                        System.out.println("Start Time" + msecstarttime);
                        for (ii = 2000; ii <= n; ii++) {
                            //System.out.println("i value---"+i);
                            for (int j = 2; j < ii; j++) {
                                if (ii <= n) //System.out.println("j value---"+j);
                                {
                                    if (ii % j == 0) {
                                        flag = true;
                                    }
                                }



                            }
                            if (!flag) {
                                
                                System.out.print("_______" + ii + "\n");
                            }
                            flag = false;

                        }
                        System.out.println("\n" + "Task Finished........");

                        Calendar cal1 = new GregorianCalendar();//
                        final String DATE_FORMAT_NOW1 = "yyyy-MM-dd HH:mm:ss";
                        final SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW1);
                        System.out.println(sdf1.format(cal1.getTime()));
                        msecendtime = sdf1.format(cal1.getTime());
                        System.out.println("End Time" + msecendtime);
                    }
                    String strqry1 = "insert into tblTaskProcess values('21','" + msecstarttime + "','" + msecendtime + "','" + n + "')";
                    try {
                        System.out.println(strqry1);
                        db.stat.executeUpdate(strqry1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                totalcost = totalcost + (int) cloudlet.getProcessingCost();
                totCputime = totCputime + (int) cloudlet.getActualCPUTime();
            }


            
            String gridlet11 ="20";
            System.out.println("total cost for simulation" + totalcost);
            
            System.out.println("total CPU Time taken For simulation" + totCputime);
            
            String strr = "insert into tblcompare values('" + gridlet11 + "','" + totalcost + "','" + totCputime + "')";




            System.out.println("Print Process  *************");
            System.out.println("Cloudlet History" + cloudlet.getCloudletHistory());
            System.out.println("Cloudlet Vm id---" + cloudlet.getVmId());

            System.out.println("Cloudlet getWaitingTime---" + cloudlet.getWaitingTime());
            System.out.println("Cloudlet TotalLength---" + cloudlet.getCloudletTotalLength());
            System.out.println("Cloudlet UtilizationModelRam()---" + cloudlet.getUtilizationModelRam());
            System.out.println("Cloudlet Costpercsecond---" + cloudlet.getCostPerSec());
            System.out.println("Cloudlet UtilizationModelCpu()---" + cloudlet.getUtilizationModelCpu());
            System.out.println("Cloudlet Wallclock time---" + cloudlet.getWallClockTime());




            try {
                db.stat.executeUpdate(strr);
            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println("\n" + "Task Finished........");
        }
    }

    // JOptionPane.showMessageDialog(null,cloudlet.
    //}
    //}
    //}
//}
    private static void showExelData(List sheetData) {
        //
        // Iterates the data and print it out to the console.
        //
        for (int i = 0; i < sheetData.size(); i++) {
            List list = (List) sheetData.get(i);
            for (int j = 0; j < list.size(); j++) {
                HSSFCell cell = (HSSFCell) list.get(j);
                System.out.print(cell.getNumericCellValue());
                gggg=(int) (cell.getNumericCellValue());
                
                if (j < list.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("");
        }
    }
    private static int showRandomInteger(int aStart, int aEnd, Random aRandom) {
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        int randomNumber = (int) (fraction + aStart);
        //log("Generated : " + randomNumber);
        return randomNumber;
    }
}
