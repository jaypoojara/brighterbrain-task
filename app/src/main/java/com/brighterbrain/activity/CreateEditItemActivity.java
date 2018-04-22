package com.brighterbrain.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brighterbrain.AppController;
import com.brighterbrain.R;
import com.brighterbrain.adapter.ImageAdapter;
import com.brighterbrain.custom_listner.CrossRemoveImageListener;
import com.brighterbrain.database.DBHandler;
import com.brighterbrain.model.Item;
import com.brighterbrain.util.Constants;
import com.brighterbrain.util.MySharedPreferences;
import com.brighterbrain.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.master.permissionhelper.PermissionHelper;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEditItemActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult>, CrossRemoveImageListener {
    private final int REQUEST_LOCATION = 108;
    PendingResult<LocationSettingsResult> resultLocation;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    String lattitude, longitude;
    private Toolbar toolbar;
    private EditText edtName;
    private EditText edtDescription;
    private EditText edtCost;
    private RecyclerView rvImages;
    private Button btnSave;
    private List<Image> images;
    private ImageAdapter adapter;
    private DBHandler dbHandler;
    private PermissionHelper permissionHelper;
    private boolean isEdit = false;
    private Item item;

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public void requestLocationUpdate() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_item);
        findViews();
        dbHandler = new DBHandler(this);
        images = new ArrayList<>();
        buildGoogleApiClient();
        initRecyclerView();
        takePermission();
        editMode();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void editMode() {
        isEdit = getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra("data");
        if (isEdit) {
            item = getIntent().getExtras().getParcelable("data");
            edtCost.setText(String.valueOf(item.getCost()));
            edtDescription.setText(item.getDescription());
            edtName.setText(item.getName());
            for (int i = 0; i < item.getItemImages().size(); i++) {
                File file = new File(item.getItemImages().get(i).getImagePath());
                if (file.exists()) {
                    Image image = new Image(1, file.getName(), file.getPath());
                    images.add(image);
                    adapter.updateAdapter(images);
                }
            }
            lattitude = item.getLatitude();
            longitude = item.getLongitude();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                //.enableAutoManage(this, 0, this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
    }


    private void checkGps() {
        enableGPSDialog();
    }

    public void enableGPSDialog() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);


        resultLocation = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        resultLocation.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        requestLocationUpdate();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(CreateEditItemActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });

    }

    private void takePermission() {

        permissionHelper = new PermissionHelper(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                checkGps();
            }

            @Override
            public void onIndividualPermissionGranted(String[] grantedPermission) {
//                Log.d(TAG, "onIndividualPermissionGranted() called with: grantedPermission = [" + TextUtils.join(",",grantedPermission) + "]");
            }

            @Override
            public void onPermissionDenied() {
//                Log.d(TAG, "onPermissionDenied() called");
            }

            @Override
            public void onPermissionDeniedBySystem() {
//                Log.d(TAG, "onPermissionDeniedBySystem() called");
            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );

        result.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }


    private void initRecyclerView() {
        adapter = new ImageAdapter(images, this, this);
        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImages.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        rvImages.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSave) {
            if (validate()) {
                if (isEdit) {
                    try {
                        createItem();
                        long id = (long) item.getId();
                        dbHandler.deleteRecord(Constants.ITEM_IMAGES, "item_id=" + id);
                        copyFiles(String.valueOf(id));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    long id = 0;
                    try {
                        id = createItem();
                        copyFiles(String.valueOf(id));
                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                }
            }
        }
    }

    private long createItem() throws IOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.NAME, edtName.getText().toString());
        contentValues.put(Constants.DESCRIPTION, edtDescription.getText().toString());
        contentValues.put(Constants.COST, edtCost.getText().toString());
        contentValues.put(Constants.LATITUDE, lattitude);
        contentValues.put(Constants.LONGITUDE, longitude);
        if (isEdit)
            dbHandler.updateRecord(Constants.ITEMS, "id = " + item.getId(), contentValues);
        else {
            sendDataToServer();
            return dbHandler.insertRecord(Constants.ITEMS, contentValues);
        }
        return -1;
    }

    private void sendDataToServer() throws IOException {

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), new MySharedPreferences(this).getSharedValue(Constants.PREFERENCE_USER_ID));
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), edtName.getText().toString());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), edtDescription.getText().toString());
        RequestBody latitude = RequestBody.create(MediaType.parse("text/plain"), lattitude);
        RequestBody longitudeBody = RequestBody.create(MediaType.parse("text/plain"), longitude);
        RequestBody costBody = RequestBody.create(MediaType.parse("text/plain"), edtCost.getText().toString());


        MultipartBody.Part[] bodies = null;
        if (images != null && !images.isEmpty()) {
            bodies = new MultipartBody.Part[images.size()];

            for (int i = 0; i < images.size(); i++) {
                File imageFile = new File(images.get(i).getPath());
                RequestBody images = RequestBody.create(MediaType.parse("image/*"), imageFile);
                MultipartBody.Part imageBody = MultipartBody.Part.createFormData("image[]", imageFile.getName(), images);
                bodies[i] = imageBody;
            }
        }
        Utils.showProgressBar(this);
        Call<ResponseBody> call = AppController.getInstance().getApiInterface().uploadData(userId, name, description, latitude, longitudeBody, costBody, bodies);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.endProgressDialog();
                if (response.isSuccessful()) {
                    try {
                        String message = response.body().string();
                        JSONObject jsonObject = new JSONObject(message);
                        if (jsonObject.getBoolean("status"))
                            Toasty.success(CreateEditItemActivity.this, "Item successfully saved on cloud.").show();
                        else
                            Toasty.error(CreateEditItemActivity.this, "Error while uploading data").show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.endProgressDialog();
                t.printStackTrace();
                Toasty.error(CreateEditItemActivity.this, "Error while uploading data").show();
            }
        });
    }


    private void copyFiles(String record) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.app_name), record + "");
        if (!file.exists()) {
            file.mkdirs();
        }
        for (int i = 0; i < images.size(); i++) {
            try {
                File temp = new File(file, images.get(i).getName());
                if (!temp.exists())
                    copy(new File(images.get(i).getPath()), temp);


                ContentValues contentValues = new ContentValues();
                contentValues.put(Constants.ITEM_ID, record);
                contentValues.put(Constants.IMAGE_PATH, temp.getPath());
                dbHandler.insertRecord(Constants.ITEM_IMAGES, contentValues);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!isEdit) {
            Toasty.success(this, "Item created successfully", Toast.LENGTH_LONG).show();
            clearData();
        } else
            finish();
    }

    private void clearData() {
        edtName.setText("");
        edtDescription.setText("");
        edtCost.setText("");
        images.clear();
        adapter.updateAdapter(images);
    }

    private boolean validate() {
        if (!Utils.isEmptyEditText(edtName, "Please enter item name")
                && !Utils.isEmptyEditText(edtDescription, "Please enter description")
                && !Utils.isEmptyEditText(edtCost, "Please enter cost")) {
            if (images == null || images.isEmpty()) {
                Toasty.error(this, "Please select image").show();
            } else
                return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            adapter.updateAdapter(images);
        } else if (resultCode == REQUEST_LOCATION && requestCode == Activity.RESULT_OK) {
            requestLocationUpdate();
        } else if (resultCode == REQUEST_LOCATION && requestCode == Activity.RESULT_CANCELED) {
            Toasty.warning(this, "Location not enabled.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionHelper != null) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        edtName = findViewById(R.id.edtName);
        edtDescription = findViewById(R.id.edtDescription);
        edtCost = findViewById(R.id.edtCost);
        rvImages = findViewById(R.id.rvImages);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (lattitude == null && longitude == null) {
            lattitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:

                // NO need to show the dialog;

                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(this, REQUEST_LOCATION);

                } catch (IntentSender.SendIntentException e) {

                    //failed to show
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @Override
    public void removeAt(int position) {
        if (images != null && !images.isEmpty() && images.size() > position) {
            images.remove(position);
            adapter.updateAdapter(images);
        }
    }
}
