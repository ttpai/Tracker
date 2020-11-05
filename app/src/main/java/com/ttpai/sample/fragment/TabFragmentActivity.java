package com.ttpai.sample.fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ttpai.sample.R;

public class TabFragmentActivity extends AppCompatActivity {

    Fragment lastFragment;

    private TabFragment mTabFragment;
    private MainFragment mMainFragment;
    private ItemFragment mItemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_fragment);

        mMainFragment = MainFragment.newInstance("main", "Tab");
        mItemFragment = ItemFragment.newInstance(10);
        mTabFragment = TabFragment.newInstance("Name", "age");

        final Fragment[] fragments=new Fragment[]{mMainFragment,mItemFragment,mTabFragment};
        ViewPager viewPager=findViewById(R.id.viewpage);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments[i];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        /*getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, mMainFragment)
                .add(R.id.frame_layout, mItemFragment)
                .add(R.id.frame_layout, mTabFragment)
                .hide(mItemFragment)
                .hide(mTabFragment)
                .commit();*/
        lastFragment = mMainFragment;

    }

}
