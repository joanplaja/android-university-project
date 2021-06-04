package org.udg.pds.todoandroid.fragment;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.udg.pds.todoandroid.R;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import org.udg.pds.todoandroid.entity.NearRoutes;
import org.udg.pds.todoandroid.entity.Point;
import org.udg.pds.todoandroid.entity.Route;
import org.udg.pds.todoandroid.entity.Workout;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreRoutesFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,GoogleMap.OnInfoWindowClickListener {

    TodoApi mTodoService;

    Context context;

    private BroadcastReceiver mMessageReceiver;

    private static final String TAG = "FRAGMENT EXPLORE ROUTES";

    private GoogleMap map;
    MapView mMapView;//Instancia del mapa de google
    private static final int DEFAULT_ZOOM = 14;

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

    private List<Polyline> polylines;
    private List<Marker> markers;

    public ExploreRoutesFragment() {
    }

    public static ExploreRoutesFragment newInstance(String param1, String param2) {
        ExploreRoutesFragment fragment = new ExploreRoutesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((mMessageReceiver),
            new IntentFilter("Notification Data")
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String title = intent.getExtras().getString("title");
                String body = intent.getExtras().getString("body");

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_personalizado,
                    (ViewGroup) getActivity().findViewById(R.id.custom_toast_container));

                TextView messageToast = (TextView) layout.findViewById(R.id.text);

                String text = title + body;

                messageToast.setText(text);

                Toast toast = new Toast(getActivity());

                toast.setGravity(Gravity.TOP, 0, 0);

                toast.setDuration(Toast.LENGTH_LONG);

                toast.setView(layout);

                toast.show();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_explore_routes, container, false);

        context = this.getContext();

        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mMapView = (MapView) rootView.findViewById(R.id.mapView_exploreRoutes);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        polylines = new ArrayList<>();
        markers = new ArrayList<>();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

        return rootView;
    }

    private void updateLocationUI() {
        Log.d("tag:","updateLocationUI");
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                Log.d("loaction enabled:","yes");
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

    public void getNearRoutes(){

        Log.d("afds","getNearRoutes");

        Integer  limit = 1;

        try {
            NearRoutes nearRoutes = new NearRoutes();
            nearRoutes.latitude = lastKnownLocation.getLatitude();
            nearRoutes.longitude = lastKnownLocation.getLongitude();
            nearRoutes.limit = limit;
            Call<List<Route>> call = mTodoService.getNearRoutes(nearRoutes);
            call.enqueue(new Callback<List<Route>>() {
                @Override
                public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                    if (response.isSuccessful()) {
                        Log.d("afds",response.body().toString());
                        List<Route> routes = response.body();
                        drawRoutesOnMap(routes);
                    }
                    else Toast.makeText(context, "Error al carregar near routes(On response)", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(Call<List<Route>> call, Throwable t) {
                    Toast.makeText(context, "Error al carregar near routes(On Failure)", Toast.LENGTH_LONG).show();
                }
            });


        }
        catch (Exception e){
            throw e;
        }
    }

    public void drawRoutesOnMap(List<Route> routes){
        deleteRoutesAndMarkers();
        Log.d("afds","drawRoutes");
        for (Iterator<Route> i = routes.iterator(); i.hasNext();) {
            Route route = i.next();
            Log.d("R","route "+i+" l:"+route.points.size());
            Marker m = map.addMarker(new MarkerOptions()
                .position(new LatLng(route.initialLatitude,route.initialLongitude))
                .title(route.id.toString()).snippet("Info temps,distancia .. (click mes info)"));
            m.setTag(route.id);
            markers.add(m);
            Polyline polyline = map.addPolyline(new PolylineOptions().clickable(true));
            List<LatLng> points = new ArrayList<>();
            for (Iterator<Point> j = route.points.iterator(); j.hasNext();) {
                Log.d("P","point "+j);
                Point p = j.next();
                points.add(new LatLng(p.latitude,p.longitude));
            }
            polyline.setPoints(points);
            polylines.add(polyline);
        }
    }

    public void deleteRoutesAndMarkers(){
        for(Polyline line : polylines)
        {
            line.remove();
        }
        for(Marker marker : markers)
        {
            marker.remove();
        }

        polylines.clear();
        markers.clear();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("tag:","onMapReady");
        this.map = map;
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);


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
                            getNearRoutes();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Toast.makeText(context, "Location null cannot get near routes", Toast.LENGTH_LONG).show();

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
            Toast.makeText(context, "error getting location cannot get near routes", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Long markerId = (Long) marker.getTag();

        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        Toast.makeText(context, "Has premut al mark:"+markerId.toString(), Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Long markerId = (Long) marker.getTag();
        Toast.makeText(context, "Has premut al infoWindow:"+markerId.toString(), Toast.LENGTH_LONG).show();
    }
}
