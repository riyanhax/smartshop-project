package com.smartshop.docs.share;

import java.io.Serializable;

import vnfoss2010.smartshop.webbased.share.WGoogleUser;

public class GoogleUser implements Serializable {
	private static final long serialVersionUID = 1L;
	public boolean isAdmin;
	public boolean isLogin;
	public String linkLogin;
	public String linkLogout;
	public String username;
	public String email;
	public String authDomain;
	public String nickName;
	public String userId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GoogleUser [authDomain=" + authDomain + ", email=" + email
				+ ", isAdmin=" + isAdmin + ", isLogin=" + isLogin
				+ ", linkLogin=" + linkLogin + ", linkLogout=" + linkLogout
				+ ", nickName=" + nickName + ", userId=" + userId
				+ ", username=" + username + "]";
	}

	public WGoogleUser cloneObject() {
		WGoogleUser wg = new WGoogleUser();
		wg.isAdmin = this.isAdmin;
		wg.isLogin = this.isLogin;
		wg.linkLogin = this.linkLogin;
		wg.linkLogout = this.linkLogout;
		wg.username = this.username;
		wg.email = this.email;
		wg.authDomain = this.authDomain;
		wg.nickName = this.nickName;
		wg.userId = this.userId;

		return wg;
	}
}
