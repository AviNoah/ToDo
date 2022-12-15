package com.example.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdapterUserProfileList extends ArrayAdapter {
    private Context context; //Current context
    private ArrayList<UserModel> list; //Stores profiles


    public AdapterUserProfileList(Context context, ArrayList<UserModel> list) {
        super(context, R.layout.custom_layout_user_profile, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Inflate into an XML.
        LayoutInflater layoutInflater = ((AppCompatActivity) context).getLayoutInflater(); // fetches current activity
        View view = layoutInflater.inflate(R.layout.custom_layout_user_profile, parent, false); // insert into an xml

        UserModel userModel = this.list.get(position);

        TextView tvProfileImage, tvEmail;
        tvProfileImage = view.findViewById(R.id.tv_user_profile_image);
        tvEmail = view.findViewById(R.id.tv_user_email);

        String initials = userModel.getInitials();

        tvProfileImage.setText(initials);
        tvEmail.setText(userModel.getEmail());
        return view;
    }
}
