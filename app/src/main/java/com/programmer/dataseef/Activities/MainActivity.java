package com.programmer.dataseef.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.Utils;
import com.cloudinary.utils.ObjectUtils;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;
import com.programmer.dataseef.Helpers.Helper;
import com.programmer.dataseef.Models.Home;
import com.programmer.dataseef.Interfaces.IHome;
import com.programmer.dataseef.Helpers.LocationGPS;
import com.programmer.dataseef.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton imgOne, imgTwo, imgThree;
    private TextView txtLocation;
    private EditText txtNombreLugar, txtPrecio, txtReferencia, txtDireccion, txtDescripcion;
    private RadioButton rbtnSi, rbtnNo;

    private Double latitude = 0.0, longitude = 0.0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
    }

    private void init() {
        configComponents();
        configLocation();
    }

    public void setLocation(Location location) {
        if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!list.isEmpty()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    txtLocation.setText("lat: " + location.getLatitude() + ", lon: " + location.getLongitude());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void configLocation() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationGPS Local = new LocationGPS();
            Local.setMainActivity(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        } else {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        configLocation();
    }

    private void configComponents() {

        imgOne = (ImageButton)findViewById(R.id.imgOne);
        imgTwo = (ImageButton)findViewById(R.id.imgTwo);
        imgThree = (ImageButton)findViewById(R.id.imgThree);

        imgOne.setOnClickListener(this);
        imgTwo.setOnClickListener(this);
        imgThree.setOnClickListener(this);

        txtLocation = (TextView)findViewById(R.id.txtLocation);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        txtDescripcion = (EditText)findViewById(R.id.txtDescripcion);
        txtNombreLugar = (EditText)findViewById(R.id.txtNombre);
        txtDireccion = (EditText)findViewById(R.id.txtDireccion);
        txtPrecio = (EditText)findViewById(R.id.txtPrecio);
        txtReferencia = (EditText)findViewById(R.id.txtReferencia);

        rbtnSi = (RadioButton)findViewById(R.id.rbtSi);
        rbtnNo = (RadioButton)findViewById(R.id.rbtnNo);
    }

    private void startUpload() {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            Map[] results = new Map[3];
            @Override
            protected String doInBackground(String... params) {
                Cloudinary cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(getApplicationContext()));
                InputStream inOne = null;
                InputStream inTwo = null;
                InputStream inThree = null;
                try {
                    if (Helper.imageOne != null) {
                        inOne = getContentResolver().openInputStream(Helper.imageOne);
                        results[0] = cloudinary.uploader().upload(inOne, ObjectUtils.emptyMap());
                    }
                    if (Helper.imageTwo != null) {
                        inTwo = getContentResolver().openInputStream(Helper.imageTwo);
                        results[1] = cloudinary.uploader().upload(inTwo, ObjectUtils.emptyMap());
                    }
                    if (Helper.imageThree != null) {
                        inThree = getContentResolver().openInputStream(Helper.imageThree);
                        results[2] = cloudinary.uploader().upload(inThree, ObjectUtils.emptyMap());
                    }
                } catch (FileNotFoundException e) {
                    Log.e("File", e.getMessage());
                } catch (IOException e) {
                    Log.e("Cloudinary", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                saveData(results);
            }
        };
        progressDialog = ProgressDialog.show(this, "Uploading", "Uploading Data");
        progressDialog.setCancelable(false);
        task.execute();
    }

    private void saveData(Map[] map) {
        final String BASE_URL = "http://159.203.182.38/DataSeeF/public/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Home home = new Home();
        if (Helper.imageOne != null)
            home.setImage_one(map[0].get("url").toString());
        else
            home.setImage_one("No tiene");
        if (Helper.imageTwo != null)
            home.setImage_two(map[1].get("url").toString());
        else
            home.setImage_two("No tiene");
        if (Helper.imageThree != null)
            home.setImage_three(map[2].get("url").toString());
        else
            home.setImage_three("No tiene");
        home.setAddress(txtDireccion.getText().toString());
        home.setNombre(txtNombreLugar.getText().toString());
        if (txtReferencia.getText().toString().equals(""))
            home.setPhone(0);
        else
            home.setPhone(Integer.parseInt(txtReferencia.getText().toString()));
        if (txtPrecio.getText().toString().equals(""))
            home.setPrice(0);
        else
            home.setPrice(Integer.parseInt(txtPrecio.getText().toString()));
        home.setDescripcion(txtDescripcion.getText().toString());
        home.setX_coordinate(latitude);
        home.setY_coordinate(longitude);
        if (rbtnSi.isChecked()) {
            home.setGarage(1);
        } else {
            home.setGarage(0);
        }

        IHome iHome = retrofit.create(IHome.class);
        Call<ResponseBody> call = iHome.addHome(home);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Datos Subidos Correctamente", Toast.LENGTH_SHORT).show();
                finishActivity();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void finishActivity() {
        finish();
        startActivity(new Intent(MainActivity.this, MainActivity.class));
    }

    public boolean isConnectedWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public boolean isConnectedMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgOne:
                takeCamera(1);
                break;
            case R.id.imgTwo:
                takeCamera(2);
                break;
            case R.id.imgThree:
                takeCamera(3);
                break;
            case R.id.fab:
                if(isConnectedMobile(this) || isConnectedWifi(this) || isOnline(this))
                    if (latitude != 0.0 && longitude != 0.0)
                        startUpload();
                    else {
                        Toast.makeText(this, "Su ubicacion no a sido detectada espere porfavor", Toast.LENGTH_SHORT).show();
                        configLocation();
                    }
                else
                    Toast.makeText(this, "Conectarse a internet", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void takeCamera(final int option) {
        RxImagePicker.with(this).requestImage(Sources.CAMERA).subscribe(new Action1<Uri>() {
            @Override
            public void call(Uri uri) {
                switch (option) {
                    case 1:
                        Helper.imageOne = uri;
                        imgOne.setImageResource(R.drawable.success);
                        break;
                    case 2:
                        Helper.imageTwo = uri;
                        imgTwo.setImageResource(R.drawable.success);
                        break;
                    case 3:
                        Helper.imageThree = uri;
                        imgThree.setImageResource(R.drawable.success);
                        break;
                }
            }
        });
    }
}
