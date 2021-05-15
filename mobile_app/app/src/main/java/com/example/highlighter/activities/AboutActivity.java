package com.example.highlighter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.highlighter.R;
import com.example.highlighter.utils.Configuration;
import com.example.highlighter.utils.TextUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Activity showing details about the application
 */
public class AboutActivity extends AppCompatActivity implements OnMapReadyCallback {

  MapView fixedLocationMapView;

  /**
   * Starts the activity.
   *
   * @param savedInstanceState Saved data
   */
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
    TextUtils.stripUnderlines(creditsTextView);
    TextView thanksTextView = this.findViewById(R.id.tv_thanks);
    thanksTextView.setMovementMethod(LinkMovementMethod.getInstance());
    thanksTextView.setLinkTextColor(primaryColor);
    TextUtils.stripUnderlines(thanksTextView);

    // Create the map
    this.fixedLocationMapView = this.findViewById(R.id.mv_map);
    this.fixedLocationMapView.onCreate(savedInstanceState);
    this.fixedLocationMapView.getMapAsync(this);
  }

  /**
   * See the documentation of the com.google.android.gms.maps.OnMapReadyCallback.
   */
  @Override
  protected void onResume() {
    super.onResume();

    this.fixedLocationMapView.onResume();
  }

  /**
   * See the documentation of the com.google.android.gms.maps.OnMapReadyCallback.
   */
  @Override
  public void onMapReady(GoogleMap map) {
    // Add a marker
    LatLng coordinates = new LatLng(Configuration.UI_MAP_LATITUDE, Configuration.UI_MAP_LONGITUDE);
    String markerTitle = this.getResources().getString(R.string.desc_marker);
    map.addMarker(new MarkerOptions().position(coordinates).title(markerTitle));

    // Updates the location focus and zoom of the map
    CameraUpdate cameraUpdate = CameraUpdateFactory
        .newLatLngZoom(new LatLng(Configuration.UI_MAP_LATITUDE, Configuration.UI_MAP_LONGITUDE),
            Configuration.UI_MAP_ZOOM);
    map.animateCamera(cameraUpdate);
  }

  /**
   * See the documentation of the com.google.android.gms.maps.OnMapReadyCallback.
   */
  @Override
  protected void onPause() {
    this.fixedLocationMapView.onPause();

    super.onPause();
  }

  /**
   * See the documentation of the com.google.android.gms.maps.OnMapReadyCallback.
   */
  @Override
  protected void onDestroy() {
    this.fixedLocationMapView.onDestroy();

    super.onDestroy();
  }

  /**
   * See the documentation of the com.google.android.gms.maps.OnMapReadyCallback.
   */
  @Override
  public void onLowMemory() {
    super.onLowMemory();

    this.fixedLocationMapView.onLowMemory();
  }

  /**
   * See the documentation of the com.google.android.gms.maps.OnMapReadyCallback.
   */
  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);

    this.fixedLocationMapView.onSaveInstanceState(outState);
  }

  /**
   * Goes back to the calling activity.
   *
   * @param view View representing the clicked back button
   */
  public void goBack(View view) {
    Intent intent = new Intent();
    setResult(RESULT_OK, intent);
    finish();
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
  }

  /**
   * Goes back to the calling activity on back button press.
   */
  @Override
  public void onBackPressed() {
    super.onBackPressed();

    this.goBack(null);
  }

}