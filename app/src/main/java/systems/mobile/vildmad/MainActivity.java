package systems.mobile.vildmad;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import systems.mobile.vildmad.find_fragment.FindFragment;
import systems.mobile.vildmad.login.LoginActivity;
import systems.mobile.vildmad.login.SignupActivity;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener{

    private TextView mTextMessage;
    Fragment fragment = null;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = HomeFragment.newInstance("hi", "hi");
                    replaceFragment(fragment);
                    return true;
                case R.id.navigation_map:
                    fragment = MapFragment.newInstance("hi", "hi");
                    replaceFragment(fragment);
                    return true;
                case R.id.navigation_landscape:
                    mTextMessage.setText(R.string.title_landscape);
                    return true;
                case R.id.navigation_find:
                    fragment = FindFragment.newInstance("", "");
                    replaceFragment(fragment);
                    return true;
                case R.id.navigation_more:
                    mTextMessage.setText(R.string.title_more);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragment = new HomeFragment();
        replaceFragment(fragment);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        startActivity(new Intent(MainActivity.this, LoginActivity.class));

}
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void returnToFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
