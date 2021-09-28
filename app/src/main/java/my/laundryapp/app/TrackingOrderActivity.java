package my.laundryapp.app;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import my.laundryapp.app.Common.Common;
import my.laundryapp.app.Model.ShippingOrderModel;
import my.laundryapp.app.Remote.IGoogleAPI;
import my.laundryapp.app.Remote.RetrofitGoogleAPIClient;

public class TrackingOrderActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    private Marker shipperMarker;
    private Polyline yellowPolyline,grayPolyline,blackPolyline;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private List<LatLng> polylineList;
    private IGoogleAPI iGoogleAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private DatabaseReference shipperRef;

    //move marker
    private Handler handler;
    private int index, next;
    private LatLng start, end;
    private float v;
    private double lat, lng;

    private boolean isInit = false;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);

        iGoogleAPI = RetrofitGoogleAPIClient.getInstance().create(IGoogleAPI.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        subscibeShipperMove();
    }

    private void subscibeShipperMove() {
        shipperRef = FirebaseDatabase.getInstance()
                .getReference(Common.SHIPPING_ORDER_REF)
                .child(Common.currentShippingOrder.getKey());

        shipperRef.addValueEventListener(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
                    R.raw.uber_light_with_label));
            if (!success)
                Log.e("EMTDEV", "Style parsing failed");

        } catch (Resources.NotFoundException ex) {
            Log.e("EMTDEV", "Resource not found");
        }

        drawRoutes();


    }

    private void drawRoutes() {
        LatLng locationOrder = new LatLng(Common.currentShippingOrder.getOrderModel().getLat(),
                Common.currentShippingOrder.getOrderModel().getLng());
        LatLng locationShipper = new LatLng(Common.currentShippingOrder.getCurrentLat(), Common.currentShippingOrder.getCurrentLng());

        //add box
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.box))
                .title(Common.currentShippingOrder.getOrderModel().getCustUserName())
                .snippet(Common.currentShippingOrder.getOrderModel().getShippingAddress())
                .position(locationOrder));

        //add runner
        if (shipperMarker == null) {
            int height, width;
            height = width = 80;
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat
                    .getDrawable(TrackingOrderActivity.this, R.drawable.shippernew);
            Bitmap resized = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), width, height, false);

            shipperMarker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(resized))
                    .title(Common.currentShippingOrder.getRunnerName())
                    .snippet(Common.currentShippingOrder.getRunnerPhone())
                    .position(locationShipper));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper, 18));


        } else {
            shipperMarker.setPosition(locationShipper);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper, 18));

        }

        //Draw route
        String to = new StringBuilder()
                .append(Common.currentShippingOrder.getOrderModel().getLat())
                .append(",")
                .append(Common.currentShippingOrder.getOrderModel().getLng())
                .toString();
        String from = new StringBuilder()
                .append(Common.currentShippingOrder.getCurrentLat())
                .append(",")
                .append(Common.currentShippingOrder.getCurrentLng())
                .toString();

        compositeDisposable.add(iGoogleAPI.getDirections("driving",
                "less_driving",
                from, to,
                getString(R.string.google_maps_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyline = poly.getString("points");
                            polylineList = Common.decodePoly(polyline);
                        }
                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.RED);
                        polylineOptions.width(12);
                        polylineOptions.startCap(new SquareCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polylineList);
                        yellowPolyline = mMap.addPolyline(polylineOptions);

                    } catch (Exception e) {
                        Toast.makeText(TrackingOrderActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }, throwable -> Toast.makeText(TrackingOrderActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show()));

    }

    @Override
    protected void onDestroy() {
        shipperRef.removeEventListener(this);
        isInit = false;
        super.onDestroy();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        //save old position
        String from = new StringBuilder()
                .append(Common.currentShippingOrder.getCurrentLat())
                .append(",")
                .append(Common.currentShippingOrder.getCurrentLng())
                .toString();
        //update position
        Common.currentShippingOrder = snapshot.getValue(ShippingOrderModel.class);
        Common.currentShippingOrder.setKey(snapshot.getKey());
        //save new position
        String to = new StringBuilder()
                .append(Common.currentShippingOrder.getCurrentLat())
                .append(",")
                .append(Common.currentShippingOrder.getCurrentLng())
                .toString();

        if (snapshot.exists()) {
            if (isInit)
                moveMarkerAnimation(shipperMarker, from, to);
            else
                isInit = true;
        }

    }

    private void moveMarkerAnimation(Marker shipperMarker, String from, String to) {
        compositeDisposable.add(iGoogleAPI.getDirections("driving",
                "less_driving",
                from,to,
                getString(R.string.google_maps_key))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(returnResult ->{
            try {
                JSONObject jsonObject = new JSONObject(returnResult);
                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject route = jsonArray.getJSONObject(i);
                    JSONObject poly = route.getJSONObject("overview_polyline");
                    String polyline = poly.getString("points");
                    polylineList = Common.decodePoly(polyline);
                }
                polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.GRAY);
                polylineOptions.width(12);
                polylineOptions.startCap(new SquareCap());
                polylineOptions.jointType(JointType.ROUND);
                polylineOptions.addAll(polylineList);
                grayPolyline = mMap.addPolyline(polylineOptions);

                blackPolylineOptions = new PolylineOptions();
                blackPolylineOptions.color(Color.BLACK);
                blackPolylineOptions.width(5);
                blackPolylineOptions.startCap(new SquareCap());
                blackPolylineOptions.jointType(JointType.ROUND);
                blackPolylineOptions.addAll(polylineList);
                blackPolyline = mMap.addPolyline(blackPolylineOptions);


                //Animator
                ValueAnimator polylineAnimator = ValueAnimator.ofInt(0,100);
                polylineAnimator.setDuration(2000);
                polylineAnimator.setInterpolator(new LinearInterpolator());
                polylineAnimator.addUpdateListener(valueAnimator -> {
                    List<LatLng> points = grayPolyline.getPoints();
                    int percentValue = (int) valueAnimator.getAnimatedValue();
                    int size = points.size();
                    int newPoints = (int) (size*(percentValue/100.0f));
                    List<LatLng> p = points.subList(0,newPoints);
                    blackPolyline.setPoints(p);
                        }
                        );
                polylineAnimator.start();


                //bikemoving
                handler = new Handler();
                index = -1;
                next = 1;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(index < polylineList.size()-1)
                        {
                            index++;
                            next = index+1;
                            start = polylineList.get(index);
                            end = polylineList.get(next);
                        }

                        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,1);
                        valueAnimator.setDuration(1500);
                        valueAnimator.setInterpolator(new LinearInterpolator());
                        valueAnimator.addUpdateListener(valueAnimator1 -> {
                            v = valueAnimator1.getAnimatedFraction();
                            lng = v*end.longitude+(1-v)
                                    *start.longitude;
                            lat = v*end.latitude+(1-v)
                                    *start.latitude;
                            LatLng newPos = new LatLng(lat,lng);
                            shipperMarker.setPosition(newPos);
                            shipperMarker.setAnchor(0.5f,0.5f);
                            shipperMarker.setRotation(Common.getBearing(start,newPos));

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(newPos));

                        });
                        valueAnimator.start();
                        if (index < polylineList.size() -2)  //reach destination
                            handler.postDelayed(this,1500);

                    }
                },1500);






            } catch (Exception e) {
                Toast.makeText(TrackingOrderActivity.this, "Silap Sini" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        },throwable -> {
            Toast.makeText(this,throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }));
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}