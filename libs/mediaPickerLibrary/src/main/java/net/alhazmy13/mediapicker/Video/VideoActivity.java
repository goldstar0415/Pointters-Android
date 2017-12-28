package net.alhazmy13.mediapicker.Video;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import net.alhazmy13.camerapicker.R;
import net.alhazmy13.mediapicker.Utility;
import net.alhazmy13.mediapicker.Video.VideoPicker.Extension;
import net.alhazmy13.mediapicker.Video.VideoPicker.Mode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alhazmy13 on 10/26/15.
 * MediaPicker
 */
public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";

    private final int CAMERA_REQUEST = 1878;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 133;
    private final int REQUEST_CODE_SELECT_VIDEO = 44;
    private final static int CAMERA_RQ = 6969;

    private File destination;
    private Extension extension;
    private Uri mVideoUri;
    private Mode mode;
    private String directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            extension = (Extension) intent.getSerializableExtra(VideoTags.EXTENSION);
            mode = (Mode) intent.getSerializableExtra(VideoTags.MODE);
            directory = intent.getStringExtra(VideoTags.DIRECTORY);
        }

        if (savedInstanceState == null) {
            pickImageWrapper();
        }
    }

    private void pickVideo() {
        Utility.createFolder(directory);
        destination = new File(directory, Utility.getRandomString() + extension.getValue());
        switch (mode) {
            case CAMERA:
                startActivityFromCamera();
                break;
            case GALLERY:
                startActivityFromGallery();
                break;
            case CAMERA_AND_GALLERY:
                showFromCameraOrGalleryAlert();
                break;
        }
    }

    private void showFromCameraOrGalleryAlert() {
        new AlertDialog.Builder(this)
                .setTitle(/*getString(R.string.media_picker_select_from)*/"Select From:")
                .setPositiveButton(getString(R.string.media_picker_camera), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityFromCamera();
                    }
                })
                .setNegativeButton(getString(R.string.media_picker_gallery), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityFromGallery();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d(TAG, "onCancel: ");
                        finish();
                    }
                })
                .show();
    }

    private void startActivityFromGallery() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("video/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_SELECT_VIDEO);

    }

    private void startActivityFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mVideoUri = Uri.fromFile(destination);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri);
        startActivityForResult(intent, CAMERA_REQUEST);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mVideoUri != null) {
            outState.putString(VideoTags.CAMERA_IMAGE_URI, mVideoUri.toString());
        }

        if (directory != null) {
            outState.putString(VideoTags.MATERIAL_VIDEO_URI, directory);

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(VideoTags.CAMERA_IMAGE_URI)) {
            mVideoUri = Uri.parse(savedInstanceState.getString(VideoTags.CAMERA_IMAGE_URI));
            destination = new File(mVideoUri.getPath());
        }

        if (savedInstanceState.containsKey(VideoTags.MATERIAL_VIDEO_URI)) {
            directory = savedInstanceState.getString(VideoTags.MATERIAL_VIDEO_URI);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CAMERA_RQ:
                if (resultCode == RESULT_OK) {
                    // new CompressImageTask(destination.getAbsolutePath(),
                    //       compressLevel.getValue(), VideoActivity.this).execute();

                    if (data.getDataString().toLowerCase().startsWith("file://")) {

                        finishActivity(data.getDataString().substring(7));
                    } else {
                        finishActivity(data.getDataString());

                    }
                } else {
                    finish();
                }

                break;
            case REQUEST_CODE_SELECT_VIDEO:
                if (resultCode == RESULT_OK) {


                    String selectedVideoPath = null;
                    try {
                        Uri selectedVideo;
                        String selectedVideoStr = data.getDataString();

                        if (selectedVideoStr.startsWith("file://")) {
                            selectedVideo = Uri.parse(selectedVideoStr.substring(7));
                            finishActivity(selectedVideo.getPath());
                        } else {
                            selectedVideo = Uri.parse(selectedVideoStr);
                          selectedVideoPath = Utility.getRealPathFromURI(this, selectedVideo);
                            finishActivity(selectedVideoPath);
                        }



                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    finish();
                }
        }
    }

    private void finishActivity(String path) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(VideoPicker.EXTRA_VIDEO_PATH, path);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void pickImageWrapper() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();

            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add(getString(R.string.media_picker_read_Write_external_storage));

            if (permissionsList.size() > 0) {

                ActivityCompat.requestPermissions(VideoActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }

            pickVideo();
        } else {
            pickVideo();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(VideoActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.media_picker_ok), okListener)
                .setNegativeButton(getString(R.string.media_picker_cancel), null)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(VideoActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(VideoActivity.this, permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    pickVideo();
                } else {
                    // Permission Denied
                    if (ActivityCompat.shouldShowRequestPermissionRationale(VideoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //Show permission explanation dialog...
                        finish();
                    } else {
                        Toast.makeText(VideoActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public static Intent getCallingIntent(Context activity, Extension extension
            , Mode mode,
                                          String directory) {
        Intent intent = new Intent(activity, VideoActivity.class);
        intent.putExtra(VideoTags.EXTENSION, extension);
        intent.putExtra(VideoTags.MODE, mode);
        intent.putExtra(VideoTags.DIRECTORY, directory);
        return intent;
    }

}