package meme.app.dmcx.chatapp.Fragment.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import meme.app.dmcx.chatapp.Activities.Super.SuperMethods;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentAdapter;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.R;

public class Start extends Fragment {

    private Button signUpNewAccount;
    private Button signInNewAccount;

    private void Program(View view) {

        // Initialize
        signUpNewAccount = view.findViewById(R.id.signUpNewAccount);
        signInNewAccount = view.findViewById(R.id.signInNewAccount);

        // Events
        signUpNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(SuperVariables.APPTAG, "onClick: Starting Sign Up");
                AppFragmentAdapter.AppFragment(new SignUp(), AppFragmentTag.TAG_SIGN_UP, AppFragmentAdapter.FRAGMENT_REPLACE);
                SuperMethods.ToggleToolbar(false, "Create Account", true);
            }
        });

        signInNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(SuperVariables.APPTAG, "onClick: Starting Sign In");
                AppFragmentAdapter.AppFragment(new SignIn(), AppFragmentTag.TAG_SIGN_IN, AppFragmentAdapter.FRAGMENT_REPLACE);
                SuperMethods.ToggleToolbar(false,"Sign In", true);
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        Program(view);
        return view;
    }
}
