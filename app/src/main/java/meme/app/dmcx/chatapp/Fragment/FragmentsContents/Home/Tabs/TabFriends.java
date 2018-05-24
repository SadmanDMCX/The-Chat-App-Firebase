package meme.app.dmcx.chatapp.Fragment.FragmentsContents.Home.Tabs;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import meme.app.dmcx.chatapp.Activities.Super.SuperMethods;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseDataSnapshot;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentAdapter;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.Fragment.Fragments.Chat;
import meme.app.dmcx.chatapp.Fragment.Fragments.Profile;
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Chat.ChatMethods;
import meme.app.dmcx.chatapp.Models.FriendsModel;
import meme.app.dmcx.chatapp.R;

public class TabFriends extends Fragment {

    // Class
    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void Set(String url, String name, String status, boolean online) {
            CircleImageView displayCircleImageView = itemView.findViewById(R.id.displayCircleImageView);
            ImageView displayOnlineImageView = itemView.findViewById(R.id.displayOnlineImageView);
            TextView displayNameTextView = itemView.findViewById(R.id.displayNameTextView);
            TextView displayStatusTextView = itemView.findViewById(R.id.displayStatusTextView);

            if (!(url == null || url.isEmpty() || url.equals("default"))) {
                Picasso.with(SuperVariables._MainActivity).load(url).placeholder(R.drawable.default_avater).into(displayCircleImageView);
            }
            displayNameTextView.setText(name);
            displayStatusTextView.setText(status);

            if (online)
                displayOnlineImageView.setVisibility(View.VISIBLE);
            else
                displayOnlineImageView.setVisibility(View.INVISIBLE);
        }
    }

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private RecyclerView friendseRecyclerView;

    private String friendUserId;

    // Program
    private void Program(View view) {

        // Initializa
        friendseRecyclerView = view.findViewById(R.id.friendseRecyclerView);

        // Program
        friendseRecyclerView.setHasFixedSize(true);
        friendseRecyclerView.setLayoutManager(new LinearLayoutManager(SuperVariables._MainActivity));

        DatabaseReference reference = SuperVariables._AppFirebase.getFriendsDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId());
//        reference.keepSynced(true);
        FirebaseRecyclerOptions firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<FriendsModel>()
                .setQuery(reference, FriendsModel.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendsModel, FriendsViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(final FriendsViewHolder holder, int position, FriendsModel model) {
                // holder.Set(model.getDate());

                friendUserId = getRef(position).getKey();
                DatabaseReference reference = SuperVariables._AppFirebase.getUsersDatabase().child(friendUserId);
//                reference.keepSynced(true);
                SuperVariables._AppFirebase.retrive(false, reference, new CallbackFirebaseDataSnapshot() {
                    @Override
                    public void onCallback(DataSnapshot dataSnapshot) {

                        if (dataSnapshot != null) {
                            String url = Objects.requireNonNull(dataSnapshot.child(AppFirebaseVariables.uthumbimage).getValue()).toString();
                            String name = Objects.requireNonNull(dataSnapshot.child(AppFirebaseVariables.uname).getValue()).toString();
                            String status = Objects.requireNonNull(dataSnapshot.child(AppFirebaseVariables.ustatus).getValue()).toString();
                            boolean online = (Boolean) dataSnapshot.child(AppFirebaseVariables.uonline).getValue();
                            holder.Set(url, name, status, online);
                        }
                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence[] options = new CharSequence[] {"Open Profile", "Send Message"};

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                        alertBuilder.setTitle("Select Options");
                        alertBuilder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        SuperVariables._AppFirebase.setProfileUserId(friendUserId);
                                        AppFragmentAdapter.AppFragment(new Profile(), AppFragmentTag.TAG_PROFILE, AppFragmentAdapter.FRAGMENT_REPLACE);
                                        SuperMethods.ToggleToolbar(true, null, false);
                                        break;
                                    case 1:
                                        SuperVariables._AppFirebase.setProfileUserId(friendUserId);
                                        AppFragmentAdapter.AppFragment(new Chat(), AppFragmentTag.TAG_CHAT, AppFragmentAdapter.FRAGMENT_REPLACE);
                                        SuperMethods.ToggleToolbar(false, null, true);
                                        ChatMethods.CustomChatActionBar();
                                        break;
                                }
                            }
                        });

                        alertBuilder.show();
                    }
                });
            }

            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zapp_layout_single_user_card_view, parent, false);
                return new FriendsViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        firebaseRecyclerAdapter.notifyDataSetChanged();
        friendseRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_tab_friends_layout, container, false);
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
