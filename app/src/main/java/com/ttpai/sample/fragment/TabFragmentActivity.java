package com.ttpai.sample.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ttpai.sample.R;

public class TabFragmentActivity extends AppCompatActivity {

    Fragment lastFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment fragment;

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = mMainFragment;
                    break;
//                    mTextMessage.setText(R.string.title_home);
                case R.id.navigation_dashboard:
                    fragment = mItemFragment;
                    break;
                case R.id.navigation_notifications:
                    fragment = mTabFragment;
                    break;
            }
            getSupportFragmentManager().beginTransaction()
                    .hide(lastFragment).show(fragment).commit();
            lastFragment = fragment;
            return true;
        }
    };
    private TabFragment mTabFragment;
    private MainFragment mMainFragment;
    private ItemFragment mItemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_fragment);
        BottomNavigationView navView = findViewById(R.id.nav_view);
//        mTextMessage = findViewById(R.id.frame_layout);
//        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
