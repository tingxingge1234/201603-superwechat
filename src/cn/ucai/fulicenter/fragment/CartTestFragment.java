package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.fuliCenterMainActivity;
import cn.ucai.fulicenter.adapter.CartTestAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartTestFragment extends Fragment {
    public static final String TAG= CartTestFragment.class.getName();
    fuliCenterMainActivity mContext;
    ArrayList<CartBean> mCartList;
    CartTestAdapter mAdapter;
    private int action = I.ACTION_DOWNLOAD;
    String path;

    /**
     * 下拉刷新空间
     */
    SwipeRefreshLayout msrl;
    RecyclerView mRecyclerView;
    TextView mtvHint;
    LinearLayoutManager mLinearLayoutManager;

    public CartTestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (fuliCenterMainActivity)getActivity();
        View layout = inflater.inflate(R.layout.fragment_cart, container, false);
        mCartList = new ArrayList<CartBean>();
        initView(layout);
        setListener();
        initData();
        return layout;
    }
    private void initData() {
        Log.e(TAG, "cart path=" + getPath());
//        getPath();
//        mContext.executeRequest(new GsonRequest<CartBean[]>(path, CartBean[].class,
//                responseDownloadCartListener(), mContext.errorListener()));
        ArrayList<CartBean> cartList = FuliCenterApplication.getInstance().getCartList();
        Log.e(TAG, "cartList=" + cartList);
        mCartList.clear();
        mCartList.addAll(cartList);
    }

    private Response.Listener<CartBean[]> responseDownloadCartListener() {
        return new Response.Listener<CartBean[]>() {
            @Override
            public void onResponse(CartBean[] cart) {
                if (cart != null) {
                    mAdapter.setMore(true);
                    msrl.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    //将数组转换为集合
                    ArrayList<CartBean> list = Utils.array2List(cart);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mAdapter.initContact(list);
                    } else if (action==I.ACTION_PULL_UP) {
                        mAdapter.addContact(list);
                    }
                    if (cart.length < I.PAGE_SIZE_DEFAULT) {
                        mAdapter.setMore(false);
                    }
                }
            }
        };
    }


    private String getPath() {
        try {
            path = new ApiParams()
                    .getRequestUrl(I.REQUEST_FIND_BOUTIQUES);
            Log.e(TAG, "path=" + path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
    }

    /**
     * 上拉刷新
     */
    private void setPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastItemPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition == mAdapter.getItemCount() - 1) {
                    if (mAdapter.isMore()) {
                        msrl.setRefreshing(true);
                        action = I.ACTION_PULL_UP;
                        getPath();
                        mContext.executeRequest(new GsonRequest<CartBean[]>(path, CartBean[].class,
                                responseDownloadCartListener(), mContext.errorListener()));
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                msrl.setEnabled(mLinearLayoutManager.findFirstCompletelyVisibleItemPosition()==0);
            }
        });
    }
    /**
     * 下拉刷新
     */
    private void setPullDownRefreshListener() {
        msrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mtvHint.setVisibility(View.VISIBLE);
                action = I.ACTION_PULL_DOWN;
                getPath();
                mContext.executeRequest(new GsonRequest<CartBean[]>(path, CartBean[].class,
                        responseDownloadCartListener(), mContext.errorListener()));
            }
        });
    }

    private void initView(View layout ) {
        msrl = (SwipeRefreshLayout) layout.findViewById(R.id.srl_cart);
        msrl.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mtvHint = (TextView) layout.findViewById(R.id.tvRefreshHint);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_cart);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new CartTestAdapter(mContext, mCartList);
        mRecyclerView.setAdapter(mAdapter);
    }

}
