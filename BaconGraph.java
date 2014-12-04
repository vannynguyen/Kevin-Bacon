import java.util.ArrayList;
import java.util.Map;
import java.util.*;
import net.datastructures.*;
import java.io.*;

/**
 * 
 * @author Vanny Nguyen
 *
 */
public class BaconGraph {
	private Map<Integer, String> actors;
	private Map<Integer, String> movies;
	private Map<Integer, ArrayList<Integer>> movieActors;
	private NamedAdjacencyMapGraph<String, String> baconGraph;
	private SentinelDLL<String> bfsQueue; 
	private NamedAdjacencyMapGraph<String, String> bfsGraph;
	public BaconGraph() throws IOException{
		this.readActors();
		this.readMovies();
		this.readMovieActors();
		this.makeBacon();
		this.makeBFSTree();
	}
	
	/**
	 * @author Vanny Nguyen
	 * @throws IOException
	 * Reads file of actors and places their names and IDs in map. Creates Vertices for actors and inserts them in baconGraph.
	 */
	public void readActors() throws IOException{
		actors = new HashMap<Integer, String>();
		baconGraph = new NamedAdjacencyMapGraph<String,String>(false);
		
		BufferedReader inputFile =  new BufferedReader(new FileReader("src/actors.txt"));
		String line = inputFile.readLine();
		try {
			while(line!=null && !line.isEmpty()){
				//Parse line
				Integer actorID = Integer.parseInt(line.substring(0,line.indexOf("|")));
				String actorName = line.substring(line.indexOf("|")+1);
				
				//place info in actor map
				actors.put(actorID,actorName);
				//insert vertex in baconGraph
				baconGraph.insertVertex(actorName);
				line = inputFile.readLine();
			}
		}
		finally {
			inputFile.close();
		}
	}
	
	/**
	 * @author Vanny Nguyen
	 * @throws IOException
	 * Reads file of movies and places their names and IDs in map
	 */
	public void readMovies() throws IOException{
		movies = new HashMap<Integer, String>();
		BufferedReader inputFile =  new BufferedReader(new FileReader("src/movies.txt"));
		String line = inputFile.readLine();
		try {
			while(line!=null && !line.isEmpty()){
				//Parse line
				Integer movieID = Integer.parseInt(line.substring(0,line.indexOf("|")));
				String movieName = line.substring(line.indexOf("|")+1);
				
				//place info in movie map
				movies.put(movieID,movieName);
				
				line = inputFile.readLine();
			}
		}
		finally {
			inputFile.close();
		}
	}
	
	/**
	 * @author Vanny Nguyen
	 * @throws IOException
	 * Reads file of movies & actors ID and places them IDs in map
	 */
	public void readMovieActors() throws IOException{
		movieActors = new HashMap<Integer, ArrayList<Integer>>();
		BufferedReader inputFile =  new BufferedReader(new FileReader("src/movie-actors.txt"));
		String line = inputFile.readLine();
		try {
			while(line!=null && !line.isEmpty()){
				//Parse line
				Integer movieID = Integer.parseInt(line.substring(0,line.indexOf("|")));
				Integer actorID = Integer.parseInt(line.substring(line.indexOf("|")+1));
				
				//check to see if movie already in map, if not add an ArrayList
				if(!movieActors.containsKey(movieID)){
					ArrayList<Integer> actorList = new ArrayList<Integer>();
					actorList.add(actorID);
					movieActors.put(movieID,actorList);
				}
				else{
					movieActors.get(movieID).add(actorID);
				}
				
				line = inputFile.readLine();
			}
		}
		finally {
			inputFile.close();
		}
	}
	
	/**
	 * @author Vanny Nguyen
	 * Insert edges in baconGraph
	 */
	public void makeBacon(){
		//iterate through movies
		for(Integer movie:movieActors.keySet()){	
				//iterate through actors movie; keep going until less than two left
				for(int i=0;movieActors.get(movie).size()-i>1;i++){
					//iterate through sublist of actors to create edges
					for(int j=i+1;j<movieActors.get(movie).size();j++){
						String actor1 = actors.get(movieActors.get(movie).get(i));
						String actor2 = actors.get(movieActors.get(movie).get(j));
						String mutualMovie = movies.get(movie);
						try {
							baconGraph.insertEdge(actor1,actor2,mutualMovie);
						}
						catch(IllegalArgumentException e) {
							//ignore
						}
					}
				}
		}		
	}
	
	/**
	 * @author Vanny Nguyen
	 * Construct BFS Tree to traverse and find distances
	 */
	public void makeBFSTree(){
		bfsQueue = new SentinelDLL<String>();
		bfsGraph = new NamedAdjacencyMapGraph<String,String>(true);
		bfsQueue.addLast(baconGraph.getVertex("Kevin Bacon").getElement());
		bfsGraph.insertVertex(bfsQueue.getFirst());
		while(!bfsQueue.isEmpty()){			
			String x = bfsQueue.getFirst();		
			bfsQueue.remove();
			
			for(Edge<String> e: baconGraph.incomingEdges(baconGraph.getVertex(x))){
				String y = baconGraph.opposite(baconGraph.getVertex(x),e).getElement();
				if(!bfsGraph.vertexInGraph(y)){
					bfsGraph.insertVertex(y);
					bfsGraph.insertEdge(y, x, e.getElement());
					bfsQueue.addLast(y);
				}
			}			
		}
	}
	/**
	 * @author Vanny Nguyen
	 * @param startActor
	 * traverses graph in order to calculate Bacon number
	 */
	public void calcBaconNum(String startActor){
		if(baconGraph.vertexInGraph(startActor)&&bfsGraph.vertexInGraph(startActor)){
			ArrayList<String> movieList = new ArrayList<String>();
			ArrayList<String> partnerList = new ArrayList<String>();
			
			//create temporary Vertex; initial value Vertex of input
			Vertex<String> tempV = bfsGraph.getVertex(startActor);
			Iterator<Edge<String>> edgeIter = bfsGraph.outgoingEdges(tempV).iterator();
			
			while(edgeIter.hasNext()){
				//temporary Edge
				Edge<String> tempE = edgeIter.next();
				tempV = bfsGraph.opposite(tempV, tempE);
				
				//add movie to list
				movieList.add(tempE.getElement());
				//add partner to list
				partnerList.add(tempV.getElement());
				
				//change Iterators
				edgeIter = bfsGraph.outgoingEdges(tempV).iterator();
			}
			//check for infinite KB number
			if(partnerList.contains("Kevin Bacon")&&!startActor.equals("Kevin Bacon")){
				//print out results
				System.out.println(startActor+"'s Kevin Bacon number is "+movieList.size()+".");
				String tempActor = startActor;
				for(int i=0;i<movieList.size();i++){
					
					System.out.println(tempActor+" appeared in "+movieList.get(i)+" with "+partnerList.get(i)+".");
					tempActor = partnerList.get(i);
				}
			}
			else if(startActor.equals("Kevin Bacon")){
				System.out.println("Kevin Bacon's Kevin Bacon number is 0.");
			}
				
			
		}
		//if actor not in database
		else if(!bfsGraph.vertexInGraph(startActor)&&baconGraph.vertexInGraph(startActor)){
			System.out.println(startActor+"'s Kevin Bacon number is infinity.");
		}
		else
			System.out.println(startActor+" is not in our database.");
	}
	
	/**
	 * @author Vanny Nguyen 
	 */
	public void printBFS(){
		System.out.println(bfsGraph);
	}
	
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		BaconGraph test = new BaconGraph();
		Scanner sc = new Scanner(System.in);
		String inputLine = "";
		String yesOrNo = "y";
		while(yesOrNo.equals("y")){
			System.out.print("Enter the name of an actor: ");
			inputLine = sc.nextLine();
			test.calcBaconNum(inputLine);
			System.out.print("Try again? (y/n): ");
			yesOrNo = sc.nextLine();
		}
		sc.close();

	}

}
