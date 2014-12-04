import net.datastructures.*;
import java.util.Map;
import java.util.*;
public class NamedAdjacencyMapGraph<V,E> extends AdjacencyMapGraph<V,E>{
	
	Map<V,Vertex<V>> names;
	public NamedAdjacencyMapGraph(boolean directed) {
		super(directed);
		names = new HashMap<V,Vertex<V>>();
		}
	
	public Vertex<V> getVertex(V name){
			return names.get(name);
	}
	boolean vertexInGraph(V name){
		return names.containsKey(name);
	}
	
	public Vertex<V> insertVertex(V name) {
    Vertex<V> newVertex = super.insertVertex(name);
    names.put(name, newVertex);
    return newVertex;
  }
	
	public void removeVertex(Vertex<V> v) throws IllegalArgumentException {
 		names.remove(v.getElement());
 		super.removeVertex(v);
  }
	
	public Edge<E> insertEdge(V uName, V vName, E element) throws IllegalArgumentException {
	  return super.insertEdge(getVertex(uName), getVertex(vName), element);
	}
	
	public Edge<E> getEdge(V uName, V vName) throws IllegalArgumentException {
    return super.getEdge(getVertex(uName), getVertex(vName));
  }
	
}

