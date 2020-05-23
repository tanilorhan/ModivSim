import java.util.HashMap;

public class Message {

    private int bandwidth;
    private int receiverID;
    private int senderID;
    private HashMap<Integer,Integer> distanceVector;

    public Message(){

    }

    public Message(int bnwd,int recvId,int sendId,HashMap<Integer,Integer> distanceVector){
        this.distanceVector=distanceVector;
        this.receiverID=recvId;
        this.senderID=sendId;
        this.bandwidth=bnwd;
    }

    public HashMap<Integer, Integer> getDistanceVector() {
        return distanceVector;
    }

    public void setDistanceVector(HashMap<Integer, Integer> distanceVector) {
        this.distanceVector = distanceVector;
    }


    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public int getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }
}
