package edu.ecjtu.domain.vo.objects;


public class User {
	private String username;
	private String password;
	private String islogin;
	private byte[] headphoto;

	public User(String username, String password, String islogin,
			byte[] headphoto) {
		super();
		this.username = username;
		this.password = password;
		this.islogin = islogin;
		this.headphoto = headphoto;
	}

	public byte[] getHeadphoto() {
		return headphoto;
	}

	public void setHeadphoto(byte[] headphoto) {
		this.headphoto = headphoto;
	}

	public String getIslogin() {
		return islogin;
	}

	public void setIslogin(String islogin) {
		this.islogin = islogin;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password
				+ ", islogin=" + islogin + ", headphoto="
				+ Arrays.toString(headphoto) + "]";
	}

	public User() {
		super();
	}

	public User(String username, String password, String islogin) {
		super();
		this.username = username;
		this.password = password;
		this.islogin = islogin;
	}

}
