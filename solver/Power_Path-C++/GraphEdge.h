#ifndef GRAPH_EDGE_H
#define GRAPH_EDGE_H

class GraphEdge 
{
public:
	//the node from which this edge is extending. represented by an index in a graph's array of nodes
	int from;
    //the node to which this edge is extending. represented by an index in a graph's array of nodes
    int to;
    bool active;//based on how connections were drawn, determines if edge is lit up. 
    
    //constructors   
    GraphEdge(int ifrom, int  ito)
    {
        from = ifrom;
        to = ito; 
		active = false;
    }
    
    GraphEdge()
    {
        from = -1;//invalid index
        to = -1; //invalid index
		active = false;
    }

   

};//end class GraphEdge

#endif