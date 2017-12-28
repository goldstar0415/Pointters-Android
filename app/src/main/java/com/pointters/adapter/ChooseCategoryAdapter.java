package com.pointters.adapter;

/**
 * Created by vikas on 9/25/2017.
 */


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.model.CategoryModel;

import java.util.HashMap;
import java.util.List;

public class ChooseCategoryAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<CategoryModel>> _listDataChild;
    private int seletedGroupPosition, seletedChildPosition;

    public ChooseCategoryAdapter(Context _context, List<String> _listDataHeader, HashMap<String, List<CategoryModel>> _listDataChild, int seletedGroupPosition, int seletedChildPosition) {
        this._context = _context;
        this._listDataHeader = _listDataHeader;
        this._listDataChild = _listDataChild;
        this.seletedGroupPosition = seletedGroupPosition;
        this.seletedChildPosition = seletedChildPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon).getName();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        Log.d("getChildView", "getChildView: " + parent.isSelected());
        TextView txtListChild = (TextView) convertView.findViewById(R.id.mKitchen_Repair);
        View selectedView = (View) convertView.findViewById(R.id.view_seleted);

        if (seletedGroupPosition == groupPosition && seletedChildPosition == childPosition)
            selectedView.setBackground(ContextCompat.getDrawable(_context,R.color.colorAccent));
        else
            selectedView.setBackground(ContextCompat.getDrawable(_context,R.color.disabled_btn_color));
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();


    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.mHomeService);
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