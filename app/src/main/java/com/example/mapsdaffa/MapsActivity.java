package com.example.mapsdaffa;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.invoke.VolatileCallSite;
import java.util.ArrayList;
import java.util.Locale;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //private static final int REQUEST_LOCATION_PERMISSION = 1;

    private static final float ZOOM_MAP= 13;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private ArrayList<LatLng> latLngs = new ArrayList<>();
    private MarkerOptions marker = new MarkerOptions();
    public Context context;
    private JSONArray result;
    private static final String URL = "https://daffajulistyo11.000webhostapp.com/addmarker.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        latLngs.add(new LatLng(-0.9240392, 100.362468));
        latLngs.add(new LatLng(-0.9286704, 100.349956));
        latLngs.add(new LatLng(-0.9440204, 100.354481));
        latLngs.add(new LatLng(-0.9483817, 100.360866));
        latLngs.add(new LatLng(-0.9430870, 100.368504));

        mMap.addMarker(new MarkerOptions().position(latLngs.get(0)).title("Mesjid Raya Sumbar")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker)));
        mMap.addMarker(new MarkerOptions().position(latLngs.get(1)).title("Pantai Padang")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        mMap.addMarker(new MarkerOptions().position(latLngs.get(2)).title("Gramedia")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.addMarker(new MarkerOptions().position(latLngs.get(3)).title("Pasar Raya Padang")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        mMap.addMarker(new MarkerOptions().position(latLngs.get(4)).title("RSUP Dr. M. Djamil Padang")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

        LatLng basko = new LatLng(-0.901733, 100.350783);
        mMap.addMarker(new MarkerOptions().position(basko).title("Basko Grand Mall"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(basko,ZOOM_MAP));

        enableMapStyles(mMap);
        enableLongClick(mMap);
        enableDynamicMarker();

    }
    private void enableDynamicMarker(){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("JSONResult", response.toString());
                        JSONObject jobj = null;
                        try {
                            jobj = new JSONObject(response);
                            result = jobj.getJSONArray("LOCATION");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject jsonObject1 = result.getJSONObject(i);
                                String latPoint = jsonObject1.getString("1");
                                String longPoint = jsonObject1.getString("2");
                                String locationName = jsonObject1.getString("3");
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(latPoint),
                                                Double.parseDouble(longPoint)))
                                        .title(Double.valueOf(latPoint).toString()
                                                + "," + Double.valueOf(longPoint).toString())
                                        .icon(BitmapDescriptorFactory.defaultMarker
                                                (BitmapDescriptorFactory.HUE_YELLOW))
                                        .snippet(locationName));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom
                                        (new LatLng(-0.9021187, 100.348904), 13.0f));
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MapsActivity.this, error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }});
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }





    private void enableLongClick(GoogleMap mMap) {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String snippet = String.format(Locale.getDefault(),
                        getString(R.string.lat_long_snippet),
                        latLng.latitude,
                        latLng.longitude);
                mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.dropped_pin))
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker
                        (BitmapDescriptorFactory.HUE_YELLOW)));
            }
        });
    }

    private void enableMapStyles(GoogleMap mMap) {
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.maps_style));
            if (!success) {
                Log.e(TAG, "Style parsing gagal.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Tidak dapat menemukan style. Error: ", e);
        }
    }



    }



