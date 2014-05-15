package com.eltecalendar;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class TableFragment extends Fragment {

	public TableFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View ret = inflater.inflate(R.layout.fragment_table, container, false);
		
		GridView gridView = (GridView) ret.findViewById(R.id.gridView1);
		
		gridView.setAdapter(((MainActivity)getActivity()).getTimeTable());
		
		return ret; 
	}
}
