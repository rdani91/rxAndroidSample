package hu.rozsa.daniel.rxandroidsample;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import hu.rozsa.daniel.rxandroidsample.binding.BindingFragment;
import hu.rozsa.daniel.rxandroidsample.pulltorefresh.PullToRefreshFragment;
import hu.rozsa.daniel.rxandroidsample.subject.SubjectFragment;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUpSubjectInDrawerHeader();
    }

    private void setUpSubjectInDrawerHeader() {

        FakeRestPlugin.getInstance()
                .getLatestLong()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    TextView tvNavHeaderSubject = (TextView) findViewById(R.id.nav_subject);
                    tvNavHeaderSubject.setText("Subject current item: " + aLong);
                });

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragmentContainer, fragment).commit();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawer_subject) {
            loadFragment(SubjectFragment.newInstance());
        }
        if (id == R.id.drawer_pullToRefresh) {
            loadFragment(PullToRefreshFragment.newInstance());
        }
        if (id == R.id.drawer_binding) {
            loadFragment(BindingFragment.newInstance());
        }

        closeDrawer();
        return true;
    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
