package meme.app.dmcx.chatapp.Fragment.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import meme.app.dmcx.chatapp.Activities.Super.SuperMethods;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentAdapter;
import meme.app.dmcx.chatapp.Fragment.FragmentManager.AppFragmentTag;
import meme.app.dmcx.chatapp.Models.UsersModel;
import meme.app.dmcx.chatapp.R;

public class Users extends Fragment {

    // Class
    public class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void Set(String url, String name, String status) {
            CircleImageView displayCircleImageView = itemView.findViewById(R.id.displayCircleImageView);
            TextView displayNameTextView = itemView.findViewById(R.id.displayNameTextView);
            TextView displayStatusTextView = itemView.findViewById(R.id.displayStatusTextView);

            if (!(url == null || url.isEmpty() || url.equals("default"))) {
                Picasso.with(SuperVariables._MainActivity).load(url).placeholder(R.drawable.default_avater).into(displayCircleImageView);
            }
            displayNameTextView.setText(name);
            displayStatusTextView.setText(status);
        }
    }

    // Variables
    private RecyclerView usersRecyclerView;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    // Methods
    private void Program(View view) {
        // Initializa
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView);

        // Program
        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(SuperVariables._MainActivity));

        final AlertDialog spotsDialog = new SpotsDialog(SuperVariables._MainActivity, "Please wait...");
        spotsDialog.show();

        FirebaseRecyclerOptions firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<UsersModel>()
                .setQuery(SuperVariables._AppFirebase.getUsersDatabase(), UsersModel.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UsersModel, UsersViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(UsersViewHolder holder, int position, UsersModel model) {
                holder.Set(model.getThumb_image(), model.getName(), model.getStatus());
                spotsDialog.dismiss();

                final int currentPosition = position;
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String user_key = getRef(currentPosition).getKey();
                        SuperVariables._AppFirebase.setProfileUserId(user_key);
                        AppFragmentAdapter.AppFragment(new Profile(), AppFragmentTag.TAG_PROFILE, AppFragmentAdapter.FRAGMENT_REPLACE);
                        SuperMethods.ToggleToolbar(true, null, false);
                    }
                });
            }

            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zapp_layout_single_user_card_view, parent, false);
                return new UsersViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        usersRecyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
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
