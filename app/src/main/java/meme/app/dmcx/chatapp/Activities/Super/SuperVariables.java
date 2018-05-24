package meme.app.dmcx.chatapp.Activities.Super;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import meme.app.dmcx.chatapp.Firebase.AppFirebase;
import meme.app.dmcx.chatapp.LocalDatabase.AppLocalDatabase;

public class SuperVariables {

    public static boolean _isMainActivityRunning = false;

    public static AppCompatActivity _MainActivity;

    public static Fragment _CurrentFragment;

    public static Fragment _ParentFragment;
    public static String _ParentFragmentTag;

    public static AppFirebase _AppFirebase;

    public static AppLocalDatabase _AppLocalDatabase;

    public static final String APPTAG = "CHATAPPTTAG";

    public static final String APP_LOCALDATABASE = "CHAT_APP_SHARED_PREFS";

}
