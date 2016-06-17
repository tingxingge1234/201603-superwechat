package cn.ucai.fulicenter.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.BaiduMapActivity;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;

/**
 * Created by Administrator on 2016/6/17 0017.
 */
public class CategoryAdapter extends BaseExpandableListAdapter{
    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;
    Context mContext;
    private LayoutInflater inflater;

    public CategoryAdapter(ArrayList<CategoryGroupBean> mGroupList, ArrayList<ArrayList<CategoryChildBean>> mChildList, Context mContext) {
        this.mGroupList = mGroupList;
        this.mChildList = mChildList;
        this.mContext = mContext;
    }

    public  void addGroup(ArrayList<CategoryGroupBean> mGroupList) {
        this.mGroupList.addAll(mGroupList);
        notifyDataSetChanged();
    }
    public  void addChild(ArrayList<ArrayList<CategoryChildBean>> mChildList) {
        this.mChildList.addAll(mChildList);
        notifyDataSetChanged();
    }
    @Override
    public int getGroupCount() {
        return mGroupList==null?0:mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList==null||mChildList.get(groupPosition)==null?0:mChildList
                .get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewGroupHolder groupHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_category, parent, false);
            groupHolder = new ViewGroupHolder();
            groupHolder.tvGroupName = (TextView) convertView.findViewById(R.id.tv_category_name);
            groupHolder.ivGroupThumb = (NetworkImageView) convertView.findViewById(R.id.niv_category);
            groupHolder.tvIndicator = (ImageView) convertView.findViewById(R.id.iv_expand);
            convertView.setTag(groupHolder);
        }else{
            groupHolder = (ViewGroupHolder) convertView.getTag();
        }
        groupHolder.tvGroupName.setText(mGroupList.get(groupPosition).getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewChildHolder itemHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_category_child, parent, false);
            itemHolder = new ViewChildHolder();
            itemHolder.ivCategoryChildThumb = (NetworkImageView) convertView.findViewById(R.id.niv_category_child);
            itemHolder.tvCategoryChildName = (TextView) convertView.findViewById(R.id.tv_category_child);
            convertView.setTag(itemHolder);
        }else{
            itemHolder = (ViewChildHolder) convertView.getTag();
        }
        itemHolder.ivCategoryChildThumb.setImageResource(mChildList.get(groupPosition).get(childPosition).getId());
        itemHolder.tvCategoryChildName.setText(mChildList.get(groupPosition).get(childPosition).getName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ViewGroupHolder {
        NetworkImageView ivGroupThumb;
        TextView tvGroupName;
        ImageView tvIndicator;
    }

    class ViewChildHolder {
        RelativeLayout layoutChild;
        NetworkImageView ivCategoryChildThumb;
        TextView tvCategoryChildName;
    }
}
