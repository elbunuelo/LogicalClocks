package edu.gvsu.cis.cis656.message;

public class ErrorCodes {

	public static final int REG_FAIL = 0;

	public static final int DEREG_FAIL_UUID = 1;

	public static final int DEREG_FAIL_USERNAME = 2;

	public static final int USER_AUTHENTICATION_FAIL = 3;

	public static final int MSG_PARSING_FAILED = 4;

	public static String getStringError(int errorCode) {
		String error = null;
		switch(errorCode) {
			case 0:
				error = "Registration failed";
				break;

			case 1:
				error = "Deregistration failed due to the UUID";
				break;

			case 2:
				error = "Deregistration failed due to the username";
				break;

			case 3:
				error = "User authentication fail";
				break;

			case 4:
				error = "Message parsing failed";
				break;

			default:
				error = "Cannot decode error code";
				break;
		}

		return error;
	}
}
