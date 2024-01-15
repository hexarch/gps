package com.example.gps;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mMapView;
    private Button copyButton;
    private TextView detailsText;
    private TextView myTextView;
    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // İzin yoksa burada izin iste
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        myTextView = findViewById(R.id.progressTitle);
        copyButton = findViewById(R.id.copyButton);
        detailsText = findViewById(R.id.detailsText);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // LocationListener oluştur
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocation(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                // Eğer konum servisi kapalıysa kullanıcıyı ayarlara yönlendir
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        // Konum güncellemelerini başlat
        startLocationUpdates();

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Konum bilgilerini al
                Location lastKnownLocation = getLastKnownLocation();

                if (lastKnownLocation != null) {
                    // Koordinatları panoya kopyala
                    String coordinates = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
                    copyToClipboard(coordinates);


                    // Kullanıcıya bildirim göster
                    Toast.makeText(MainActivity.this, "Koordinat: " + coordinates, Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Konum bilgisi kopyalandı", Toast.LENGTH_SHORT).show();
                } else {
                    // Konum bilgisi alınamıyorsa kullanıcıya uyarı göster
                    Toast.makeText(MainActivity.this, "Konum bilgisi alınamıyor. Lütfen konum servislerinizin açık olduğundan emin olun.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button viewButton = findViewById(R.id.viewButton);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGoogleMaps();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
    private Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // İzin yoksa burada izin iste
            return null;
        }

        // En son bilinen konumu al
        Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location lastKnownLocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        // GPS'ten alınan son konumu kullan
        if (lastKnownLocationGPS != null) {
            return lastKnownLocationGPS;
        } else if (lastKnownLocationNetwork != null) {
            // GPS'ten alınamıyorsa network provider'dan alınan konumu kullan
            return lastKnownLocationNetwork;
        } else {
            // Herhangi bir konum bilgisi alınamıyorsa, null değeri döndür
            return null;
        }
    }

    private void updateLocation(Location location) {
        if (location != null) {
            String coordinates = location.getLatitude() + "," + location.getLongitude();
            detailsText.setText(coordinates);
            myTextView.setText(coordinates);

        }
    }

    private void copyToClipboard(String text) {
        // ClipboardManager kullanarak metni panoya kopyala
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("coordinates", text);
        clipboardManager.setPrimaryClip(clipData);
    }

    private void openGoogleMaps() {
        Location lastKnownLocation = getLastKnownLocation();

        if (lastKnownLocation != null) {
            String coordinates = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();

            // Google Maps uygulamasını açmak için bir Intent oluştur
            Uri gmmIntentUri = Uri.parse("geo:" + coordinates + "?q=" + coordinates + "(Location)");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps"); // Sadece Google Maps uygulaması açılacak şekilde belirtiyoruz

            // Google Maps uygulaması yüklü ise başlat
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Google Maps uygulaması yoksa Play Store'a yönlendir
                Toast.makeText(this, "Google Maps uygulaması bulunamadı. Lütfen yükleyin.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                startActivity(intent);
            }
        } else {
            // Konum bilgisi alınamıyorsa kullanıcıya uyarı göster
            Toast.makeText(this, "Konum bilgisi alınamıyor. Lütfen konum servislerinizin açık olduğundan emin olun.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0 ve üstü sürümler için runtime izin kontrolü
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Konum güncellemelerini başlat
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            } else {
                // İzin verilmediyse kullanıcıdan izin iste
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        } else {
            // Android 6.0 öncesi sürümler için izin kontrolü gerekmez
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        }
    }




}
