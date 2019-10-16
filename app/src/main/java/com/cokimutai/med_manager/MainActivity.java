package com.cokimutai.med_manager;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cokimutai.med_manager.data.MedDatabase;
import com.cokimutai.med_manager.data.MedEntry;
import com.cokimutai.med_manager.data.SearchMedFactory;
import com.cokimutai.med_manager.data.SearchMedViewModel;
import com.cokimutai.med_manager.sync.ReminderUtilities;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager pager;

    CurrentMedActivity currentMedication;
    ALLMedication allMedication;

    private MedDatabase db;
    private MenuItem item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pager.setCurrentItem(position, false);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setUpViewPager(pager);

        db = MedDatabase.getsInstance(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddMedication.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ReminderUtilities.scheduleSwallowReminder(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReminderUtilities.scheduleSwallowReminder(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        invalidateOptionsMenu();
        ReminderUtilities.scheduleSwallowReminder(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        item = menu.findItem(R.id.action_search);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
       // searchView.setIconifiedByDefault(false);


       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO MAIN THREAD
                //searchView.onCancelPendingInputEvents();
                //searchView.animate();
                searchView.setQuery("", true);
                searchView.setIconified(true);
                searchView.clearFocus();
                medicationSearchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Filter as you type
                //medicationSearchQuery(newText);
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        item.collapseActionView();
        return super.onPrepareOptionsMenu(menu);
    }

    private void setUpViewPager(ViewPager viewPager){
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        currentMedication = new CurrentMedActivity();
        allMedication = new ALLMedication();
        tabPagerAdapter.addFragment(currentMedication, "THIS WEEK");
        tabPagerAdapter.addFragment(allMedication, "ALL MEDICATION");
        viewPager.setAdapter(tabPagerAdapter);
    }
    private void medicationSearchQuery(String searchText) {
        SearchMedFactory factory = new SearchMedFactory(db, searchText);
        final SearchMedViewModel sModel =
                ViewModelProviders.of(this, factory).get(SearchMedViewModel.class);
        sModel.getSearchMedication().observe(this, new Observer<MedEntry>() {
                    @Override
                    public void onChanged(@Nullable MedEntry medEntry) {
                        if (medEntry == null) {
                    Toast.makeText(MainActivity.this, "Error: nothing found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int id = medEntry.getId();
                startDetailActivity(id);
            }
        });
    }
    private void startDetailActivity(int id) {
        Intent intent = new Intent(MainActivity.this, MedDetailsActivity.class);
        intent.putExtra(MedDetailsActivity.EXTRA_MED_ID, id);
        startActivity(intent);
    }
}
