package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.fragment.NewGoodFragment;

public class fuliCenterMainActivity extends BaseActivity {
    RadioButton mRadioNewGood,mRadioBoutique,mRadioCategory,mRadioCart,mRadioPersonalCenter;
    TextView mtvCount;
    int index;
    int currentTabIndex;
    RadioButton[] mRadios = new RadioButton[5];
    NewGoodFragment mNewGoodFragment;
    Fragment[] mFragments = new Fragment[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuli_center_main);
        mFragments = new Fragment[] { mNewGoodFragment };
        mNewGoodFragment = new NewGoodFragment();
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fl_contains, mNewGoodFragment)
//                .add(R.id.fragment_container, contactListFragment)
//                .hide(contactListFragment)
                .show(mNewGoodFragment)
                .commit();
        initView();
//        initFragment();
    }

//    private void initFragment() {
//        mNewGoodFragment = new NewGoodFragment();
//
//    }

    private void initView() {
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
                index = 3;
                break;
            case R.id.personal_center:
                index = 4;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentTabIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(R.id.fragment_container, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
            setRadioChecked(index);
            currentTabIndex = index;
        }
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
}
