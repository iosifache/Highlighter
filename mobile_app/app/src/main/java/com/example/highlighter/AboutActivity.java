package com.example.highlighter;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.highlighter.utils.Configuration;
import com.example.highlighter.utils.TextViewUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AboutActivity extends AppCompatActivity implements OnMapReadyCallback {

  MapView mapView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);

    // Get the primary color
    TypedValue typedValue = new TypedValue();
    getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
    int primaryColor = typedValue.data;

    // Setup the hyperlinks
    TextView creditsTextView = this.findViewById(R.id.tv_credits);
    creditsTextView.setMovementMethod(LinkMovementMethod.getInstance());
    creditsTextView.setLinkTextColor(primaryColor);
    TextViewUtils.stripUnderlines(creditsTextView);
    TextView thanksTextView = this.findViewById(R.id.tv_thanks);
    thanksTextView.setMovementMethod(LinkMovementMethod.getInstance());
    thanksTextView.setLinkTextColor(primaryColor);
    TextViewUtils.stripUnderlines(thanksTextView);

    // Create the map
    this.mapView = this.findViewById(R.id.mv_map);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(this);
  }

  @Override
  protected void onResume() {
    super.onResume();

    this.mapView.onResume();
  }

  @Override
  public void onMapReady(GoogleMap map) {
    // Add a marker
    LatLng coordinates = new LatLng(Configuration.MAP_LATITUDE, Configuration.MAP_LONGITUDE);
    map.addMarker(
        new MarkerOptions().position(coordinates).title(Configuration.MAP_MARKER_TITLE));

    // Updates the location focus and zoom of the map
    CameraUpdate cameraUpdate = CameraUpdateFactory
        .newLatLngZoom(new LatLng(Configuration.MAP_LATITUDE, Configuration.MAP_LONGITUDE),
            Configuration.MAP_ZOOM);
    map.animateCamera(cameraUpdate);
  }

  @Override
  protected void onPause() {
    this.mapView.onPause();

    super.onPause();
  }

  @Override
  protected void onDestroy() {
    this.mapView.onDestroy();

    super.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();

    this.mapView.onLowMemory();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);

    this.mapView.onSaveInstanceState(outState);
  }

  public void goBack(View view) {
    Intent intent = new Intent();

    setResult(RESULT_OK, intent);
    finish();
  }
}