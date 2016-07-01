package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by Administrator on 2016/6/15 0015.
 */
public class CartTestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final String TAG = CartTestAdapter.class.getName();
    Context mContext;
    ArrayList<CartBean> mCartList;
    boolean isMore;

    public CartTestAdapter(Context context, ArrayList<CartBean> mCartList) {
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
        Log.e(TAG, "cart" + cart);
        holder1.mtvGoodName.setText(cart.getGoods().getGoodsName());
        holder1.mtvCount.setText(""+cart.getCount());
        holder1.mtvPrice.setText(cart.getGoods().getShopPrice());
        ImageUtils.setNewGoodThumb(cart.getGoods().getGoodsThumb(),holder1.mGoodAvatar);
    }

    @Override
    public int getItemCount() {
        return mCartList ==null?0: mCartList.size();
    }


    class CartItemViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView mGoodAvatar;
        TextView mtvGoodName,mtvPrice,mtvCount;
        ImageView mivSum,mivSub;
        CheckBox mivCheckbox;
        LinearLayout ll_cart;
        public CartItemViewHolder(View itemView) {
            super(itemView);
            mGoodAvatar = (NetworkImageView) itemView.findViewById(R.id.nivGoodAvatar);
            mtvGoodName = (TextView) itemView.findViewById(R.id.tv_cart_good_name);
            mtvCount = (TextView) itemView.findViewById(R.id.tv_good_count);
            mtvPrice = (TextView) itemView.findViewById(R.id.tv_cart_price);
            mivCheckbox = (CheckBox) itemView.findViewById(R.id.iv_checkbox);
            mivSub = (ImageView) itemView.findViewById(R.id.iv_sub);
            mivSum = (ImageView) itemView.findViewById(R.id.iv_sum);
            ll_cart = (LinearLayout) itemView.findViewById(R.id.ll_cart);

        }
    }


}
