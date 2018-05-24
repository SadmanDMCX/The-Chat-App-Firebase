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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import meme.app.dmcx.chatapp.Activities.Super.SuperMethods;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseDataSnapshot;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentAdapter;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.Fragment.Fragments.Profile;
import meme.app.dmcx.chatapp.Models.FriendRequestModel;
import meme.app.dmcx.chatapp.R;

public class TabRequests extends Fragment {

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        public View mVIew;

        public FriendRequestViewHolder(View itemView) {
            super(itemView);

            mVIew = itemView;
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

    private RecyclerView friendRequestListRecyclerView;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private String requestUserId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_tab_requests_layout, container, false);

        friendRequestListRecyclerView = view.findViewById(R.id.friendRequestListRecyclerView);

        FirebaseRecyclerOptions firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<FriendRequestModel>()
                .setQuery(SuperVariables._AppFirebase.getFriendRequestDatabase().child(SuperVariables._AppFirebase.getCurrenctUserId()), FriendRequestModel.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendRequestModel, FriendRequestViewHolder>(firebaseRecyclerOptions) {

            @Override
            public FriendRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zapp_layout_single_user_card_view, parent, false);
                return new FriendRequestViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final FriendRequestViewHolder holder, int position, FriendRequestModel model) {

                requestUserId = getRef(position).getKey();
                DatabaseReference reference = SuperVariables._AppFirebase.getUsersDatabase().child(requestUserId);
                SuperVariables._AppFirebase.retrive(false, reference, new CallbackFirebaseDataSnapshot() {
                    @Override
                    public void onCallback(DataSnapshot dataSnapshot) {

                        if (dataSnapshot != null) {
                            String url  = dataSnapshot.child(AppFirebaseVariables.uthumbimage).getValue().toString();
                            String name = dataSnapshot.child(AppFirebaseVariables.uname).getValue().toString();
                            String status = dataSnapshot.child(AppFirebaseVariables.ustatus).getValue().toString();
//                            boolean online = (Boolean) dataSnapshot.child(AppFirebaseVariables.uonline).getValue();

                            holder.Set(url, name, status, false);
                        }
                    }
                });

                holder.mVIew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SuperVariables._AppFirebase.setProfileUserId(requestUserId);
                        AppFragmentAdapter.AppFragment(new Profile(), AppFragmentTag.TAG_PROFILE, AppFragmentAdapter.FRAGMENT_REPLACE);
                        SuperMethods.ToggleToolbar(true, null, false);
                    }
                });
            }
;        };

        friendRequestListRecyclerView.setHasFixedSize(true);
        friendRequestListRecyclerView.setLayoutManager(new LinearLayoutManager(SuperVariables._MainActivity));
        friendRequestListRecyclerView.setAdapter(firebaseRecyclerAdapter);

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
