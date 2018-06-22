package com.example.marisco.myapplication;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class OccurrenceFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";
    private static final String TOKEN = "token";
    private static final String MARKER_NAME = "Nova ocorrência";
    private static final int CLEAN_ID = 0, ZONE_ID = 1, OTHER_ID = 2;
    private static final int ONE = 1, TWO = 2, THREE = 3, FOUR = 4, FIVE = 5;
    private static final int PRIVATE_ID = 10, PUBLIC_ID = 11;

    private GoogleMap mapG;
    private MarkerOptions mp;
    private Location initialLoc;

    private Retrofit retrofit;
    private LoginResponse token;
    private File photoFile;

    @BindView(R.id.occurence_title)
    EditText title;
    @BindView(R.id.occurence_description)
    EditText description;
    @BindView(R.id.occurrence_type)
    RadioGroup type;
    @BindView(R.id.occurrence_privacy)
    RadioGroup privacy;
    @BindView(R.id.occurrence_severity)
    RadioGroup severity;
    @BindView(R.id.occurrence_notification)
    CheckBox notification;
    @BindView(R.id.mapView)
    MapView map;
    @BindView(R.id.occurrence_button)
    Button button;
    @BindView(R.id.occurrence_clean)
    RadioButton r_clean;
    @BindView(R.id.occurrence_zone)
    RadioButton r_zone;
    @BindView(R.id.occurrence_other)
    RadioButton r_other;
    @BindView(R.id.severity_one)
    RadioButton r_one;
    @BindView(R.id.severity_two)
    RadioButton r_two;
    @BindView(R.id.severity_three)
    RadioButton r_three;
    @BindView(R.id.severity_four)
    RadioButton r_four;
    @BindView(R.id.severity_five)
    RadioButton r_five;
    @BindView(R.id.private_area)
    RadioButton r_private;
    @BindView(R.id.public_area)
    RadioButton r_public;

    public OccurrenceFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_occurrence, container, false);
        ButterKnife.bind(this, v);
        Bundle b = this.getArguments();
        if (b != null) {
            this.token = (LoginResponse) b.getSerializable(TOKEN);
            this.photoFile = (File) b.getSerializable("PHOTO");
        }

        map.onCreate(savedInstanceState);
        map.onResume(); // needed to get the map to display immediately
        map.getMapAsync(this);
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptOccurrence();
            }
        });

        setRadioIds();
        return v;
    }

    private void setRadioIds() {
        r_clean.setId(CLEAN_ID);
        r_zone.setId(ZONE_ID);
        r_other.setId(OTHER_ID);

        r_one.setId(ONE);
        r_two.setId(TWO);
        r_three.setId(THREE);
        r_four.setId(FOUR);
        r_five.setId(FIVE);

        r_private.setId(PRIVATE_ID);
        r_public.setId(PUBLIC_ID);
    }

    private void attemptOccurrence() {
        title.setError(null);
        description.setError(null);

        String occ_title = title.getText().toString();
        String occ_description = description.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(occ_title) && !cancel) {
            title.setError(getString(R.string.error_field_required));
            focusView = title;
            cancel = true;
        }

        if (TextUtils.isEmpty(occ_description) && !cancel) {
            description.setError(getString(R.string.error_field_required));
            focusView = description;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            registerOccurrence();
        }
    }

    private void registerOccurrence() {

        String occ_title = title.getText().toString();
        String occ_description = description.getText().toString();

        String occ_type = null;
        Resources res = getResources();
        int type_id = type.getCheckedRadioButtonId();

        switch (type_id) {
            case CLEAN_ID:
                occ_type = res.getString(R.string.occurrence_type_clean);
                break;
            case ZONE_ID:
                occ_type = res.getString(R.string.occurrence_type_zone);
                break;
            case OTHER_ID:
                occ_type = res.getString(R.string.occurrence_type_other);
                break;
            default:
        }
        int level = severity.getCheckedRadioButtonId();

        int area_id = privacy.getCheckedRadioButtonId();
        boolean visibility = r_public.isChecked();

        switch (area_id) {
            case PRIVATE_ID:
                visibility = false;
                break;
            case PUBLIC_ID:
                visibility = true;
                break;
        }
        boolean notificationOn = notification.isChecked();

        double lat = mp.getPosition().latitude, lon = mp.getPosition().longitude;

        OccurrenceData data = null;
        if(this.photoFile != null){
            Log.d("PHOTO", "IS NOT NULL");
            data = new OccurrenceData(token, occ_title, occ_description, occ_type, level,
                    visibility, lat, lon, notificationOn, true, 1);
        }else{
            Log.d("PHOTO", "IS NULL");
            data = new OccurrenceData(token, occ_title, occ_description, occ_type, level,
                    visibility, lat, lon, notificationOn, false, 0);
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        final AgniAPI agniAPI = retrofit.create(AgniAPI.class);
        final List<Long> list_of_ids_to_upload_to = new ArrayList<>();

        if(data.getUploadMedia()){
            Call <MediaUploadResponse> call = agniAPI.registerOccurrencePhoto(data);
            Log.d("UPLOADING SHIT", "UPLOADING SHIT");
            call.enqueue(new Callback<MediaUploadResponse>() {
                @Override
                public void onResponse(Call<MediaUploadResponse> call, Response<MediaUploadResponse> response) {
                    if(response.code() == 200){
                        Toast toast = Toast.makeText(getActivity(), "Nova ocorrência registada, como foto"
                                , Toast.LENGTH_SHORT);
                        toast.show();

                        list_of_ids_to_upload_to.addAll(response.body().getList());
                        uploadPhoto(photoFile, list_of_ids_to_upload_to);

                    }
                    else {
                        try{
                            Toast toast = Toast.makeText(getActivity(), "Código de erro: " + response.code(), Toast.LENGTH_SHORT);
                            toast.show();
                        } catch (Exception e){
                            Toast toast = Toast.makeText(getActivity(), "excecao", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<MediaUploadResponse> call, Throwable t) {
                    Toast toast = Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }else{
            Call <ResponseBody> call = agniAPI.registerOccurrence(data);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code() == 200){
                        Toast toast = Toast.makeText(getActivity(), "Nova ocorrência registada"
                                , Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        try{
                            Toast toast = Toast.makeText(getActivity(), "Código de erro: " + response.code(), Toast.LENGTH_SHORT);
                            toast.show();
                        } catch (Exception e){
                            Toast toast = Toast.makeText(getActivity(), "excecao", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast toast = Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }

    public void uploadPhoto(File photoFile, List<Long> list_of_ids_to_upload_to){
        AgniAPI agniAPI = retrofit.create(AgniAPI.class);
        Long id = list_of_ids_to_upload_to.get(0);
        Log.d("UPLOADING SHIT", "ENTERED THE UPLOAD PHOTO METHOD");
        Log.d("ID IS -> ", String.valueOf(id));

        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            int factor = calculateResizeFactor(bitmap);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/4,
                    bitmap.getHeight()/4, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] data = stream.toByteArray();
            resized.recycle();
            Log.d("SIZE OF DATA: ", String.valueOf(data.length));
            String contentType = "image/jpeg";
            RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), data);
            Call <ResponseBody> call = agniAPI.uploadPhoto(id, contentType, body);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        Toast toast = Toast.makeText(getActivity(), "Photo successfully uploaded", Toast.LENGTH_SHORT);
                        toast.show();
                        }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast toast = Toast.makeText(getActivity(), "Failed to upload photo", Toast.LENGTH_SHORT);
                    toast.show();
                    }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) throws SecurityException {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mapG.setMyLocationEnabled(true);

                    mapG.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                        public void onMyLocationChange(Location location) {
                            if (initialLoc == null) {
                                putMarker(new LatLng(location.getLatitude(), location.getLongitude()));
                                initialLoc = location;
                            }
                        }
                    });
                } else {
                    //meter map em local standard
                }
                return;
            }
        }
    }

    public int calculateResizeFactor(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if(width + height > 2000)
            return width + height / 2000;
        else
            return 1;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.mapG = map;
        mapG.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng location) {
              putMarker(location);
            }
        });

    }

    private void putMarker(LatLng location) {
        mapG.clear();

        mp = new MarkerOptions();

        mp.position(new LatLng(location.latitude, location.longitude));

        mp.title(MARKER_NAME);

        mapG.addMarker(mp);

        mapG.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.latitude, location.longitude), 16));
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
