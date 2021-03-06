package fr.teiki.wikitahiti.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Antoine GALTIER on 07/04/2020.
 */

public class ChatHelper {
	private static final String COLLECTION_NAME = "chats";

	// --- COLLECTION REFERENCE ---

	public static CollectionReference getChatCollection(){
		return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
	}
}
