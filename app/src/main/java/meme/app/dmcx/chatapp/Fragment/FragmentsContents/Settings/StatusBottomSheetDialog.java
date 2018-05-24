package meme.app.dmcx.chatapp.Fragment.FragmentsContents.Settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import meme.app.dmcx.chatapp.Activities.Super.SuperMethods;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseBoolean;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseDataSnapshot;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.Fragment.Fragments.Profile;
import meme.app.dmcx.chatapp.R;

public class StatusBottomSheetDialog extends BottomSheetDialogFragment {

    // Variables
    private MaterialEditText statusMEditText;
    private Button updateButton;
    private Button cancelButton;

    // Methods
    private void Program(View view) {

        // Initialization
        statusMEditText = view.findViewById(R.id.statusMEditText);
        updateButton = view.findViewById(R.id.updateButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        // Program
        DatabaseReference reference = SuperVariables._AppFirebase.getUsersDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId());
        SuperVariables._AppFirebase.retrive(false, reference, new CallbackFirebaseDataSnapshot() {
            @Override
            public void onCallback(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    String status = Objects.requireNonNull(dataSnapshot.child(AppFirebaseVariables.ustatus).getValue()).toString();
                    statusMEditText.setText(status);
                }
            }
        });

        // Events
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusBottomSheetDialog.this.dismiss();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = statusMEditText.getText().toString();
                if (status.equals("") || status.isEmpty() || status.equals(null)) {
                    StatusBottomSheetDialog.this.dismiss();
                    Toast.makeText(SuperVariables._MainActivity, "Status not given!", Toast.LENGTH_SHORT).show();
                } else {
                    final AlertDialog spotsDialog = new SpotsDialog(SuperVariables._MainActivity, "Please wait...");
                    spotsDialog.show();

                    Map requestMap = new HashMap();
                    requestMap.put(AppFirebaseVariables.users + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + AppFirebaseVariables.ustatus, status);
                    SuperVariables._AppFirebase.store(requestMap, new CallbackFirebaseBoolean() {
                        @Override
                        public void onCallback(boolean isCompleted) {
                            if (!isCompleted) {
                                Toast.makeText(SuperVariables._MainActivity, "Error! Task not completed!", Toast.LENGTH_SHORT).show();
                            }

                            spotsDialog.dismiss();
                            StatusBottomSheetDialog.this.dismiss();
                        }
                    });
                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_change_status_bottom_sheet_layout, container, false);
        Program(view);
        return view;
    }

}
