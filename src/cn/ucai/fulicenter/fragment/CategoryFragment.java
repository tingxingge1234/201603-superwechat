package cn.ucai.fulicenter.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.volley.Response;
import com.squareup.okhttp.internal.Util;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.fuliCenterMainActivity;
import cn.ucai.fulicenter.adapter.CategoryAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.data.RequestManager;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {
    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;
    fuliCenterMainActivity mContext;
    String path ;
    String ChildPath ;
    ExpandableListView melv;
    CategoryAdapter mAdapter;
//    LinearLayoutManager mLayoutManager;
    public CategoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_category, container, false);
        mContext = (fuliCenterMainActivity)getActivity();

        initView(layout);
        initData();
        setListener();
        return layout;
    }

    private void setListener() {
        melv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                getChildPath();
//                mContext.executeRequest(new GsonRequest<CategoryChildBean[]>(path, CategoryChildBean[].class,
//                        responseDownloadCategoryChildListener(), mContext.errorListener()));
                return true;
            }
        });
    }

    private Response.Listener<CategoryChildBean[]> responseDownloadCategoryChildListener() {
        return new Response.Listener<CategoryChildBean[]>() {
            @Override
            public void onResponse(CategoryChildBean[] categoryChildBeen) {
                Log.e("error", "categorychild" + categoryChildBeen);
            }
        };
    }

    private void initData() {
        getPath();
        Log.e("error", "grouppath" + path);
        mContext.executeRequest(new GsonRequest<CategoryGroupBean[]>(path,CategoryGroupBean[].class,
                responseDownloadCategoryGroupListener(),mContext.errorListener()));

    }

    private Response.Listener<CategoryGroupBean[]> responseDownloadCategoryGroupListener() {
        return new Response.Listener<CategoryGroupBean[]>() {
            @Override
            public void onResponse(CategoryGroupBean[] categoryGroupBeen) {
                if (categoryGroupBeen != null) {
                    mGroupList = Utils.array2List(categoryGroupBeen);
                    for (int i = 0; i < mGroupList.size(); i++) {
                        int parentId = mGroupList.get(i).getId();
                        getChildPath(parentId);
                        Log.e("error", "childpath" + ChildPath);
                        mContext.executeRequest(new GsonRequest<CategoryChildBean[]>(path, CategoryChildBean[].class,
                                responseDownloadCategoryChildListener(), mContext.errorListener()));
                    }
                }
                Log.e("error", "categorygroup" + categoryGroupBeen);
            }
        };
    }


    private void initView(View layout) {
        melv = (ExpandableListView) layout.findViewById(R.id.el_collect);
        mAdapter = new CategoryAdapter(mGroupList,mChildList,mContext);
        mGroupList = new ArrayList<CategoryGroupBean>();
        melv.setAdapter(mAdapter);
//        mLayoutManager = new LinearLayoutManager(mContext);
//        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    }

    public String getPath() {
        try {
            path = new ApiParams()
                    .getRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public String getChildPath(int parentId) {
        try {
            path = new ApiParams()
                    .with(I.PAGE_ID,parentId+"")
                    .with(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                    .getRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ChildPath;
    }
}
