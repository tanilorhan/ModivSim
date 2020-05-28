import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Node extends Thread {

    private int nodeID;
    private Hashtable<Integer, Integer> linkCostTable;
    private Hashtable<Integer, Integer> linkBandwithTable;
    private HashMap<Integer, HashMap<Integer, Integer>> distanceTable;
    //        new HashMap<Integer,HashMap<Integer,Integer>>();
    private HashMap<Integer, Integer> bottleNeckBandwitdhTable =
            new HashMap<Integer, Integer>();
    private JFrame frame;
    private int frameWidth;
    private int frameHeight;
    private JTextArea textArea;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int totalNodeNum;

    private static boolean IS_CONVERGED = false;
    private static int NUM_NO_UPDATE = 0;

    public Node(int nodeID, int totalNodeNum) {
        this.nodeID = nodeID;
        this.totalNodeNum = totalNodeNum;
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

                    if ( ++NUM_NO_UPDATE < 6 ) {
                        IS_CONVERGED = true;
                    }
                    return;
                }
                distanceTable.replace(m.getSenderID(), m.getDistanceVector());
            }

            /*
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
                //sendUpdate();
            }

             */
        } else {
            throw new Exception("received m from not a neighbour");
        }
    }

    public HashMap<Integer,HashMap<Integer,Integer>> updateDistanceTable(){
        HashMap<Integer,HashMap<Integer,Integer>> oldDistanceTable=(HashMap<Integer,HashMap<Integer,Integer>>)distanceTable.clone();
        HashMap<Integer,Integer> ownDistanceVector=distanceTable.get(getNodeID());
        for(Map.Entry<Integer,HashMap<Integer,Integer>> dtEntry:distanceTable.entrySet()){
            int neighbourId=dtEntry.getKey();
            if(neighbourId!=getNodeID()){
                HashMap<Integer,Integer> neighbourDV=dtEntry.getValue();
                for(Map.Entry<Integer,Integer> dvEntry:neighbourDV.entrySet()){
                    if (ownDistanceVector.containsKey(dvEntry.getKey())) {
                        int newDistance = dvEntry.getValue() + linkCostTable.get(neighbourId);
                        if (newDistance < ownDistanceVector.get(dvEntry.getKey())) {
                            ownDistanceVector.replace(dvEntry.getKey(), newDistance);
                        }
                    } else {
                        ownDistanceVector.put(dvEntry.getKey(), dvEntry.getValue() + linkCostTable.get(neighbourId));
                    }
                }
            }

        }

        return oldDistanceTable;
    }

    public synchronized boolean sendUpdate() throws Exception {

        // int bnwd,int recvId,int sendId,HashMap<Integer,Integer> distanceVector

        if (IS_CONVERGED) {
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
            updateDistanceTable();
            return true;
        }
    }
    public String distanceTabletoStr(){

        /*
        String distanceTableStr=new String("Distance Table\n");
        for(Map.Entry<Integer,HashMap<Integer,Integer>> dtEntry:distanceTable.entrySet()){
            HashMap<Integer,Integer> currDistanceVector=dtEntry.getValue();
            int currId=dtEntry.getKey();
            distanceTableStr=distanceTableStr.concat(String.format("From:%2d [",currId));
            for(Map.Entry<Integer,Integer> distEntry:currDistanceVector.entrySet()){
                distanceTableStr=distanceTableStr.concat("(to:"+distEntry.getKey()+",dist:"+distEntry.getValue()+")");
            }
            distanceTableStr=distanceTableStr.concat("]\n");
        }
        */




        //Buğra:

        StringBuilder distTable = new StringBuilder("Our distance vector and routes:\n");
        distTable.append("  dist |     ");
        ArrayList<Integer> costs = new ArrayList<>();

        StringBuilder costLine = new StringBuilder("  cost |     ");
        for(int i = 0; i < this.totalNodeNum; i++) {

            distTable.append(i + "     ");
            costs.add(i, 999);

        }
        distTable.append("\n-----------------------------------------------\n");

        for(Map.Entry<Integer,HashMap<Integer,Integer>> dtEntry:distanceTable.entrySet()){

            if (dtEntry.getKey() == this.nodeID) {

                for(Map.Entry<Integer,Integer> distEntry:dtEntry.getValue().entrySet()) {

                    for(int i = 0; i < this.totalNodeNum; i++) {

                        if (i == distEntry.getKey()) {
                            costs.add(i, distEntry.getValue());
                            break;
                        }
                    }

                }
            }
        }


        for(int i = 0; i < this.totalNodeNum; i++) {

            costLine.append(costs.get(i) + "     ");

        }

        distTable.append(costLine.toString() + "\n\n");



        return distTable.toString();
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
        drawJFrame();
        textArea.append(this.toString()+ forwardTabletoStr() + "\n");
        while(!IS_CONVERGED) {
            try {
                sleep(500);
                //textArea.setText(this.toString()+forwardTabletoStr()+distanceTabletoStr());
                textArea.append(distanceTabletoStr());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        textArea.append("\n\nNODE IS CONVERGED...\n\n");
    }

    public Point calculateFramePos(int nodeId){
        int rowInd=0;
        int colPos=0;
        int screenHeight=(int)screenSize.getHeight()-100;
        int screenWidth=(int)screenSize.getWidth()-100;
        int widthSum= nodeId*frameWidth;
        rowInd=(int)Math.floor((widthSum)/(screenWidth-frameWidth));
        colPos=(int)((widthSum)%(screenWidth-frameWidth));
        if(rowInd*frameHeight>screenHeight){
            rowInd=0;
        }
        return new Point(colPos,rowInd*frameHeight);

    }

    public void drawJFrame(){
        /*
        this.frameWidth=700;
        this.frameHeight=400;
        this.frame=new JFrame("Node: "+getNodeID());
        //frame.setLayout(new GridBagLayout());
        frame.setLayout(null);
        this.textArea =new JTextArea();
        textArea.setFont(new Font("Serif",Font.PLAIN,14));

        textArea.setBounds(50,25,frameWidth-100,frameHeight-100);
        textArea.setText(this.toString()+forwardTabletoStr());
        //textArea.setLocation(200,200);
        Point start=calculateFramePos(getNodeID());
        int startx=(int)start.getX();
        int starty=(int)start.getY();
        frame.setBounds(startx,starty,frameWidth,frameHeight);
        frame.add(textArea);
        frame.setVisible(true); */

        this.frameWidth=400;
        this.frameHeight=400;
        this.frame=new JFrame("Node: "+getNodeID());
        //frame.setLayout(new GridBagLayout());
        frame.setLayout(null);
        this.textArea =new JTextArea();
        textArea.setFont(new Font("Serif",Font.PLAIN,14));
        //setBounds(frame.getX(),frame.getY() - 22,frameWidth - 10,frameHeight - 10);
        textArea.setText(this.toString()+forwardTabletoStr());

        JScrollPane scroll = new JScrollPane (textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scroll.setBounds(frame.getX(),frame.getY() - 22,frameWidth - 10,frameHeight - 10);

        //textArea.setLocation(200,200);
        Point start=calculateFramePos(getNodeID());
        int startx=(int)start.getX();
        int starty=(int)start.getY();
        frame.setBounds(startx,starty,frameWidth,frameHeight);
        //frame.add(textArea);
        frame.add(scroll);
        frame.setVisible(true);




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
