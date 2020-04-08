package fr.teiki.wikitahiti.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.teiki.wikitahiti.activities.ui.home.HomeViewModel;
import fr.teiki.wikitahiti.api.UserHelper;
import fr.teiki.wikitahiti.wikitahiti.R;

public class DrawerMainActivity extends BaseActivity {


	private AppBarConfiguration mAppBarConfiguration;

	@BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.nav_view)NavigationView navigationView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer_main);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);

		//TODO
		FloatingActionButton fab = findViewById(R.id.fab);
		final ViewModelProvider viewModelProviders = ViewModelProviders.of(this);
		fab.setOnClickListener(view -> {
			Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show();
			viewModelProviders.get(HomeViewModel.class).changeMapStyle();
		});


		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		mAppBarConfiguration = new AppBarConfiguration.Builder(
				R.id.nav_map, R.id.nav_share, R.id.nav_send)
				.setDrawerLayout(findViewById(R.id.drawer_layout))
				.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);


	}


	@Override
	protected void onResume() {
		super.onResume();
		//Setup Account UI
		updateUserAccountUI();
	}

	private void updateUserAccountUI() {
		View headerView = navigationView.getHeaderView(0);
		if (getCurrentUser() != null){

			//Get picture URL from Firebase
			if (getCurrentUser().getPhotoUrl() != null) {
				Glide.with(this)
						.load(getCurrentUser().getPhotoUrl())
						.apply(RequestOptions.circleCropTransform())
						.into(((ImageView) headerView.findViewById(R.id.img_nav_header)));
			}

			//Get email & username from Firebase
			String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : getCurrentUser().getEmail();
			String username = TextUtils.isEmpty(getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : getCurrentUser().getDisplayName();

			//Update views with data
			((TextView) headerView.findViewById(R.id.txt_nav_header_title)).setText(username);
			((TextView) headerView.findViewById(R.id.txt_nav_header_subtitle)).setText(email);
			headerView.setOnClickListener(v -> startProfileActivity());
		} else {
			((TextView) headerView.findViewById(R.id.txt_nav_header_title)).setText(getString(R.string.nav_header_title));
			((TextView) headerView.findViewById(R.id.txt_nav_header_subtitle)).setText("");
			headerView.setOnClickListener(v -> startSignInActivity());
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.drawer_main, menu);
		return true;
	}

	@Override
	public boolean onSupportNavigateUp() {
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		return NavigationUI.navigateUp(navController, mAppBarConfiguration)
				|| super.onSupportNavigateUp();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		handleResponseAfterSignIn(requestCode,resultCode,data);
	}

	// --------------------
	// REST REQUEST
	// --------------------

	// 1 - Http request that create user in firestore
	private void createUserInFirestore(){

		if (this.getCurrentUser() != null){

			String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
			String username = this.getCurrentUser().getDisplayName();
			String uid = this.getCurrentUser().getUid();

			UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(this.onFailureListener());
		}
	}


	// --------------------
	// UTILS
	// --------------------

	// 3 - Method that handles response after SignIn Activity close
	private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

		IdpResponse response = IdpResponse.fromResultIntent(data);

		if (requestCode == RC_SIGN_IN) {
			if (resultCode == RESULT_OK) { // SUCCESS
				// 2 - CREATE USER IN FIRESTORE
				this.createUserInFirestore();
				showSnackBar(this.drawerLayout, getString(R.string.connection_succeed));
			} else { // ERRORS
				if (response == null || response.getError() == null) {
					showSnackBar(this.drawerLayout, getString(R.string.error_authentication_canceled));
				} else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
					showSnackBar(this.drawerLayout, getString(R.string.error_no_internet));
				} else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
					showSnackBar(this.drawerLayout, getString(R.string.error_unknown_error));
				}
			}
		}
	}

	@Override
	public int getFragmentLayout() {
		return R.layout.activity_drawer_main;
	}
}
