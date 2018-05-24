package meme.app.dmcx.chatapp.Fragment.FragmentManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import meme.app.dmcx.chatapp.Activities.Super.SuperMethods;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.R;

public class AppFragmentManager {

    private static final int FRAGMENT_CONAINER = R.id.fragment_container;

    public static void CreateFragment(Fragment fragment, String fragmentTag) {
        SuperVariables._MainActivity.getSupportFragmentManager().beginTransaction().add(FRAGMENT_CONAINER, fragment, fragmentTag).commit();

        SuperVariables._ParentFragment = SuperVariables._CurrentFragment;
        SuperVariables._ParentFragmentTag = SuperVariables._CurrentFragment != null ? SuperVariables._CurrentFragment.getTag() : null;
        SuperVariables._CurrentFragment = fragment;
    }

    public static void ReplaceFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = SuperVariables._MainActivity.getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.setCustomAnimations(R.anim.slide_right, R.anim.slide_left);
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(FRAGMENT_CONAINER, fragment, tag);
        fragmentTransaction.commit();

        SuperVariables._ParentFragment = SuperVariables._CurrentFragment;
        SuperVariables._ParentFragmentTag = SuperVariables._CurrentFragment != null ? SuperVariables._CurrentFragment.getTag() : null;
        SuperVariables._CurrentFragment = fragment;
    }

    public static void RemoveFragment(Fragment fragment) {
        SuperVariables._MainActivity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

}
