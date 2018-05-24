package meme.app.dmcx.chatapp.Fragment.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import meme.app.dmcx.chatapp.Activities.Super.SuperMethods;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Common.Validator;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseBoolean;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentAdapter;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.R;

public class SignIn extends Fragment {

    // Variables
    private MaterialEditText emailMEditText;
    private MaterialEditText passwordMEditText;
    private Button signInAccountButton;

    private AlertDialog spotsDialog;

    // Methods
    private void Program(View view) {

        // Initialization
        emailMEditText = view.findViewById(R.id.statusMEditText);
        passwordMEditText = view.findViewById(R.id.passwordMEditText);
        signInAccountButton = view.findViewById(R.id.signInAccountButton);

        // Events
        signInAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNotEmptyEmail = !emailMEditText.getText().toString().isEmpty();
                boolean isNotEmptyPassword = !passwordMEditText.getText().toString().isEmpty();
                boolean isValidEmail = Validator.isEmailValid(emailMEditText.getText().toString());
                boolean isValidPassword = Validator.isValidPassword(passwordMEditText.getText().toString(), 6);

                String email = isValidEmail ? emailMEditText.getText().toString() : "NaN";
                String password = isValidPassword ? passwordMEditText.getText().toString() : "NaN";

                if (!email.equals("NaN") && !password.equals("NaN")) {
                    spotsDialog = new SpotsDialog(SuperVariables._MainActivity, "Siging In...");
                    spotsDialog.show();

                    SuperVariables._AppFirebase.SignIn(email, password, new CallbackFirebaseBoolean() {
                        @Override
                        public void onCallback(boolean isCompleted) {
                            if (isCompleted) {
                                SuperVariables._AppFirebase.getUsersDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId()).child(AppFirebaseVariables.uonline).setValue(true);
                                AppFragmentAdapter.AppFragment(new Home(), AppFragmentTag.TAG_HOME, AppFragmentAdapter.FRAGMENT_REPLACE);
                                SuperMethods.ToggleToolbar(false, "Chat App", false);
                            } else {
                                Toast.makeText(SuperVariables._MainActivity, "Error! Sign in failed!", Toast.LENGTH_SHORT).show();
                            }
                            spotsDialog.dismiss();
                        }
                    });
                }

                if (!isNotEmptyEmail) {
                    emailMEditText.setError("Email required!");
                } else if (!isValidEmail) {
                    emailMEditText.setError("Please enter a valid email!");
                }

                if (!isNotEmptyPassword) {
                    passwordMEditText.setError("Password required!");
                }
                if (!isValidPassword && isNotEmptyPassword) {
                    passwordMEditText.setError("Max length less then 6!");
                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        Program(view);
        return view;
    }
}
