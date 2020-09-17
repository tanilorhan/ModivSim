import java.util.ArrayList;

public class Flow {

    private String label;
    private int src;
    private int dest;
    private int size;
    private ArrayList<Integer> path;
    private int remainDuration;


    public Flow(String label,int src, int dest, int size) {
        this.label=label;
        this.src = src;
        this.dest = dest;
        this.size = size;
    }

    public Flow(String label,int src,int dest,int size,ArrayList<Integer> path, int remainDuration) {
        this.label = label;
        this.src=src;
        this.dest=dest;
        this.size=size;
        this.path = path;
        this.remainDuration = remainDuration;

    }

    public Flow(){

    }

    public ArrayList<Integer> getPath() {
        return path;
    }

    public void setPath(ArrayList<Integer> path) {
        this.path = path;
    }

    public int getRemainDuration() {
        return remainDuration;
    }

    public void setRemainDuration(int remainDuration) {
        this.remainDuration = remainDuration;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public int getDest() {
        return dest;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Flow{" +
                "label='" + label + '\'' +
                ", src=" + src +
                ", dest=" + dest +
                ", size=" + size +
                '}';
    }


}
