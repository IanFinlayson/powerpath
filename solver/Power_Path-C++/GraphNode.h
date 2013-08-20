#ifndef GRAPH_NODE_H
#define GRAPH_NODE_H

#include "Vector2d.h"

class GraphNode
{
public:
	static enum NodeState
		{
			Active,
			Deactive,
			Overloaded
		};

	//every node has an index. A valid index is >= 0
		int index;
	
		NodeState nodeState;

	

	GraphNode(int ind);
		

};

#endif