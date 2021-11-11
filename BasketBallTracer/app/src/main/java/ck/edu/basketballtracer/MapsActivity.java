package ck.edu.basketballtracer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap gMap;
    private LocationManager lm;
    final ArrayList<String> list = new ArrayList<String>();
    private String addressSelected;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.map);

        //demande d'approbation de l'utilisation de la localisation
        if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.addressSelected = "";

        // Requete pour acceder au service de localisation
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();

        // 2 - register to receive the location events before the activity becomes visible
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 10, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 4- unregister from the service when the activity becomes invisible
        lm.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Recevoir une nouvelle localisation depuis le gps
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        LatLng newPos = new LatLng(lat, lng);

        // ajout du marqueur de position et centrage de la camera
        try{
            gMap.addMarker(new MarkerOptions().position(newPos));
        } catch (Exception e){
            newPos = new LatLng(51, -0.127);
            gMap.addMarker(new MarkerOptions().position(newPos));
        }

        gMap.moveCamera(CameraUpdateFactory.newLatLng(newPos));


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);

        connexionToDB();

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent i = new Intent(MapsActivity.this, DetailsActivity.class);

                i.putExtra("title", marker.getTitle());

                startActivity(i);

                return true;
            }
        });
    }

    /**
     * Connexion à la base de données et recuperation des matchs
     */
    protected void connexionToDB(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/basketballtracer", "root", "");
                    System.out.println("Connexion MAPS reussi");
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT * from matchs");
                    while (rs.next()){
                        list.add(rs.getString(5));
                    }
                } catch (Exception e) {
                    System.out.println("ERREUR");
                }

                //lancement du thread principal
                runOnUiThread(new Runnable() {
                    public void run() {
                        for(int i=0; i<list.size(); i++){
                            LatLng point = addressToPosition(list.get(i));
                            System.out.println("Latitude longitude : "+point);
                            gMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_basket)).title(list.get(i)));
                        }
                    }
                });

            }}).start();

    }

    /**
     * Convertir les adresses en position avec une latitude et une longitude
     * @param address
     * @return latLng
     */
    protected LatLng addressToPosition(String address){
        Geocoder geoCoder = new Geocoder(this,
                Locale.getDefault());

        LatLng latLng = null;

        List<Address> geoResults = null;

        try {
            geoResults = geoCoder.getFromLocationName(address, 1);
            while (geoResults.size()==0) {
                geoResults = geoCoder.getFromLocationName(address, 1);
            }
            if (geoResults.size()>0) {
                Address addr = geoResults.get(0);
                latLng = new LatLng(addr.getLatitude(),addr.getLongitude());
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

        return latLng;
    }
    
}
