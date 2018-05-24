package meme.app.dmcx.chatapp.Activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ServerValue;

import meme.app.dmcx.chatapp.Activities.Super.SuperMethods;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Firebase.AppFirebase;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentAdapter;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.Fragment.Fragments.Chat;
import meme.app.dmcx.chatapp.Fragment.Fragments.Home;
import meme.app.dmcx.chatapp.Fragment.Fragments.Search;
import meme.app.dmcx.chatapp.Fragment.Fragments.Settings;
import meme.app.dmcx.chatapp.Fragment.Fragments.Start;
import meme.app.dmcx.chatapp.Fragment.Fragments.Users;
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Chat.ChatMethods;
import meme.app.dmcx.chatapp.LocalDatabase.AppLocalDatabase;
import meme.app.dmcx.chatapp.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar appToolBar;

    private void Program() {

        // Initialize
        appToolBar = findViewById(R.id.appToolBar);

        // Program
        setSupportActionBar(appToolBar);
        SuperMethods.ToggleToolbar(false, "Chat App", false);

        appToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActionBar.DISPLAY_HOME_AS_UP != 0) {
                    onBackPressed();
                } else {
                    Toast.makeText(SuperVariables._MainActivity, "Not Called", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void LoadFragment() {
        // Check if the users is signed in or not

        if (!SuperVariables._AppFirebase.getCurrentUser()) {
            AppFragmentAdapter.AppFragment(new Start(), AppFragmentTag.TAG_START, AppFragmentAdapter.FRAGMENT_CREATE);
            SuperMethods.ToggleToolbar(true, null, false);
        } else {
            AppFragmentAdapter.AppFragment(new Home(), AppFragmentTag.TAG_HOME, AppFragmentAdapter.FRAGMENT_CREATE);
            SuperMethods.ToggleToolbar(false, "Chat App", false);
        }
    }

    private void ToggleFragment(Fragment fragment, String tag, int flag, boolean isHide, String title, boolean isSetDisplatHomeAsUp) {
        if (!SuperVariables._CurrentFragment.getTag().equals(tag)) {
            AppFragmentAdapter.AppFragment(fragment, tag, flag);
            SuperMethods.ToggleToolbar(isHide, title, isSetDisplatHomeAsUp);
        } else {
            Toast.makeText(SuperVariables._MainActivity, "Already in this page!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load Activity
        SuperVariables._MainActivity = this;

        // Load Firebase
        SuperVariables._AppFirebase = new AppFirebase();

        // Load Local Databaes
        SuperVariables._AppLocalDatabase = new AppLocalDatabase();

        // Progaram
        Program();

        // Load Fragment
        LoadFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (SuperVariables._CurrentFragment.getTag().equals(AppFragmentTag.TAG_CHAT)) {
            return false;
        }

        if (!(
            SuperVariables._CurrentFragment.getTag().equals(AppFragmentTag.TAG_SIGN_IN) ||
            SuperVariables._CurrentFragment.getTag().equals(AppFragmentTag.TAG_SIGN_UP)
            )) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.logoutMenuItem:
                SuperVariables._AppLocalDatabase.clear();
                SuperVariables._AppFirebase.SignOut();
                ToggleFragment(new Start(), AppFragmentTag.TAG_START, AppFragmentAdapter.FRAGMENT_REPLACE, true, null, false);
                break;
            case R.id.accountSettingsMenuItem:
                ToggleFragment(new Settings(), AppFragmentTag.TAG_SETTINGS, AppFragmentAdapter.FRAGMENT_REPLACE, false, "Settings", false);
                break;
            case R.id.usersMenuItem:
                ToggleFragment(new Users(), AppFragmentTag.TAG_USERS, AppFragmentAdapter.FRAGMENT_REPLACE, false, "Users", false);
                break;
            case R.id.searchMenuItem:
                ToggleFragment(new Search(), AppFragmentTag.TAG_SEARCH, AppFragmentAdapter.FRAGMENT_REPLACE, true, null, false);
                break;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        SuperVariables._isMainActivityRunning = true;

        if (SuperVariables._AppFirebase.getCurrenctUserId() != null) {
            SuperVariables._AppFirebase.getUsersDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId()).child(AppFirebaseVariables.uonline).setValue(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SuperVariables._isMainActivityRunning = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (SuperVariables._AppFirebase.getCurrenctUserId() != null) {
            SuperVariables._AppFirebase.getUsersDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId()).child(AppFirebaseVariables.uonline).setValue(false);
            SuperVariables._AppFirebase.getUsersDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId()).child(AppFirebaseVariables.ulastseen).setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public void onBackPressed() {
        if (SuperVariables._CurrentFragment.getTag().equals(AppFragmentTag.TAG_START) ||
            SuperVariables._CurrentFragment.getTag().equals(AppFragmentTag.TAG_HOME)) {
            super.onBackPressed();
            finish();
        }
        else if (SuperVariables._CurrentFragment.getTag().equals(AppFragmentTag.TAG_SIGN_IN) ||
                SuperVariables._CurrentFragment.getTag().equals(AppFragmentTag.TAG_SIGN_UP)) {
            ToggleFragment(new Start(), AppFragmentTag.TAG_START, AppFragmentAdapter.FRAGMENT_REPLACE, true, null, false);
        }
        else if (SuperVariables._CurrentFragment.getTag().equals(AppFragmentTag.TAG_PROFILE)) {
            if (SuperVariables._ParentFragmentTag.equals(AppFragmentTag.TAG_USERS)) {
                ToggleFragment(new Users(), AppFragmentTag.TAG_USERS, AppFragmentAdapter.FRAGMENT_REPLACE, false, "Chat App", false);
            } else if (SuperVariables._ParentFragmentTag.equals(AppFragmentTag.TAG_CHAT)) {
                ToggleFragment(new Chat(), AppFragmentTag.TAG_CHAT, AppFragmentAdapter.FRAGMENT_REPLACE, false, null, false);
                ChatMethods.CustomChatActionBar();
            } else if (SuperVariables._ParentFragmentTag.equals(AppFragmentTag.TAG_HOME)) {
                ToggleFragment(new Home(), AppFragmentTag.TAG_HOME, AppFragmentAdapter.FRAGMENT_REPLACE, false, "Chat App", false);
            } else {
                ToggleFragment(new Home(), AppFragmentTag.TAG_HOME, AppFragmentAdapter.FRAGMENT_REPLACE, false, "Chat App", false);
            }
        }
        else if (
                SuperVariables._CurrentFragment.getTag().equals(AppFragmentTag.TAG_SETTINGS) ||
                SuperVariables._CurrentFragment.getTag().equals(AppFragmentTag.TAG_USERS) ||
                SuperVariables._CurrentFragment.getTag().equals(AppFragmentTag.TAG_CHAT) ||
                SuperVariables._CurrentFragment.getTag().equals(AppFragmentTag.TAG_SEARCH)
                ) {
            ToggleFragment(new Home(), AppFragmentTag.TAG_HOME, AppFragmentAdapter.FRAGMENT_REPLACE, false, "Chat App", false);
        }
    }

}
