import java.util.ArrayList;
import java.util.HashMap;

public class ModivSim {

    public static void main(String[] args){
        System.out.println("Hello");
        Node n1= new Node(1);
        Node n2= new Node(2);
        n1.start();
        n2.start();

        String filepath="./input/input1.txt";
        HashMap<Integer, ArrayList<int[]>> nodeMap=NodeConstructionHandler.getNodeMapFromText(filepath);
        System.out.println(nodeMap.toString());
        try {
            java.util.concurrent.TimeUnit.SECONDS.sleep(5);
        }catch(InterruptedException e){
            e.printStackTrace();
        }


        n1.interrupt();

        n2.interrupt();
    }



}
