package meme.app.dmcx.chatapp.Fragment.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseDataSnapshot;
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Settings.ImageBottomSheetDialog;
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Settings.StatusBottomSheetDialog;
import meme.app.dmcx.chatapp.R;

public class Settings extends Fragment {

    // Final
    private final String STATUS_BOTTOM_SHEET = "status bottom sheet";
    private final String IMAGE_BOTTOM_SHEET = "status bottom sheet";

    // Variables
    private CircleImageView displayImageView;
    private TextView displayNameTextView;
    private TextView statusTextView;
    private Button changeStatusButton;
    private Button changeImageButton;

    private String user_name;
    private String user_status;
    private String user_image;

    private AlertDialog spotsDialog;

    // Methods
    private void Program(View view) {
        // Initialization
        displayImageView = view.findViewById(R.id.displayImageView);
        displayNameTextView = view.findViewById(R.id.displayNameTextView);
        statusTextView = view.findViewById(R.id.statusTextView);
        changeImageButton = view.findViewById(R.id.changeImageButton);
        changeStatusButton = view.findViewById(R.id.changeStatusButton);

        // Program
        spotsDialog = new SpotsDialog(SuperVariables._MainActivity, "Please wait...");
        spotsDialog.show();

        DatabaseReference reference = SuperVariables._AppFirebase.getUsersDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId());
//        reference.keepSynced(true);
        SuperVariables._AppFirebase.retrive(true, reference, new CallbackFirebaseDataSnapshot() {
            @Override
            public void onCallback(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    user_name = Objects.requireNonNull(dataSnapshot.child(AppFirebaseVariables.uname).getValue()).toString();
                    user_status = Objects.requireNonNull(dataSnapshot.child(AppFirebaseVariables.ustatus).getValue()).toString();
                    user_image = Objects.requireNonNull(dataSnapshot.child(AppFirebaseVariables.uimage).getValue()).toString();

                    displayNameTextView.setText(user_name);
                    statusTextView.setText(user_status);
                    if (!(user_image == null || user_image.isEmpty() || user_image.equals("") || user_image.equals("default"))) {
                        Picasso.with(SuperVariables._MainActivity).load(user_image).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.default_avater).into(displayImageView, new Callback() {
                            @Override
                            public void onSuccess() {}

                            @Override
                            public void onError() {
                                Picasso.with(SuperVariables._MainActivity).load(user_image).placeholder(R.drawable.default_avater).into(displayImageView);
                            }
                        });
                    }
                } else {
                    Toast.makeText(SuperVariables._MainActivity, "Error! Failed to retrive data.", Toast.LENGTH_SHORT).show();
                }

                spotsDialog.dismiss();
            }
        });

        // Events
        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusBottomSheetDialog statusBottomSheetDialog = new StatusBottomSheetDialog();
                statusBottomSheetDialog.show(SuperVariables._MainActivity.getSupportFragmentManager(), STATUS_BOTTOM_SHEET);
            }
        });

        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageBottomSheetDialog imageBottomSheetDialog = new ImageBottomSheetDialog();
                imageBottomSheetDialog.show(SuperVariables._MainActivity.getSupportFragmentManager(), IMAGE_BOTTOM_SHEET);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Program(view);
        return view;
    }

}
