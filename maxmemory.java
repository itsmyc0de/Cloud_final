/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package EnergyEfficient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 *
 * @author User
 */
public class maxmemory {

    public static void main(String args[])
    {

            List<Cloudlet> cloudletList;
    /** The vmlist. */
     List<Vm> vmlist;

         int num_user = 1; // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false; // mean trace events
       CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            // Datacenters are the resource providers in CloudSim. We need at
            // list one of them to run a CloudSim simulation

            int dcent = Cloudmainpack.dcent;
            Datacenter datacenter0 = null;
            String datacenter = "";
            for (int i = 0; i < dcent; i++) {

                datacenter = datacenter + i;
                datacenter0 = Cloudmainpack.createDatacenter(datacenter);

            }
            // Datacenter datacenter1 = createDatacenter("Datacenter_1");

            // Third step: Create Broker
            DatacenterBroker broker = Cloudmainpack.createBroker();
            int brokerId = broker.getId();

            // Fourth step: Create VMs and Cloudlets and send them to broker
            int cloudletnumber = Cloudmainpack.cloudletnumber;
            int vmnumber = Cloudmainpack.vmnumber;
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

           // printCloudletList(newList);
        Cloudlet cloudlet = null;

        for (int i=0;i<10;i++){
        cloudlet = newList.get(i);
        System.out.println("__-------Cloudelt id++++++"+cloudlet.getCloudletId());

          System.out.println("__-------COST ++++++"+cloudlet.getCostPerSec());


          System.out.println("__-------STATUS++++++"+cloudlet.getStatusString(i));

          System.out.println("__-------PROCESSING COST++++++"+cloudlet.getProcessingCost());

System.out.println("__-------getUtilizationOfCpu++++++"+cloudlet.getUtilizationOfCpu(i));
System.out.println("__-------getUtilizationModelRam++++++"+cloudlet.getUtilizationModelRam());

System.out.println("__-------getUtilizationModelBw++++++"+cloudlet.getUtilizationModelBw());
//System.out.println("__-------PROCESSING COST++++++"+cloudlet.getProcessingCost());


        }
        //System.out.println()
        System.out.println(Runtime.getRuntime().maxMemory());
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


}
