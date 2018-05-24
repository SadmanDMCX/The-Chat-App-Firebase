package meme.app.dmcx.chatapp.Firebase;

import com.google.firebase.iid.FirebaseInstanceId;

public class AppFirebaseMethods {

    public static String getDeviceTokenId() {
        String token = FirebaseInstanceId.getInstance().getToken();
        return token;
    }

}
