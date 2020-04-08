package fr.teiki.wikitahiti.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.teiki.wikitahiti.models.User;

/**
 * Created by Antoine GALTIER on 07/04/2020.
 */

public class UserHelper {

	private static final String COLLECTION_NAME = "users";

	// --- COLLECTION REFERENCE ---

	public static CollectionReference getUsersCollection(){
		return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
	}

	// --- CREATE ---

	public static Task<Void> createUser(String uid, String username, String urlPicture) {
		User userToCreate = new User(uid, username, urlPicture);
		return UserHelper.getUsersCollection().document(uid).set(userToCreate);
	}

	// --- GET ---

	public static Task<DocumentSnapshot> getUser(String uid){
		return UserHelper.getUsersCollection().document(uid).get();
	}

	// --- UPDATE ---

	public static Task<Void> updateUsername(String username, String uid) {
		return UserHelper.getUsersCollection().document(uid).update("username", username);
	}

	public static Task<Void> updateIsCool(String uid, Boolean isCool) {
		return UserHelper.getUsersCollection().document(uid).update("isCool", isCool);
	}

	// --- DELETE ---

	public static Task<Void> deleteUser(String uid) {
		return UserHelper.getUsersCollection().document(uid).delete();
	}

}
