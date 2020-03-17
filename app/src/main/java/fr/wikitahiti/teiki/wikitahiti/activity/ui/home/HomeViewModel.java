package fr.wikitahiti.teiki.wikitahiti.activity.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mapbox.mapboxsdk.maps.Style;

public class HomeViewModel extends ViewModel {

	private MutableLiveData<String> mMapStyle;

	public HomeViewModel() {
		mMapStyle = new MutableLiveData<>();
		//mMapStyle.setValue("This is home fragment");
	}

	public LiveData<String> getText() {
		return mMapStyle;
	}

	public void changeMapStyle(){
		mMapStyle.setValue(Style.OUTDOORS);
	}
}