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

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import meme.app.dmcx.chatapp.Activities.Super.SuperMethods;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Common.Validator;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseMethods;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseBoolean;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentAdapter;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.R;

public class SignUp extends Fragment {

    // Variables
    private MaterialEditText nameMEditText;
    private MaterialEditText emailMEditText;
    private MaterialEditText passwordMEditText;
    private MaterialEditText confirmPasswordMEditText;
    private Button signUpAccountButton;

    private AlertDialog spotsDialog;

    // Methods
    private void Program(View view) {

        // Initialization
        nameMEditText = view.findViewById(R.id.nameMEditText);
        emailMEditText = view.findViewById(R.id.statusMEditText);
        passwordMEditText = view.findViewById(R.id.passwordMEditText);
        confirmPasswordMEditText = view.findViewById(R.id.confirmPasswordMEditText);
        signUpAccountButton = view.findViewById(R.id.signUpAccountButton);

        // Events
        signUpAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNotEmptyEmail = !emailMEditText.getText().toString().isEmpty();
                boolean isNotEmptyPassword = !passwordMEditText.getText().toString().isEmpty();
                boolean isNotEmptyComfirmPassword = !confirmPasswordMEditText.getText().toString().isEmpty();
                boolean isValidEmail = Validator.isEmailValid(emailMEditText.getText().toString());
                boolean isValidPassword = Validator.isValidPassword(passwordMEditText.getText().toString(), confirmPasswordMEditText.getText().toString(), 6);

                final String name = nameMEditText.getText().toString();
                String email = isValidEmail ? emailMEditText.getText().toString() : "NaN";
                String password = isValidPassword ? passwordMEditText.getText().toString() : "NaN";

                if (!name.equals("") && !email.equals("NaN") && !password.equals("NaN")) {
                    spotsDialog = new SpotsDialog(SuperVariables._MainActivity, "Signing Up...");
                    spotsDialog.show();

                    SuperVariables._AppFirebase.SignUp(email, password, new CallbackFirebaseBoolean() {
                        @Override
                        public void onCallback(boolean isCompleted) {
                            if (isCompleted) {
                                Map requestMap = new HashMap<>();
                                requestMap.put(AppFirebaseVariables.users + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + AppFirebaseVariables.uname, name);
                                requestMap.put(AppFirebaseVariables.users + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + AppFirebaseVariables.ustatus, "Hay there! I'm using Chat App.");
                                requestMap.put(AppFirebaseVariables.users + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + AppFirebaseVariables.uimage, "default");
                                requestMap.put(AppFirebaseVariables.users + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + AppFirebaseVariables.uthumbimage, "default");
                                requestMap.put(AppFirebaseVariables.users + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + AppFirebaseVariables.udevicetokenid, AppFirebaseMethods.getDeviceTokenId());

                                SuperVariables._AppFirebase.store(requestMap, new CallbackFirebaseBoolean() {
                                    @Override
                                    public void onCallback(boolean isCompleted) {
                                        if (isCompleted) {
                                            SuperVariables._AppFirebase.getUsersDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId()).child(AppFirebaseVariables.uonline).setValue(true);
                                            AppFragmentAdapter.AppFragment(new Home(), AppFragmentTag.TAG_HOME, AppFragmentAdapter.FRAGMENT_REPLACE);
                                            SuperMethods.ToggleToolbar(false, "Chat App", false);
                                        } else {
                                            Toast.makeText(SuperVariables._MainActivity, "Error! Failed to store.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(SuperVariables._MainActivity, "Error! Task not completed!", Toast.LENGTH_SHORT).show();
                            }
                            spotsDialog.dismiss();
                        }
                    });

                }

                if (name.isEmpty()) {
                    nameMEditText.setError("Name required!");
                }

                if (!isNotEmptyEmail) {
                    emailMEditText.setError("Email required!");
                } else if (!isValidEmail) {
                    emailMEditText.setError("Please enter a valid email!");
                }

                if (!isNotEmptyPassword) {
                    passwordMEditText.setError("Password required!");
                }
                if (!isNotEmptyComfirmPassword) {
                    confirmPasswordMEditText.setError("Confirm Password required!");
                }
                if (!isValidPassword && isNotEmptyPassword && isNotEmptyComfirmPassword) {
                    passwordMEditText.setError("Password not matched or max length less then 6!");
                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        Program(view);
        return view;
    }
}
