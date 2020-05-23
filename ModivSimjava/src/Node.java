import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class Node extends Thread {

    private int nodeID;
    private Hashtable<Integer,Integer> linkCostTable;
    private Hashtable<Integer,Integer> linkBandwithTable;

    public Node(int nodeID){
        this.nodeID=nodeID;
    }

    public Node(int nodeID, Hashtable<Integer,Integer> linkCost, Hashtable<Integer,Integer> linkBandwith){
        this.nodeID=nodeID;
        this.linkBandwithTable =linkBandwith;
        this.linkCostTable =linkCost;
    }

    public void receiveUpdate(Message m){

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
}
