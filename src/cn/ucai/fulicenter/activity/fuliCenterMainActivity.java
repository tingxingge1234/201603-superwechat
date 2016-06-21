package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.http.LoggingEventHandler;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.CartFragment;
import cn.ucai.fulicenter.fragment.CategoryFragment;
import cn.ucai.fulicenter.fragment.NewGoodFragment;
import cn.ucai.fulicenter.fragment.PersonalCenterFragment;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisPlayUtils;

public class fuliCenterMainActivity extends BaseActivity {
    public static final String TAG = "fuliCenterMainActivity";
    RadioButton mRadioNewGood,mRadioBoutique,mRadioCategory,mRadioCart,mRadioPersonalCenter;
    TextView mtvCount;
    int index;
    int currentTabIndex;
    RadioButton[] mRadios = new RadioButton[5];
    NewGoodFragment mNewGoodFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;
    CartFragment mCartFragment;
    PersonalCenterFragment mPersonalCenterFragment;
    Fragment[] mFragments = new Fragment[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuli_center_main);
        initFragment();
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_contains, mNewGoodFragment)
                .add(R.id.fl_contains,mBoutiqueFragment).hide(mBoutiqueFragment)
//                .add(R.id.fragment_container, contactListFragment)
                .add(R.id.fl_contains,mCategoryFragment).hide(mCategoryFragment)
                .add(R.id.fl_contains,mCartFragment).hide(mCartFragment)
                .add(R.id.fl_contains,mPersonalCenterFragment).hide(mPersonalCenterFragment)
                .show(mNewGoodFragment)
                .commit();
        initView();
        registerCartReceiver();
    }

    private void initFragment() {
        mNewGoodFragment = new NewGoodFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment= new CategoryFragment();
        mCartFragment = new CartFragment();
        mPersonalCenterFragment = new PersonalCenterFragment();
        mFragments[0] = mNewGoodFragment;
        mFragments[1] = mBoutiqueFragment;
        mFragments[2] = mCategoryFragment;
        mFragments[3] = mCartFragment;
        mFragments[4] = mPersonalCenterFragment;
    }

    private void initView() {
        String update_cart_list = getIntent().getStringExtra("update_cart_list");
        mRadioNewGood = (RadioButton) findViewById(R.id.new_good);
        mRadioBoutique = (RadioButton) findViewById(R.id.boutique);
        mRadioCategory = (RadioButton) findViewById(R.id.categroy);
        mRadioCart = (RadioButton) findViewById(R.id.cart);
        mRadioPersonalCenter = (RadioButton) findViewById(R.id.personal_center);
        mtvCount = (TextView) findViewById(R.id.tvCartHint);
        mRadios[0] = mRadioNewGood;
        mRadios[1] = mRadioBoutique;
        mRadios[2] = mRadioCategory;
        mRadios[3] = mRadioCart;
        mRadios[4] = mRadioPersonalCenter;
    }
    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.new_good:
                index = 0;
                break;
            case R.id.boutique:
                index = 1;
                break;
            case R.id.categroy:
                index = 2;
                break;
            case R.id.cart:
                if (FuliCenterApplication.getInstance().getUser() != null) {
                    index = 3;
                } else {
                    gotoLogin();
                }
                break;
            case R.id.personal_center:
                if (FuliCenterApplication.getInstance().getUser() != null) {
                    index = 4;
                } else {
                    gotoLogin();
                }
                break;
        }
        if (currentTabIndex != index) {
            Log.e(TAG, "index" + index);
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentTabIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(R.id.fl_contains, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
            setRadioChecked(index);
            currentTabIndex = index;
            Log.e(TAG, "currentTabIndex=" + currentTabIndex);
        }
    }

    private void gotoLogin() {
        startActivity(new Intent(this,LoginActivity.class).putExtra("action","personal"));
    }

    private void setRadioChecked(int index) {
        for (int i = 0; i <mRadios.length ; i++) {
            if (i == index) {
                mRadios[i].setChecked(true);
            } else {
                mRadios[i].setChecked(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "currentTabIndex =" + currentTabIndex+",index="+index);
        Log.e(TAG,"user="+FuliCenterApplication.getInstance().getUser());
        String action = getIntent().getStringExtra("action");
        if (action!=null&&FuliCenterApplication.getInstance().getUser() != null) {
            if (action.equals("personal")) {
                index = 4;
            }
        } else {
            setRadioChecked(index);
        }
        if (currentTabIndex == 4 && FuliCenterApplication.getInstance().getUser() == null) {
            index = 0;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentTabIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(R.id.fl_contains, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
            setRadioChecked(index);
            currentTabIndex = index;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.e(TAG, "intent" + intent);
    }

    class UpdateCartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int count = Utils.sumCartCount();
            if (count > 0) {
                mtvCount.setVisibility(View.VISIBLE);
                mtvCount.setText("" + count);
            } else {
                mtvCount.setVisibility(View.GONE);
            }
            if (FuliCenterApplication.getInstance().getUser() == null) {
                mtvCount.setVisibility(View.GONE);
            }
        }
    }
    UpdateCartReceiver mReceiver;
    private void registerCartReceiver() {
        mReceiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("update_user");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
