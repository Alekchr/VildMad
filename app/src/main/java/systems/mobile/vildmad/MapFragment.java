package systems.mobile.vildmad;

import android.Manifest;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;
import java.util.HashMap;
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
    Marker mCurrLocationMarker;
    BroadcastReceiver br;
    FusedLocationProviderClient mFusedLocationClient;
    EditText mEditTextNote;
    CheckBox mPublicCheckBox;
    private long lastTouchTime = -1;
    private DatabaseHandler db;
    private HashMap<Marker, Integer> markerHashMap = new HashMap<Marker, Integer>();
    private FirebaseAuth auth;


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
        db = new DatabaseHandler();
        auth = FirebaseAuth.getInstance();
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
    public void addAllMarkersFromDatabase() {
        for(Object marker : db.returnAllMarkers() ) {
            Double lati = ((CustomMarker) marker).getLat();
            Double longti = ((CustomMarker) marker).getLng();
            String descr = ((CustomMarker) marker).getDescription();
            //String img = ((CustomMarker) marker).getPictureUrl();
            String title = ((CustomMarker) marker).getTitle();


            try {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(longti, lati));

                CustomMarker info = new CustomMarker();
                //info.setPictureUrl(img);
                info.setDescription(descr);
                info.setTitle(title);

                Marker m = mGoogleMap.addMarker(markerOptions);
                m.setTag(info);
            }
            catch (Exception e){
                System.out.println(e);
            }
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
                checkCameraPermission();
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
                addAllMarkersFromDatabase(); // TESTING THE METHOD FOR LOADING DATABASE MARKERS
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


                marker.getId();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View markerSelectedLayout = inflater.inflate(R.layout.marker_selected_layout, null);

                Button closeMarkerDialog = markerSelectedLayout.findViewById(R.id.closeMarkerDialogBtn);
                TextView type = markerSelectedLayout.findViewById(R.id.mTypeText);
                TextView kind = markerSelectedLayout.findViewById(R.id.mKindText);
                TextView descr = markerSelectedLayout.findViewById(R.id.mNoteTextView);

                CustomMarker cm = (CustomMarker) marker.getTag();
                type.setText(cm.getTitle());
                kind.setText(cm.getType()); // CHANGE THIS TO WHAT KIND IT IS LATER
                descr.setText(cm.getDescription());

                final AlertDialog markerDialog = new AlertDialog.Builder(getContext()).setTitle("Marker")
                        .setCancelable(false)
                        .setView(markerSelectedLayout)
                        .show();
                closeMarkerDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        markerDialog.dismiss();
                    }
                });
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
                //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20)); NEJ TAK

            }
        }
    };

    public void addMarkerOnCurrentPosition(boolean bln, String description, String kind, String type) {
        {
            Double lat = mLastLocation.getLatitude();
            Double lng = mLastLocation.getLongitude();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(lat, lng));

            CustomMarker cm = new CustomMarker();
            //info.setPictureUrl(img);
            cm.setId(auth.getCurrentUser().getUid());
            cm.setDescription(description);
            cm.setType(type);
            cm.setPublic(bln);
            cm.setTitle(kind);
            cm.setLat(lng);
            cm.setLng(lat); //is swapped for Firebase purpose. Minor bug

            Marker m = mGoogleMap.addMarker(markerOptions);
            m.setTag(cm);

            db.writeNewMarker(cm);

        }
    }

        public void addMarkerOnClick () {

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
                    switch (spinnerValue) {
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
            Button addNewMarker = addMarkerLayout.findViewById(R.id.addMarkerBtn);
            Button closeAddMarker = addMarkerLayout.findViewById(R.id.closeAddMarkerBtn);

            final AlertDialog addMarkerDialog = new AlertDialog.Builder(getContext()).setTitle("Tilføj et punkt")
                    .setCancelable(false)
                    .setView(addMarkerLayout)
                    .show();
            addNewMarker.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean bool;
                    // add marker with LatLng geo
                    if (mCheckBox.isChecked())
                        bool = true;
                    else
                        bool = false;
                    addMarkerOnCurrentPosition(bool, mEditTextNote.getText().toString(), mKindSpinner.getSelectedItem().toString(), mTypeSpinner.getSelectedItem().toString());
                    addMarkerDialog.dismiss();
                }
            });
            closeAddMarker.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addMarkerDialog.dismiss();
                }
            });
            mAddPictureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddPicture ap = AddPicture.newInstance(getActivity());
                    ap.takePicture();
                    ap.addPhotoToGallery();
                }
            });
        }

        public void settingsOnClick () {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View settingsLayout = inflater.inflate(R.layout.marker_settings_layout, null);
            Button saveSettings = settingsLayout.findViewById(R.id.saveSettingsBtn);
            Button closeSettings = settingsLayout.findViewById(R.id.closeSettingsBtn);
            final AlertDialog settingsDialog = new AlertDialog.Builder(getContext()).setTitle("Indstillinger")
                    .setCancelable(false)
                    .setView(settingsLayout)
                    .show();

            saveSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    settingsDialog.dismiss(); //TO BE FIXED
                }
            });
            closeSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    settingsDialog.dismiss();
                }
            });
        }

        public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
        private void checkLocationPermission () {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Lokationer")
                            .setMessage("Denne applikation skal have adgang til enhedens lokation for at fungere. Tillad venligst dette")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_LOCATION);
                                }
                            })
                            .create()
                            .show();


                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }
        }


        private final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 0x00AF;
        private void checkCameraPermission() {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.CAMERA)) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Kamera")
                            .setMessage("Denne applikation skal have adgang til enhedens kamera for at fungere. Tillad venligst dette")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_USE_CAMERA);
                                }
                            })
                            .create()
                            .show();

                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }
        }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
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
            case MY_PERMISSIONS_REQUEST_USE_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
