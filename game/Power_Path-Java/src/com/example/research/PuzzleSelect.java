package com.example.research;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class PuzzleSelect extends Activity
{
	public static int puzzleIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.puzzle_select);
		
		((ImageButton)findViewById(R.id.b_puzzle1)).setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{		
				puzzleIndex = 1;
				startActivity(new Intent("android.intent.action.PLAYGAME"));				
			}
		});
		
		((ImageButton)findViewById(R.id.b_puzzle2)).setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{		
				puzzleIndex = 2;
				startActivity(new Intent("android.intent.action.PLAYGAME"));				
			}
		});
		
		((ImageButton)findViewById(R.id.b_puzzle3)).setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{		
				puzzleIndex = 3;
				startActivity(new Intent("android.intent.action.PLAYGAME"));				
			}
		});
		
		((ImageButton)findViewById(R.id.b_puzzle4)).setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{		
				puzzleIndex = 4;
				startActivity(new Intent("android.intent.action.PLAYGAME"));				
			}
		});
		
		((ImageButton)findViewById(R.id.b_puzzle5)).setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{		
				puzzleIndex = 5;
				startActivity(new Intent("android.intent.action.PLAYGAME"));				
			}
		});
		
		((ImageButton)findViewById(R.id.b_puzzle6)).setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{		
				puzzleIndex = 6;
				startActivity(new Intent("android.intent.action.PLAYGAME"));				
			}
		});
		
		
		
	}
	
}
