package miniBean.app;

public interface Config {

	// used to share GCM regId with application server - using php app server
	//static final String APP_SERVER_URL = "http://192.168.1.17/gcm/gcm.php?shareRegId=1";

	// GCM server using java
	 static final String APP_SERVER_URL =
	"http://192.168.2.51:8080/GCM-Server-Device-To-Device/GCMNotification?shareRegId=1";

	// Google Project Number
	static final String GOOGLE_PROJECT_ID = "1010528108195";
	static final String MESSAGE_KEY = "message";

}
