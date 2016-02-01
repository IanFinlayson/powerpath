package com.example.research;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.research.GraphNode.NodeState;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameView extends View
{
	ArrayList<GraphNode> nodes;//connects to myGraph.nodes
	public ArrayList<ArrayList<GraphEdge>> edges;//connects to myGraph.edges
	ArrayList<Point> points;//points for drawing clearing line. 
	Path path;
	
	int numActiveNodes = 0;
	Bitmap nodeActive, nodeDeactive, nodeOverloaded;
	Paint paint;// default paint passed to stuff
	Paint strokePaint;// stroke of the edges
	Paint fillPaint;// stroke of the edges
	Paint clearPaint;//for clear path
	
	float layoutSize;
	float nodeSize;
	Chronometer timer;

	GraphNode currentTouched;// keep track of the node we are touching/just touched.
	boolean gameWon = false;// when this sets to true, we finish the game.	
	boolean clearMode = false;//when the clear button is pressed once this allows you to circle nodes
	float nodeScale = .1f;// to be set in .txt determines how big the nodes are	based on density of graph
	float nodeSpacing = .2f;// to be set in .txt determines how far apart the nodes are based on density of graph
	ImageButton b_clear;
	
	//audio:
	//MediaPlayer mp_on, mp_op, mp_fin;
	
	SharedPreferences prefs;
	Editor editor;

	public GameView(Context context)
	{
		super(context); 
		nodeActive = BitmapFactory.decodeResource(getResources(),
				R.drawable.node_active);
		nodeDeactive = BitmapFactory.decodeResource(getResources(),
				R.drawable.node_deactive);
		nodeOverloaded = BitmapFactory.decodeResource(getResources(),
				R.drawable.node_overloaded);
		
		path = new Path();
		points = new ArrayList<Point>();
		b_clear = ((ImageButton)((Activity) getContext()).findViewById(R.id.b_clear));
		
		paint = new Paint();
		strokePaint = new Paint();
		fillPaint = new Paint();
		clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		strokePaint.setStrokeWidth(getResources().getDimension(R.dimen.strokesize));
		strokePaint.setARGB(255, 177, 119, 53);
		fillPaint.setStrokeWidth(getResources().getDimension(R.dimen.fillsize));
		fillPaint.setARGB(255, 83, 55, 28);		
	
		
		clearPaint.setStyle(Paint.Style.STROKE);
		clearPaint.setStrokeWidth(2);
		clearPaint.setColor(Color.RED);
		
		timer = (Chronometer) ((Activity) getContext()).findViewById(R.id.chronometer);
		timer.setBase(SystemClock.elapsedRealtime());
		timer.start();		
		/*
		mp_on = MediaPlayer.create(context, R.raw.mp_on2);
		mp_on.setVolume(.5f, .5f);
		mp_op = MediaPlayer.create(context, R.raw.mp_op);	
		mp_fin = MediaPlayer.create(context, R.raw.mp_fin2);
		mp_fin.setVolume(.6f, .6f);*/
		
		//prefs = context.getSharedPreferences("researchPref", Context.MODE_PRIVATE);
		prefs =  PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit(); 
		int tempScore = prefs.getInt("bestTime_"+PuzzleSelect.puzzleIndex, -1);
		if(tempScore == -1) ((TextView)((Activity) getContext()).findViewById(R.id.tv_score)).setText("Best Time:--");
		else 
			{
			int min = (tempScore/60);
			int sec = (tempScore%60);
				((TextView)((Activity) getContext()).findViewById(R.id.tv_score)).setText("Best Time:\n"+ min + ":" + String.format("%02d", sec));	
			}
				
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if (nodes.size() > 0)
		{
			// draw edges:
			for (ArrayList<GraphEdge> edgeList : edges)
			{
				for (GraphEdge curEdge : edgeList)
				{
					if (curEdge.active)// active
					{
						fillPaint.setARGB(255, 200, 246, 84);// active green
					} else
					// inactive
					{
						fillPaint.setARGB(255, 83, 55, 28);// deactive brown
					}

					canvas.drawLine(nodes.get(curEdge.from).position.x,	nodes.get(curEdge.from).position.y,
							nodes.get(curEdge.to).position.x, nodes.get(curEdge.to).position.y, strokePaint);
					canvas.drawLine(nodes.get(curEdge.from).position.x, nodes.get(curEdge.from).position.y,
							nodes.get(curEdge.to).position.x, nodes.get(curEdge.to).position.y, fillPaint);
				}
			}

			for (GraphNode curNode : nodes)//draw nodes
			{

				if (curNode.nodeState == NodeState.Active)
				{
					canvas.drawBitmap(nodeActive, curNode.position.x - nodeSize	/ 2, curNode.position.y - nodeSize / 2, paint);
				} 
				else if (curNode.nodeState == NodeState.Overloaded)
				{
					canvas.drawBitmap(nodeOverloaded, curNode.position.x - nodeSize / 2, curNode.position.y - nodeSize / 2, paint);
				}else
				{
					canvas.drawBitmap(nodeDeactive, curNode.position.x - nodeSize / 2, curNode.position.y - nodeSize / 2, paint);
				}
				
			}

		}//end if (nodes.size > 0)
		
		if(clearMode)//draw clear path
		{
			path.reset();

		    boolean first = true;
		    for(Point point : points)
		    {
		        if(first)
		        {
		            first = false;
		            path.moveTo(point.x, point.y);
		        }
		        else{
		            path.lineTo(point.x, point.y);
		        }
		    }
		    canvas.drawPath(path, clearPaint);			
		}
		
	}//end onDraw

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		layoutSize = getWidth();// gets width of this's view space
		if (getHeight() < layoutSize)
			layoutSize = getHeight();

		// pretty sure onMeasure is called before first draw...
		if (layoutSize > 0)// as soon as layout dimensions are created
		{
			nodeActive = Bitmap.createScaledBitmap(nodeActive,(int)(nodeScale * layoutSize),(int) (nodeScale * layoutSize), true);
			nodeDeactive = Bitmap.createScaledBitmap(nodeDeactive,(int)(nodeScale * layoutSize),(int) (nodeScale * layoutSize), true);
			nodeOverloaded = Bitmap.createScaledBitmap(nodeOverloaded,(int)(nodeScale * layoutSize),(int) (nodeScale * layoutSize), true);
			
			// loop through all nodes and adjust their positions by layout
			// dimension.
			for (GraphNode curNode : nodes)
			{
				curNode.position.x *= layoutSize;
				curNode.position.y *= layoutSize;
			}

		}
		nodeSize = nodeActive.getWidth();// nodes are square so we only need one side

	}//end onMeasure

	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// while we touch the screen, we will keep track of a current node which
		// is the last that was touched.
		// if there is no last touched node, then we have to loop through all
		// nodes constantly checking if one is
		// in range. if we have a current node, then we will only loop through
		// it's .to connetions until we are in
		// range of one of those nodes then the current will switch to that one.
		if (gameWon)
			return false;// dont listen for touch if we win game.

		try
		{
			Thread.sleep(16);// in milliseconds
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			// tv_timer.setText("posy: " + event.getY());

			break;
		case MotionEvent.ACTION_UP:
			
			if(clearMode)
			{			
				RunClear();//this calculates the clear path and determine what to do with nodes inside it	
				
			}
			//((TextView)((Activity) getContext()).findViewById(R.id.tv_score)).setText("an: " + numActiveNodes);
			invalidate();//to show the total nodes again... >_>
			currentTouched = null;//I use this inside RunClear as a placeholder
			break;
		case MotionEvent.ACTION_MOVE:
			// <= nodesize is where we set touch radius
			float touchX = event.getX();//store touch coords so I dont have to keep calling them. 
			float touchY = event.getY();
			
			if(clearMode)
			{
				Point point = new Point();
		        point.x = (int)touchX;
		        point.y = (int)touchY;
		        points.add(point);
		        invalidate();		       
			}
			else if (currentTouched == null)// we haven't touched a node yet, so loop through all for in range check							
			{
				for (GraphNode curNode : nodes)
				{
					// if the difference in positions is within the radius of the node
					if (Math.abs(curNode.position.x - touchX) <= nodeSize && Math.abs(curNode.position.y - touchY) <= nodeSize)
					{
						currentTouched = curNode;
					}
				}
			} 
			else// we've got a current node, lets check its possible neighbors for contact
			{
				for (GraphEdge curEdge : edges.get(currentTouched.index))// all the edges connected to this node
				{
					if (Math.abs(nodes.get(curEdge.to).position.x - touchX) <= nodeSize 
							&& Math.abs(nodes.get(curEdge.to).position.y - touchY) <= nodeSize)
					{
						//checks different states of currentTouched and next node to determine action
						EvaluateSituation(curEdge);
						//I think I'll always set currentTouched to the next node so they never have to lift their fingers.
						currentTouched = nodes.get(curEdge.to);
						invalidate();// calls onDraw
					}
				}
			}
			break;
		default:
			return false;
		}//end switch

		return true;
	}
	
	void ActivateNode(GraphEdge curEdge)//this is only called on a deactive node. It activates the .to and checks .from for overload
	{		
		nodes.get(curEdge.to).nodeState = NodeState.Active;
		numActiveNodes++;
		
		//play sound:
	//	mp_on.start();
		
		curEdge.active = true;
		//acitvate reverse edge
		for (GraphEdge littleEdge : edges.get((curEdge.to)))
		{
			if (littleEdge.to == curEdge.from)
			{ littleEdge.active = true; }
		}
		
		//current might have just overloaded, so we need to check for that. 
		if(currentTouched.nodeState == NodeState.Active)//it might have been an overloaded node. 
		{
			int numActiveNeighbors = 0;
			for (GraphEdge neighborEdge : edges.get(curEdge.from))
			{
				if (neighborEdge.active) { numActiveNeighbors++; }
			}
			if(numActiveNeighbors > 2)
			{
//				mp_op.start();
				nodes.get(curEdge.from).nodeState = NodeState.Overloaded;
				numActiveNodes--;
			}		
		}
		
		
	}
	
	void DeactivateNode()//deactivates .from and its edges. then checks all .from neighbors for overload update
	{

		if(currentTouched.nodeState == NodeState.Active)//dont diminish if it was overloaded
		{
			numActiveNodes--;
		}
		
		currentTouched.nodeState = NodeState.Deactive;
		
		// deactive all edges
		for (GraphEdge neighborEdge : edges.get(currentTouched.index))
		{
			if(neighborEdge.active == true)//no need to process if its already deactive
			{
				neighborEdge.active = false;			
				for (GraphEdge littleEdge : edges.get((neighborEdge.to)))
				{
					if (littleEdge.to == neighborEdge.from)
					{ littleEdge.active = false; }
				}
				
				CheckOverloads(neighborEdge.to);
			}
		}		
	}
	
	void RunClear()
	{
		//on up we need to clear out the points list, turn off clear mode.
		//also need to loop through points first and figure out nodes within that space... >_> 
		//for every node, we will go through the path and check if there is a point to each of the 4 sides of it. 
		
		//check every edge. (store current point and next point) and check that the edge extends from bottom to top, then from left to right
		for(GraphNode curNode : nodes)
		{
			
			if(curNode.nodeState != NodeState.Deactive)//dont bother if its already deactive 
			{				 
				//4 types of line:
				boolean crossesTop = false, crossesBottom = false, crossesRight = false, crossesLeft = false;
				//some lines can set 2 of those to true. for example a diagonal from bottom left to top right				
				
				for(int i = 0; i < points.size() - 1; i++)
				{
					//at least one of the points must be on top.				
					if(points.get(i).y > curNode.position.y || points.get(i+1).y > curNode.position.y)
					{ 
						//one of them has to be to the left, the other to the right. 
						if( (points.get(i).x < curNode.position.x && points.get(i+1).x > curNode.position.x) ||
								(points.get(i).x > curNode.position.x && points.get(i+1).x < curNode.position.x))
						{
							crossesTop = true;
						}
					}
					
					//at least one of the points must be to the right.	
					if(points.get(i).x > curNode.position.x || points.get(i+1).x > curNode.position.x)
					{
						//one of them has to be above, the other below. 
						if( (points.get(i).y < curNode.position.y && points.get(i+1).y > curNode.position.y) ||
								(points.get(i).y > curNode.position.y && points.get(i+1).y < curNode.position.y))
						{
							crossesRight = true;
						}					
					}
					
					//at least one of the points must be to the bottom.	
					if(points.get(i).y < curNode.position.y || points.get(i+1).y < curNode.position.y)
					{
						//one of them has to be to the left, the other to the right. 
						if( (points.get(i).x < curNode.position.x && points.get(i+1).x > curNode.position.x) ||
								(points.get(i).x > curNode.position.x && points.get(i+1).x < curNode.position.x))
						{
							crossesBottom = true;
						}
					}
					
					//at least one of the points must be to the left.	
					if(points.get(i).x < curNode.position.x || points.get(i+1).x < curNode.position.x)
					{
						//one of them has to be above, the other below. 
						if( (points.get(i).y < curNode.position.y && points.get(i+1).y > curNode.position.y) ||
								(points.get(i).y > curNode.position.y && points.get(i+1).y < curNode.position.y))
						{
							crossesLeft = true;
						}					
					}
					
				}
				
				if(crossesTop == true && crossesBottom == true && crossesLeft == true && crossesRight == true)
				{
					currentTouched = curNode;	
					DeactivateNode();//deactivates currentTouched
				}		
			
			 }//end if(curNode.nodeState != NodeState.Deactive)
			
			/*
			int i, j=points.size()-1 ;
			boolean  oddNodes = false;

			for (i=0; i<points.size(); i++) 
			{
				if (points.get(i).y< curNode.position.y && points.get(j).y>=curNode.position.y ||  
				    		points.get(j).y<curNode.position.y && points.get(i).y>=curNode.position.y) 
				{
					if (points.get(i).x+(curNode.position.y-points.get(i).y)/(points.get(j).y-points.get(i).y)*(points.get(j).x-points.get(i).x)<curNode.position.x) 
					{
						oddNodes=!oddNodes; 
					}
				}
				    
				j=i; 
			}
        Log.d("oddNodes", "" + oddNodes);*/
		      
		}//end for(GraphNode curNode : nodes)						
		
		points.clear();
		clearMode = false;
		b_clear.setBackgroundResource(R.drawable.b_clear);
		
	}//end RunClear
	
	void CheckOverloads(int nodeIndex)//count all edges and determine if an overload state has changed
	{
		int numActiveEdges = 0;
		for(GraphEdge neighborEdge : edges.get(nodeIndex))
		{
			if(neighborEdge.active) numActiveEdges++;				
		}
		
		Log.d("CheckOverloads()", "activeEdges: " + numActiveEdges);
		
		if(nodes.get(nodeIndex).nodeState == NodeState.Overloaded)
		{
			if (numActiveEdges < 3)//stopped being overloaded
			{
//  			mp_on.start();
				nodes.get(nodeIndex).nodeState = NodeState.Active;
				numActiveNodes++;
			}
		}
		else if (nodes.get(nodeIndex).nodeState == NodeState.Active)
		{
			if(numActiveEdges < 1)
			{
				nodes.get(nodeIndex).nodeState = NodeState.Deactive;
				numActiveNodes--;
			}
			else if (numActiveEdges > 2)
			{
	//			mp_op.start();
				nodes.get(nodeIndex).nodeState = NodeState.Overloaded;
				numActiveNodes--;				
			}
		}
	}

	void EvaluateSituation(GraphEdge curEdge)//checks different states of currentTouched and next node to determine action
	{
		switch(currentTouched.nodeState)
		{
		case  Active:
			if (nodes.get(curEdge.to).nodeState == NodeState.Deactive)// A-D
			{
				ActivateNode(curEdge);//activates .to and the edge between			
				
			}
			else  //A-A and A-O
			{
				if(curEdge.active)
				{
					DeactivateNode();//deactivates currentTouched					
				}
				else//just activate the edge between them
				{
					curEdge.active = true;
					for (GraphEdge littleEdge : edges.get((curEdge.to)))
					{
						if (littleEdge.to == curEdge.from)
						{ littleEdge.active = true; }
					}
				}
				
				//need to check .from and .to for overloads
				CheckOverloads(curEdge.from);
				if (nodes.get(curEdge.to).nodeState == NodeState.Active)//A-A
				{
					CheckOverloads(curEdge.to);
					Log.d("A-A", "Checking overloads on node: " + curEdge.to);
				}
			}
			break;
			
		case Deactive://always activate it
			currentTouched.nodeState = NodeState.Active;
			numActiveNodes++;
			
			if (nodes.get(curEdge.to).nodeState == NodeState.Deactive)//D-D
			{					
				ActivateNode(curEdge);//this will activate the .to and the edge between
			}
			else //D-A and D-O
			{				
				//activate edges between them
				curEdge.active = true;
				for (GraphEdge littleEdge : edges.get((curEdge.to)))
				{
					if (littleEdge.to == curEdge.from)
					{ littleEdge.active = true; }
				}
				
				if (nodes.get(curEdge.to).nodeState == NodeState.Active)//D-A
				{
					CheckOverloads(curEdge.to);
					Log.d("D-A", "Checking overloads");
				}
			}
			
			break;
			
		case Overloaded://will probably combine overloaded and active with an extra check inside. 
			if (nodes.get(curEdge.to).nodeState == NodeState.Deactive)//O-D
			{				
				ActivateNode(curEdge);//activates .to and the edge between		
			}
			else if (nodes.get(curEdge.to).nodeState == NodeState.Active)//O-A
			{
				if(curEdge.active)
				{
					DeactivateNode();//deactivates currentTouched					
				}
				else//just activate the edge between them
				{
					curEdge.active = true;
					for (GraphEdge littleEdge : edges.get((curEdge.to)))
					{
						if (littleEdge.to == curEdge.from)
						{ littleEdge.active = true; }
					}
					//A might have overloaded.
					CheckOverloads(curEdge.to);
				}
				
				
			}//TODO ^ these 2 can be combined. and overloaded can be integerated into active case
			else //must be swiping to an overloaded node //O-O
			{
				if(curEdge.active)
				{
					DeactivateNode();//deactivates currentTouched					
				}
				else//just activate the edge between them
				{
					curEdge.active = true;
					for (GraphEdge littleEdge : edges.get((curEdge.to)))
					{
						if (littleEdge.to == curEdge.from)
						{ littleEdge.active = true; }
					}
				}
			}
			break;
			
			default:
				break;
		
		}		
		
		//after the switch, check for gamewon and update draw
		if(numActiveNodes == nodes.size())//we've activated all nodes... any that had a 3rd edge would be overpowered rather than active
		{
			//active edges for each node should be exactly 2
			int numActiveEdges = 0;
			for(ArrayList<GraphEdge> edgeList : edges)
			for(GraphEdge  theEdge: edgeList)
			{
				if(theEdge.active) numActiveEdges++;				
			}
			
			//active edges should be twice as many as there are nodes
			if(numActiveEdges == nodes.size()*2)
			{			
				gameWon = true;
				//mp_fin.start();
				//long elapsedMillis = SystemClock.elapsedRealtime() - timer.getBase();//this is time value to use for besttime. 
				timer.stop();
				
				//set pref? check if they beat their record
				int finishTime = (int)((SystemClock.elapsedRealtime() - timer.getBase())/1000);//in seconds
				int currentSavedTime = prefs.getInt("bestTime_"+PuzzleSelect.puzzleIndex, -1);//i believe this will return a specified default (-1) if
				if(currentSavedTime == -1 || currentSavedTime > finishTime)//if it hasn't been set or is a worse time
				{					
					editor.putInt("bestTime_"+PuzzleSelect.puzzleIndex, finishTime);
					editor.commit();
					//need to also send data
					try
					{
						SendToServer();
					} catch (Exception e)
					{e.printStackTrace();}
				}
				
				//((TextView)((Activity) getContext()).findViewById(R.id.tv_score)).setText("Best Time:"+prefs.getInt("bestTime_"+PuzzleSelect.puzzleIndex, -1));
				((LinearLayout) ((Activity) getContext()).findViewById(R.id.l_right)).setBackgroundResource(R.drawable.bg_gamelit);
			}
		}		
		
		invalidate();// calls onDraw
	}//end EvaluateSituation()


	void SendToServer() throws Exception
	{		
		//at this function call, it is assumed that the bestTime preference is in fact set. 
		try
		{			
			HttpGet request = new HttpGet("http://cs.umw.edu/~finlayson/powerpath/add-score.php/?game_index=" + PuzzleSelect.puzzleIndex +
					"&seconds="+prefs.getInt("bestTime_"+PuzzleSelect.puzzleIndex, -1)+"&player_name="+prefs.getString("playerName", "Player"));
			HttpClient client = new DefaultHttpClient();
			client.execute(request);
		//	Log.d("sending seconds:" , ""+prefs.getInt("bestTime_"+PuzzleSelect.puzzleIndex, -1));
		}catch(Exception e)
		{}
	}
	      



}// end GameVew.java


