package com.example.research;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BestTimes extends Activity
{
	int numberOfPuzzles = 6;//should coincide with number of .txt files 
	//LinearLayout l_bestTimes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.best_times);
		
		LinearLayout l_bestTimes;
		TextView tv;
		int tempScore = 0;
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
		
		for(int i = 1; i <= 6; i++)
		{
			tv = new TextView(this);
			
			tempScore = prefs.getInt("bestTime_"+i, -1);
			if(tempScore == -1) 
			{
				tv.setText("Puzzle " + i + ":     --");				
			}
			else
			{
				//convert to minute seconds
				int min = tempScore/60;
				int sec = tempScore%60;
				tv.setText("Puzzle " + i + ":     "+ min + ":" + String.format("%02d", sec));
			}
			
			l_bestTimes = ((LinearLayout)findViewById(R.id.l_bTimes));			
			tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));		
			tv.setPadding(0, 10, 0, 0);
			tv.setTextSize(20);
			tv.setTextColor(Color.WHITE);
			
			l_bestTimes.addView(tv);
			
			//create button under each: and add to layout
			l_bestTimes.addView(MakeButton(i));//this button has clickListener that sends data to site		

		}
		
		
	}
	
	private Button MakeButton(final int index)//pass puzzle index
	{
		Button tempButton = new Button(this);		
		tempButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));		
		tempButton.setTextSize(20);
		tempButton.setText("Puzzle " + index + " High Scores");
		
		tempButton.setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{					
				setContentView(R.layout.webscores);
				WebView webView = (WebView)findViewById(R.id.webview);
				webView.loadUrl("http://cs.umw.edu/~finlayson/powerpath/high-scores.php/?game_index="+index);		
			}
		});	
		
		return tempButton;
	}
	

}
