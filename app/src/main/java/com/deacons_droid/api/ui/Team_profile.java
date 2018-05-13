package com.deacons_droid.api.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.deacons_droid.R;
import com.deacons_droid.api.model.Change;
import com.deacons_droid.api.model.Comment;
import com.deacons_droid.api.model.Post;
import com.deacons_droid.api.model.UserStaff;
import com.deacons_droid.api.service.PostClient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Team_profile extends AppCompatActivity {


    Retrofit.Builder builder;
    Retrofit retrofit;
    PostClient apiService;

    SharedPreferences settings2 ;
    ListView lv ;
    ArrayList<Comment> cl;
    ArrayList<Post> al;
    ArrayList<String> noInternet;
    Post t;
    Intent mIntent;
    int teampk;

    String content;
    int value;
    String note;
    UserStaff staff;
    String url;
    String token;
    Context context = this;
    TextView pteam_name;
    Button delcomment;

    TextView pteam_points;
    Button staff_comment;
    Button staff_points;
    ImageView pteam_avatar;
    Boolean p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_profile);



        url = this.getResources().getString(R.string.url);
        builder = new Retrofit.Builder()
                .baseUrl(url+"/")
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        apiService = retrofit.create(PostClient.class);

        settings2 = PreferenceManager.getDefaultSharedPreferences(context);
        token = settings2.getString("token", "");
        mIntent = getIntent();
        teampk = mIntent.getIntExtra("teampk", 0);


        new MyAsyncTask().execute();

    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            getStaff(token);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

            getTeam(token);
        }
    }




    private void getStaff(String token) {
        url = this.getResources().getString(R.string.url);


        Call<UserStaff> call = apiService.getStaff("Token " + token);
        call.enqueue(new Callback<UserStaff>() {
            @Override
            public void onResponse(Call<UserStaff> call, Response<UserStaff> response) {
                if (response.isSuccessful()) {

                    staff_comment = (Button) findViewById(R.id.staff_comment);
                    staff_points = (Button) findViewById(R.id.staff_points);
                    staff = response.body();

                    if(staff.getIsStaff() == true)
                    {
                        staff_points.setVisibility(staff_points.VISIBLE);
                        staff_comment.setVisibility(staff_comment.VISIBLE);

                    }


                } else {

                    Toast.makeText(Team_profile.this, "Error Fetching ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserStaff> call, Throwable t) {

                Toast.makeText(Team_profile.this, "error, check internet connection", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void getTeam(String token)
    {
        url = this.getResources().getString(R.string.url);


        Call<Post> call = apiService.getTeamid("Token "+token,teampk);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful())
                {



                    t=response.body();





                    if (t.getAvatar() != null) {
                        Glide.with(context)
                                .load(url + t.getAvatar())
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into((ImageView) findViewById(R.id.team_avatar));
                    }
                    pteam_name = (TextView) findViewById(R.id.team_name);
                    pteam_points = (TextView) findViewById(R.id.team_points);

                    pteam_avatar = (ImageView) findViewById(R.id.team_avatar);

                    pteam_name.setText(t.getName());
                    pteam_points.setText(String.valueOf(t.getPoints()) + " Points");
                    if (t.getPoints() == null){

                        pteam_points.setVisibility(TextView.INVISIBLE);
                        pteam_avatar.setVisibility(ImageView.INVISIBLE);}
                    else {
                        pteam_avatar.setVisibility(ImageView.VISIBLE);

                        pteam_points.setVisibility(TextView.VISIBLE);
                    }


                    pteam_name.setVisibility(TextView.VISIBLE);


                    if(t.getPk() != null) {
                        SharedPreferences.Editor editor = settings2.edit();
                        editor.putInt("team", t.getPk());
                        editor.commit();
                    }
                    else if(t.getPk() == null) {

                        SharedPreferences.Editor editor = settings2.edit();
                        editor.putInt("team", -1);
                        editor.commit();

                    }

                }
                else
                {

                    Toast.makeText(Team_profile.this, "Internal Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                pteam_name = (TextView) findViewById(R.id.pteam_name);
                pteam_points = (TextView) findViewById(R.id.pteam_points);

                pteam_avatar = (ImageView) findViewById(R.id.pteam_avatar);

                pteam_avatar.setVisibility(ImageView.INVISIBLE);

                pteam_name.setVisibility(TextView.INVISIBLE);
                pteam_points.setVisibility(TextView.INVISIBLE);

                Toast.makeText(Team_profile.this, "error, check internet connection", Toast.LENGTH_SHORT).show();
            }
        });


        Call<List<Comment>> call2 = apiService.getCommentList("Token " + token, teampk);

        call2.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call2, Response<List<Comment>> response) {
                if (response.isSuccessful()) {


                    lv = (ListView) findViewById(R.id.tlist2);
                    lv.setOnTouchListener(new ListView.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            int action = event.getAction();
                            switch (action) {
                                case MotionEvent.ACTION_DOWN:
                                    // Disallow ScrollView to intercept touch events.
                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                    break;

                                case MotionEvent.ACTION_UP:
                                    // Allow ScrollView to intercept touch events.
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                    break;
                            }

                            // Handle ListView touch events.
                            v.onTouchEvent(event);
                            return true;
                        }
                    });
                    cl = new ArrayList<Comment>();
                    cl.addAll(response.body());
                    PostAdapter2 adapter = new PostAdapter2(getApplicationContext(), R.layout.commentrow_admin, cl);
                    lv.setAdapter(adapter);

                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {


                lv = (ListView) findViewById(R.id.myList2);
                noInternet = new ArrayList<String>();
                noInternet.add("Error loading, Check internet connection");
                PostAdapter_no adapter = new PostAdapter_no(getApplicationContext(), R.layout.no_internet, noInternet);
                lv.setAdapter(adapter);


            }
        });



    }

    public void pointChange(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Points");

// Set up the input
        Context context = this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText pointValue = new EditText(context);
        pointValue.setHint("Value");
        layout.addView(pointValue);

        final EditText changeNote = new EditText(context);
        changeNote.setHint("Change Note");
        layout.addView(changeNote);
        builder.setView(layout);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                note = changeNote.getText().toString();
                value = Integer.parseInt(pointValue.getText().toString());

                Call<ResponseBody> call = apiService.updatePoints("Token "+token,teampk,value,note,value);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful())
                        {


                            Toast.makeText(Team_profile.this, "Points Updated", Toast.LENGTH_SHORT).show();

                            Intent myIntent = new Intent(Team_profile.this, Team_profile.class);
                            myIntent.putExtra("teampk", teampk);
                            startActivity(myIntent);



                        }
                        else
                        {

                            Toast.makeText(Team_profile.this, "connection error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {


                        Toast.makeText(Team_profile.this, "error, check internet connection", Toast.LENGTH_SHORT).show();
                    }
                });





            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();



    }

    public void addComment(View view) {






        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Comment");

// Set up the input

        Context context = this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final CheckBox checkBox = new CheckBox(context) ;
        checkBox.setText("public?");
        layout.addView(checkBox);



        final EditText comment = new EditText(context);
        comment.setHint("Add Comment");
        layout.addView(comment);

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

        builder.setView(layout);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                content = comment.getText().toString();
        if (checkBox.isChecked())
              p = true;
        else p =false;
                Call<ResponseBody> call = apiService.createComment("Token "+token,teampk,content,p);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful())
                        {



                            Toast.makeText(Team_profile.this, "Comment Added", Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(Team_profile.this, Team_profile.class);
                            myIntent.putExtra("teampk", teampk);
                            startActivity(myIntent);



                        }
                        else
                        {

                            Toast.makeText(Team_profile.this, "not sent", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {


                        Toast.makeText(Team_profile.this, "error, check internet connection", Toast.LENGTH_SHORT).show();
                    }
                });





            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();



    }

    public class PostAdapter2 extends ArrayAdapter {
        private List<Comment> comments;
        private int resource;
        private LayoutInflater inflater;
        public PostAdapter2(@NonNull Context context, @LayoutRes int resource, @NonNull List<Comment> objects) {
            super(context, resource, objects);
            comments = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)   ;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView  == null)
            {
                convertView = inflater.inflate(resource, null);
            }
            TextView content2;

            delcomment = (Button) convertView.findViewById(R.id.comment_delete);
            if(staff.getIsStaff() != true)
                delcomment.setVisibility(Button.GONE);
            content2 = (TextView) convertView.findViewById(R.id.comment_content_admin);



                content2.setText(comments.get(position).getContent());
            delcomment.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    Call<ResponseBody> call = apiService.deleteComment("Token "+token,comments.get(position).getPk());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful())
                            {
                                Toast.makeText(Team_profile.this, "Comment Deleted", Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(Team_profile.this, Team_profile.class);
                                myIntent.putExtra("teampk", teampk);
                                startActivity(myIntent);


                            }
                            else
                            {

                                Toast.makeText(Team_profile.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {


                            Toast.makeText(Team_profile.this, "error, check internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });



                }
            });

            return convertView;
        }
    }
    public class PostAdapter_no extends ArrayAdapter{
        private List<String> posts;
        private int resource;
        private LayoutInflater inflater;
        public PostAdapter_no(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            posts = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)   ;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView  == null)
            {
                convertView = inflater.inflate(resource, null);
            }
            TextView post_content;


            post_content = (TextView) convertView.findViewById(R.id.no_internet_txt);
            post_content.setTextColor(Color.parseColor("#FF0000"));
            post_content.setText(posts.get(position));

            return convertView;
        }
    }


    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(Team_profile.this, Feed2.class);

        startActivity(myIntent);
    }
}
