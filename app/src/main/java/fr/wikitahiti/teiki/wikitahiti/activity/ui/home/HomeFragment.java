package fr.wikitahiti.teiki.wikitahiti.activity.ui.home;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import fr.wikitahiti.teiki.wikitahiti.R;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

	private HomeViewModel homeViewModel;
	private static final String MARKER_SOURCE = "markers-source";
	private static final String MARKER_STYLE_LAYER = "markers-style-layer";
	private static final String MARKER_IMAGE = "custom-marker";

	private MapView mapView;
	private MapboxMap mapboxMap;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 final ViewGroup container, Bundle savedInstanceState) {
		Mapbox.getInstance(inflater.getContext(), "pk.eyJ1IjoiYW1va3Jpc3MiLCJhIjoiY2s2aXJzb3dkMDQ2YzNsbnV3OHZ6MzBpayJ9.YK1BCxNN5_ArlAggOCxflw");
		homeViewModel =
				ViewModelProviders.of(this).get(HomeViewModel.class);
		View root = inflater.inflate(R.layout.fragment_home, container, false);

		homeViewModel.getText().observe(getActivity(), new Observer<String>() {
			@Override
			public void onChanged(@Nullable String s) {
				mapboxMap.setStyle(s);
			}
		});

		//ANG Initiate MapBox Instance with own credentials

		mapView = root.findViewById(R.id.mapView);
		mapView.onCreate(savedInstanceState);
		mapView.getMapAsync(this);
		return root;
	}


	@Override
	public void onStart() {
		super.onStart();
		mapView.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		mapView.onStop();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	public void onMapReady(@NonNull MapboxMap mapboxMap) {
		this.mapboxMap = mapboxMap;
		mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
			@Override
			public void onStyleLoaded(@NonNull Style style) {
				// Map is set up and the style has loaded. Now you can add data or make other map adjustments.

				style.addImage(MARKER_IMAGE, BitmapFactory.decodeResource(
						getResources(), R.drawable.custom_marker));
				addMarkers(style);
			}
		});

	}

	private void addMarkers(@NonNull Style loadedMapStyle) {
		List<Feature> features = new ArrayList<>();
		features.add(Feature.fromGeometry(Point.fromLngLat(-149.5394, -17.5616)));

		/* Source: A data source specifies the geographic coordinate where the image marker gets placed. */

		loadedMapStyle.addSource(new GeoJsonSource(MARKER_SOURCE, FeatureCollection.fromFeatures(features)));

		/* Style layer: A style layer ties together the source and image and specifies how they are displayed on the map. */
		loadedMapStyle.addLayer(new SymbolLayer(MARKER_STYLE_LAYER, MARKER_SOURCE)
				.withProperties(
						PropertyFactory.iconAllowOverlap(true),
						PropertyFactory.iconIgnorePlacement(true),
						PropertyFactory.iconImage(MARKER_IMAGE),
// Adjust the second number of the Float array based on the height of your marker image.
// This is because the bottom of the marker should be anchored to the coordinate point, rather
// than the middle of the marker being the anchor point on the map.
						PropertyFactory.iconOffset(new Float[] {0f, -52f})
				));
	}
}