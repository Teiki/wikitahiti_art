package fr.teiki.wikitahiti.api;

import com.google.firebase.firestore.Query;

/**
 * Created by Antoine GALTIER on 07/04/2020.
 */

public class MessageHelper {
	private static final String COLLECTION_NAME = "messages";

	// --- GET ---

	public static Query getAllMessageForChat(String chat){
		return ChatHelper.getChatCollection()
				.document(chat)
				.collection(COLLECTION_NAME)
				.orderBy("dateCreated")
				.limit(50);
	}
}
