package meme.app.dmcx.chatapp.Fragment.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Common.TimeSince;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseBoolean;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseDataSnapshot;
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Chat.ChatMethods;
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Chat.MessagesAdapter;
import meme.app.dmcx.chatapp.Models.MessagesModel;
import meme.app.dmcx.chatapp.R;

import static android.app.Activity.RESULT_OK;

public class Chat extends Fragment {

    // Final
    private static final int TOTAL_NUM_OF_MESSAGE_TO_LOAD = 2;
    private static final int GALLARY_INTENT_REQUEST_CODE = 771;

    // Variables
    private ImageView addFileImageView;
    private EditText messageEdiText;
    private ImageView sendMessageImageView;
    private RecyclerView conversationRecyclerView;
    private SwipeRefreshLayout messageSwipeRefreshLayout;

    private String friendUserId;
    private String currentUserId;
    private final List<MessagesModel> messagesModelList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter conversationMessagesAdapter;
    private int mCurrentPage = 1;
    private int itemPosition = 0;

    private String mFirstMessageKey;
    private String mLastMessageKey;
    private String mPrevMessageKey;

    // Methods
    private void Programs(View view) {

        // Initializa
        messageSwipeRefreshLayout = view.findViewById(R.id.messageSwipeRefreshLayout);
        addFileImageView = view.findViewById(R.id.addFileImageView);
        messageEdiText = view.findViewById(R.id.messageEdiText);
        sendMessageImageView = view.findViewById(R.id.sendMessageImageView);
        conversationRecyclerView = view.findViewById(R.id.conversationRecyclerView);
        conversationRecyclerView = view.findViewById(R.id.conversationRecyclerView);

        linearLayoutManager = new LinearLayoutManager(getContext());
        conversationMessagesAdapter = new MessagesAdapter(messagesModelList);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setAdapter(conversationMessagesAdapter);

        friendUserId = SuperVariables._AppFirebase.getProfileUserId();
        currentUserId = SuperVariables._AppFirebase.getCurrenctUserId();

        // Program
        DatabaseReference reference = SuperVariables._AppFirebase.getUsersDatabase().child(friendUserId);
        SuperVariables._AppFirebase.retrive(true, reference, new CallbackFirebaseDataSnapshot() {
            @Override
            public void onCallback(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    String name = dataSnapshot.child(AppFirebaseVariables.uname).getValue().toString();
                    boolean online = (Boolean) dataSnapshot.child(AppFirebaseVariables.uonline).getValue();
                    String lastseen = dataSnapshot.child(AppFirebaseVariables.ulastseen).getValue().toString();

                    if (online) {
                        ChatMethods.setUserActivityStatusTextViewActionBar("Online");
                    } else {
                        ChatMethods.setUserActivityStatusTextViewActionBar(TimeSince.getTimeAgo(Long.parseLong(lastseen)));
                    }

                    ChatMethods.setDisplayNameTextViewActionBar(name);
                    ChatMethods.setUserProfileCirculerImageViewActionBar();
                }
            }
        });

        DatabaseReference chatReference = SuperVariables._AppFirebase.getRootDatabase().child(AppFirebaseVariables.chats).child(currentUserId);
        SuperVariables._AppFirebase.retrive(false, chatReference, new CallbackFirebaseDataSnapshot() {
            @Override
            public void onCallback(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Map requestMapUserActivity = new HashMap();
                    requestMapUserActivity.put(AppFirebaseVariables.chats_seen, false);
//                    requestMapUserActivity.put(AppFirebaseVariables.chats_timestamp, ServerValue.TIMESTAMP);

                    Map requestMapUser = new HashMap();
                    requestMapUser.put(AppFirebaseVariables.chats + "/" + currentUserId + "/" + friendUserId, requestMapUserActivity);
                    requestMapUser.put(AppFirebaseVariables.chats + "/" + friendUserId + "/" + currentUserId, requestMapUserActivity);

                    SuperVariables._AppFirebase.store(requestMapUser, new CallbackFirebaseBoolean() {
                        @Override
                        public void onCallback(boolean isCompleted) {
                            if (!isCompleted) {
                                Log.d(SuperVariables.APPTAG, "onCallback: Error Found!");
                            }
                        }
                    });
                }
            }
        });

        messageSwipeRefreshLayout.setEnabled(false);

        LoadMessages();

        // Events
        sendMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });

        messageSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                itemPosition = 0;
                mCurrentPage++;
                LoadMoreMessages();
            }
        });

        addFileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLARY_INTENT_REQUEST_CODE);
            }
        });
    }

    private void LoadMoreMessages() {
        SuperVariables._AppFirebase.getMessagesDatabase().child(currentUserId).child(friendUserId).limitToFirst(1).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null)
                    mFirstMessageKey = dataSnapshot.getKey();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        SuperVariables._AppFirebase.getMessagesDatabase()
                .child(currentUserId)
                .child(friendUserId)
                .orderByKey()
                .endAt(mLastMessageKey)
                .limitToLast(mCurrentPage * TOTAL_NUM_OF_MESSAGE_TO_LOAD)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if (dataSnapshot != null) {
                            if (mFirstMessageKey.equals(mLastMessageKey)) {
                                messageSwipeRefreshLayout.setEnabled(false);
                            }

                            MessagesModel model = dataSnapshot.getValue(MessagesModel.class);
                            String messageKey = dataSnapshot.getKey();

                            if (!mPrevMessageKey.equals(messageKey)) {
                                messagesModelList.add(itemPosition++, model);
                            } else {
                                mPrevMessageKey = mLastMessageKey;
                            }

                            if (itemPosition == 1)
                                mLastMessageKey = messageKey;

                            conversationMessagesAdapter.notifyDataSetChanged();
                            linearLayoutManager.scrollToPositionWithOffset(TOTAL_NUM_OF_MESSAGE_TO_LOAD, 0);
                        }

                        messageSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        messageSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        messageSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        messageSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        messageSwipeRefreshLayout.setRefreshing(false);
                    }
                });

    }

    private void LoadMessages() {
        SuperVariables._AppFirebase.getMessagesDatabase()
                .child(currentUserId)
                .child(friendUserId)
                .limitToLast(mCurrentPage * TOTAL_NUM_OF_MESSAGE_TO_LOAD)
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    if (!messageSwipeRefreshLayout.isEnabled())
                        messageSwipeRefreshLayout.setEnabled(true);

                    MessagesModel model = dataSnapshot.getValue(MessagesModel.class);
                    itemPosition++;
                    if (itemPosition == 1) {
                        mLastMessageKey = dataSnapshot.getKey();
                        mPrevMessageKey = dataSnapshot.getKey();
                    }
                    messagesModelList.add(model);
                    conversationMessagesAdapter.notifyDataSetChanged();
                    conversationRecyclerView.scrollToPosition(messagesModelList.size() - 1);
                } else {
                    messageSwipeRefreshLayout.setEnabled(false);
                }


                messageSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                messageSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                messageSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                messageSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                messageSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void SendMessage() {
        String message = messageEdiText.getText().toString();
        messageEdiText.setText("");

        if (!TextUtils.isEmpty(message)) {
            String storePointCurrentUser = AppFirebaseVariables.messages + "/" + currentUserId + "/" + friendUserId + "/";
            String storePointFriend = AppFirebaseVariables.messages + "/" + friendUserId + "/" + currentUserId + "/";
            String messagesPushId = SuperVariables._AppFirebase.getPushId(SuperVariables._AppFirebase.getMessagesDatabase().child(currentUserId).child(friendUserId));

            String notificationPushId = SuperVariables._AppFirebase.getPushId(SuperVariables._AppFirebase.getNotificationsDatabase());

            HashMap<String, String> notificationData = new HashMap<>();
            notificationData.put(AppFirebaseVariables.notification_from, SuperVariables._AppFirebase.getCurrenctUserId());
            notificationData.put(AppFirebaseVariables.notification_type, AppFirebaseVariables.notification_type_chat);

            Map requestMapMessagesBody = new HashMap();
            requestMapMessagesBody.put(AppFirebaseVariables.mmessage, message);
            requestMapMessagesBody.put(AppFirebaseVariables.mseen, false);
            requestMapMessagesBody.put(AppFirebaseVariables.mtype, AppFirebaseVariables.mtype_text);
            requestMapMessagesBody.put(AppFirebaseVariables.mtimestamp, ServerValue.TIMESTAMP);
            requestMapMessagesBody.put(AppFirebaseVariables.mfrom, currentUserId);

            Map requestMap = new HashMap();
            requestMap.put(storePointCurrentUser + "/" + messagesPushId, requestMapMessagesBody);
            requestMap.put(storePointFriend + "/" + messagesPushId, requestMapMessagesBody);
            requestMap.put(AppFirebaseVariables.notifications + "/" + friendUserId + "/" + notificationPushId, notificationData);

            SuperVariables._AppFirebase.store(requestMap, new CallbackFirebaseBoolean() {
                @Override
                public void onCallback(boolean isCompleted) {
                    if (!isCompleted)
                        Log.d(SuperVariables.APPTAG, "onCallback: Error Found!");
                }
            });

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Programs(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_INTENT_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            final String storePointCurrentUser = AppFirebaseVariables.messages + "/" + currentUserId + "/" + friendUserId + "/";
            final String storePointFriend = AppFirebaseVariables.messages + "/" + friendUserId + "/" + currentUserId + "/";
            final String messagesPushId = SuperVariables._AppFirebase.getPushId(SuperVariables._AppFirebase.getMessagesDatabase().child(currentUserId).child(friendUserId));

            StorageReference storageReference = SuperVariables._AppFirebase.getStorageReference().child(AppFirebaseVariables.messageimages).child(messagesPushId + ".jpg");
            storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        String downloadUrl = task.getResult().getDownloadUrl().toString();

                        Map requestMapMessagesBody = new HashMap();
                        requestMapMessagesBody.put(AppFirebaseVariables.mmessage, downloadUrl);
                        requestMapMessagesBody.put(AppFirebaseVariables.mseen, false);
                        requestMapMessagesBody.put(AppFirebaseVariables.mtype, AppFirebaseVariables.mtype_image);
                        requestMapMessagesBody.put(AppFirebaseVariables.mtimestamp, ServerValue.TIMESTAMP);
                        requestMapMessagesBody.put(AppFirebaseVariables.mfrom, currentUserId);

                        Map requestMapMessages = new HashMap();
                        requestMapMessages.put(storePointCurrentUser + "/" + messagesPushId, requestMapMessagesBody);
                        requestMapMessages.put(storePointFriend + "/" + messagesPushId, requestMapMessagesBody);

                        SuperVariables._AppFirebase.store(requestMapMessages, new CallbackFirebaseBoolean() {
                            @Override
                            public void onCallback(boolean isCompleted) {
                                if (!isCompleted)
                                    Log.d(SuperVariables.APPTAG, "onCallback: Error Found!");
                            }
                        });

                    } else {
                        Toast.makeText(SuperVariables._MainActivity, "Error! Image not uploaded!", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }
}
