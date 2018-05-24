package meme.app.dmcx.chatapp.Common;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Firebase.AppFirebase;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseBoolean;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseDataSnapshot;

public class TheChat extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Offline Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);

        // Offline Picasso
        Picasso.Builder picassoBuilder = new Picasso.Builder(this);
        picassoBuilder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso picasso = picassoBuilder.build();
        picasso.setIndicatorsEnabled(true);
        picasso.setLoggingEnabled(true);
        Picasso.setSingletonInstance(picasso);

        // User Status
        AppFirebase firebase = new AppFirebase();
        if (firebase.getCurrenctUserId() != null) {
            final DatabaseReference reference = firebase.getUsersDatabase().child(firebase.getCurrenctUserId());
            firebase.retrive(false, reference, new CallbackFirebaseDataSnapshot() {
                @Override
                public void onCallback(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        reference.child(AppFirebaseVariables.uonline).onDisconnect().setValue(false);
                        reference.child(AppFirebaseVariables.ulastseen).setValue(ServerValue.TIMESTAMP);
                    }
                }
            });
        }
    }
}
