package meme.app.dmcx.chatapp.Fragment.Fragments;

import android.os.Bundle;
import android.os.UserManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
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
import meme.app.dmcx.chatapp.Fragment.FragmentsContents.Chat.ChatMethods;
import meme.app.dmcx.chatapp.Models.UsersModel;
import meme.app.dmcx.chatapp.R;

public class Search extends Fragment {

    private class SearchViewHolder extends RecyclerView.ViewHolder {

        public View mView;

        public SearchViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void Set(String name, String url, String status) {
            CircleImageView displayCircleImageView = mView.findViewById(R.id.displayCircleImageView);
            TextView displayNameTextView = mView.findViewById(R.id.displayNameTextView);
            TextView displayStatusTextView = mView.findViewById(R.id.displayStatusTextView);

            if (!(url.equals("") && url.equals("default"))) {
                Picasso.with(getContext()).load(url).placeholder(R.drawable.default_avater).into(displayCircleImageView);
            }

            displayNameTextView.setText(name);
            displayStatusTextView.setText(status);
        }
    }

    private EditText searchEditText;
    private RecyclerView searchResultRecyclerView;

    private FirebaseRecyclerOptions firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private void Program(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);
        searchResultRecyclerView = view.findViewById(R.id.searchResultRecyclerView);

        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(SuperVariables._MainActivity));
        ((SimpleItemAnimator) searchResultRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        Query query = SuperVariables._AppFirebase.getUsersDatabase();
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<UsersModel>()
                .setQuery(query, UsersModel.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UsersModel, SearchViewHolder>(firebaseRecyclerOptions) {
            @Override
            public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_card_view, parent, false);
                return new SearchViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(SearchViewHolder holder, int position, UsersModel model) {

                holder.Set(model.getName(), model.getThumb_image(), model.getStatus());
                final String selectedUserKey = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnClickItemEvent(selectedUserKey);
                    }
                });

            }
        };

        searchResultRecyclerView.setAdapter(firebaseRecyclerAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!charSequence.equals("")) {
                    Query query = SuperVariables._AppFirebase.getUsersDatabase()
                            .orderByChild(AppFirebaseVariables.uname)
                            .startAt(String.valueOf(charSequence))
                            .endAt(String.valueOf(charSequence) + "\uf8ff");

                    firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<UsersModel>()
                            .setQuery(query, UsersModel.class).build();

                    firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UsersModel, SearchViewHolder>(firebaseRecyclerOptions) {
                        @Override
                        public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_card_view, parent, false);
                            return new SearchViewHolder(view);
                        }

                        @Override
                        protected void onBindViewHolder(SearchViewHolder holder, int position, UsersModel model) {

                            holder.Set(model.getName(), model.getThumb_image(), model.getStatus());
                            final String selectedUserKey = getRef(position).getKey();

                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    OnClickItemEvent(selectedUserKey);
                                }
                            });

                        }
                    };

                    firebaseRecyclerAdapter.startListening();
                    searchResultRecyclerView.setAdapter(firebaseRecyclerAdapter);

                } else {
                    searchResultRecyclerView.setAdapter(firebaseRecyclerAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void OnClickItemEvent(final String selectedUserKey) {
        DatabaseReference reference = SuperVariables._AppFirebase.getFriendsDatabase();
        SuperVariables._AppFirebase.retrive(false, reference, new CallbackFirebaseDataSnapshot() {
            @Override
            public void onCallback(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    boolean isKeyExists = dataSnapshot.hasChild(selectedUserKey);
                    if (isKeyExists) {
                        SuperVariables._AppFirebase.setProfileUserId(selectedUserKey);
                        AppFragmentAdapter.AppFragment(new Chat(), AppFragmentTag.TAG_CHAT, AppFragmentAdapter.FRAGMENT_REPLACE);
                        SuperMethods.ToggleToolbar(false, null, true);
                        ChatMethods.CustomChatActionBar();
                    } else {
                        SuperVariables._AppFirebase.setProfileUserId(selectedUserKey);
                        AppFragmentAdapter.AppFragment(new Profile(), AppFragmentTag.TAG_PROFILE, AppFragmentAdapter.FRAGMENT_REPLACE);
                        SuperMethods.ToggleToolbar(true, null, false);
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
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
