package com.eltecalendar;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
 
public class MyExpandableListAdapter extends BaseExpandableListAdapter {	
    private Context _context;
    private List<Group> _groups; 
    
    public static class Group{
    	public String name;
    	public OnGroupClickListener clickHandler;
    	public ArrayList<Child> children;
    	
    	public Group(String pName){
    		name = pName;
    		
    		children = new ArrayList<Child>();
    	}
    	
    	public void addChild(String pName){
    		addChild(new Child(pName));
    	}

    	public void addChild(Child child){
    		addChild(children.size(),child);
    	}
    	
    	public void addChild(int i, Child child){
    		children.add(i,child);
    	}
    	

		public void removeChild(int i) {
			children.remove(i);
		}
		

		public void removeChild(Child child) {
			children.remove(child);
		}

		public boolean onGroupClick(ExpandableListView parent, View view,
				int groupPosition, long childPosition) {
			return false;
		}
    	
    	public static class Child {
    		String name;
        	public OnChildClickListener clickHandler;
        	
        	public Child(String pName){
        		name = pName;
        	}

			public boolean onChildClick(ExpandableListView parent, View view,
					int groupPosition, int childPosition, long id) {
				return false;
			}
        	
        	public View getView(int groupPosition, final int childPosition,
                    boolean isLastChild, View convertView, ViewGroup parent){
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) parent.getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.list_item, null);
                }
         
                TextView txtListChild = (TextView) convertView
                        .findViewById(R.id.lblListItem);
                txtListChild.setText(name);
                return convertView;
        	}
    	}
    }
 
    public MyExpandableListAdapter(Context context) {
        this._context = context;
        _groups = new ArrayList<Group>();
    }
    
    public void addGroup(Group group){
    	_groups.add(group);
    }
    
    public void attachToExpandableListView(ExpandableListView lView){
    	lView.setAdapter(this);
    	
    	lView.setOnGroupClickListener(new OnGroupClickListener(){
			@Override
			public boolean onGroupClick(ExpandableListView parent, View view,
					int groupPosition, long childPosition) {
				return _groups.get(groupPosition).onGroupClick(parent,view,groupPosition, childPosition);
			}
		});
    	
    	lView.setOnChildClickListener(new OnChildClickListener(){
			@Override
			public boolean onChildClick(ExpandableListView parent, View view,
					int groupPosition, int childPosition, long id) {
				return _groups.get(groupPosition).children.get(childPosition).onChildClick(parent, view, groupPosition, childPosition,id);
			}
    	});
    }
    
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return _groups.get(groupPosition).children.get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) { 
        return _groups.get(groupPosition).children.get(childPosition).getView(groupPosition, childPosition, isLastChild, convertView, parent);
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
    	return _groups.get(groupPosition).children.size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return _groups.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return _groups.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = ((Group) getGroup(groupPosition)).name;
        
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListItem);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
