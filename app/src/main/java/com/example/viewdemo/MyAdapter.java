package com.example.viewdemo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyAdapter extends BaseAdapter {
	private Context context;
	private List<String>list ;
	

	public MyAdapter(Context context,List<String>list) {
		super();
		// TODO Auto-generated constructor stub
		this.context =context;
		this.list=list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		  convertView = LayoutInflater.from(context).inflate(
                  R.layout.item_mainproduct, null);
		
		
		return convertView;
	}

}
