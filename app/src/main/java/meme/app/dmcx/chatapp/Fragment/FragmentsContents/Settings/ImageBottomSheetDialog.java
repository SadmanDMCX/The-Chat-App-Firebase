package meme.app.dmcx.chatapp.Fragment.FragmentsContents.Settings;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import meme.app.dmcx.chatapp.Activities.Super.SuperVariables;
import meme.app.dmcx.chatapp.Firebase.CallbackFirebaseBoolean;
import meme.app.dmcx.chatapp.R;

import static android.app.Activity.RESULT_OK;

public class ImageBottomSheetDialog extends BottomSheetDialogFragment {

    // Final
    private final String GALLERY_TITLE = "IMAGE SELECTION";
    private final int GALLERY_PICK_ID = 551;
    private final int READ_WRITE_REQUEST_CODE = 991;
    private final int CAMERA_REQUEST_CODE = 992;
    private final int CAMERA_CODE = 661;

    // Variables
    private Button takePhotoButton;
    private Button openGalleryButton;
    private Button chooseImageButton;

    // Methods
    private void Program(View view) {

        // Initialize
        takePhotoButton = view.findViewById(R.id.takePhotoButton);
        openGalleryButton = view.findViewById(R.id.openGalleryButton);
        chooseImageButton = view.findViewById(R.id.chooseImageButton);

        // Events
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(SuperVariables._MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(SuperVariables._MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(SuperVariables._MainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA }, CAMERA_REQUEST_CODE);
                } else {
                    ChoosePictureActivity();
                }
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(SuperVariables._MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(SuperVariables._MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(SuperVariables._MainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA }, CAMERA_REQUEST_CODE);
                } else {
                    TakePictureActivity();
                }
            }
        });

        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    OpenImageActivity();
                }
            }
        });

    }

    private void ChoosePictureActivity() {
        CropImage.activity()
                .setAspectRatio(1, 1)
                .setMaxCropResultSize(500, 500)
                .start(SuperVariables._MainActivity, this);
    }

    private void TakePictureActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(SuperVariables._MainActivity.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_CODE);
        }
    }

    private void OpenImageActivity() {
        Intent galleryImage = new Intent();
        galleryImage.setType("image/*");
        galleryImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryImage, GALLERY_TITLE), GALLERY_PICK_ID);
    }

    public File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mFileTemp = null;
        String root=SuperVariables._MainActivity.getDir("my_sub_dir", Context.MODE_PRIVATE).getAbsolutePath();
        File myDir = new File(root + "/Img");
        if(!myDir.exists()){
            myDir.mkdirs();
        }
        try {
            mFileTemp=File.createTempFile(imageFileName,".jpg",myDir.getAbsoluteFile());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return mFileTemp;
    }

    //
    private boolean checkPermission() {
        if (
            ActivityCompat.checkSelfPermission(SuperVariables._MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(SuperVariables._MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
            requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, READ_WRITE_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_change_image_bottom_sheet_layout, container, false);
        Program(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK_ID && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMaxCropResultSize(800, 800)
                    .start(SuperVariables._MainActivity, this);
        }

        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            File file = createImageFile();
            if (file != null) {
                FileOutputStream fout;
                try {
                    fout = new FileOutputStream(file);
                    image.compress(Bitmap.CompressFormat.PNG, 70, fout);
                    fout.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Uri imageUri = Uri.fromFile(file);
            if (imageUri != null) {
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .setMaxCropResultSize(800, 800)
                        .start(SuperVariables._MainActivity, this);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d(SuperVariables.APPTAG, "onActivityResult: Start cropping...");

            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final AlertDialog spotsDialog = new SpotsDialog(SuperVariables._MainActivity, "Please wait...");
                spotsDialog.show();

                Uri imageUri = activityResult.getUri();
                File thumbFilePath = new File(imageUri.getPath());
                Bitmap thumbBitmap = null;
                if (checkPermission()) {
                    try {
                        thumbBitmap = new Compressor(SuperVariables._MainActivity)
                                .setMaxWidth(200)
                                .setMaxHeight(200)
                                .setQuality(75)
                                .compressToBitmap(thumbFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] thumbByteBitmap = byteArrayOutputStream.toByteArray();

                    SuperVariables._AppFirebase.StoreImageFile(thumbByteBitmap, imageUri, new CallbackFirebaseBoolean() {
                        @Override
                        public void onCallback(boolean isCompleted) {
                            if (isCompleted) {
                                Toast.makeText(SuperVariables._MainActivity, "Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SuperVariables._MainActivity, "Error! Failed to upload!", Toast.LENGTH_SHORT).show();
                            }
                            spotsDialog.dismiss();
                            ImageBottomSheetDialog.this.dismiss();
                        }
                    });

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = activityResult.getError();
                Log.d(SuperVariables.APPTAG, "onActivityResult: " + exception.getMessage());
            }
        } else {
            Log.d(SuperVariables.APPTAG, "onActivityResult: Cropping failed.");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(SuperVariables._MainActivity);
            builder.setTitle("Permission")
                    .setCancelable(false)
                    .setMessage("Need permission to get the image from the storage. We don't access any of your private data. Be safe stay safe.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkPermission();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            if (requestCode == READ_WRITE_REQUEST_CODE)
                OpenImageActivity();
            else if (requestCode == CAMERA_REQUEST_CODE)
                TakePictureActivity();
        }
    }
}
