package fr.teiki.wikitahiti.models;

import androidx.annotation.Nullable;

/**
 * Created by Antoine GALTIER on 07/04/2020.
 */

public class User {
	private String uid;
	private String username;
	private Boolean isCool;
	@Nullable private String urlPicture;

	public User() { }

	public User(String uid, String username, String urlPicture) {
		this.uid = uid;
		this.username = username;
		this.urlPicture = urlPicture;
		this.isCool = false;
	}

	// --- GETTERS ---
	public String getUid() { return uid; }
	public String getUsername() { return username; }
	public String getUrlPicture() { return urlPicture; }
	public Boolean getIsCool() { return isCool; }

	// --- SETTERS ---
	public void setUsername(String username) { this.username = username; }
	public void setUid(String uid) { this.uid = uid; }
	public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
	public void setIsCool(Boolean mentor) { isCool = mentor; }
}
