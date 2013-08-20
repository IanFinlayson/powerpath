#include <iostream>
#include <fstream>
#include <string>
using namespace std;
#include "Graph.h"
#include <algorithm>
#include <time.h>
//#include <Windows.h>

const bool Calculate(Graph &myGraph, int tempPath[], int numNodes);
void GenerateGraph(Graph &myGraph, int numNodes);

int main()
{
	clock_t t1,t2;
    t1=clock();
//	long int before = GetTickCount();

	const int numOfNodes = 16;
	//create my own instance of a graph. 
	Graph myGraph(false);

	GenerateGraph(myGraph, numOfNodes);//reads from file

	//create array of numbers
	int numnums[numOfNodes];
	for(int i = 0; i < numOfNodes; i++)
	{
		numnums[i] = i;
	}

	do
	{
		/*
		for(int i = 0; i < numOfNodes; i++)
		{
			cout << numnums[i] << ' ';
		}
		cout << endl << endl;
		*/
		
		//try to travel teh graph		
		if(numnums[0] != 0)
		{			
			break;
		}
		if(Calculate(myGraph, numnums, numOfNodes) )
		{
			break;
		}

	}while(	std::next_permutation(numnums,numnums+numOfNodes));
	 
	
   
    t2=clock();
    float diff = ((float)t2-(float)t1) / CLOCKS_PER_SEC;
	
	//long int after = GetTickCount();

	cout << "end time: " << diff<< endl;//(after - before)/ 1000 
}

const bool Calculate(Graph &myGraph, int tempPath[], int numNodes)
{
//	cout << "calculating" << endl;
	
	for(int i = 0; i < numNodes; i++)//check every node in the graph
	{
		bool isthereanextedge = false;
		//check that the edges coming out of the index pertaining to tempPath connects to the next node in thep ath. 
		for (list<GraphEdge>::const_iterator curEdge = myGraph.edges[tempPath[i]].begin();
			curEdge != myGraph.edges[tempPath[i]].end(); ++curEdge)
		{			
		//	cout << "curEdge->to, tempPath[i+1]" << curEdge->to << ", " << tempPath[i+1] << endl;
			int wrappy2 = i+1;
			if(wrappy2 >= numNodes) wrappy2 -= numNodes;//set it to start from 0 onward
	//		cout << "curEdge->to: " << curEdge->to << "tempPath[wrappy2]: " << tempPath[wrappy2] << endl;
			if(curEdge->to == tempPath[wrappy2])//any edges at this index that lead to next node in tempPath?
			{
		//		cout << "curEdge->to == tempPath[i+1]" << (curEdge->to == tempPath[i+1]) <<  endl;
				isthereanextedge = true;
				break;//no need to continue on edges. 	
			}			
		}
		//just finished iterating edges, bout to move to next node's edges
		if (isthereanextedge == false) 
		{
			return false;
		}
	}

	cout << "A hamiltonian path exists along these nodes:" << endl;
	for(int i = 0; i < numNodes; i++)
	{
		cout << tempPath[i] << endl;
	}
	cout << 0 << endl;
			
	return true;
}

unsigned int split(const std::string &txt, std::vector<std::string> &strs, char ch)
{
    unsigned int pos = txt.find( ch );
    unsigned int initialPos = 0;
    strs.clear();

    // Decompose statement
    while( pos != std::string::npos ) {
        strs.push_back( txt.substr( initialPos, pos - initialPos ) );
        initialPos = pos + 1;

        pos = txt.find( ch, initialPos );
    }

    // Add the last one
    strs.push_back( txt.substr( initialPos, std::min( pos, txt.size() ) - initialPos + 1 ) );

    return strs.size();
}

void GenerateGraph(Graph &myGraph, int numNodes)
{
	ifstream inputStream;
	inputStream.open("puzzle_1.txt");

	string input = "";		

	for(int i = 0; i < numNodes; i++)
	{		
		myGraph.AddNode(GraphNode(myGraph.nextNodeIndex));	
	}
		    
	while (!inputStream.eof()) //now we should be at the edges and we'll just read until we can't read no more...
	{
		getline(inputStream, input);
		std::vector<std::string> v;
		split( input, v, ' ' );
		myGraph.AddEdge(GraphEdge(stoi(v[0]), stoi(v[1])));
	}
			
	inputStream.close();
}

