package systems.mobile.vildmad;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.MapFragment;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Fragment fragment = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_map:
                    //mTextMessage.setText(R.string.title_map);
                    fragment = new MapFragment();
                    startFragment(fragment);
                    return true;
                case R.id.navigation_landscape:
                    mTextMessage.setText(R.string.title_landscape);
                    return true;
                case R.id.navigation_pic:
                    mTextMessage.setText(R.string.title_pic);
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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void startFragment(Fragment startFragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.container, startFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void replaceFragment(Fragment newFragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
