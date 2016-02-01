package com.example.research;

public class GraphNode 
{	
	public static enum NodeState
	{
		Active,
		Deactive,
		Overloaded
	}
	
	//every node has an index. A valid index is >= 0
	int index;
  	//the node's position
	Vector2d position;
	
	NodeState nodeState;
	
	//boolean active = false;//determines if a node is drawn green and how edges respond to it. 
	    
	public GraphNode(int ind, Vector2d pos)
	{
	    index = ind;
	    position = pos;	
	    nodeState = NodeState.Deactive;
	}	
}