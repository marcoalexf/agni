package com.example.marisco.myapplication;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    String username, email, type, name;

    //@BindView(R.id.profile_edit_avatar) ImageView profile_edit_avatar;

    @BindView(R.id.profile_username) TextView profile_username;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
        Bundle args = getArguments();

        this.username = args.getString("username");
        profile_username.setText(this.username);

        return v;
    }

}
