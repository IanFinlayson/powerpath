package com.example.research;

public class GraphEdge 
{
	//the node from which this edge is extending. represented by an index in a graph's array of nodes
	int from;
    //the node to which this edge is extending. represented by an index in a graph's array of nodes
    int to;
    boolean active = false;//based on how connections were drawn, determines if edge is lit up. 
    
    //constructors   
    public GraphEdge(int ifrom, int  ito)
    {
        from = ifrom;
        to = ito; 
    }
    
    public GraphEdge()
    {
        from = -1;//invalid index
        to = -1; //invalid index
    }

    //Returns true if both edges are the same
    public boolean IsEqual(GraphEdge lhs, GraphEdge rhs)
    {
    	if(lhs == rhs)//I'm hoping this means they are the same object. accounting for both null.
    	{
    		return true;    		
    	}
    	//if one of them is null, return false
    	if(lhs == null || rhs == null)
    	{
    		return false;    		
    	}    	
    	
    	return rhs.from == lhs.from && rhs.to == lhs.to;
    }

    //Copies the data from the edge passed to this one. 
    public void CopyEdge(GraphEdge other)
    {
        if (other == null) return;//we cant assign an edge to be null
        from = other.from;
        to = other.to;
    }

}//end class GraphEdge

