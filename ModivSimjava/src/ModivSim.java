import java.util.*;

public class ModivSim {

    private static volatile Hashtable<Integer,Node> nodeThreadsTable =new Hashtable<Integer,Node>();



    public static void main(String[] args){
        System.out.println("Hello");
        int period=1000;

        String filepath="./input/input1.txt";
        HashMap<Integer, ArrayList<int[]>> nodeMap=NodeConstructionHandler.getNodeMapFromText(filepath);
        System.out.println(nodeMap.toString());
        initializeNodeTreads(nodeMap);





        int convergenceNum=3;
        for(int i=0;i<convergenceNum;i++) {
            for (Map.Entry<Integer, Node> nodeEntry : ModivSim.getNodeThreadsTable().entrySet()) {
                try {
                    nodeEntry.getValue().sendUpdate();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        for(Map.Entry<Integer,Node> nodeEntry : ModivSim.getNodeThreadsTable().entrySet()){
            String out=nodeEntry.getValue().toString();
            System.out.println(out);
            String forwardStr=nodeEntry.getValue().forwardTabletoStr();
            System.out.println(forwardStr);
        }

        System.out.println("finished");
        /*
        Timer sendUpdateTimer=new Timer();
        PeriodicSendUpdateTask sendUpdateTask=new PeriodicSendUpdateTask(period);
        sendUpdateTimer.schedule(sendUpdateTask,0,sendUpdateTask.getPeriod());

        System.out.println("finished");
        while(true){

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        */


/*        Message m= new Message();
        HashMap<Integer,Integer> hm= new HashMap<Integer,Integer>();
        hm.put(3,50);
        m.setDistanceVector(hm);
        nodeThreadsTable.get(0).receiveUpdate(m);*/

/*        try {
            java.util.concurrent.TimeUnit.SECONDS.sleep(5);
        }catch(InterruptedException e){
            e.printStackTrace();
        }*/


/*
        Node a=new Node(0);
        Node b=new Node(1);
        b=a;
        b.setNodeID(3);
        System.out.println("Node a id: "+a.getNodeID()+" node b id: "+b.getNodeID());
*/

/*        Node n1= new Node(1);
        Node n2= new Node(2);
        n1.start();
        n2.start();
        n1.interrupt();
        n2.interrupt();*/

    }

    public static void initializeNodeTreads(final HashMap<Integer, ArrayList<int[]>> nodeMap){

        Iterator<Map.Entry<Integer,ArrayList<int[]>>> it=nodeMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Integer,ArrayList<int[]>> entry = (Map.Entry<Integer,ArrayList<int[]>>)it.next();
            ArrayList<int[]> currList=entry.getValue();
            Hashtable<Integer,Integer> currLinkcostTable=new Hashtable<Integer,Integer>();
            Hashtable<Integer,Integer> currLinkbandwidthTable=new Hashtable<Integer,Integer>();
            currLinkcostTable.put(entry.getKey(),0);
            Node currNode=new Node(entry.getKey());

            for(int[] neighbourInfo:currList ){
                if(neighbourInfo.length!=3){
                    System.out.println("error in initializeNodeThreads method");
                }else{
                    currLinkcostTable.put(neighbourInfo[0],neighbourInfo[1]);
                    currLinkbandwidthTable.put(neighbourInfo[0],neighbourInfo[2]);
                }
            }
            currNode.setLinkCostTable(currLinkcostTable);
            currNode.setLinkBandwithTable(currLinkbandwidthTable);
            try {
                currNode.initializeDistanceTable();
            }catch(Exception e){
                e.printStackTrace();
            }
            nodeThreadsTable.put(currNode.getNodeID(),currNode);
        }
    }

    public static Hashtable<Integer, Node> getNodeThreadsTable() {
        return nodeThreadsTable;
    }

}
