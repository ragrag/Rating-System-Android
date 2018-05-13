package com.deacons_droid.api.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.deacons_droid.R;
import com.deacons_droid.api.model.Change;
import com.deacons_droid.api.model.Comment;
import com.deacons_droid.api.model.Post;
import com.deacons_droid.api.service.PostClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class tab2_fragment extends Fragment {
    private static final String TAB = "Tab 2";
    private static String url;
    Retrofit.Builder builder ;
    Retrofit retrofit ;
    PostClient apiService;
    String username;
    SharedPreferences settings2 ;
    TextView pteam_name ;
    TextView pteam_comments;
    TextView pteam_points ;
    ImageView pteam_avatar;
    Post t;

    ListView lv3;
    ArrayList<Change> chl;
    ListView lv2;
    ArrayList<Comment> cl;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment, container, false);
        url = getActivity().getResources().getString(R.string.url);
         builder = new Retrofit.Builder()
                .baseUrl(url + "/")
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        apiService = retrofit.create(PostClient.class);



        settings2 = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        username = settings2.getString("username", "");



        TextView logout = (TextView) view.findViewById(R.id.logout_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.app_name);
                builder.setMessage("Logout?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        settings2.edit().remove("token").commit();
                        settings2.edit().remove("username").commit();
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(i);

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });


        ImageView refresh_btn = (ImageView) view.findViewById(R.id.profile_refresh_btn);

        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String token = settings2.getString("token", "");
                url = getActivity().getResources().getString(R.string.url);
                Dialog progress_spinner;
                progress_spinner = LoadingSpinner.Spinner(getActivity());
                progress_spinner.show();
                Call<Post> call = apiService.getTeam("Token " + token);
                call.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        if (response.isSuccessful()) {
                            progress_spinner.dismiss();
                            t = response.body();
                            if (t.getAvatar() != null) {
                                Glide.with(getActivity())
                                        .load(url + t.getAvatar())
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into((ImageView) view.findViewById(R.id.pteam_avatar));
                            }
                            pteam_name = (TextView) view.findViewById(R.id.pteam_name);
                            pteam_points = (TextView) view.findViewById(R.id.pteam_points);
                            pteam_comments = (TextView) view.findViewById(R.id.textView4);
                            pteam_avatar = (ImageView) view.findViewById(R.id.pteam_avatar);

                            pteam_name.setText(t.getName());
                            pteam_points.setText(String.valueOf(t.getPoints()) + " Points");
                            if (t.getPoints() == null) {
                                pteam_comments.setVisibility(TextView.INVISIBLE);
                                pteam_points.setVisibility(TextView.INVISIBLE);
                                pteam_avatar.setVisibility(ImageView.INVISIBLE);
                            } else {
                                pteam_comments.setVisibility(TextView.VISIBLE);
                                pteam_points.setVisibility(TextView.VISIBLE);
                                pteam_avatar.setVisibility(ImageView.VISIBLE);
                            }
                            pteam_name.setVisibility(TextView.VISIBLE);


                        } else {
                            progress_spinner.dismiss();
                            Toast.makeText(getActivity(), "Internal Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {
                        progress_spinner.dismiss();
                        pteam_name = (TextView) view.findViewById(R.id.pteam_name);
                        pteam_points = (TextView) view.findViewById(R.id.pteam_points);
                        pteam_comments = (TextView) view.findViewById(R.id.textView4);
                        pteam_avatar = (ImageView) view.findViewById(R.id.pteam_avatar);

                        pteam_avatar.setVisibility(ImageView.INVISIBLE);
                        pteam_comments.setVisibility(TextView.INVISIBLE);
                        pteam_name.setVisibility(TextView.INVISIBLE);
                        pteam_points.setVisibility(TextView.INVISIBLE);

                        Toast.makeText(getActivity(), "error, check internet connection", Toast.LENGTH_SHORT).show();
                    }
                });







                int teampk = settings2.getInt("team", 0);
                if(teampk != -1) {

                    Call<List<Comment>> call2 = apiService.getCommentList("Token " + token, teampk);


                    call2.enqueue(new Callback<List<Comment>>() {
                        @Override
                        public void onResponse(Call<List<Comment>> call2, Response<List<Comment>> response) {
                            if (response.isSuccessful()) {
                                progress_spinner.dismiss();
                                lv2 = (ListView) view.findViewById(R.id.myList2);
                                cl = new ArrayList<Comment>();
                                cl.addAll(response.body());
                                PostAdapter2 adapter = new PostAdapter2(getActivity(), R.layout.commentrow, cl);
                                lv2.setAdapter(adapter);

                            } else {
                                progress_spinner.dismiss();

                            }
                        }

                        @Override
                        public void onFailure(Call<List<Comment>> call, Throwable t) {
                            progress_spinner.dismiss();
                            Toast.makeText(getActivity(), "error, check internet connection", Toast.LENGTH_SHORT).show();


                        }
                    });


                    Call<List<Change>> call3 = apiService.getChangeList("Token " + token, teampk);

                    call3.enqueue(new Callback<List<Change>>() {
                        @Override
                        public void onResponse(Call<List<Change>> call3, Response<List<Change>> response) {
                            if (response.isSuccessful()) {


                                lv3 = (ListView) view.findViewById(R.id.myList3);
                                chl = new ArrayList<Change>();
                                chl.addAll(response.body());
                                PostAdapter3 adapter = new PostAdapter3(getActivity(), R.layout.changerow, chl);
                                lv3.setAdapter(adapter);

                            } else {
                                Toast.makeText(getActivity(), "check connection", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Change>> call, Throwable t) {


                            Toast.makeText(getActivity(), "check connection", Toast.LENGTH_SHORT).show();


                        }
                    });

                }
            }});

        return view;
        }
    public class PostAdapter2 extends ArrayAdapter {
        private List<Comment> comments;
        private int resource;
        private LayoutInflater inflater;
        public PostAdapter2(@NonNull Context context, @LayoutRes int resource, @NonNull List<Comment> objects) {
            super(context, resource, objects);
            comments = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE)   ;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView  == null)
            {
                convertView = inflater.inflate(resource, null);
            }
            TextView content;



            content = (TextView) convertView.findViewById(R.id.comment_content);

            content.setText(comments.get(position).getContent());


            return convertView;
        }
    }
    public class PostAdapter3 extends ArrayAdapter{
        private List<Change> changes;
        private int resource;
        private LayoutInflater inflater;
        public PostAdapter3(@NonNull Context context, @LayoutRes int resource, @NonNull List<Change> objects) {
            super(context, resource, objects);
            changes = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE)   ;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView  == null)
            {
                convertView = inflater.inflate(resource, null);
            }
            TextView note;



            note= (TextView) convertView.findViewById(R.id.change_note);

            if(changes.get(position).getValue() >0) {
                note.setText("+"+String.valueOf(changes.get(position).getValue()) + " " + changes.get(position).getNote());
                note.setTextColor(Color.parseColor("#00FF00"));
            }
            else if(changes.get(position).getValue() <0) {
                note.setText(String.valueOf(changes.get(position).getValue()) + " " + changes.get(position).getNote());
                note.setTextColor(Color.parseColor("#FF0000"));
            }
            return convertView;
        }
    }
}
