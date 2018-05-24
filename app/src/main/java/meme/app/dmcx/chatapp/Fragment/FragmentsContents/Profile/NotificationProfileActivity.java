package meme.app.dmcx.chatapp.Fragment.FragmentsContents.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import meme.app.dmcx.chatapp.Activities.MainActivity;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentAdapter;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.Fragment.Fragments.Profile;
import meme.app.dmcx.chatapp.R;

public class NotificationProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_notification_activity);

        this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new Profile(), "TAG_PROFILE_NOTIFICATION_FRIEND_REQUEST").commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
