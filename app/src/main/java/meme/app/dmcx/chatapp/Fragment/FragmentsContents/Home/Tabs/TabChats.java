package meme.app.dmcx.chatapp.Fragment.FragmentsContents.Home.Tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import meme.app.dmcx.chatapp.Activities.Super.SuperMethods;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseDataSnapshot;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentAdapter;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.Fragment.Fragments.Chat;
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Chat.ChatMethods;
import meme.app.dmcx.chatapp.Models.MessagesModel;
import meme.app.dmcx.chatapp.R;

public class TabChats extends Fragment {

    private class ChatsViewHolder extends RecyclerView.ViewHolder {

        public View mView;

        public ChatsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void Set(String url, String name, boolean online) {
            CircleImageView displayCircleImageView = itemView.findViewById(R.id.displayCircleImageView);
            ImageView displayOnlineImageView = itemView.findViewById(R.id.displayOnlineImageView);
            TextView displayNameTextView = itemView.findViewById(R.id.displayNameTextView);

            if (!(url == null || url.isEmpty() || url.equals("default"))) {
                Picasso.with(SuperVariables._MainActivity).load(url).placeholder(R.drawable.default_avater).into(displayCircleImageView);
            }
            displayNameTextView.setText(name);

            if (online)
                displayOnlineImageView.setVisibility(View.VISIBLE);
            else
                displayOnlineImageView.setVisibility(View.INVISIBLE);
        }

        public void setMessage(String lastChatMessage) {
            TextView displayStatusTextView = itemView.findViewById(R.id.displayStatusTextView);
            displayStatusTextView.setText(lastChatMessage);
        }
    }

    private RecyclerView chatListRecyclerView;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private String currentUserId;
    private String friendUserId;

    private String chatMessage;
    private String chatType;

    private void Program(View view) {

        chatListRecyclerView = view.findViewById(R.id.chatListRecyclerView);

        currentUserId = SuperVariables._AppFirebase.getCurrenctUserId();

        DatabaseReference reference = SuperVariables._AppFirebase.getMessagesDatabase().child(currentUserId);
        FirebaseRecyclerOptions firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<MessagesModel>()
                .setQuery(reference, MessagesModel.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MessagesModel, ChatsViewHolder>(firebaseRecyclerOptions) {

            @Override
            public ChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zapp_layout_single_user_card_view, parent, false);
                return new ChatsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final ChatsViewHolder holder, int position, MessagesModel model) {

                friendUserId = getRef(position).getKey();
                final ChatsViewHolder chatHolder = holder;
                SuperVariables._AppFirebase.retrive(true, SuperVariables._AppFirebase.getUsersDatabase().child(friendUserId), new CallbackFirebaseDataSnapshot() {
                    @Override
                    public void onCallback(DataSnapshot dataSnapshot) {

                        if (dataSnapshot != null) {

                            String url = dataSnapshot.child(AppFirebaseVariables.uthumbimage).getValue().toString();
                            String name = dataSnapshot.child(AppFirebaseVariables.uname).getValue().toString();
                            boolean online = (Boolean) dataSnapshot.child(AppFirebaseVariables.uonline).getValue();

                            Query ref = SuperVariables._AppFirebase.getMessagesDatabase()
                                            .child(currentUserId)
                                            .child(friendUserId).limitToLast(1);
                            ref.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot != null) {
                                        chatMessage = dataSnapshot.child(AppFirebaseVariables.mmessage).getValue().toString();
                                        chatType = dataSnapshot.child(AppFirebaseVariables.mtype).getValue().toString();

                                        if (chatType.equals(AppFirebaseVariables.mtype_text)) {
                                            holder.setMessage(chatMessage);
                                        } else if (chatType.equals(AppFirebaseVariables.mtype_image)) {
                                            holder.setMessage("Photo");
                                        }
                                    }
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

                            chatHolder.Set(url, name, online);

                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    SuperVariables._AppFirebase.setProfileUserId(friendUserId);
                                    AppFragmentAdapter.AppFragment(new Chat(), AppFragmentTag.TAG_CHAT, AppFragmentAdapter.FRAGMENT_REPLACE);
                                    SuperMethods.ToggleToolbar(false, null, true);
                                    ChatMethods.CustomChatActionBar();
                                }
                            });

                        }

                    }
                });
            }
        };

        chatListRecyclerView.setHasFixedSize(true);
        chatListRecyclerView.setLayoutManager(new LinearLayoutManager(SuperVariables._MainActivity));
        chatListRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_tab_chats_layout, container, false);
        Program(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        firebaseRecyclerAdapter.stopListening();
    }
}
