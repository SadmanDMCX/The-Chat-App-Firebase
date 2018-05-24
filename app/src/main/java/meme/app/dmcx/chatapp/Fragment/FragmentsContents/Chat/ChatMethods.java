package meme.app.dmcx.chatapp.Fragment.FragmentsContents.Chat;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import meme.app.dmcx.chatapp.Activities.Super.SuperMethods;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentAdapter;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.Fragment.Fragments.Profile;
import meme.app.dmcx.chatapp.R;

public class ChatMethods {

    private static TextView displayNameTextViewActionBar;
    private static TextView userActivityStatusTextViewActionBar;
    private static ImageView userProfileCirculerImageViewActionBar;

    public static void CustomChatActionBar() {
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        LayoutInflater inflater = (LayoutInflater) SuperVariables._MainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customActionBar = inflater.inflate(R.layout.chat_action_bar_layout, null);

        SuperVariables._MainActivity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        SuperVariables._MainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SuperVariables._MainActivity.getSupportActionBar().setCustomView(customActionBar, layoutParams);

        // Initialize
        displayNameTextViewActionBar = customActionBar.findViewById(R.id.displayNameTextViewActionBar);
        userActivityStatusTextViewActionBar = customActionBar.findViewById(R.id.userActivityStatusTextViewActionBar);
        userProfileCirculerImageViewActionBar = customActionBar.findViewById(R.id.userProfileCirculerImageViewActionBar);

        userProfileCirculerImageViewActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppFragmentAdapter.AppFragment(new Profile(), AppFragmentTag.TAG_PROFILE, AppFragmentAdapter.FRAGMENT_REPLACE);
                SuperMethods.ToggleToolbar(true, null, false);
            }
        });
    }

    public static void setDisplayNameTextViewActionBar(String value) {
        displayNameTextViewActionBar.setText(value);
    }

    public static void setUserActivityStatusTextViewActionBar(String value) {
        userActivityStatusTextViewActionBar.setText(value);
    }

    public static void setUserProfileCirculerImageViewActionBar() {
        userProfileCirculerImageViewActionBar.setImageResource(R.drawable.info);
    }
}
