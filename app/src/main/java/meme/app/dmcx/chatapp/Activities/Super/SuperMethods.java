package meme.app.dmcx.chatapp.Activities.Super;

import android.support.v7.app.ActionBar.LayoutParams;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.util.Objects;

import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentAdapter;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.Fragment.Fragments.Home;
import meme.app.dmcx.chatapp.Fragment.Fragments.Profile;
import meme.app.dmcx.chatapp.Fragment.Fragments.Start;
import meme.app.dmcx.chatapp.R;

public class SuperMethods {

    public static void ToggleToolbar(boolean hide, String title, boolean isSetDisplayHomeAsUpEnable) {

        Objects.requireNonNull(SuperVariables._MainActivity.getSupportActionBar()).setDisplayShowCustomEnabled(false);

        if (hide) {
            SuperVariables._MainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(isSetDisplayHomeAsUpEnable);
            SuperVariables._MainActivity.getSupportActionBar().hide();
        } else {
            SuperVariables._MainActivity.getSupportActionBar().setTitle(title);
            SuperVariables._MainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(isSetDisplayHomeAsUpEnable);
            SuperVariables._MainActivity.getSupportActionBar().show();
        }

        SuperVariables._MainActivity.supportInvalidateOptionsMenu();
    }

}
