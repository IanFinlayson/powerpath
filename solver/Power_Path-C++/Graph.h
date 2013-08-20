#ifndef GRAPH_H
#define GRAPH_H

#include "GraphNode.h"
#include "GraphEdge.h"
#include <list>
#include <vector>
using namespace std;

class Graph
{
public:
	vector<GraphNode> nodes;//all the nodes that make up this graph
	
    //each node in the graph has an associated ArrayList of edges that are connected to it
    vector <list<GraphEdge>> edges;

    bool isDigraph;//assigned in the constructor
    int nextNodeIndex;//index of the next node to be added: might not be necessary with ArrayList
  
    //constructor

	 Graph(bool digraph);    
  
    //adds a node to the graph and returns its index
    int   AddNode(GraphNode node);
		  

	 //Use this to add an edge to the graph. The method will ensure that the
	 //edge passed as a parameter is valid before adding it to the graph. If the
	 //graph is a digraph then a similar edge connecting the nodes in the opposite
	 //direction will be automatically added.
	 void  AddEdge(GraphEdge edge);	 
	  
	    
	    //returns true if the graph contains no nodes
	    bool isEmpty();

	    //returns true if a node with the given index is present in the graph
	    bool isNodePresent(int nd);

	  //clears the graph ready for new node insertions.
	  void Clear();

	  //returns true if an edge is not already present in the graph. Used
	  //when adding edges to make sure no duplicates are created.
	  bool UniqueEdge(int from, int to);
	 
};

#endif