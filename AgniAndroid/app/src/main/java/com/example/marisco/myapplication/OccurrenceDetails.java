package com.example.marisco.myapplication;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OccurrenceDetails extends Fragment implements OnMapReadyCallback, AbsListView.OnScrollListener {

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String VISIBILITY = "visibility";
    private static final String LEVEL = "level";
    private static final String ID = "occurrence_id";
    private static final String TOKEN = "token";
    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";

    private GoogleMap mapG;
    private MarkerOptions mp;
    private double lat, lon;
    private String title;
    private Long occurrence_id ,userID;
    private Retrofit retrofit;
    private LoginResponse token;
    private String cursor;
    private List<Map<String, Object>> comments;
    private ListAdapterComments adapter;


    @BindView(R.id.detail_title)
    TextView o_title;
    @BindView(R.id.detail_description)
    TextView o_description;
    @BindView(R.id.detail_level)
    TextView o_level;
    @BindView(R.id.detail_visibility)
    TextView o_visibility;
    @BindView(R.id.occurrence_map)
    MapView map;
    @BindView(R.id.occurrence_image)
    ImageView image;
    @BindView(R.id.comment_btn)
    Button comment_btn;
    @BindView(R.id.new_comment_text)
    EditText comment_text;
    @BindView(R.id.comment_list)
    ListView comment_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.detail_occurrence, container, false);
        ButterKnife.bind(this, v);
        Bundle b = this.getArguments();
        title = "";
        String description = "";
        lat = 0;
        lon = 0;
        boolean visibility = false;
        double level = 1;
        occurrence_id = 0l;
        userID = 0l;
        ArrayList<String> mediaIDs = null;
        if (b != null) {
            title = (String)b.getSerializable(TITLE);
            description = (String)b.getSerializable(DESCRIPTION);
            lat = (double)b.getSerializable(LATITUDE);
            lon = (double)b.getSerializable(LONGITUDE);
            visibility = (boolean)b.getSerializable(VISIBILITY);
            level = (double)b.getSerializable(LEVEL);
            occurrence_id = (Long) b.getSerializable(ID);
            userID = (Long) b.getSerializable("userID");
            mediaIDs = (ArrayList<String>) b.getSerializable("mediaIDs");
            this.token = (LoginResponse) b.getSerializable(TOKEN);
        }

        map.onCreate(savedInstanceState);
        map.onResume(); // needed to get the map to display immediately
        map.getMapAsync(this);
        fillInfo(title, description, visibility, level, occurrence_id, userID, mediaIDs);
        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newComment();
            }
        });
        comments = new LinkedList<Map<String, Object>>();
        getMoreComments();
        return v;
    }

    private void fillInfo(String title, String description, boolean visibility, double level
    , Long occurrence_id, Long userID, ArrayList<String> mediaIDs){
        o_title.setText(title);
        o_description.setText(description);

        o_level.setText((int)level + "");

        if(visibility)
            o_visibility.setText(R.string.occurrence_public);
        else o_visibility.setText(R.string.occurrence_private);

        if(mediaIDs != null && !mediaIDs.isEmpty()) {
            Picasso.get().load("https://storage.googleapis.com/custom-tine-204615.appspot.com/user/"
                    + userID + "/occurrence/" + occurrence_id + "/" + mediaIDs.get(0)).rotate(90).into(image);
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.mapG = map;
        putMarker();
    }

    private void putMarker() {
        mapG.clear();
        mp = new MarkerOptions();
        mp.position(new LatLng(lat, lon));
        mp.title(title);
        mapG.addMarker(mp);
        mapG.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat, lon), 11));
    }

    private void newComment(){
        final String text = comment_text.getText().toString();
        if(text == null || text.equals("")){
            Toast toast = Toast.makeText(getActivity(), "Texto do comentário inválido ", Toast.LENGTH_SHORT);
             toast.show();
        }else{
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(ENDPOINT)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }

            AgniAPI agniAPI = retrofit.create(AgniAPI.class);

            Call<ResponseBody> call = agniAPI.postComment(new OccurrenceCommentData(token, userID, occurrence_id, text));

            call.enqueue(new Callback<ResponseBody>() {
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        addCommentToList(text);
                        Toast toast = Toast.makeText(getActivity(), "Comentário publicado", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        Toast toast = Toast.makeText(getActivity(), "Failed to post comment: " + response.code(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("ERROR", t.toString());
                }
            });
        }
    }

    private void getMoreComments(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        Call<CursorList> call = agniAPI.getMoreComments(new ListOccurrenceCommentData(token, userID, occurrence_id, cursor));

        call.enqueue(new Callback<CursorList>() {
            public void onResponse(Call<CursorList> call, Response<CursorList> response) {
                if (response.code() == 200) {
                    CursorList c = response.body();
                    cursor = c.getCursor();
                    if(!c.getMapList().isEmpty()){
                        comments.addAll( c.getMapList());
                        adapter = new ListAdapterComments(getContext(), comments, android.R.layout.simple_list_item_1);
                        comment_list.setAdapter(adapter);
                        putComments();
                        Toast toast = Toast.makeText(getActivity(), "Comentários : " + comments.size(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to get comments: " + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            public void onFailure(Call<CursorList> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    @Override
    public void onScrollStateChanged (AbsListView view, int scrollState){
        if(comment_list != null && comment_list.getAdapter() != null) {
            if (comment_list.getLastVisiblePosition() == comment_list.getAdapter().getCount() - 1){
                getMoreComments();
            }
        }
    }

    @Override
    public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount,
                          int totalItemCount){  }

    private void putComments(){
        if (adapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, comment_list);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = comment_list.getLayoutParams();
        params.height = totalHeight + (comment_list.getDividerHeight() * (adapter.getCount() - 1));
        comment_list.setLayoutParams(params);
        comment_list.requestLayout();
    }

    private void addCommentToList(String text){
        Map<String, Object> newComment = new HashMap<>();
        newComment.put("comment_text", text);
        newComment.put("comment_date", new Date());
        newComment.put("comment_userID", token.userID);
        comments.add(0, newComment);
        adapter.notifyDataSetChanged();
    }
}
