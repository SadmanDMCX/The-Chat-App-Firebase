package meme.app.dmcx.chatapp.Firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;

public class AppFirebase {

    private FirebaseAuth mAuth;
    private StorageReference mStorageReference;

    private DatabaseReference mRootDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mFriendsDatabaseReference;
    private DatabaseReference mFriendRequestDatabaseReference;
    private DatabaseReference mNotificationtDatabaseReference;
    private DatabaseReference mChatsDatabaseReference;
    private DatabaseReference mMessagesDatabaseReference;

    private FirebaseUser mCurrentAuthUser;
    private String mProfileUserId = null;

    public AppFirebase() {
        mAuth = FirebaseAuth.getInstance();

        mStorageReference = FirebaseStorage.getInstance().getReference();

        mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mUsersDatabaseReference = mRootDatabaseReference.child(AppFirebaseVariables.users);
        mFriendRequestDatabaseReference = mRootDatabaseReference.child(AppFirebaseVariables.friend_requests);
        mNotificationtDatabaseReference = mRootDatabaseReference.child(AppFirebaseVariables.notifications);
        mFriendsDatabaseReference = mRootDatabaseReference.child(AppFirebaseVariables.friends);
        mChatsDatabaseReference = mRootDatabaseReference.child(AppFirebaseVariables.chats);
        mMessagesDatabaseReference = mRootDatabaseReference.child(AppFirebaseVariables.messages);
    }

    // Storage
    public StorageReference getStorageReference() {
        return mStorageReference;
    }

    // Root
    public DatabaseReference getRootDatabase() {
        return mRootDatabaseReference;
    }

    // User
    public DatabaseReference getUsersDatabase() {
        return mUsersDatabaseReference;
    }

    // Friend Request
    public DatabaseReference getFriendRequestDatabase() {
        return mFriendRequestDatabaseReference;
    }

    // Notification
    public DatabaseReference getNotificationsDatabase() {
        return mNotificationtDatabaseReference;
    }

    // Friend
    public DatabaseReference getFriendsDatabase() {
        return mFriendsDatabaseReference;
    }

    // Chats
    public DatabaseReference getChatsDatabase() {
        return mChatsDatabaseReference;
    }

    // Messages
    public DatabaseReference getMessagesDatabase() {
        return mMessagesDatabaseReference;
    }

    // Profile User Id
    public void setProfileUserId(String key) {
        mProfileUserId = key;
    }
    public String getProfileUserId() {
        return mProfileUserId;
    }

    // Current User Id
    public String getCurrenctUserId() {
        if (mAuth.getCurrentUser() == null)
            return null;
        return mAuth.getCurrentUser().getUid();
    }

    // Notification Id
    public String getPushId(DatabaseReference reference) {
        return reference.push().getKey();
    }

    // If Current User Exists
    public boolean getCurrentUser() {
        mCurrentAuthUser = mAuth.getCurrentUser();
        return mCurrentAuthUser != null;
    }

    // Sign Up
    public void SignUp(final String email, final String password, final CallbackFirebaseBoolean callback) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    callback.onCallback(true);
                    Log.d(SuperVariables.APPTAG, "onComplete: True");
                } else {
                    Log.d(SuperVariables.APPTAG, "onComplete: False");
                    Log.d(SuperVariables.APPTAG, "onComplete: " + Objects.requireNonNull(task.getException()).getMessage());

                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthInvalidCredentialsException ignored) {
                        Toast.makeText(SuperVariables._MainActivity, "Error! Invlid Email!", Toast.LENGTH_SHORT).show();
                        callback.onCallback(false);
                    } catch (FirebaseAuthUserCollisionException  ignored) {
                        Toast.makeText(SuperVariables._MainActivity, "Error! User already exist!", Toast.LENGTH_SHORT).show();
                        callback.onCallback(false);
                    } catch (Exception ex) {
                        Toast.makeText(SuperVariables._MainActivity, "Error! Unknown Failed!", Toast.LENGTH_SHORT).show();
                        callback.onCallback(false);
                    }

                }
            }
        });
    }

    // Sign Out
    public void SignOut() {
        FirebaseAuth.getInstance().signOut();
    }

    // Sign In
    public void SignIn(String email, String password, final CallbackFirebaseBoolean callback) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (getCurrentUser()) {
                    if (task.isSuccessful()) {
                        mUsersDatabaseReference.child(mCurrentAuthUser.getUid()).child(AppFirebaseVariables.udevicetokenid).setValue(AppFirebaseMethods.getDeviceTokenId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    callback.onCallback(true);
                                } else {
                                    callback.onCallback(false);
                                    Toast.makeText(SuperVariables._MainActivity, "Error! Sign in failed. Token not inserted.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        callback.onCallback(false);
                        Toast.makeText(SuperVariables._MainActivity, "Error! Sign in failed.", Toast.LENGTH_SHORT).show();
                        Log.d(SuperVariables.APPTAG, "onComplete: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                } else {
                    callback.onCallback(false);
                    Toast.makeText(SuperVariables._MainActivity, "Error! Sign in failed.", Toast.LENGTH_SHORT).show();
                    Log.d(SuperVariables.APPTAG, "onComplete: " + Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }

    // Store Image File
    public void StoreImageFile(final byte[] thumbByteBitmap, final Uri imageUri, final CallbackFirebaseBoolean callback) {
        if (getCurrentUser()) {
            StorageReference filePath = mStorageReference.child(AppFirebaseVariables.profileimages).child(mCurrentAuthUser.getUid() + ".jpg");
            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        final String imageUrl = Objects.requireNonNull(task.getResult().getDownloadUrl()).toString();
                        StorageReference thumbFilePath = mStorageReference.child(AppFirebaseVariables.profileimages).child(AppFirebaseVariables.profileimages_thumb).child(mCurrentAuthUser.getUid() + ".jpg");

                        UploadTask uploadTask = thumbFilePath.putBytes(thumbByteBitmap);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                final String thumbDownloadUrl = Objects.requireNonNull(task.getResult().getDownloadUrl()).toString();

                                if (task.isSuccessful()) {

                                    Map requestMap = new HashMap<>();
                                    requestMap.put(AppFirebaseVariables.users + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + AppFirebaseVariables.uimage, imageUrl);
                                    requestMap.put(AppFirebaseVariables.users + "/" + SuperVariables._AppFirebase.getCurrenctUserId() + "/" + AppFirebaseVariables.uthumbimage, thumbDownloadUrl);

                                    SuperVariables._AppFirebase.store(requestMap, new CallbackFirebaseBoolean() {
                                        @Override
                                        public void onCallback(boolean isCompleted) {
                                            if (isCompleted) {
                                                callback.onCallback(true);
                                            } else {
                                                Toast.makeText(SuperVariables._MainActivity, "Error! File not uploaded successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(SuperVariables._MainActivity, "Error! Thumbnail not uploaded!", Toast.LENGTH_SHORT).show();
                                    callback.onCallback(false);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(SuperVariables._MainActivity, "Error! Image not uploaded!", Toast.LENGTH_SHORT).show();
                        callback.onCallback(false);
                    }
                }
            });

        } else {
            callback.onCallback(false);
            Log.d(SuperVariables.APPTAG, "StoreImageFile: User not active!");
        }
    }

    // CRUD
    public void store(Map requestMap, final CallbackFirebaseBoolean callback) {
        mRootDatabaseReference.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(SuperVariables._MainActivity, "Error! Some error found while inserting data!", Toast.LENGTH_SHORT);
                    callback.onCallback(false);
                } else {
                    callback.onCallback(true);
                }
            }
        });
    }

    public void remove(DatabaseReference[] references, final CallbackFirebaseBoolean callback) {
        for (DatabaseReference reference : references) {
            reference.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(SuperVariables._MainActivity, "Error! Some error found while inserting data!", Toast.LENGTH_SHORT);
                        if (callback != null)
                            callback.onCallback(false);
                    } else {
                        if (callback != null)
                            callback.onCallback(true);
                    }
                }
            });
        }
    }

    public void delete(List<String> items, final CallbackFirebaseBoolean callback) {
        Map requestMap = new HashMap();
        for (String item : items) {
            requestMap.put(item, null);
        }

        store(requestMap, callback);
    }

    public void retrive(boolean isSingleValueEventListener, DatabaseReference reference, final CallbackFirebaseDataSnapshot callback) {
        // Multi = false
        // Single = true

        if (reference != null) {
            if (!isSingleValueEventListener) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        callback.onCallback(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(SuperVariables.APPTAG, "onCancelled: Retrive " + databaseError.getMessage());
                        callback.onCallback(null);
                    }
                });
            } else {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        callback.onCallback(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(SuperVariables.APPTAG, "onCancelled: Retrive " + databaseError.getMessage());
                        callback.onCallback(null);
                    }
                });
            }
        } else {
            Toast.makeText(SuperVariables._MainActivity, "Error! No reference given!", Toast.LENGTH_SHORT);
            callback.onCallback(null);
        }

    }

}
