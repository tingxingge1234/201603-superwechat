package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by Administrator on 2016/6/15 0015.
 */
public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context mContext;
    ArrayList<CartBean> mCartList;
    ViewGroup parent;
    static final int TYPE_ITEM = 0;
    boolean isMore;

    public CartAdapter(Context context, ArrayList<CartBean> mCartList) {
        this.mContext = context;
        this.mCartList = mCartList;
    }

    public boolean isMore() {
        return isMore;

    }

    public void setMore(boolean more) {
        isMore = more;
        notifyDataSetChanged();
    }

    public void initContact(ArrayList<CartBean> list) {
        this.mCartList.clear();
        this.mCartList.addAll(list);
        notifyDataSetChanged();
    }

    public void addContact(ArrayList<CartBean> contactList) {
        this.mCartList.addAll(contactList);
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View layout = null;
        final LayoutInflater filter = LayoutInflater.from(mContext);
        layout = filter.inflate(R.layout.item_cart, parent, false);
        holder = new CartItemViewHolder(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CartItemViewHolder holder1 = (CartItemViewHolder)holder;
        final CartBean cart = mCartList.get(position);
        holder1.mtvGoodName.setText(cart.getUserName());
        holder1.mtvPrice.setText(cart.getGoods().getShopPrice());
        holder1.mtvCount.setText(cart.getCount());
        if (cart.isChecked()) {
            holder1.mivCheckbox.setImageResource(R.drawable.checkbox_pressed);
        } else {
            holder1.mivCheckbox.setImageResource(R.drawable.checkbox_normal);
        }
        String imgUrl = cart.getGoods().getGoodsImg();
        String url = I.REQUEST_FIND_CARTS+imgUrl;
        ImageUtils.setThumb(url,holder1.mGoodAvatar);
        holder1.mivSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //减少购物车商品数量
            }
        });
        holder1.mivSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //增加购物车商品数量
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }

    @Override
    public int getItemViewType(int position) {
            return TYPE_ITEM;
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView mGoodAvatar;
        TextView mtvGoodName,mtvPrice,mtvCount;
        ImageView mivCheckbox,mivSum,mivSub;
        LinearLayout ll_cart;
        public CartItemViewHolder(View itemView) {
            super(itemView);
            mGoodAvatar = (NetworkImageView) itemView.findViewById(R.id.nivGoodAvatar);
            mtvGoodName = (TextView) itemView.findViewById(R.id.tv_cart_good_name);
            mtvCount = (TextView) itemView.findViewById(R.id.tv_cart_count);
            mtvPrice = (TextView) itemView.findViewById(R.id.tv_cart_price);
            mivCheckbox = (ImageView) itemView.findViewById(R.id.iv_checkbox);
            mivSub = (ImageView) itemView.findViewById(R.id.iv_sub);
            mivSum = (ImageView) itemView.findViewById(R.id.iv_sum);
            ll_cart = (LinearLayout) itemView.findViewById(R.id.ll_cart);

        }
    }


}
