package cs5551.smiles;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import cs5551.smiles.places.GooglePlacesReadTask;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

//public class ProviderSearchActivity extends FragmentActivity implements OnMapReadyCallback {
public class ProviderSearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.providerSearchToolbar);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Setup
        LocationManager userCurrentLocation = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        LocationListener userCurrentLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // Shouldn't need this, because we could never have gottento this activity
        // unless we had permission (unless the user said to the prompt possibly?)
        if (checkSelfPermission(ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            //show message or ask permissions from the user.
            return;
        }

        LatLng userCurrentLocationCorodinates = null;
        double latitude = 0, longitude = 0;

        //Getting the current location of the user.
        userCurrentLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, userCurrentLocationListener);
        latitude = userCurrentLocation
                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                .getLatitude();
        longitude = userCurrentLocation
                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                .getLongitude();
        userCurrentLocationCorodinates = new LatLng(latitude,longitude);

        // used in the map marker
        StringBuilder userAddress = new StringBuilder();

        // Set current
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = addresses.get(0);
            userAddress =  new StringBuilder();
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                userAddress.append(address.getAddressLine(i)).append("\t");
            }
            userAddress.append(address.getCountryName()).append("\t");

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        // Add Map Marker
        mMap.addMarker(new MarkerOptions().position(userCurrentLocationCorodinates)
                .title("Your current address.").snippet(userAddress.toString()));

        // Move camera and zoom level of the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocationCorodinates, 15));

        // WAYNE
        String type = "Orthodontics";
        // Places Search
//        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        googlePlacesUrl.append("location=" + latitude + "," + longitude);
//        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
//        googlePlacesUrl.append("&radius=" + 5000);
//        googlePlacesUrl.append("&types=" + type);
//        googlePlacesUrl.append("&sensor=true");
//        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
//        googlePlacesUrl.append("&key=" + "AIzaSyCGeqWbwxzG6zHv2Nxgg3w4RJKisDepayo");
        // Text Search
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
        googlePlacesUrl.append("query=" + type);
        googlePlacesUrl.append("&location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 5000);
        googlePlacesUrl.append("&key=" + "AIzaSyCGeqWbwxzG6zHv2Nxgg3w4RJKisDepayo");

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[2];
        toPass[0] = mMap;
        toPass[1] = googlePlacesUrl.toString();
        googlePlacesReadTask.execute(toPass);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.provider_search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pics:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_profile:
                // direct them to profile
                startActivity( new Intent(getApplicationContext(), ProfileActivity.class) );
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
