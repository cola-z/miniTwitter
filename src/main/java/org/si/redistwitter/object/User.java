package org.si.redistwitter.object;

public class User {
	private int uid;
	private String userName;
	private String password;
	private int followers;//关注该用户的人数
	private int following;//该用户关注的人数
	private int posts;//发推数
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getFollowers() {
		return followers;
	}
	public void setFollowers(int followers) {
		this.followers = followers;
	}
	public int getFollowing() {
		return following;
	}
	public void setFollowing(int following) {
		this.following = following;
	}
	public int getPosts() {
		return posts;
	}
	public void setPosts(int posts) {
		this.posts = posts;
	}
	
	@Override
	public String toString() {
		return "User [userName=" + userName + ", followers=" + followers
				+ ", following=" + following + "]";
	}
	
}
