package segment;

import java.util.*;

class Component2D
{
    ArrayList<Node2D> nodes;
    ArrayList<Edge> edges;
    double k;
    Graph2D g;

    public Component2D(Graph2D graph, double k)
    {
        g = graph;
        this.k = k;
        nodes = new ArrayList<Node2D>();
        edges = new ArrayList<Edge>();
    }


    public void addNode(Node2D n)
    {
        for(Node2D n2 : nodes){
            if(areNeighbors(n,n2))
               edges.add(new Edge(n, n2));
        }
        nodes.add(n);
        if(nodes.size()-1 > edges.size())
            System.out.println("SOMETHING IS VERY WRONG!!! "+nodes.size()+"   "+edges.size());
    }

    private boolean areNeighbors(Node2D n1, Node2D n2)
    {
        boolean neighbors = false;

        int x1 = n1.location[0];
        int x2 = n2.location[0];
        int y1 = n1.location[1];
        int y2 = n2.location[1];

        int diffx = Math.abs(x1-x2);
        int diffy = Math.abs(y1-y2);

        if(diffx < 2 && diffy < 2)
            neighbors = true;

        return neighbors;
    }

    public void addEdge(Edge e)
    {
        edges.add(e);
    }

    public void addEdge(Node2D n1, Node2D n2)
    {
        edges.add(new Edge(n1, n2));
    }

    public boolean containsNode(int x, int y)
    {
        boolean contains = false;

        for(Node2D n : nodes){
            if(n.location[0] == x && n.location[1] == y){
                contains = true;
                break;
            }
        }

        return contains;
    }

    public boolean containsNode(Node2D n)
    {
        boolean contains = false;

        for(Node2D n1 : nodes){
            if(n.nodeEqual(n1)){
                contains = true;
                break;
            }
        }

        return contains;
    }

    public boolean containsNodes(Node2D n1, Node2D n2)
    {
        return (containsNode(n1) && containsNode(n2));
    }


    public boolean disjointSet(Component2D c)
    {
        boolean disjoint = true;

        if(c.containsNode(nodes.get(0)))
           disjoint = false;
        //System.out.println(disjoint);
        return disjoint;
    }


    /** Internal difference function as defined by paper. **/
    public double internalDifference()
    {
        ArrayList<Edge> minST = minSpanTree(edges);
        //System.out.println("#Nodes: "+nodes.size()+" #Edge: "+edges.size()+" in minST: "+minST.size()+"  "+maxWeight(minST));
        return maxWeight(minST);
    }


    /** Difference between function as defined by paper. **/
    public double differenceBetween(Component2D c)
    {
        ArrayList<Edge> eBetween = edgesBetween(this, c);

        //System.out.println("Size eBetween: "+eBetween.size());

        if(eBetween.size() == 0) return 1000000;
        else return minWeight(eBetween);
    }


    /** Pairwise comparison between this component and another as defined by
     ** paper. **/
    public boolean pairwiseComparison(Component2D c)
    {
        boolean predicate = false;

        double intDiff1 = internalDifference() + threshold(this);
        double intDiff2 = c.internalDifference() + threshold(c);
        //System.out.println("Internal Differences: "+intDiff1+", "+intDiff2);

        //System.out.println(nodes.size()+"\t"+differenceBetween(c)+"\t"+minimum(intDiff1,intDiff2));
        if(differenceBetween(c) < minimum(intDiff1, intDiff2))
            predicate = true;
        //System.out.println(predicate);
        return predicate;
    }


    /** Threshold function as defined by paper. **/
    private double threshold(Component2D c)
    {
        double n = c.nodes.size();
        return c.k/n;
    }


    /** Find the minimum between two doubles and return it. **/
    private double minimum(double a, double b)
    {
        double min = a;
        if (b < a) min = b;
        return min;
    }


    /** Find the minimum spanning tree using Kruskal's algorithm**/
    public ArrayList<Edge> minSpanTree(ArrayList<Edge> allEdges)
    {
        ArrayList<Edge> minST = new ArrayList<Edge>();

        ArrayList<Edge> unconsideredEdges = new ArrayList<Edge>();
        for(Edge e : allEdges){
            unconsideredEdges.add(e);
        }

        //while(minST < nodes.size()){
        for(int i=0; i<nodes.size()-1; i++){
            boolean addedEdge = false;

            while(!addedEdge && unconsideredEdges.size() > 0){
                int location = g.minimumEdge(unconsideredEdges);
                Edge minimum = unconsideredEdges.get(location);

                if(!closesCircuit(minimum, minST)){
                    minST.add(minimum);
                    addedEdge = true;
                }
                unconsideredEdges.remove(location);
            }
        }
        return minST;
    }


    /** Check whether adding an edge to the list of edges would create a circuit.**/
    private boolean closesCircuit(Edge e, ArrayList<Edge> alreadyInST)
    {
        boolean n1Included = false;
        boolean n2Included = false;

        for(Edge edge : alreadyInST){
            if (edge.containsNode(e.node1))
                n1Included = true;
            if (edge.containsNode(e.node2))
                n2Included = true;
        }
        return (n1Included && n2Included);
    }


    /** Find the largest weight in a lest of edges.**/
    private double maxWeight(ArrayList<Edge> allEdges)
    {
        if(allEdges.size() == 0) return 0;
        double maxWeight = -1;

        for(int i=0; i<allEdges.size(); i++){
            if(allEdges.get(i).weight > maxWeight){
                maxWeight = allEdges.get(i).weight;
            }
        }
        return maxWeight;
    }


    /** Find the smallest weight in a list of edges.**/
    private double minWeight(ArrayList<Edge> allEdges)
    {
        double minWeight = 10000;

        for(int i=0; i<allEdges.size(); i++){
            if(allEdges.get(i).weight < minWeight){
                minWeight = allEdges.get(i).weight;
            }
        }
        return minWeight;
    }


    /** Get a list of edges that connect two components.  Will be empty if there
     ** are no connecting edges. **/
    private ArrayList<Edge> edgesBetween(Component2D c1, Component2D c2)
    {
        ArrayList<Edge> eBetween = new ArrayList<Edge>();

        for(Node2D n : c1.nodes){
            ArrayList<Edge> es = g.allEdges(n);
            for(Edge e : es){
                for(Node2D n2 : c2.nodes){
                    if (e.containsNode(n2)){
                        eBetween.add(e);
                    }
                }
            }
        }
        return eBetween;
    }
}