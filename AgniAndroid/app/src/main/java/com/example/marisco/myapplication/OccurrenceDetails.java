package com.example.marisco.myapplication;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.marisco.myapplication.constructors.CursorList;
import com.example.marisco.myapplication.constructors.ListOccurrenceCommentData;
import com.example.marisco.myapplication.constructors.OccurrenceAcceptData;
import com.example.marisco.myapplication.constructors.OccurrenceCommentData;
import com.example.marisco.myapplication.constructors.OccurrenceDeleteData;
import com.example.marisco.myapplication.constructors.OccurrenceEditData;
import com.example.marisco.myapplication.constructors.OccurrenceLikeCheckData;
import com.example.marisco.myapplication.constructors.OccurrenceLikeCountData;
import com.example.marisco.myapplication.constructors.ProfileRequest;
import com.example.marisco.myapplication.constructors.ProfileResponse;
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

public class OccurrenceDetails extends Fragment implements OnMapReadyCallback {

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String VISIBILITY = "visibility";
    private static final String LEVEL = "level";
    private static final String ID = "occurrence_id";
    private static final String TOKEN = "token";
    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";
    private static final String WORKER = "WORKER";
    private static final int COMMENT_LIMIT = 20;

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

    private String i_title, i_description;
    private boolean i_visibility;
    private boolean finishedComments;

    @BindView(R.id.detail_title)
    EditText o_title;
    @BindView(R.id.detail_description)
    EditText o_description;
    @BindView(R.id.detail_level)
    EditText o_level;
    @BindView(R.id.spinner_visibility)
    Spinner o_visibility;
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
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.edit_button)
    FloatingActionButton edit_btn;
    @BindView(R.id.btnSave)
    Button save_btn;
    @BindView(R.id.btnCancelSave)
    Button cancel_btn;
    @BindView(R.id.likeBtn)
    ToggleButton like_btn;
    @BindView(R.id.number_of_likes)
    TextView likes;
    @BindView(R.id.accept_occurrence)
    Button accept_btn;

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
        finishedComments = false;
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
        getLikes();
        setInitialLikeState();
        checkUserType();
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView != null) {
                    if (scrollView.getChildAt(0).getBottom() <= (scrollView.getHeight() + scrollView.getScrollY())) {
                        if(!finishedComments){
                            getMoreComments();
                        }
                    }
                }
            }
        });
        save_btn.setVisibility(View.GONE);
        cancel_btn.setVisibility(View.GONE);
        accept_btn.setVisibility(View.GONE);
        fieldsSetup();
         if(userID != Long.parseLong(token.userID)){
            edit_btn.setVisibility(View.GONE);
        }
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInitialValues();
                setEditMode();
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldsSetup();
                editOccurrence();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldsSetup();
                restoreInitialValues();
            }
        });
        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptOccurrence();
            }
        });

        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.visibility_types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        o_visibility.setAdapter(adapter);

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike();
            }
        });
        return v;
    }

    private void checkUserType(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        ProfileRequest request = new ProfileRequest(token.username, token);
        Call<ProfileResponse> call = agniAPI.getProfile(request);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.code() == 200){
                    if(response.body().getRole().equals(WORKER)){
                        accept_btn.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to get profile" + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    private void saveInitialValues(){
        i_title = o_title.getText().toString();
        i_description = o_description.getText().toString();

        if(o_visibility.getSelectedItem().toString().equals(getResources().getString(R.string.occurrence_public)))
            i_visibility = true;
        else i_visibility = false;
    }

    private void restoreInitialValues(){
        o_title.setText(i_title);
        o_description.setText(i_description);

        if(i_visibility)
            o_visibility.setSelection(0);
        else o_visibility.setSelection(1);
    }

    private void fieldsSetup(){
        o_title.setInputType(InputType.TYPE_NULL);
        o_title.setEnabled(false);
        o_title.setTextIsSelectable(false);

        o_description.setInputType(InputType.TYPE_NULL);
        o_description.setEnabled(false);
        o_description.setTextIsSelectable(false);

        o_level.setInputType(InputType.TYPE_NULL);
        o_level.setEnabled(false);
        o_level.setTextIsSelectable(false);

        o_visibility.setEnabled(false);

        save_btn.setVisibility(View.GONE);
        cancel_btn.setVisibility(View.GONE);
    }

    private void setEditMode(){
        o_title.setInputType(InputType.TYPE_CLASS_TEXT);
        o_title.setEnabled(true);
        o_title.setTextIsSelectable(true);

        o_description.setInputType(InputType.TYPE_CLASS_TEXT);
        o_description.setEnabled(true);
        o_description.setTextIsSelectable(true);

        o_level.setInputType(InputType.TYPE_CLASS_TEXT);
        o_level.setEnabled(true);
        o_level.setTextIsSelectable(true);

        o_visibility.setEnabled(true);

        save_btn.setVisibility(View.VISIBLE);
        cancel_btn.setVisibility(View.VISIBLE);
    }

    private void fillInfo(String title, String description, boolean visibility, double level
    , Long occurrence_id, Long userID, ArrayList<String> mediaIDs){
        o_title.setText(title);
        o_description.setText(description);

        o_level.setText((int)level + "");

        if(visibility)
            o_visibility.setSelection(0);
        else o_visibility.setSelection(1);


        if(mediaIDs != null && !mediaIDs.isEmpty()) {
            Picasso.get().load("https://storage.googleapis.com/custom-tine-204615.appspot.com/user/"
                    + userID + "/occurrence/" + occurrence_id + "/" + mediaIDs.get(0)).into(image);
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
                        comment_text.setText("");
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
                    if(!c.getCursor().equals(cursor)){
                        cursor = c.getCursor();
                        if(!c.getMapList().isEmpty()){
                            if(c.getMapList().size() < COMMENT_LIMIT){
                                finishedComments = true;
                            }
                            comments.addAll( c.getMapList());
                            adapter = new ListAdapterComments(getContext(), comments, android.R.layout.simple_list_item_1);
                            comment_list.setAdapter(adapter);
                            putComments();
                        }
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

    private void putComments(){
        if (adapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = comment_list.getPaddingTop() + comment_list.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(comment_list.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, comment_list);

            if(listItem != null){
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
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
        newComment.put("comment_userID", Double.parseDouble(token.userID));
        newComment.put("comment_author", token.username);
        comments.add(0, newComment);
        adapter = new ListAdapterComments(getContext(), comments, android.R.layout.simple_list_item_1);
        comment_list.setAdapter(adapter);
        putComments();
    }

    private void editOccurrence(){
        String title = o_title.getText().toString();
        String description = o_description.getText().toString();
        boolean visibility = false;
        if(o_visibility.getSelectedItem().toString().equals(getResources().getString(R.string.occurrence_public)))
            visibility = true;

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        Call<ResponseBody> call = agniAPI.editOccurrence(new OccurrenceEditData(token, userID, occurrence_id,
                title, description, visibility, false, false, 0));

        call.enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Toast toast = Toast.makeText(getActivity(), "Ocorrência editada", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed edit occurrence: " + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    private void toggleLike(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        Call<ResponseBody> call = agniAPI.toggleLike(new OccurrenceDeleteData(token, userID, occurrence_id));

        call.enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    int likesN = Integer.parseInt(likes.getText().toString());
                    if(like_btn.isChecked())
                        likes.setText(likesN + 1 + "");
                    else likes.setText(likesN - 1 + "");
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed like/dislike: " + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    private void getLikes(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        Call<Integer> call = agniAPI.getLikes(new OccurrenceLikeCountData(token,  userID, occurrence_id));

        call.enqueue(new Callback<Integer>() {
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.code() == 200) {
                    if(response.body() != null)
                        likes.setText(response.body() + "");
                    else likes.setText("0");
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to get likes: " + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    private void setInitialLikeState(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        Call<Boolean> call = agniAPI.checkLike(new OccurrenceLikeCheckData(token,  Long.parseLong(token.userID), userID, occurrence_id));

        call.enqueue(new Callback<Boolean>() {
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.code() == 200) {
                    if(response.body())
                        like_btn.setChecked(true);

                }
            }
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    private void acceptOccurrence(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        Call<ResponseBody> call = agniAPI.acceptOccurrence(new OccurrenceAcceptData(token,  userID, occurrence_id));

        call.enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Toast toast = Toast.makeText(getActivity(), "Ocorrência aceite" , Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getActivity(), "Falha ao aceitar ocorrência: " +response.code() , Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }
}
