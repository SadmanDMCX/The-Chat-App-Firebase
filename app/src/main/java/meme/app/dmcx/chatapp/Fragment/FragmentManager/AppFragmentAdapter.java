package meme.app.dmcx.chatapp.Fragment.FragmentManager;

import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;

public class AppFragmentAdapter {

    public static final int FRAGMENT_CREATE = 111;
    public static final int FRAGMENT_REPLACE = 112;
    public static final int FRAGMENT_REMOVE = 113;

    public static void AppFragment(Fragment fragment, int flag) {
        AppFragment(fragment, null, flag);
    }

    public static void AppFragment(Fragment fragment, String tag, int flag) {
        switch (flag) {
            case FRAGMENT_CREATE:
                AppFragmentManager.CreateFragment(fragment, tag);
                break;
            case FRAGMENT_REPLACE:
                AppFragmentManager.ReplaceFragment(fragment, tag);
                break;
            case FRAGMENT_REMOVE:
                AppFragmentManager.RemoveFragment(fragment);
                break;
        }
    }

}
