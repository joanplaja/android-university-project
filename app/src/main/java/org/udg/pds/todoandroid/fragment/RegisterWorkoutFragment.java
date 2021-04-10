package org.udg.pds.todoandroid.fragment;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.location.Location;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Route;
import org.udg.pds.todoandroid.entity.Workout;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterWorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterWorkoutFragment extends /*SupportMapFragment*/ Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TodoApi mTodoService;

    Context context;

    private IdObject workoutId;
    private IdObject routeId;
    private boolean pause = true;

    LinearLayout lytControl;
    LinearLayout lytStart;


    AppCompatButton btnStart;
    FloatingActionButton btnPause;
    FloatingActionButton btnPlay;
    FloatingActionButton btnSave;

    Chronometer chronometer;
    long mLastStopTime = 0;

    private static final String TAG = "FRAGMENT WORKOUT";

    private GoogleMap map;
    MapView mMapView;//Instancia del mapa de google
    private static final int DEFAULT_ZOOM = 15;                             //Zoom per defecte

    Polyline polyline1;                                                     //polygon per dibuixar
    List<LatLng> list;                                                     //llista de latLong
    int lastPointSaved = 0;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;  //Constant per comprovar si s'ha acceptat permisos ubicacio
    private boolean locationPermissionGranted;                              //Variable per controlar si s'ha acceptat permisos ubicacio

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    LocationCallback locationCallback;                                  //variable per definir la funcio callback de la ubicacio
    LocationRequest locationRequest;                                    //variable per definir parametres de crides de localitzacio


    public RegisterWorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterWorkoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterWorkoutFragment newInstance(String param1, String param2) {
        RegisterWorkoutFragment fragment = new RegisterWorkoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_register_workout, container, false);

        context = this.getContext();

        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();

        lytControl = (LinearLayout) rootView.findViewById(R.id.lytControl);
        lytStart = (LinearLayout) rootView.findViewById(R.id.lytStart);

        btnStart = (AppCompatButton) rootView.findViewById(R.id.btnStart);
        btnPause = (FloatingActionButton) rootView.findViewById(R.id.btnPause);
        btnPlay = (FloatingActionButton) rootView.findViewById(R.id.btnPlay);
        btnSave = (FloatingActionButton) rootView.findViewById(R.id.btnSave);

        chronometer = (Chronometer) rootView.findViewById(R.id.chronometer);

        Log.d(TAG, "on create view");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //Definir cada cuan es crida la ubicacio i la acuracitat
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(200);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

        list = new ArrayList<LatLng>();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Log.d("loaction enabled:", location.getLatitude() + "," + location.getLongitude());
                    LatLng ubi = new LatLng(location.getLatitude(), location.getLongitude());
                    if(!pause){
                        list.add(ubi);
                        polyline1.setPoints(list);
                    }
                }
                saveLastPoints(false);
            }
        };


        if (workoutId != null) {
            lytControl.setVisibility(View.VISIBLE);
            if (pause) {
                btnPlay.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
            } else {
                btnPause.setVisibility(View.VISIBLE);
                chronoStart();
            }
        } else lytStart.setVisibility(View.VISIBLE);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Workout workout = new Workout();
                    workout.type = "cycling";
                    Route route = new Route();
                    route.initialLatitude = lastKnownLocation.getLatitude();
                    route.initialLongitude = lastKnownLocation.getLongitude();
                    workout.route = route;

                    Call<IdObject> call = mTodoService.createWorkout(workout);
                    call.enqueue(new Callback<IdObject>() {
                        @Override
                        public void onResponse(Call<IdObject> call, Response<IdObject> response) {
                            if (response.isSuccessful()) {

                                workoutId = response.body();
                                pause = false;
                                chronoStart();

                                lytStart.setVisibility(View.GONE);
                                lytControl.setVisibility(View.VISIBLE);
                                btnPause.setVisibility(View.VISIBLE);

                                /*
                                Route route = new Route();
                                route.initialLatitude = lastKnownLocation.getLatitude();
                                route.initialLongitude = lastKnownLocation.getLongitude();

                                Call<IdObject> callRoute = mTodoService.createRoute(workoutId.id.toString(),route);
                                callRoute.enqueue(new Callback<IdObject>() {
                                    @Override
                                    public void onResponse(Call<IdObject> call, Response<IdObject> response) {

                                        routeId = response.body();
                                        System.out.println("route id:"+routeId.id.toString());


                                    }

                                    @Override
                                    public void onFailure(Call<IdObject> call, Throwable t) {
                                        Toast.makeText(context, "Error al crear la route(On response)", Toast.LENGTH_LONG).show();
                                    }
                                });
                                */
                            }
                            else Toast.makeText(context, "Error al crear workout(On response)", Toast.LENGTH_LONG).show();


                        }

                        @Override
                        public void onFailure(Call<IdObject> call, Throwable t) {
                            //IMPLEMENT ERROR
                            Toast.makeText(context, "Error al crear workout(On Failure)", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                catch (Exception ex) {
                    //IMPLEMENT ERROR
                    Toast.makeText(context, "Error al crear workout(catch)", Toast.LENGTH_LONG).show();
                    throw ex;
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronoPause();
                pause = true;
                btnPause.setVisibility(View.GONE);
                btnPlay.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronoStart();
                pause = false;
                btnPause.setVisibility(View.GONE);
                btnPlay.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLastPoints(true);
                mLastStopTime = 0;
                pause = true;
                list = new ArrayList<>();
                polyline1.remove();
                lytStart.setVisibility(View.VISIBLE);
                lytControl.setVisibility(View.GONE);
                btnPause.setVisibility(View.GONE);
                btnPlay.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
            }
        });

        return rootView;

    }

    private void saveLastPoints(boolean saveWorkout){
        if(saveWorkout | !pause)//si guardem workout no hem de mirar variable pause, pero si no guardem si hem de mirar
            if(workoutId != null && workoutId != null)
                if(lastPointSaved < list.size()){ // al principi last = 0, size per exemple 1
                    System.out.println("lasPointSaved:"+lastPointSaved+"  list size:"+list.size());
                    int totalPoints = list.size()-lastPointSaved;
                     Double [][] arrayPoints = new Double[totalPoints][2];
                     for (int i=0;i<totalPoints;i++){
                         LatLng pos = list.get(lastPointSaved+i);
                         arrayPoints[i][0] = pos.latitude;
                         arrayPoints[i][1] = pos.longitude;
                     }

                     Call<IdObject> call = mTodoService.addPoints(workoutId.id.toString(),arrayPoints);
                     call.enqueue(new Callback<IdObject>() {
                         @Override
                         public void onResponse(Call<IdObject> call, Response<IdObject> response) {
                             System.out.println("punts guardats correctament a la ruta:"+response.body());
                             lastPointSaved = totalPoints+lastPointSaved;
                         }

                         @Override
                         public void onFailure(Call<IdObject> call, Throwable t) {
                             System.out.println("Error al guardar els punts");
                         }
                     });
                }
    }

    private void chronoStart()
    {
        // on first start
        if ( mLastStopTime == 0 )
            chronometer.setBase( SystemClock.elapsedRealtime() );
            // on resume after pause
        else
        {
            long intervalOnPause = (SystemClock.elapsedRealtime() - mLastStopTime);
            chronometer.setBase( chronometer.getBase() + intervalOnPause );
        }

        chronometer.start();
    }

    private void chronoPause()
    {
        chronometer.stop();

        mLastStopTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG,"onActivityCreated");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*if(getActivity()!=null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }*/
    }



    private void updateLocationUI() {
        Log.d("tag:","updateLocationUI");
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                Log.d("loaction enabled:","yes");
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("tag:","onMapReady");
        this.map = map;
        polyline1 = map.addPolyline(new PolylineOptions().clickable(true));
        // ..

        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }

    private void getLocationPermission() {
        Log.d("tag:","getLocationPermission");
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {
        Log.d("tag:","getDeviceLocation");
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

}
