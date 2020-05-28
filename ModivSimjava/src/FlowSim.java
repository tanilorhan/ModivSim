import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class FlowSim {

    private static volatile Hashtable<Integer,Node> nodeThreadsTable;
    private Hashtable<Integer, HashMap<Integer,Integer>> busyLinkTable=new Hashtable<Integer, HashMap<Integer,Integer>>();

    public FlowSim(Hashtable<Integer,Node> nodeThreadsTable){
        this.nodeThreadsTable=nodeThreadsTable;
    }

    public void initializeFlow(String label,int src,int dest,int size){
        Node srcNode=nodeThreadsTable.get(src);
        ArrayList<Integer> path=new ArrayList<Integer>();
        Node currNode=srcNode;
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

        Hashtable<String,int[]> currForwardingTable;
        int[] forwardNodes;
        boolean forwardNodeAvail=false;
        while(currNode.getNodeID()!=dest){
            currForwardingTable=currNode.getForwardingTable();
            forwardNodes=currForwardingTable.get(Integer.toString(dest));
            forwardNodeAvail=false;
            int nextNodeId=-1;
            int firstForwardNodeId=forwardNodes[0];
            int secondForwardNodeId=forwardNodes[1];

            if(busyLinkTable.containsKey(currNode.getNodeID())) {
                HashMap<Integer, Integer> busyLinks = busyLinkTable.get(currNode.getNodeID());

                if (!busyLinks.containsKey(firstForwardNodeId)) {
                    forwardNodeAvail = true;
                    nextNodeId = firstForwardNodeId;
                } else if (!busyLinks.containsKey(secondForwardNodeId)) {
                    forwardNodeAvail = true;
                    nextNodeId = secondForwardNodeId;
                } else {
                    System.out.println("Path not available for flow " + label);
                    break;
                }
            }else{
                forwardNodeAvail = true;
                nextNodeId = firstForwardNodeId;
            }

            if(nextNodeId<0){
                forwardNodeAvail=false;
                break;
            }
            currNode=nodeThreadsTable.get(nextNodeId);
            path.add(currNode.getNodeID());
        }
        //path.add(dest);
        if(forwardNodeAvail) {
            System.out.println(path.toString());
        }
    }
}
