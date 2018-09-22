package com.irfancan.deliverpad.views.activitys;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.irfancan.deliverpad.R;
import com.irfancan.deliverpad.constants.Constants;

public class DeliveredItemDetailsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    private TextView mDescriptionTextView;
    private TextView mLocationTextView;
    private ImageView mImageOfItemImageView;


    //Variables holding delivery info
    private String mItemName="";
    private String mItemAddress="";
    private String mItemImgUrl="";
    private Double mItemLong;
    private Double mItemLati;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered_item_details);

        mDescriptionTextView = findViewById(R.id.descriptionTextView);
        mLocationTextView= findViewById(R.id.locationNameTextView);
        mImageOfItemImageView= findViewById(R.id.itemImageView);

        Intent intent = getIntent();
        Bundle deliveredItemBundle = intent.getExtras();

        if(deliveredItemBundle!=null){

            mItemName=deliveredItemBundle.getString((Constants.ITEM_NAME));
            mItemAddress=deliveredItemBundle.getString((Constants.ITEM_ADDRESS));
            mItemImgUrl=deliveredItemBundle.getString((Constants.ITEM_IMG_URL));
            mItemLong=deliveredItemBundle.getDouble(Constants.ITEM_LONG);
            mItemLati=deliveredItemBundle.getDouble(Constants.ITEM_LATI);

        }



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        updateViews();


    }



    private void updateViews(){

        mDescriptionTextView.setText(mItemName);
        mLocationTextView.setText(mItemAddress);
        Glide.with(this).load(mItemImgUrl).into(mImageOfItemImageView);

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
        LatLng sydney = new LatLng(mItemLati , mItemLong);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
