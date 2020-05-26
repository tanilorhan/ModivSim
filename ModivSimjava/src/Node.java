import java.util.*;

public class Node extends Thread {

    private int nodeID;
    private Hashtable<Integer, Integer> linkCostTable;
    private Hashtable<Integer, Integer> linkBandwithTable;
    private HashMap<Integer, HashMap<Integer, Integer>> distanceTable;
    //        new HashMap<Integer,HashMap<Integer,Integer>>();
    private HashMap<Integer, Integer> bottleNeckBandwitdhTable =
            new HashMap<Integer, Integer>();

    public Node(int nodeID) {
        this.nodeID = nodeID;
    }

    public Node(int nodeID, Hashtable<Integer, Integer> linkCost, Hashtable<Integer, Integer> linkBandwith) {
        this.nodeID = nodeID;
        this.linkCostTable = linkCost;
        this.linkBandwithTable = linkBandwith;
    }

    public synchronized void receiveUpdate(Message m) throws Exception {
        boolean isUpdated=false;
        if (linkCostTable.containsKey(m.getSenderID())) {
            HashMap<Integer, Integer> neighbourDV = m.getDistanceVector();
            HashMap<Integer, Integer> ownDistanceVector = distanceTable.get(getNodeID());
            if(!distanceTable.containsKey(m.getSenderID())){
                distanceTable.put(m.getSenderID(),m.getDistanceVector());
            }else {
                if (distanceTable.get(m.getSenderID()).equals(neighbourDV)){
                    System.out.println("no update");
                    return;
                }
                distanceTable.replace(m.getSenderID(), m.getDistanceVector());
            }

            for (Map.Entry<Integer, Integer> entry : neighbourDV.entrySet()) {
                if (ownDistanceVector.containsKey(entry.getKey())) {
                    int newDistance = entry.getValue() + linkCostTable.get(m.getSenderID());
                    if (newDistance < ownDistanceVector.get(entry.getKey())) {
                        ownDistanceVector.replace(entry.getKey(), newDistance);
                        isUpdated=true;
                    }
                } else {
                    ownDistanceVector.put(entry.getKey(), entry.getValue() + linkCostTable.get(m.getSenderID()));
                }
            }

            if(isUpdated){
                System.out.println("Node: "+getNodeID()+" is updated");
                sendUpdate();
            }
        } else {
            throw new Exception("received m from not a neighbour");
        }
    }


    public synchronized boolean sendUpdate() throws Exception {

        boolean isConverged = false;

        // int bnwd,int recvId,int sendId,HashMap<Integer,Integer> distanceVector

        if (isConverged) {
            return false;
        } else {
            for (Map.Entry<Integer, Integer> entry : linkCostTable.entrySet()) {
                if(entry.getKey()!=getNodeID()) {
                    int bnwd = linkBandwithTable.get(entry.getKey());
                    int recvId = entry.getKey();
                    int sendId = getNodeID();
                    Message m = new Message(bnwd, recvId, sendId, distanceTable.get(getNodeID()));
                    Node currNeighbour = ModivSim.getNodeThreadsTable().get(entry.getKey());
                    currNeighbour.receiveUpdate(m);
                }
            }
            return true;
        }
    }

    public String forwardTabletoStr(){
        Hashtable<String, int[]> forwardTable= getForwardingTable();
        String forwardTableStr=new String("ForwardTable: \n");
        forwardTableStr=forwardTableStr.concat("Dest   |  Forward\n");
        for(Map.Entry<String,int[]> forwardEntry:forwardTable.entrySet()){
            forwardTableStr=forwardTableStr.concat(String.format("%7s",forwardEntry.getKey()));
            forwardTableStr=forwardTableStr.concat("| (");
            forwardTableStr=forwardTableStr.concat(String.format("%d",forwardEntry.getValue()[0]));
            forwardTableStr=forwardTableStr.concat(",");
            forwardTableStr=forwardTableStr.concat(String.format("%d",forwardEntry.getValue()[1]));
            forwardTableStr=forwardTableStr.concat(")\n");
        }
        return forwardTableStr;
    }

    public String toString() {
        String nodeString = "nodeID: " + nodeID + "\n";
        Set<Integer> neighbours = linkCostTable.keySet();
        Iterator<Integer> itr = neighbours.iterator();
        while (itr.hasNext()) {
            Integer next = itr.next();
            if(next!=getNodeID()) {
                nodeString=nodeString.concat("(");
                nodeString=nodeString.concat(Integer.toString(next));
                nodeString=nodeString.concat(" ");
                nodeString=nodeString.concat(Integer.toString(linkCostTable.get(next)));
                nodeString=nodeString.concat(" ");
                nodeString=nodeString.concat(Integer.toString(linkBandwithTable.get(next)));
                nodeString=nodeString.concat(")");
            }
        }
        //nodeString=nodeString.concat(forwardTabletoStr());
        return nodeString + "\n";
    }

    public void initializeDistanceTable() throws Exception {
        this.distanceTable = new HashMap<Integer, HashMap<Integer, Integer>>();
        if (linkCostTable != null) {
            Set<Map.Entry<Integer, Integer>> entrySet = linkCostTable.entrySet();
            HashMap<Integer, Integer> distanceVector = new HashMap<Integer, Integer>();
            for (Map.Entry<Integer, Integer> entry : entrySet) {
                distanceVector.put(entry.getKey(), entry.getValue());
            }
            distanceTable.put(getNodeID(), distanceVector);
        } else {
            throw new Exception("initialize distancetable error,linkcosttable empty");
        }
    }

    public HashMap<Integer, HashMap<Integer, Integer>> getDistanceTable() {
        return distanceTable;
    }


    public synchronized Hashtable<String, int[]> getForwardingTable() {
        HashMap<Integer, Integer> ownDistanceVector = distanceTable.get(getNodeID());
        Hashtable<String, int[]> forwardingTable = new Hashtable<String, int[]>();
        for (Map.Entry<Integer, Integer> ownDV_entry : ownDistanceVector.entrySet()) {
            int currDest = ownDV_entry.getKey();
            int forwardTuple[] = new int[2];
            forwardTuple[0]=-1;
            forwardTuple[1]=-1;
            for (Map.Entry<Integer, HashMap<Integer, Integer>> neighbourEntry : distanceTable.entrySet()) {
                int currNeighbourId = neighbourEntry.getKey();
                HashMap<Integer,Integer> currNeighDV=neighbourEntry.getValue();
                if (currNeighbourId != getNodeID()) {
                    if (currNeighDV.containsKey(currDest)) {
                        int currNeighDisttoDestFromSrc = currNeighDV.get(currDest)+linkCostTable.get(currNeighbourId);
                        if (!forwardingTable.containsKey(Integer.toString(currDest))) {
                            forwardTuple[0] = currNeighbourId;
                            forwardingTable.put(Integer.toString(currDest), forwardTuple);
                        } else {
                            int currForwardTuple[]=forwardingTable.get(Integer.toString(currDest));
                            int firstForwardNodeID=currForwardTuple[0];
                            int secondForwardNodeID=currForwardTuple[1];
                            int firstDist=distanceTable.get(firstForwardNodeID).get(currDest)+linkCostTable.get(firstForwardNodeID);
                            if(currNeighDisttoDestFromSrc<firstDist){
                                currForwardTuple[1]=currForwardTuple[0];
                                currForwardTuple[0]=currNeighbourId;
                                //forwardingTable.replace(Integer.toString(currDest),currForwardTuple);
                            }else if(currForwardTuple[1]!=-1){
                                int secondDist=distanceTable.get(secondForwardNodeID).get(currDest)+linkCostTable.get(secondForwardNodeID);
                                if(currNeighDisttoDestFromSrc<secondDist){
                                    currForwardTuple[1]=currNeighbourId;
                                    //forwardingTable.replace(Integer.toString(currDest),currForwardTuple);
                                }
                            }else{
                                currForwardTuple[1]=currNeighbourId;
                            }
                        }
                    }
                }
            }

        }
        return forwardingTable;
    }


    public void run() {
        /*while(true){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                System.out.println("thread interruped, will terminate"+this.getId());
                return;
            }
            System.out.println("node "+nodeID+ " is running\n");
        }*/
        System.out.println("Node " + nodeID + " threadID" + getId() + " is running ");

    }


    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public Hashtable<Integer, Integer> getLinkCostTable() {
        return linkCostTable;
    }

    public void setLinkCostTable(Hashtable<Integer, Integer> linkCostTable) {
        this.linkCostTable = linkCostTable;
    }

    public Hashtable<Integer, Integer> getLinkBandwithTable() {
        return linkBandwithTable;
    }

    public void setLinkBandwithTable(Hashtable<Integer, Integer> linkBandwithTable) {
        this.linkBandwithTable = linkBandwithTable;
    }

    public void setDistanceTable(HashMap<Integer, HashMap<Integer, Integer>> distanceTable) {
        this.distanceTable = distanceTable;
    }

    public HashMap<Integer, Integer> getBottleNeckBandwitdhTable() {
        return bottleNeckBandwitdhTable;
    }

    public void setBottleNeckBandwitdhTable(HashMap<Integer, Integer> bottleNeckBandwitdhTable) {
        this.bottleNeckBandwitdhTable = bottleNeckBandwitdhTable;
    }


}
