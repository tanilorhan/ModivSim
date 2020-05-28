import java.util.Map;
import java.util.TimerTask;

public class PeriodicSendUpdateTask extends TimerTask {

    private int period;

    public PeriodicSendUpdateTask(int period){
        this.period=period;
    }

    public void run(){
        for(Map.Entry<Integer,Node> nodeEntry : ModivSim.getNodeThreadsTable().entrySet()){
            try {
                nodeEntry.getValue().sendUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getPeriod(){
        return period;
    }

    public void setPeriod(int period){
        this.period=period;
    }
}
