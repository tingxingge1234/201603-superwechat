package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.Albums;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisPlayUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class GoodDetailActivity extends BaseActivity {
    public static final String TAG = GoodDetailActivity.class.getName();
    GoodDetailActivity mContext;
    GoodDetailsBean mGoodDetails;
    int mGoodsId;

    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;

    /**
     * 显示颜色的容器布局
     */
    LinearLayout mLayoutColors;
    ImageView mivCollect;
    ImageView mivAddCart;
    ImageView mivShare;
    TextView mtvCartCount;
    TextView tvGoodName;
    TextView tvGoodEngishName;
    TextView tvShopPrice;
    TextView tvCurrencyPrice;
    WebView wvGoodBrief;
    /**
     * 当前的颜色值
     **/
    int mCurrentColor;
    boolean isCollect;
    int actionCollect;

//    @Override
//    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.good_details);
        mContext = this;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setCollectClickListener();

    }

    private void setCollectClickListener() {
        mivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = FuliCenterApplication.getInstance().getUser();
                if (user == null) {
                    startActivity(new Intent(GoodDetailActivity.this, LoginActivity.class));
                } else {
                    try {
                        String path;
                        if (isCollect) {
                            actionCollect = I.ACTION_DEL_COLLECT;
                            path = new ApiParams()
                                    .with(I.Collect.USER_NAME, user.getMUserName())
                                    .with(I.Collect.GOODS_ID, mGoodsId + "")
                                    .getRequestUrl(I.REQUEST_DELETE_COLLECT);
                        } else {
                            actionCollect = I.ACTION_ADD_COLLECT;
                            path = new ApiParams()
                                    .with(I.Collect.USER_NAME, user.getMUserName())
                                    .with(I.Collect.GOODS_ID, mGoodsId + "")
                                    .with(I.Collect.GOODS_NAME, mGoodDetails.getGoodsName())
                                    .with(I.Collect.GOODS_ENGLISH_NAME, mGoodDetails.getGoodsEnglishName())
                                    .with(I.Collect.GOODS_IMG, mGoodDetails.getGoodsImg())
                                    .with(I.Collect.GOODS_THUMB, mGoodDetails.getGoodsThumb())
                                    .with(I.Collect.ADD_TIME, mGoodDetails.getAddTime() + "")
                                    .getRequestUrl(I.REQUEST_ADD_COLLECT);
                        }
                        executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class,
                                responseSetCollectListener(), errorListener()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


        });
    }

    private Response.Listener<MessageBean> responseSetCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean.isSuccess()) {
                    if (actionCollect == I.ACTION_ADD_COLLECT) {
                        isCollect = true;
                        mivCollect.setImageResource(R.drawable.bg_collect_out);
                    } else {
                        isCollect = false;
                        mivCollect.setImageResource(R.drawable.bg_collect_in);
                    }
                    new DownloadCollectCountTask(mContext).execute();
                }
                Utils.showToast(mContext,messageBean.getMsg(),Toast.LENGTH_SHORT);
            }
        };
    }

    private void initData() {
        mGoodsId = getIntent().getIntExtra(D.NewGood.KEY_GOODS_ID, 0);
        try {
            String path = new ApiParams()
                    .with(D.NewGood.KEY_GOODS_ID, mGoodsId + "")
                    .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
            executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class, responseDownloadGoodDetailsListener(), errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<GoodDetailsBean> responseDownloadGoodDetailsListener() {
        return new Response.Listener<GoodDetailsBean>() {
            @Override
            public void onResponse(GoodDetailsBean goodDetailsBean) {
                if (goodDetailsBean != null) {
                    mGoodDetails = goodDetailsBean;
                    DisPlayUtils.initBackwithTitle(mContext, getResources().getString(R.string.title_good_details));
                    tvCurrencyPrice.setText(mGoodDetails.getCurrencyPrice());
                    tvGoodEngishName.setText(mGoodDetails.getGoodsEnglishName());
                    tvGoodName.setText(mGoodDetails.getGoodsName());
                    wvGoodBrief.loadDataWithBaseURL(null, mGoodDetails.getGoodsBrief().trim(), D.TEXT_HTML, D.UTF_8, null);
                    //初始化颜色面板
                    initColorsBanner();
                } else {
                    Utils.showToast(mContext, "商品详情下载失败", Toast.LENGTH_LONG);
                    finish();
                }
            }
        };
    }

    private void initColorsBanner() {
        //设置第一个颜色的图片轮播
        updateColor(0);
        for (int i = 0; i < mGoodDetails.getPropertyBean().length; i++) {
            mCurrentColor = i;
            View layout = View.inflate(mContext, R.layout.layout_property_color, null);
            final NetworkImageView ivColor = (NetworkImageView) layout.findViewById(R.id.ivColorItem);
            Log.e(TAG, "initColorsBanner.goodDetails=" + mGoodDetails.getPropertyBean()[i].toString());
            String colorImg = mGoodDetails.getPropertyBean()[i].getColorImg();
            if (colorImg.isEmpty()) {
                continue;
            }
            ImageUtils.setGoodDetailThumb(colorImg, ivColor);
            mLayoutColors.addView(layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateColor(mCurrentColor);
                }
            });
        }
    }

    private void updateColor(int i) {
        Albums[] albums = mGoodDetails.getPropertyBean()[i].getAlbumBean();
        String[] albumImgUrl = new String[albums.length];
        for (int j = 0; j < albumImgUrl.length; j++) {
            albumImgUrl[j] = albums[j].getImgUrl();
        }
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator, albumImgUrl, albumImgUrl.length);
    }

    private void initView() {
        mivCollect = (ImageView) findViewById(R.id.iv_collect);
        mivAddCart = (ImageView) findViewById(R.id.iv_cart);
        mivShare = (ImageView) findViewById(R.id.iv_share);
        mtvCartCount = (TextView) findViewById(R.id.tv_cart_count);
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        mLayoutColors = (LinearLayout) findViewById(R.id.layoutColorSelector);
        tvGoodEngishName = (TextView) findViewById(R.id.tv_english_name);
        tvGoodName = (TextView) findViewById(R.id.tv_chinese_name);
        tvShopPrice = (TextView) findViewById(R.id.tv_price);
        tvCurrencyPrice = (TextView) findViewById(R.id.tv_now_price);
        wvGoodBrief = (WebView) findViewById(R.id.wv_good_brief);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initCollectStatus();
    }

    private void initCollectStatus() {
        User user = FuliCenterApplication.getInstance().getUser();
        Log.e(TAG, "user=" + user);
        if (user != null) {
            try {
                String path = new ApiParams()
                        .with(I.Collect.USER_NAME, FuliCenterApplication.getInstance().getUserName())
                        .with(I.Collect.GOODS_ID, mGoodsId + "")
                        .getRequestUrl(I.REQUEST_IS_COLLECT);
                Log.e(TAG, "path=" + path);
                executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class,
                        responseIsCollectListener(), errorListener()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            isCollect = false;
            mivCollect.setImageResource(R.drawable.bg_collect_in);
        }
    }

    private Response.Listener<MessageBean> responseIsCollectListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean.isSuccess()) {
                    isCollect = true;
                    mivCollect.setImageResource(R.drawable.bg_collect_out);
                } else {
                    isCollect = false;
                    mivCollect.setImageResource(R.drawable.bg_collect_in);
                }
            }
        };
    }
}
