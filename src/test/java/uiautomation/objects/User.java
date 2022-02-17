package uiautomation.objects;

public class User {

	private String userName;

	private String password;

	private boolean isValid;
	
	private String expectedMessage;

	public User(String userName, String password, boolean isValid,String expectedMessage) {
		this.userName = userName;
		this.password = password;
		this.isValid = isValid;
		this.expectedMessage=expectedMessage;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public boolean isValid() {
		return isValid;
	}

	public String getExpectedMessage() {
		return expectedMessage;
	}

}
