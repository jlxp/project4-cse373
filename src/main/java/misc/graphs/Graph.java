package misc.graphs;

import java.util.Stack;

import javax.print.attribute.standard.MediaSize.Other;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.Searcher;
import misc.exceptions.NoPathExistsException;
import misc.exceptions.NotYetImplementedException;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends Edge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated then usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've contrained Graph
    //   so that E *must* always be an instance of Edge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the Edge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.
    private IDictionary<V, ISet<E>> graph;
    private IList<V> vertices; 
    private IList<E> edges;
    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException  if any of the edges have a negative weight
     * @throws IllegalArgumentException  if one of the edges connects to a vertex not
     *                                   present in the 'vertices' list
     */
    public Graph(IList<V> vertices, IList<E> edges) {
        this.graph = new ChainedHashDictionary<>();
        this.vertices = vertices; 
        this.edges = edges;
        for (V vertex : vertices) {
            this.graph.put(vertex, new ChainedHashSet<E>());
            for (E edge : edges) {
                if (edge.getWeight() < 0.0) {
                    throw new IllegalArgumentException("edge is negative");
                }
                V edgeVertex1 = edge.getVertex1();
                V edgeVertex2 = edge.getVertex2();
                if (!vertices.contains(edgeVertex1) || !vertices.contains(edgeVertex2)) {
                    throw new IllegalArgumentException("edge is not valid");
                }
                if(vertex.equals(edgeVertex1) || vertex.equals(edgeVertex2)) {
                    this.graph.get(vertex).add(edge);
                }
            }
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return this.vertices.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return this.edges.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        ISet<E> resultSet = new ChainedHashSet<>();
//        IList<E> topEdges = Searcher.topKSort(this.edges.size(), this.edges);
//        ISet<V> visited = new ChainedHashSet<>();
//        for (E edge : topEdges) {
//            V vertex1 = edge.getVertex1();
//            V vertex2 = edge.getVertex2();
//            if (!visited.contains(vertex1) || !visited.contains(vertex2)) {
//                resultSet.add(edge);
//            }
//            visited.add(vertex1);
//            visited.add(vertex2);
//        }
        
        IPriorityQueue<E> edgeHeap = new ArrayHeap<>();
        IDisjointSet<V> verticesSet = new ArrayDisjointSet<>();
        
        for (E edge : this.edges) {
            edgeHeap.insert(edge);
        }
        for (V vertex : this.vertices) {
            verticesSet.makeSet(vertex);
        }
        
        int index = 0;
        while (index < this.vertices.size() - 1) {
            E edge = edgeHeap.removeMin();
            V ver1 = edge.getVertex1();
            V ver2 = edge.getVertex2();
            if (verticesSet.findSet(ver1) != verticesSet.findSet(ver2)) {
                verticesSet.union(ver1, ver2);
                resultSet.add(edge);
                index++;
            }
        }
        return resultSet;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        // first init vertex's weight as infinities and source weight = 0
        // vertices = unprocessed
        // source == priority 0
        
//        ISet<PseudoVertex<V>> pseudovertices = new ChainedHashSet<>();
//        PseudoVertex<V> init = new PseudoVertex<V>(start, 0); 
//        pseudovertices.add(init);  
        
        IList<E> result = new DoubleLinkedList<>();
        IDictionary<V, PseudoVertex<V, E>> pseudovertices = new ChainedHashDictionary<>();
        for (V vertex : this.vertices) {
                pseudovertices.put(vertex, new PseudoVertex<V, E>(vertex));
        }

        pseudovertices.put(start, new PseudoVertex<V, E>(start, 0.0));
       
        ISet<PseudoVertex<V, E>> processed = new ChainedHashSet<>();
        
        IPriorityQueue<PseudoVertex<V, E>> vertexHeap = new ArrayHeap<>();
        
        vertexHeap.insert(pseudovertices.get(start));

        while(!vertexHeap.isEmpty()) {
            PseudoVertex<V, E> currentVer = vertexHeap.removeMin();
            V current = currentVer.getVertex();
            double currentDist = currentVer.getDistance();
            ISet<E> currentEdges = this.graph.get(current);
            for (E edge : currentEdges) { // pick the edge attached to the current
                V other;
                if (current.equals(edge.getVertex1())) {
                    other = edge.getVertex2(); 
                } else {
                    other = edge.getVertex1();
                }
              
                if (!processed.contains(pseudovertices.get(other))) { // processed vertex is skipped!
                    PseudoVertex<V, E> otherpseudo = pseudovertices.get(other);
                    
                    double distance = otherpseudo.getDistance();
                    
                    double newDistance = currentDist + edge.getWeight();
                    
                    if (newDistance < distance) {
                        otherpseudo.setDistance(newDistance);
                        otherpseudo.setEdge(edge); // not only setting edge, but implicitly storing predecessor
                        vertexHeap.insert(otherpseudo);
                        // decrease Priority problem is solved by creating class of pseudovertex 
                    }                
                    pseudovertices.put(other, otherpseudo); // update the pseudovertices (distance and predecessor)   
                }
            }
            processed.add(currentVer);
        }

        Stack<PseudoVertex<V, E>> tempStack = new Stack<>();
        V currentVertex = end; 
        while(!currentVertex.equals(start)) { // we are backtracking from the end, using precedecessor
            PseudoVertex<V, E> current = pseudovertices.get(currentVertex);
            if (current.getEdge() == null || tempStack.contains(current)) {
                throw new NoPathExistsException("no path from start to end");
            }
            tempStack.push(current); // bug?? was before if statement
            currentVertex = current.callPredecessor();             
        }
        
        while(!tempStack.isEmpty()) { // classic 143 stack-queue transfer
            result.add((E) tempStack.pop().getEdge()); // this is possible as edge of pseudovertex is from its predecessor to current
        }
        
        return result; 
        
    }
    
    private static class PseudoVertex<V, E extends Edge<V>> implements Comparable<PseudoVertex<V, E>> {
        private V vertex;
        private E edge; // edge coming to this vertex
        private double distance;
        
        public PseudoVertex(V vertex) {
            this(vertex, Double.POSITIVE_INFINITY);            
        }
        
        public PseudoVertex(V vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance; 
            this.edge = null;       
        }
        
        public void setEdge(E edge) {
            this.edge = edge;
        }

        public void setDistance(double distance) {
            this.distance = distance; 
        }
        
        public V callPredecessor() {
            return this.edge.getOtherVertex(this.vertex);
        }
        
        public E getEdge() {
            return this.edge;
        }
        
        public V getVertex() {
            return this.vertex;
        }
        
        public double getDistance() {
            return this.distance;
        }
        

        @Override
        public int compareTo(PseudoVertex<V, E> o) {
            return Double.compare(this.distance, o.getDistance());
        }

    }
    
//    private static class GraphEdge<V> implements Edge<V>, Comparable<Edge<V>> {
//        private V vertex1;
//        private V vertex2;
//        private double weight;
//        
//        
//        public GraphEdge(V vertex1, V vertex2, double weight) {
//            this.vertex1 = vertex1;
//            this.vertex2 = vertex2;
//            this.weight = weight;
//        }
//        
//        @Override
//        public V getVertex1() {
//            return this.vertex1;
//        }
//
//        @Override
//        public V getVertex2() {
//            return this.vertex2;
//        }
//
//        @Override
//        public double getWeight() {
//            return weight; 
//        }
//
//        @Override
//        public int compareTo(Edge<V> o) {
//            return Double.compare(this.weight, o.getWeight());
//        }
//        
//    }
}
