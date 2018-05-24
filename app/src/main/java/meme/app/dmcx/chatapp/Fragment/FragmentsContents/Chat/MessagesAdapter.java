package meme.app.dmcx.chatapp.Fragment.FragmentsContents.Chat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Firebase.AppFirebaseVariables;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseDataSnapshot;
import meme.app.dmcx.chatapp.Models.MessagesModel;
import meme.app.dmcx.chatapp.R;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    public class MessagesViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView friendImageCircularImageView;
        public LinearLayout messageLinearLayout;
        public TextView userNameTextView;
        public TextView messageTextView;
        public ImageView messageImageView;

        public MessagesViewHolder(View itemView) {
            super(itemView);

            friendImageCircularImageView = itemView.findViewById(R.id.friendImageCircularImageView);
            messageLinearLayout = itemView.findViewById(R.id.messageLinearLayout);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageImageView = itemView.findViewById(R.id.messageImageView);
        }
    }

    private List<MessagesModel> mMessageList;

    public MessagesAdapter(List<MessagesModel> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_single_layout, parent, false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessagesViewHolder holder, int position) {
        MessagesModel message = mMessageList.get(position);

        final String messageTextOrUrl = message.getMessage();
        String messageType = message.getType();

        if (messageType.equals(AppFirebaseVariables.mtype_text)) {
            holder.messageLinearLayout.setEnabled(true);
            holder.messageLinearLayout.setVisibility(View.VISIBLE);
            holder.messageTextView.setText(messageTextOrUrl);

            holder.messageImageView.setVisibility(View.GONE);
            holder.messageImageView.setEnabled(false);

            if (SuperVariables._AppFirebase.getCurrenctUserId().equals(message.getFrom())) {
                holder.messageLinearLayout.setBackgroundResource(R.drawable.message_text_background_secondry);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.messageLinearLayout.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.removeRule(RelativeLayout.RIGHT_OF);
                holder.messageLinearLayout.setLayoutParams(params);

                holder.friendImageCircularImageView.setVisibility(View.INVISIBLE);

                holder.userNameTextView.setTextColor(Color.parseColor("#212121"));
                holder.messageTextView.setTextColor(Color.parseColor("#212121"));

            } else {
                holder.messageLinearLayout.setBackgroundResource(R.drawable.message_text_background_primary);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.messageLinearLayout.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, R.id.friendImageCircularImageView);
                params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.messageLinearLayout.setLayoutParams(params);

                holder.friendImageCircularImageView.setVisibility(View.VISIBLE);

                holder.userNameTextView.setTextColor(Color.parseColor("#ffffff"));
                holder.messageTextView.setTextColor(Color.parseColor("#ffffff"));

                DatabaseReference reference = SuperVariables._AppFirebase.getUsersDatabase().child(message.getFrom());
                SuperVariables._AppFirebase.retrive(true, reference, new CallbackFirebaseDataSnapshot() {
                    @Override
                    public void onCallback(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            String name = dataSnapshot.child(AppFirebaseVariables.uname).getValue().toString();
                            String url = dataSnapshot.child(AppFirebaseVariables.uthumbimage).getValue().toString();

                            holder.userNameTextView.setText(name);
                            if (!url.isEmpty() && !url.equals("default")) {
                                Picasso.with(SuperVariables._MainActivity).load(url).placeholder(R.drawable.info).into(holder.friendImageCircularImageView);
                            }

                        }
                    }
                });
            }
        } else {
            holder.messageImageView.setEnabled(true);
            holder.messageImageView.setVisibility(View.VISIBLE);
            Picasso.with(SuperVariables._MainActivity).load(messageTextOrUrl).placeholder(R.drawable.default_avater).into(holder.messageImageView);

            holder.messageLinearLayout.setEnabled(false);
            holder.messageLinearLayout.setVisibility(View.INVISIBLE);

            holder.messageImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(messageTextOrUrl), "image/*");
                    SuperVariables._MainActivity.startActivity(intent);
                }
            });

            if (SuperVariables._AppFirebase.getCurrenctUserId().equals(message.getFrom())) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.messageImageView.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.removeRule(RelativeLayout.RIGHT_OF);
                holder.messageImageView.setLayoutParams(params);

                holder.friendImageCircularImageView.setVisibility(View.INVISIBLE);
            } else {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.messageImageView.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, R.id.friendImageCircularImageView);
                params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.messageImageView.setLayoutParams(params);

                holder.friendImageCircularImageView.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}
