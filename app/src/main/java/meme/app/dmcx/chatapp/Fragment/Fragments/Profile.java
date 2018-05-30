package meme.app.dmcx.chatapp.Fragment.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseBoolean;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseDataSnapshot;
import meme.app.dmcx.chatapp.R;

public class Profile extends Fragment {

    // Final
    private final String cs_request_received = "req_received";
    private final String cs_request_sent = "req_sent";
    private final String cs_not_friends = "not_friends";
    private final String cs_friends = "friends";

    // Variables
    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView statusTextView;
    private TextView totalFriendsTextView;
    private TextView sendFriendRequestButton;
    private TextView declineFriendRequestButton;

    private String currentState;
    private String profileUserId;

    private String profile_image;
    private String profile_name;
    private String profile_status;
    private String profile_totalFriends;

    // Methods
    private void Program(View view) {

        // Initialization
        profileImageView = view.findViewById(R.id.profileImageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        statusTextView = view.findViewById(R.id.statusTextView);
        totalFriendsTextView = view.findViewById(R.id.totalFriendsTextView);
        sendFriendRequestButton = view.findViewById(R.id.sendFriendRequestButton);
        declineFriendRequestButton = view.findViewById(R.id.declineFriendRequestButton);

        currentState = cs_not_friends;

        // Program
        final AlertDialog spotDialog = new SpotsDialog(SuperVariables._MainActivity, "Please Wait...");
        spotDialog.show();

        profileUserId = SuperVariables._AppFirebase.getProfileUserId();

        if (profileUserId.equals(SuperVariables._AppFirebase.getCurrenctUserId())) {
            sendFriendRequestButton.setVisibility(View.INVISIBLE);
            sendFriendRequestButton.setEnabled(false);
            declineFriendRequestButton.setVisibility(View.INVISIBLE);
            declineFriendRequestButton.setEnabled(false);
        } else {
            sendFriendRequestButton.setVisibility(View.VISIBLE);
            sendFriendRequestButton.setEnabled(true);
        }

        DatabaseReference reference = SuperVariables._AppFirebase.getRootDatabase().child(AppFirebaseVariables.users).child(profileUserId);
        SuperVariables._AppFirebase.retrive(false, reference, new CallbackFirebaseDataSnapshot() {
            @Override
            public void onCallback(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    profile_image = Objects.requireNonNull(dataSnapshot.child(AppFirebaseVariables.uimage).getValue()).toString();
                    profile_name = Objects.requireNonNull(dataSnapshot.child(AppFirebaseVariables.uname).getValue()).toString();
                    profile_status = Objects.requireNonNull(dataSnapshot.child(AppFirebaseVariables.ustatus).getValue()).toString();
                    profile_totalFriends = "10";

                    if (!profile_image.isEmpty() && !profile_image.equals("default")) {
                        Picasso.with(SuperVariables._MainActivity).load(profile_image).placeholder(R.drawable.default_avater).into(profileImageView);
                    }
                    nameTextView.setText(profile_name);
                    statusTextView.setText(profile_status);
                    totalFriendsTextView.setText(profile_totalFriends);

                    // Request Handler
                    DatabaseReference reference = SuperVariables._AppFirebase.getFriendRequestDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId());
                    SuperVariables._AppFirebase.retrive(false, reference, new CallbackFirebaseDataSnapshot() {
                        @Override
                        public void onCallback(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                if (dataSnapshot.hasChild(profileUserId)) {
                                    String req_type = dataSnapshot.child(profileUserId).child(AppFirebaseVariables.request_type).getValue().toString();
                                    if (req_type != null) {
                                        if (req_type.equals(AppFirebaseVariables.request_type_sent)) {
                                            currentState = cs_request_sent;
                                            ToggleButtons(true, "Cancel Friend Request", false, View.INVISIBLE);
                                        } else if (req_type.equals(AppFirebaseVariables.request_type_received)) {
                                            currentState = cs_request_received;
                                            ToggleButtons(true, "Accept Friend Request", true, View.VISIBLE);
                                        }
                                    } else {
                                        currentState = cs_not_friends;
                                        ToggleButtons(true, "Send Friend Request", false, View.INVISIBLE);
                                    }

                                    spotDialog.dismiss();
                                } else {
                                    DatabaseReference reference = SuperVariables._AppFirebase.getFriendsDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId());
                                    SuperVariables._AppFirebase.retrive(true, reference, new CallbackFirebaseDataSnapshot() {
                                        @Override
                                        public void onCallback(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(profileUserId)) {
                                                currentState = cs_friends;
                                                ToggleButtons(true, "Unfriend", false, View.INVISIBLE);
                                            } else {
                                                currentState = cs_not_friends;
                                                ToggleButtons(true, "Send Friend Request", false, View.INVISIBLE);
                                            }

                                            spotDialog.dismiss();
                                        }
                                    });
                                }
                            }
                        }
                    });

                }
            }
        });

        // Events
        sendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog spotDialog = new SpotsDialog(getContext(), "Please Wait...");
                spotDialog.show();

                sendFriendRequestButton.setEnabled(false);

                if (currentState.equals(cs_not_friends)) {

                    String notificationPushId = SuperVariables._AppFirebase.getPushId(SuperVariables._AppFirebase.getNotificationsDatabase());

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put(AppFirebaseVariables.notification_from, SuperVariables._AppFirebase.getCurrenctUserId());
                    notificationData.put(AppFirebaseVariables.notification_type, AppFirebaseVariables.notification_type_request);

                    Map requestMap = new HashMap();
                    requestMap.put(AppFirebaseVariables.friend_requests + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + profileUserId + "/" + AppFirebaseVariables.request_type, AppFirebaseVariables.request_type_sent);
                    requestMap.put(AppFirebaseVariables.friend_requests + "/" + profileUserId + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + AppFirebaseVariables.request_type, AppFirebaseVariables.request_type_received);
                    requestMap.put(AppFirebaseVariables.notifications + "/" + profileUserId + "/" + notificationPushId, notificationData);

                    SuperVariables._AppFirebase.store(requestMap, new CallbackFirebaseBoolean() {
                        @Override
                        public void onCallback(boolean isCompleted) {
                            if (isCompleted) {
                                currentState = cs_request_sent;
                                ToggleButtons(true, "Cancel Friend Request", false, View.INVISIBLE);
                            }

                            spotDialog.dismiss();
                        }
                    });

                } else if (currentState.equals(cs_request_sent)) {
                    DatabaseReference[] references = new DatabaseReference[] {
                            SuperVariables._AppFirebase.getFriendRequestDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId()).child(profileUserId).child(AppFirebaseVariables.request_type),
                            SuperVariables._AppFirebase.getFriendRequestDatabase().child(profileUserId).child(SuperVariables._AppFirebase.getCurrenctUserId()).child(AppFirebaseVariables.request_type),
                    };
                    SuperVariables._AppFirebase.remove(references, new CallbackFirebaseBoolean() {
                        @Override
                        public void onCallback(boolean isCompleted) {
                            currentState = cs_not_friends;
                            ToggleButtons(true, "Send Friend Request", false, View.INVISIBLE);
                            spotDialog.dismiss();
                        }
                    });
                } else if (currentState.equals(cs_request_received)) {
                    String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    Map requestMap = new HashMap();
                    requestMap.put(AppFirebaseVariables.friends + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + profileUserId + "/" + AppFirebaseVariables.friends_date, currentDate);
                    requestMap.put(AppFirebaseVariables.friends + "/" + profileUserId + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + AppFirebaseVariables.friends_date, currentDate);

                    SuperVariables._AppFirebase.store(requestMap, new CallbackFirebaseBoolean() {
                        @Override
                        public void onCallback(boolean isCompleted) {
                            if (isCompleted) {
                                DatabaseReference[] references = new DatabaseReference[] {
                                        SuperVariables._AppFirebase.getFriendRequestDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId()).child(profileUserId).child(AppFirebaseVariables.request_type),
                                        SuperVariables._AppFirebase.getFriendRequestDatabase().child(profileUserId).child(SuperVariables._AppFirebase.getCurrenctUserId()).child(AppFirebaseVariables.request_type),
                                };
                                SuperVariables._AppFirebase.remove(references, null);

                                currentState = cs_friends;
                                ToggleButtons(true, "Unfriend", false, View.INVISIBLE);
                                spotDialog.dismiss();
                            }
                        }
                    });
                } else if (currentState.equals(cs_friends)) {
                    List<String> deleteValues = new ArrayList<>();
                    deleteValues.add(AppFirebaseVariables.friends + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + profileUserId);
                    deleteValues.add(AppFirebaseVariables.friends + "/" + profileUserId + "/" + SuperVariables._AppFirebase.getCurrenctUserId());

                    SuperVariables._AppFirebase.delete(deleteValues, new CallbackFirebaseBoolean() {
                        @Override
                        public void onCallback(boolean isCompleted) {
                            if (isCompleted) {
                                currentState = cs_not_friends;
                                ToggleButtons(true, "Send Friend Request", false, View.INVISIBLE);
                                spotDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

        declineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference[] references = new DatabaseReference[] {
                        SuperVariables._AppFirebase.getFriendRequestDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId()).child(profileUserId).child(AppFirebaseVariables.request_type),
                        SuperVariables._AppFirebase.getFriendRequestDatabase().child(profileUserId).child(SuperVariables._AppFirebase.getCurrenctUserId()).child(AppFirebaseVariables.request_type),
                };

                SuperVariables._AppFirebase.remove(references, new CallbackFirebaseBoolean() {
                    @Override
                    public void onCallback(boolean isCompleted) {
                        currentState = cs_not_friends;
                        ToggleButtons(true, "Send Friend Request", false, View.INVISIBLE);
                        spotDialog.dismiss();
                    }
                });
            }
        });

    }

    public void ToggleButtons(boolean send, String sendText, boolean decline, int declineFlag) {
        sendFriendRequestButton.setEnabled(send);
        sendFriendRequestButton.setText(sendText);

        declineFriendRequestButton.setVisibility(declineFlag);
        declineFriendRequestButton.setEnabled(decline);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Program
        Program(view);

        return view;
    }
}
