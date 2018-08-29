package com.pointters.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.pointters.R;
import com.pointters.activity.ProfileScreenActivity;
import com.pointters.utils.ConstantUtils;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by mac on 12/7/17.
 */

public class QrcodeFragment extends Fragment implements View.OnClickListener, OnQRCodeReadListener {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private final int PERMISSION_REQUEST_CAMERA = 16;

    private View view;
    private RelativeLayout myView, scanView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private KProgressHUD loader;
    Button scanQrCodeButton;
    Button scanQrFromGalleryButton;
    ImageView myQrcodeView;
    String userid;
    private QRCodeReaderView qrCodeReaderView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_qrcode, container, false);

        sharedPreferences = getActivity().getSharedPreferences(ConstantUtils.APP_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        if (sharedPreferences.getString(ConstantUtils.USER_DATA, "") != null) {
            String json = sharedPreferences.getString(ConstantUtils.USER_DATA, "");
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (jsonObject.has("_id") && jsonObject.get("_id") != null && !jsonObject.get("_id").equals("")) {
                    userid = (String) jsonObject.get("_id");
                    Log.e("userid:  ",  userid);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        initViews();
        setOnClickListners();

        loader = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        return view;
    }

    private void initViews() {
        myQrcodeView = (ImageView) view.findViewById(R.id.qr_code_image);
        scanQrCodeButton = (Button) view.findViewById(R.id.btn_scan_qr_code);
        scanQrFromGalleryButton = (Button) view.findViewById(R.id.btn_scan_qr_code_gallery);
        myView = (RelativeLayout) view.findViewById(R.id.my_qr_code_view);
        scanView = (RelativeLayout)view.findViewById(R.id.decorder_view);
        myView.setVisibility(View.VISIBLE);
        scanView.setVisibility(View.GONE);
        qrCodeReaderView = (QRCodeReaderView) view.findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(1000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
        geraQR(userid);
    }

    private void geraQR(String texto){
        Writer writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(texto, BarcodeFormat.QR_CODE, 512, 512);
            int width = 512;
            int height = 512;
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (bitMatrix.get(x,y))
                        bmp.setPixel(x, y, Color.BLACK);
                    else
                        bmp.setPixel(x, y, Color.WHITE);
                }
            }
            myQrcodeView.setImageBitmap(bmp);
        } catch (WriterException e) {
            Log.e("QR ERROR", e.toString());
        }
    }

//    private void getInviteSuggestedData() {
//        ApiInterface apiService = ApiClient.getClient(false).create(ApiInterface.class);
//        Call<ArrayList<InviteSuggestedUserModel>> callGetCategoryApi = apiService.getInviteSuggested(ConstantUtils.TOKEN_PREFIX + sharedPreferences.getString(ConstantUtils.PREF_TOKEN, ""));
//        callGetCategoryApi.enqueue(new Callback<ArrayList<InviteSuggestedUserModel>>() {
//            @Override
//            public void onResponse(Call<ArrayList<InviteSuggestedUserModel>> call, Response<ArrayList<InviteSuggestedUserModel>> response) {
//                if (loader.isShowing()) {
//                    loader.dismiss();
//                }
//
//                if (response.code() == 200 && response.body() != null) {
//                    inviteSuggestedUserModels = response.body();
//                    postDataAdapter.setData(inviteSuggestedUserModels);
//                    postDataAdapter.notifyDataSetChanged();
//
//                }
//                else if (response.code() == 401) {
//                }
//                else if (response.code() == 404) {
//                }
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(Call<ArrayList<InviteSuggestedUserModel>> call, Throwable t) {
//                if (loader.isShowing())     loader.dismiss();
////                Toast.makeText(getActivity(), "Connection Failed!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void setOnClickListners() {
        scanQrCodeButton.setOnClickListener(this);
        scanQrFromGalleryButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch ((v.getId())) {
            case R.id.btn_scan_qr_code:
                myView.setVisibility(View.GONE);
                scanView.setVisibility(View.VISIBLE);
                qrCodeReaderView.startCamera();
                break;
            case R.id.btn_scan_qr_code_gallery:
                readQrcodeFromGallery();
                break;
        }
    }

    public void readQrcodeFromGallery(){
        qrCodeReaderView.stopCamera();

        new ImagePicker.Builder(getActivity())
                .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                .mode(ImagePicker.Mode.GALLERY)
                .directory(ImagePicker.Directory.DEFAULT)
                .extension(ImagePicker.Extension.JPG)
                .allowMultipleImages(false)
                .enableDebuggingMode(true)
                .build();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE) {
                List<String> mPaths = (List<String>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH);
                Log.e("result: ", String.valueOf(mPaths));
                if (mPaths != null && mPaths.size() == 1) {
                    String imagePath = mPaths.get(0);
                    Bitmap photo = BitmapFactory.decodeFile(imagePath);
                    String contents = null;
                    int[] intArray = new int[photo.getWidth()*photo.getHeight()];
                    photo.getPixels(intArray, 0, photo.getWidth(), 0, 0, photo.getWidth(), photo.getHeight());
                    LuminanceSource source = new RGBLuminanceSource(photo.getWidth(), photo.getHeight(), intArray);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Reader reader = new MultiFormatReader();
                    Result result = null;
                    Log.e("result: ", String.valueOf(bitmap));
                    try {
                        result = reader.decode(bitmap);
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    } catch (ChecksumException e) {
                        e.printStackTrace();
                    } catch (FormatException e) {
                        e.printStackTrace();
                    }
                    contents = result.getText();
                    Log.e("result: ", contents);
                    Intent intent = new Intent(getActivity(), ProfileScreenActivity.class);
                    if (contents == userid) {
                        intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, true);
                        startActivity(intent);
                    } else{
                        intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, false);
                        intent.putExtra(ConstantUtils.PROFILE_USERID, contents);
                        startActivity(intent);
                    }

                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Log.e("qrcode: ", text);
        Intent intent = new Intent(getActivity(), ProfileScreenActivity.class);

        if (text == userid) {
            intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, true);
            startActivity(intent);
        } else{
            intent.putExtra(ConstantUtils.PROFILE_LOGINUSER, false);
            intent.putExtra(ConstantUtils.PROFILE_USERID, text);
            startActivity(intent);
        }


    }
}