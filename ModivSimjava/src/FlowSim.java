import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class FlowSim extends Thread{

    private static volatile Hashtable<Integer, Node> nodeThreadsTable;
    //for every node, holds how many seconds the link is busy
    private Hashtable<Integer, HashMap<Integer, Integer>> busyLinkTable = new Hashtable<Integer, HashMap<Integer, Integer>>();
    private ArrayList<Flow> flowList=new ArrayList<Flow>();
    private ArrayList<Flow> enquedFlowList=new ArrayList<Flow>();
    public FlowSim(Hashtable<Integer, Node> nodeThreadsTable) {
        this.nodeThreadsTable = nodeThreadsTable;
    }

    public void run(){
        int i=0;
        while(true) {
            if(!flowList.isEmpty()){
                i++;
                System.out.println("Step "+i);
            }

            for (Flow currFlow : flowList) {
                System.out.println(activeFlow_toString(currFlow));
                updateBusyLinkTable(currFlow);
                currFlow.setRemainDuration(currFlow.getRemainDuration()-1);

//                if(currFlow.getRemainDuration()<=0) {
//                    flowList.remove(currFlow);
//                }
            }
            flowList.removeIf(flow -> flow.getRemainDuration()<=0);
            if(!enquedFlowList.isEmpty()) {
                System.out.println("enqued flows: " + enquedFlowList.toString());
            }
            dequeFlows();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void insertFlow(String label,int src,int dest,int size){
        if(findFlowPath(label,src,dest,size) != null){
            ArrayList<Integer> path=findFlowPath(label,src,dest,size);
            int duration=insertFlowIntoBusyLinkTable(path,size);
            Flow flow=new Flow(label,src,dest,size,path,duration);
            flowList.add(flow);
        }else{
            System.out.println("Cannot find a flow path available,will enqueue flow "+label);
            enquedFlowList.add(new Flow(label,src,dest,size));
        }
    }

    public void dequeFlows(){
        ArrayList<Flow> dequeList=new ArrayList<Flow>();
        for(Flow flow:enquedFlowList){
            if(findFlowPath(flow.getLabel(),flow.getSrc(),flow.getDest(),flow.getSize())!=null){
                dequeList.add(flow);
            }
        }
        enquedFlowList.removeAll(dequeList);

        for(Flow flow:dequeList){
            if(findFlowPath(flow.getLabel(),flow.getSrc(),flow.getDest(),flow.getSize()) != null){
                ArrayList<Integer> path=findFlowPath(flow.getLabel(),flow.getSrc(),flow.getDest(),flow.getSize());
                int duration=insertFlowIntoBusyLinkTable(path,flow.getSize());
                flow.setRemainDuration(duration);
                flow.setPath(path);
                flowList.add(flow);
            }else{
                enquedFlowList.add(flow);
            }
        }
    }

    //also returns flow duration for the path
    public int insertFlowIntoBusyLinkTable(ArrayList<Integer> path, int size) {
        int bottleNeckBnwd = findBottleNeckBandwidthOfPath(path);
        int seconds = (int) Math.ceil((double) size / bottleNeckBnwd);
        Node currNode = nodeThreadsTable.get(path.get(0));
        if (busyLinkTable.containsKey(currNode.getNodeID())) {
            if (busyLinkTable.get(currNode.getNodeID()).containsKey(1)) {
                busyLinkTable.get(currNode.getNodeID()).replace(path.get(1), seconds);
            } else {
                busyLinkTable.get(currNode.getNodeID()).put(path.get(1), seconds);
            }
        }else{
            busyLinkTable.put(currNode.getNodeID(),new HashMap<Integer, Integer>());
            busyLinkTable.get(currNode.getNodeID()).put(path.get(1),seconds);
        }

        for (int i = 1; i < path.size() - 1; i++) {
            currNode = nodeThreadsTable.get(path.get(i));
            if (busyLinkTable.containsKey(currNode.getNodeID())) {
                if (busyLinkTable.get(currNode.getNodeID()).containsKey(path.get(i+1))) {
                    busyLinkTable.get(currNode.getNodeID()).replace(path.get(i+1), seconds);
                } else {
                    busyLinkTable.get(currNode.getNodeID()).put(path.get(i+1), seconds);
                }
            }else{
                busyLinkTable.put(currNode.getNodeID(),new HashMap<Integer, Integer>());
                busyLinkTable.get(currNode.getNodeID()).put(path.get(i+1),seconds);
            }

        }

        return seconds;
    }

    public int findBottleNeckBandwidthOfPath(ArrayList<Integer> path) {
        int bottleNeckBnwd = Integer.MAX_VALUE;
        Node currNode = nodeThreadsTable.get(path.get(0));
        int currBnwd = currNode.getLinkBandwithTable().get(path.get(1));
        if (currBnwd < bottleNeckBnwd)
            bottleNeckBnwd = currBnwd;
        for (int i = 1; i < path.size() - 1; i++) {
            currNode = nodeThreadsTable.get(path.get(i));
            currBnwd = currNode.getLinkBandwithTable().get(path.get(i + 1));
            if (currBnwd < bottleNeckBnwd)
                bottleNeckBnwd = currBnwd;
        }
        return bottleNeckBnwd;
    }

    public ArrayList<Integer> findFlowPath(String label, int src, int dest, int size) {
        Node srcNode = nodeThreadsTable.get(src);
        ArrayList<Integer> path = new ArrayList<Integer>();
        Node currNode = srcNode;
        path.add(currNode.getNodeID());

        /*
        Hashtable<String,int[]> currForwardingTable =currNode.getForwardingTable();
        int[] forwardNodes=currForwardingTable.get(Integer.toString(dest));

        Hashtable<String,int[]> currForwardingTable;
        int[] forwardNodes;
        int nextNodeId=forwardNodes[0];
        path.add(nextNodeId);
        currNode=nodeThreadsTable.get(nextNodeId);

        while(nextNodeId!=dest){
            currForwardingTable =currNode.getForwardingTable();
            forwardNodes=currForwardingTable.get(Integer.toString(dest));
            nextNodeId=forwardNodes[0];
            path.add(nextNodeId);
            currNode=nodeThreadsTable.get(nextNodeId);
        }

         */

        Hashtable<String, int[]> currForwardingTable;
        int[] forwardNodes;
        boolean forwardNodeAvail = false;
        while (currNode.getNodeID() != dest) {
            currForwardingTable = currNode.getForwardingTable();
            forwardNodes = currForwardingTable.get(Integer.toString(dest));
            forwardNodeAvail = false;
            int nextNodeId = -1;
            int firstForwardNodeId = forwardNodes[0];
            int secondForwardNodeId = forwardNodes[1];

            if (busyLinkTable.containsKey(currNode.getNodeID())) {
                HashMap<Integer, Integer> busyLinks = busyLinkTable.get(currNode.getNodeID());

                if (!busyLinks.containsKey(firstForwardNodeId)) {
                    forwardNodeAvail = true;
                    nextNodeId = firstForwardNodeId;
                } else if (!busyLinks.containsKey(secondForwardNodeId)) {
                    forwardNodeAvail = true;
                    nextNodeId = secondForwardNodeId;
                } else {
                    //System.out.println("Path not available for flow " + label);
                    break;
                }
            } else {
                forwardNodeAvail = true;
                nextNodeId = firstForwardNodeId;
            }

            if (nextNodeId < 0) {
                forwardNodeAvail = false;
                break;
            }
            currNode = nodeThreadsTable.get(nextNodeId);
            if (path.contains(currNode.getNodeID())) {
                System.out.println("Cycle detected");
                break;
            }
            path.add(currNode.getNodeID());
        }
        //path.add(dest);
        if (forwardNodeAvail) {
            System.out.println(path.toString());
            return path;

        }
        return null;
    }

    private void updateBusyLinkTable(Flow flow){
        ArrayList<Integer> path=flow.getPath();
        for(int i=0;i<path.size()-1;i++){
            int currNodeId=path.get(i);
            if(busyLinkTable.containsKey(currNodeId)){
                HashMap<Integer,Integer> busyLinks=busyLinkTable.get(currNodeId);
                if(busyLinks.containsKey(path.get(i+1))){
                    int newDuration=busyLinks.get(path.get(i+1))-1;
                    busyLinks.replace(i+1,newDuration);
                    if(newDuration<=0){
                        busyLinks.remove(i+1);
                    }
                }
            }
        }
    }

    public String activeFlow_toString(Flow flow){
        String activeFlowString=flow.toString();
        activeFlowString=activeFlowString.concat(" path: "+flow.getPath().toString());
        activeFlowString=activeFlowString.concat(" remaining duration: "+flow.getRemainDuration()+"s  ");
        return activeFlowString;
    }
}
