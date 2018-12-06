package systems.mobile.vildmad;

import android.Manifest;
import android.provider.Settings;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private Button mAddMarkerButton;
    private Button mAddPictureButton;
    private Button mSettingsButton;
    private Spinner mTypeSpinner;
    private Spinner mKindSpinner;
    CheckBox mCheckBox;
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    Button button;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    BroadcastReceiver br;
    FusedLocationProviderClient mFusedLocationClient;
    EditText mEditTextNote;
    private long lastTouchTime = -1;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            int scale = intent.getIntExtra("scale", 100);
            int batpercentage = level * 100 / scale;
            if (batpercentage >= 50) {
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(5000);
            } else if (batpercentage < 50 && batpercentage > 15) {
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                mLocationRequest.setInterval(100000);
                mLocationRequest.setFastestInterval(10000);
            } else if (batpercentage <= 15) {
                mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                mLocationRequest.setInterval(3600000);
                mLocationRequest.setFastestInterval(60000);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mapFrag = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        mAddMarkerButton = (Button) mView.findViewById(R.id.addMarkerButton);
        mSettingsButton = (Button) mView.findViewById(R.id.settingsButton);

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mLocationRequest = new LocationRequest();

        getActivity().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }

        //For adding a new marker on the current position
        mAddMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarkerOnClick();
            }
        });
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsOnClick();
            }
        });

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                return false;

            }
        });


        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View markerSelectedLayout = inflater.inflate(R.layout.marker_selected_layout, null);
                new AlertDialog.Builder(getContext()).setTitle("Marker")
                        .setCancelable(false)
                        .setView(markerSelectedLayout)
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //do nothing
                                        dialog.dismiss();
                                    }
                                }
                        ).show();

                return false;
            }

        });

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));

            }
        }
    };
    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public void addMarkerOnCurrentPosition(boolean bln, String description, String title) {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListenerGps, null);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        MarkerOptions marker = new MarkerOptions();
        marker.position(new LatLng(location.getLatitude(), location.getLongitude())).title(title);
        CustomMarker cm = new CustomMarker(marker,bln,description,"","");

        mGoogleMap.addMarker(cm.getMarker());

    }

    public void addMarkerOnClick(){

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View addMarkerLayout = inflater.inflate(R.layout.add_marker_layout, null);
        mCheckBox = (CheckBox) addMarkerLayout.findViewById(R.id.mPublicCheckBox);
        mEditTextNote = (EditText) addMarkerLayout.findViewById(R.id.mEditTextNote);
        mTypeSpinner = (Spinner) addMarkerLayout.findViewById(R.id.spinner_type);
        mKindSpinner = (Spinner) addMarkerLayout.findViewById(R.id.spinner_kind);
        mAddPictureButton = (Button) addMarkerLayout.findViewById(R.id.addPictureButton);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerValue = mTypeSpinner.getSelectedItem().toString();
                switch (spinnerValue){
                    case "Svampe":
                        ArrayAdapter<CharSequence> svampeadapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.svampe, android.R.layout.simple_spinner_dropdown_item);
                        mKindSpinner.setAdapter(svampeadapter);
                        break;
                    case "Frugter":
                        ArrayAdapter<CharSequence> frugtadapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.frugter, android.R.layout.simple_spinner_dropdown_item);
                        mKindSpinner.setAdapter(frugtadapter);
                        break;
                    case "Krydderurter":
                        ArrayAdapter<CharSequence> krydderadapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.krydderurter, android.R.layout.simple_spinner_dropdown_item);
                        mKindSpinner.setAdapter(krydderadapter);
                        break;
                    case "Bær":
                        ArrayAdapter<CharSequence> baeradapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.baer, android.R.layout.simple_spinner_dropdown_item);
                        mKindSpinner.setAdapter(baeradapter);
                        break;
                    case "Nødder":
                        ArrayAdapter<CharSequence> noeddeadapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.nodder, android.R.layout.simple_spinner_dropdown_item);
                        mKindSpinner.setAdapter(noeddeadapter);
                        break;

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        new AlertDialog.Builder(getContext()).setTitle("Tilføj et punkt")
                .setCancelable(false)
                .setView(addMarkerLayout)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                boolean bool;
                                // add marker with LatLng geo
                                if(mCheckBox.isChecked())
                                    bool = true;
                                else
                                    bool = false;
                                addMarkerOnCurrentPosition(bool,mEditTextNote.getText().toString(), "");
                            }
                        }
                )
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                //do nothing
                                dialog.dismiss();
                            }
                        }
                ).show();

        mAddPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = AddPicture.newInstance("hi", "hi");
                ((MainActivity) getActivity()).replaceFragment(fragment);
            }
        });
    }
    public void settingsOnClick() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View settingsLayout = inflater.inflate(R.layout.marker_settings_layout, null);
        new AlertDialog.Builder(getContext()).setTitle("Confirm")
                .setMessage("Do you want to add Marker?")
                .setCancelable(false)
                .setView(settingsLayout)
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                //do nothing
                                dialog.dismiss();
                            }
                        }
                ).show();


    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}