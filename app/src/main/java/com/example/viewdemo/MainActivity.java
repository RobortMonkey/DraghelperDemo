package com.example.viewdemo;


import java.util.ArrayList;
import java.util.List;

import com.example.viewdemo.RefreshListView.MyOnRefreshListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.os.Handler;

public class MainActivity extends Activity implements MyOnRefreshListener {

    private RefreshListView rs_view;
    private SwipeLayoutVertical content;
    private Handler handler =new Handler();

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> list =new ArrayList<String>();
        for(int i=0;i<20;i++)
        {
        	list.add(i+" conten  ");
        }
      
//        
        	content=(SwipeLayoutVertical)findViewById(R.id.content);
//        	MyListView mylistView =new MyListView(this);
//        AbsListView.LayoutParams params =new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//        LinearLayout ll_assets_content = content.getLinearLayout();
       
//        ll_assets_content.addView(mylistView, params);
        	rs_view=(RefreshListView)findViewById(R.id.rs_view);
        	rs_view.setAdapter(new MyAdapter(this,list));
//
//
        rs_view.setMyOnRefreshListener(this);
        	content.setMark(true);
       
       

        
    }
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
//		 content.open();
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onRefreshing() {
		// TODO Auto-generated method stub
		rs_view.refreshFinished();
	}


	@Override
	public void onLoadingMore() {
		// TODO Auto-generated method stub
		rs_view.refreshFinished();
	}


	@Override
	public boolean hasMore() {
		// TODO Auto-generated method stub
		return false;
	}
    


	
    
}
