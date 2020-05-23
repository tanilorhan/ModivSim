import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NodeConstructionHandler {


    public static HashMap<Integer,ArrayList<int[]>> getNodeMapFromText(String fileName){


            List<String> lines = readLinesFromText(fileName);
            HashMap<Integer,ArrayList<int[]>> nodeMap= new HashMap<Integer,ArrayList<int[]>>();
            for(String line: lines){
                System.out.println("-----start of line-----");
                System.out.println(line);
                String[] tokens=line.split(",\\(");
                Integer nodeID=Integer.valueOf(tokens[0]);
                ArrayList<int[]> neighbourList=new ArrayList<>();
                System.out.println("-------");
                //for(String token:tokens)
                //    System.out.println(token);
                //System.out.println("nodeID: "+nodeID);
                for(int i=1;i<tokens.length;i++){
                    String currToken=tokens[i];
                    String delimns="[,\\)]";
                    String[] neighbourTokens=currToken.split(delimns);
                    System.out.println("currtoken"+currToken);
                    int currNeighbour[]=new int[3];
                    int currNeighbourInd=0;
                    for(String neighbourToken: neighbourTokens){
                        System.out.println("neighbourtoken: "+neighbourToken);
                        currNeighbour[currNeighbourInd++]=Integer.parseInt(neighbourToken);
                    }
                    neighbourList.add(currNeighbour);
                }
                nodeMap.put(nodeID,neighbourList);
            }

        return nodeMap;
    }

    public static List<String> readLinesFromText(String fileName){
        Path path = Paths.get(fileName);
        try {
            byte[] bytes = Files.readAllBytes(path);
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            return lines;
        }catch(IOException e){
            e.printStackTrace();
        }
        return (List)new ArrayList<>();
    }

}
