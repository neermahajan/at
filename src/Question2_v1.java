import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import javax.json.*;
import javax.json.stream.JsonParsingException;

public class Question2_v1 {

	private HashMap<Integer, HashSet<Integer>> adjacencyList = new HashMap<Integer, HashSet<Integer>>();
	private HashMap<String, Integer> nodeNameToIndexMapping = new HashMap<String, Integer>();
	private ArrayList<String> indexToNodeName = new ArrayList<String>();
	private int root = 0;
	private boolean visited[];
	private int nodeCounter = 0;
	
	public static void main(String[] args) {
		Question2_v1 q2 = new Question2_v1();
		q2.readJSonObject();
	}

	private void readJSonObject() {
		JsonReader reader = null;
		JsonObject graphObject = null;
		try {
			reader = Json.createReader(new FileReader("src/question2json.txt"));
			graphObject = reader.readObject();
			
			parseData(graphObject);
			dfsStack();
			getDanglingNodes();
		} catch (FileNotFoundException e) {
			System.out.println("File does not exists");
		} catch(JsonParsingException jpse){
			System.out.println("Error while parsing data");
			System.out.println(jpse);
        }catch(JsonException je){
        	System.out.println(je);
        }finally{
        	if(reader != null)
        		reader.close();
        }
	}

	private void parseData(JsonObject graphObject) {
		JsonObject object;
		JsonArray nodes = graphObject.getJsonArray("nodes");
		String nodeName = "";
		int toIndex = 0;
        int fromIndex = 0;
        HashSet<Integer> temp = null;
        
        for (JsonValue jsonValue : nodes) {
        	object = (JsonObject) jsonValue;
        	nodeName = object.getString("id");
        	if(!nodeNameToIndexMapping.containsKey(nodeName)){
        		nodeNameToIndexMapping.put(nodeName, nodeCounter);
        		indexToNodeName.add(nodeName);
        		nodeCounter++;
        	}
        }
        
        root = nodeNameToIndexMapping.get(graphObject.getString("root"));
        
        JsonArray edges = graphObject.getJsonArray("edges");
        for (JsonValue jsonValue : edges) {
        	object = (JsonObject) jsonValue;
        	toIndex = nodeNameToIndexMapping.get(object.getString("to"));
        	fromIndex = nodeNameToIndexMapping.get(object.getString("from"));
        	
        	if(!adjacencyList.containsKey(toIndex)){
        		adjacencyList.put(toIndex, new HashSet<Integer>());
        	}
        	
        	temp = adjacencyList.get(toIndex);
        	temp.add(fromIndex);
        	adjacencyList.put(toIndex, temp);
        }
        
        //printGraph();
        
        JsonArray deletedEdges = graphObject.getJsonArray("deletedEdge");
        for (JsonValue jsonValue : deletedEdges) {
        	object = (JsonObject) jsonValue;
        	toIndex = nodeNameToIndexMapping.get(object.getString("to"));
        	fromIndex = nodeNameToIndexMapping.get(object.getString("from"));
        	
        	if(toIndex != -1 && fromIndex != -1){
        		temp = adjacencyList.get(toIndex);
            	temp.remove(fromIndex);
            	adjacencyList.put(toIndex, temp);
        	}
        }
        
        System.out.println("After edges removed");
        
        //printGraph();
	}

	private void printGraph() {
		System.out.println("Node names to Index Mapping\n");
		for(String key: nodeNameToIndexMapping.keySet()){
			System.out.println(key + " >>> " + nodeNameToIndexMapping.get(key));
		}
		
		System.out.println("Index to Node name Mapping\n");
		for(int index = 0; index < indexToNodeName.size(); index++){
			System.out.println(index + " >>> " + indexToNodeName.get(index));
		}
		
		System.out.println("Edges in the graph\n");
		for(int key: adjacencyList.keySet()){
			System.out.print(key);
			for(int node: adjacencyList.get(key)){
				System.out.print(" --> " + node);
			}
			System.out.println();
		}
	}
	
	private void dfsStack(){
		visited = new boolean[nodeNameToIndexMapping.size()];
		Stack<Integer> stack = new Stack<Integer>();
		stack.add(root);
		visited[root] = true;
		
		while(!stack.isEmpty()){
			int currentNode = stack.pop();
			//System.out.println("Visited Node >>> " + indexToNodeName.get(currentNode) + " >>> " + currentNode);
			//System.out.println(adjacencyList.get(currentNode));
			
			if(adjacencyList.get(currentNode) != null)
				for(int neighbour: adjacencyList.get(currentNode)){
					if(!visited[neighbour]){
						stack.add(neighbour);
						visited[neighbour] = true;
					}
				}
		}
	}
	
	private void getDanglingNodes(){
		System.out.println();
		System.out.println("List of dangling nodes");
		ArrayList<String> danglingNodes = new ArrayList<String>(); 
		for(int i = 0; i < visited.length; i++){
			if(visited[i] == false)
				danglingNodes.add(indexToNodeName.get(i));
		}
		
		if(danglingNodes.size() == 0){
			System.out.println("No dangling nodes");
		}else{
			for(String temp: danglingNodes){
				System.out.print(temp + " ");
			}
		}
	}
}
