public class Flow {

    private Node source;
    private Node destination;
    private int dataBits;

    public Flow(Node source, Node destination, int dataBits) {
        this.source = source;
        this.destination = destination;
        this.dataBits = dataBits;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    @Override
    public String toString() {
        return "Flow{" +
                "source=" + source +
                ", destination=" + destination +
                ", dataBits=" + dataBits +
                '}';
    }
}
