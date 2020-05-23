import java.util.*;

public class Node extends Thread {

    private int nodeID;
    private Hashtable<Integer,Integer> linkCostTable;
    private Hashtable<Integer,Integer> linkBandwithTable;
    private HashMap<Integer,HashMap<Integer,Integer>> distanceTable;
    //        new HashMap<Integer,HashMap<Integer,Integer>>();
    private HashMap<Integer,Integer> bottleNeckBandwitdhTable=
            new HashMap<Integer,Integer>();

    public Node(int nodeID){
        this.nodeID=nodeID;
    }

    public Node(int nodeID, Hashtable<Integer,Integer> linkCost, Hashtable<Integer,Integer> linkBandwith){
        this.nodeID=nodeID;
        this.linkCostTable =linkCost;
        this.linkBandwithTable =linkBandwith;
    }

    public synchronized void receiveUpdate(Message m) throws Exception {
        if(distanceTable.containsKey(m.getSenderID())) {
            HashMap<Integer, Integer> neighbourDV = m.getDistanceVector();
            HashMap<Integer, Integer> ownDistanceVector = distanceTable.get(getNodeID());
            distanceTable.replace(m.getSenderID(),m.getDistanceVector());
            for (Map.Entry<Integer, Integer> entry : neighbourDV.entrySet()) {
                if (ownDistanceVector.containsKey(entry.getKey())) {
                    int newDistance = entry.getValue() + linkCostTable.get(m.getSenderID());
                    if (newDistance < ownDistanceVector.get(entry.getKey())) {
                        ownDistanceVector.replace(entry.getKey(), newDistance);
                    }
                } else {
                    if (linkCostTable.containsKey(entry.getKey())) {
                        ownDistanceVector.put(entry.getKey(), entry.getValue() + linkCostTable.get(entry.getKey()));
                    } else {
                        throw new Exception("Not a neighbour");
                    }
                }
            }
        }else{
            throw new Exception("received m from not a neighbour");
        }
    }

    public String toString(){
        String nodeString="nodeID: "+nodeID+"\n";
        Set<Integer> neighbours=linkCostTable.keySet();
        Iterator<Integer> itr=neighbours.iterator();
        while(itr.hasNext()){
            Integer next=itr.next();
            nodeString.concat(next.toString());
            nodeString.concat(" ");
            nodeString.concat(linkCostTable.get(next).toString());
            nodeString.concat(" ");
            nodeString.concat(linkBandwithTable.get(next).toString());
        }
        return nodeString+"\n";
    }

    public void initializeDistanceTable() throws Exception {
        this.distanceTable=new HashMap<Integer,HashMap<Integer,Integer>>();
        if(linkCostTable!=null){
            Set<Map.Entry<Integer,Integer>> entrySet=linkCostTable.entrySet();
            HashMap<Integer,Integer> distanceVector= new HashMap<Integer,Integer>();
            for(Map.Entry<Integer,Integer> entry : entrySet){
                distanceVector.put(entry.getKey(),entry.getValue());
            }
            distanceTable.put(getNodeID(),distanceVector);
        }else{
            throw new Exception("initialize distancetable error");
        }
    }

    public HashMap<Integer,HashMap<Integer,Integer>> getDistanceTable(){
        return distanceTable;
    }



    public void run(){
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
        System.out.println("Node "+nodeID+" threadID"+getId()+" is running ");

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
