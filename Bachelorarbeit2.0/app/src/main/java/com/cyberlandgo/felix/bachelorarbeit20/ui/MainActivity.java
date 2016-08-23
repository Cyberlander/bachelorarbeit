package com.cyberlandgo.felix.bachelorarbeit20.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.cyberlandgo.felix.bachelorarbeit20.Helper.DialogHelper;
import com.cyberlandgo.felix.bachelorarbeit20.Helper.StationDistanceHelper;
import com.cyberlandgo.felix.bachelorarbeit20.R;
import com.cyberlandgo.felix.bachelorarbeit20.application.BillingSystemApplication;
import com.cyberlandgo.felix.bachelorarbeit20.application.Values;
import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.SubsectionDataSource;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Subsection;
import com.cyberlandgo.felix.bachelorarbeit20.ui.fragments.FragmentDebug;
import com.cyberlandgo.felix.bachelorarbeit20.ui.fragments.FragmentOverview;
import com.cyberlandgo.felix.bachelorarbeit20.ui.fragments.FragmentSubsections;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    FragmentOverview fragmentOverview;

    String toastOnUIThreadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialisieren des FragmentOverviews
        fragmentOverview = new FragmentOverview();



        //initialisiert das Feld monitoringActivity im Application-Objekt
        //dieses weiß dann, das die Activity gestartet wurde
        ((BillingSystemApplication) this.getApplicationContext()).setMonitoringActivity(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);



        tabLayout.setupWithViewPager(viewPager);


        updateUIonFragmentOverview();


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragmentOverview, "Übersicht");
        adapter.addFragment(new FragmentSubsections(), "Teilstrecken");
        adapter.addFragment(new FragmentDebug(), "Debug");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public void ToastOnUIThread(String message)
    {
        toastOnUIThreadMessage = message;
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                Toast.makeText(MainActivity.this, toastOnUIThreadMessage, Toast.LENGTH_LONG).show();
            }
        });
    }



    public void updateUIonFragmentOverview()
    {
        //fragmentOverview.updateUI();
    }




    public void showBluetoothGuardDialog()
    {
        Log.e("MainActivity hier", "Dialog wird gezeigt");
        DialogHelper.getBluetoothGuardDialog(this).show();
    }






    @Override
    public void onResume() {
        super.onResume();
        ((BillingSystemApplication) this.getApplicationContext()).setMonitoringActivity(this);
        //initUIElements();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BillingSystemApplication) this.getApplicationContext()).setMonitoringActivity(null);
    }
}