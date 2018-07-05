package com.example.marisco.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marisco.myapplication.constructors.ProfileUsernameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListAdapterComments extends ArrayAdapter {

    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";

    private List<Map<String, Object>> mData;
    Context context;
    private Retrofit retrofit;

    public ListAdapterComments(Context context, List<Map<String, Object>> list, int resource) {
        super(context, resource, list);
        mData = new ArrayList<>();
        mData.addAll(list);
        this.context = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map<String, Object> getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mData.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View result;

        if (view == null) {
            result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment, viewGroup, false);
        } else {
            result = view;
        }

        Map<String, Object> temp = getItem(i);
        if((String)temp.get("comment_author") == null){
            getCommentUsername((double)temp.get("comment_userID"), result, i);
        }
        else{
            //Toast.makeText(context, "Author cached", Toast.LENGTH_LONG).show();
            ((TextView) result.findViewById(R.id.comment_author)).setText(temp.get("comment_author").toString());
        }

        ((TextView) result.findViewById(R.id.comment_text)).setText(temp.get("comment_text").toString());
        ((TextView) result.findViewById(R.id.comment_date)).setText(temp.get("comment_date").toString());

        return result;
    }

    private void getCommentUsername(double userID, final View result, final int i){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        Call<String> call = agniAPI.getUsername(new ProfileUsernameData(userID));

        call.enqueue(new Callback<String>() {
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    getItem(i).put("comment_author", response.body());
                    ((TextView) result.findViewById(R.id.comment_author)).setText(response.body());
                }
                else {
                    Toast.makeText(context, "Failed to get username: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }
            public void onFailure(Call<String > call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }
}
