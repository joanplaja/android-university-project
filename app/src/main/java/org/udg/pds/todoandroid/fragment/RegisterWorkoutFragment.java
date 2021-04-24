package org.udg.pds.todoandroid.fragment;

//android x libraries
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//andriod libraries
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;
import android.location.Location;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
//google libraries
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//java libraries
import java.util.ArrayList;
import java.util.List;
//udg.pds libraries
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Route;
import org.udg.pds.todoandroid.entity.Workout;
import org.udg.pds.todoandroid.rest.TodoApi;
//retrofit libraries
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterWorkoutFragment extends Fragment implements OnMapReadyCallback {

    TodoApi mTodoService;                                                   //Api singelton reference
    Context context;                                                        //context reference
    private IdObject workoutId;                                             //actual workout id reference
    private boolean pause = true;                                           //indicates if the register is paused or not
    LinearLayout lytControl;                                                //reference to LinerLayout of controlButtons ( Pause, Resume, Save)
    LinearLayout lytStart;                                                  //reference to LinerarLayout of start button
    TextView tvDistance;                                                    //reference to TextView which display distance registered
    TextView tvVelocity;                                                    //reference to TextView
    AppCompatButton btnStart;                                               //reference to start btn
    FloatingActionButton btnPause;                                          //reference to pause button
    FloatingActionButton btnPlay;                                           //reference to play button
    FloatingActionButton btnSave;                                           //reference to save button
    Chronometer chronometer;                                                //Chronometer object used for tracking the workout register
    long mLastStopTime = 0;                                                 //variable for saving the last stopTime
    private static final String TAG = "FRAGMENT WORKOUT";                   //TAG used on debug
    private GoogleMap map;                                                  //google map object
    MapView mMapView;                                                       //reference to mapView on registerworkout layout
    private static final int DEFAULT_ZOOM = 15;                             //default zoom
    Polyline polyline1;                                                     //polygon used for save lat,long points and paint the polygon on the map
    List<LatLng> list;                                                      //list for points (Latitude and Longitude)
    int lastPointSaved = 0;                                                 //variable for tracking the last point saved on the database from the list
    Double lastLat = -1.0;                                                  //variable for saving last latitude, default -1 for checkings
    Double lastLng = -1.0;                                                  //variable for saving last latitude, default -1 for checkings
    Long lastTimeStamp;                                                     //time for velocity calculations
    Double distance = 0.0;                                                  //variable for acumlate distance on a workout
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;  //constant for check if user has accepted location permissions
    private boolean locationPermissionGranted;                              //Variable per controlar si s'ha acceptat permisos ubicacio
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;                                      //variable per definir la funcio callback de la ubicacio
    LocationRequest locationRequest;                                        //variable per definir parametres de crides de localitzacio


    public RegisterWorkoutFragment() {
        // Required empty public constructor
    }

    public static RegisterWorkoutFragment newInstance(String param1, String param2) {
        RegisterWorkoutFragment fragment = new RegisterWorkoutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //We save the view beofre returning it, so we can manage it before
        View rootView = inflater.inflate(R.layout.fragment_register_workout, container, false);
        //We save the context in a variable, so we dont call getContenxt always
        context = this.getContext();
        //Get api service
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();

        //Get view components layouts,buttons,textviews...
        lytControl = (LinearLayout) rootView.findViewById(R.id.lytControl);
        lytStart = (LinearLayout) rootView.findViewById(R.id.lytStart);
        tvDistance = (TextView) rootView.findViewById(R.id.tvDistance);
        tvVelocity = (TextView) rootView.findViewById(R.id.tvVelocity);
        btnStart = (AppCompatButton) rootView.findViewById(R.id.btnStart);
        btnPause = (FloatingActionButton) rootView.findViewById(R.id.btnPause);
        btnPlay = (FloatingActionButton) rootView.findViewById(R.id.btnPlay);
        btnSave = (FloatingActionButton) rootView.findViewById(R.id.btnSave);
        chronometer = (Chronometer) rootView.findViewById(R.id.chronometer);

        //get the provider client for locations
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //Difine the interval when we requests the locations to the client and with which accuaracy
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //get the map view
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

;       //intitlize the map
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get the map async (this => we have defined the method on this class using implements..)
        mMapView.getMapAsync(this);

        list = new ArrayList<LatLng>();
        //We define the location callback implementation
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) { //if there's not result we return
                    return;
                }
                //foreach location returned
                for (Location location : locationResult.getLocations()) {

                    LatLng ubi = new LatLng(location.getLatitude(), location.getLongitude());

                    if(!pause){ //if its not paused we save the latlng on the list and we add it on the route polygon
                        list.add(ubi);
                        polyline1.setPoints(list);
                    }
                    //calculations for velocity now
                    //if there's not previous location saved we created equals the actual
                    if(lastLat == -1 && lastLng == -1){
                        lastLat = location.getLatitude();
                        lastLng = location.getLongitude();
                        lastTimeStamp = location.getTime();
                        return;
                    }

                    //calculation of the distance between the last location saved and the actual
                    Location l = new Location("");
                    l.setLatitude(lastLat);
                    l.setLongitude(lastLng);
                    double distanceInMeters = location.distanceTo(l);
                    //get the actual time
                    long timeDelta = (location.getTime() - lastTimeStamp)/1000;
                    double speed = 0;
                    if(timeDelta > 0){
                        speed = (distanceInMeters/timeDelta);
                    }
                    Log.d("Calculations","Distance: "+distanceInMeters+", TimeDelta: "+timeDelta+" seconds"+",speed: "+speed+" Accuracy: "+location.getAccuracy());
                    //if speed is less than 0.4 m/s we supose thats probably and error of preccision so we ignore it,
                    //if not that could supose some one is moving and probably its not
                    if(speed <= 0.40){
                        speed = 0.0;
                        distanceInMeters = 0.0;
                    }
                    //if not pause we add the distance to totalDistance and we show it on the layout
                    if(!pause){
                        distance += distanceInMeters;
                        tvDistance.setText(String.format("%.2f m", distance));
                    }
                    //we dont care about if its paused or not to show the velocity
                    tvVelocity.setText(String.format("%.2f m/s", speed));

                    //save the actual to the last for next interation
                    lastLat = location.getLatitude();
                    lastLng = location.getLongitude();
                    lastTimeStamp = location.getTime();
                }
                //that probably will change but for now we save each point on each time this funciton is called
                saveLastPoints(false);
            }
        };

        //if there's a workout we show the control buttons corresponding to stop,pause,resume
        //otherwise we show the start button for starting one
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

        //start button logic
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //show workout selection
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                CharSequence items[] = new CharSequence[] {"hiking", "cycling", "running","walking"};
                adb.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface d, int n) {

                        try {
                            //new workout instance
                            Workout workout = new Workout();
                            workout.type = items[n].toString();;
                            Route route = new Route();
                            route.initialLatitude = lastKnownLocation.getLatitude();
                            route.initialLongitude = lastKnownLocation.getLongitude();
                            workout.route = route;

                            //we try to create the workout if its done we start the cronometer and show the pause button
                            //otherwise we notifiy about the error
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
                adb.setPositiveButton("Select",null);
                adb.setNegativeButton("Cancel", null);
                adb.setTitle("Choose an workout type");
                adb.show();
            }
        });

        //pause button logic
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

        //play button logic
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronoStart();
                pause = false;
                btnPause.setVisibility(View.VISIBLE);
                btnPlay.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
            }
        });

        //save button logic
        //we restart all the variables to default values like there's non a workout
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLastPoints(true);
                mLastStopTime = 0;
                lastPointSaved = 0;
                lastLat = -1.0;
                lastLng = -1.0;
                distance = 0.0;
                pause = true;
                list = new ArrayList<>();
                polyline1.remove();
                tvDistance.setText(String.format("%.2f m", distance));
                lytStart.setVisibility(View.VISIBLE);
                lytControl.setVisibility(View.GONE);
                btnPause.setVisibility(View.GONE);
                btnPlay.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
            }
        });

        return rootView;

    }

    //function for sending all last latlng points not saved on the workout
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

    //function for starting the chromoeter
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
    //function for pause the chronometer
    private void chronoPause()
    {
        chronometer.stop();

        mLastStopTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    //function which depending on the user permision starts the client provider and then update map atributes
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

    //On map ready, we start all the location logic
    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("tag:","onMapReady");
        this.map = map;
        polyline1 = map.addPolyline(new PolylineOptions().clickable(true));

        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }

    //Request location permission, so that we can get the location of the device. The result of the permission request is handled by a callback, onRequestPermissionsResult.
    private void getLocationPermission() {
        Log.d("tag:","getLocationPermission");
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


    //Get the best and most recent location of the device, which may be null in rare cases when a location is not available
    private void getDeviceLocation() {
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
