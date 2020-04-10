package fr.teiki.wikitahiti.activities;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import fr.teiki.wikitahiti.wikitahiti.R;

/**
 * Created by Antoine GALTIER on 07/04/2020.
 */

public abstract class BaseActivity extends AppCompatActivity {

	// 1 - Identifier for Sign-In Activity
	protected static final int RC_SIGN_IN = 123;

	@Nullable
	public FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }
	protected Boolean isCurrentUserLogged(){ return (getCurrentUser() != null); }

	public abstract int getFragmentLayout();

	protected void configureToolbar(){
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	}

	protected void startSignInActivity(){
		startActivityForResult(
				AuthUI.getInstance()
						.createSignInIntentBuilder()
						.setTheme(R.style.LoginTheme)
						.setAvailableProviders(Arrays.asList(
								new AuthUI.IdpConfig.EmailBuilder().build(),
								new AuthUI.IdpConfig.FacebookBuilder().build(),
								new AuthUI.IdpConfig.GoogleBuilder().build()))
						.setIsSmartLockEnabled(false, true)
						.setLogo(android.R.drawable.btn_plus)
						.build(),
				RC_SIGN_IN);
	}

	protected void startProfileActivity() {
		Intent intent = new Intent(this, ProfileActivity.class);
		startActivity(intent);
	}


	protected void showSnackBar(DrawerLayout drawerLayout, String message){
		Snackbar.make(drawerLayout, message, Snackbar.LENGTH_SHORT).show();
	}

	// --------------------
	// ERROR HANDLER
	// --------------------

	public OnFailureListener onFailureListener(){
		return new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
			}
		};
	}
}
