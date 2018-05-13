package com.deacons_droid.api.ui;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.deacons_droid.R;
import com.deacons_droid.api.model.Change;
import com.deacons_droid.api.model.Comment;
import com.deacons_droid.api.model.Post;
import com.deacons_droid.api.service.PostClient;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Feed2 extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener {
    private static final String Tag = "MainFeed";
    private static String url;
    public SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout swipeRefreshLayout2;
    Retrofit.Builder builder;
    Retrofit retrofit;
    PostClient apiService;

    SharedPreferences settings2 ;
    ListView lv ;
    ArrayList<Post> al;
    ArrayList<String> noInternet;
    Post t;

    ListView lv2;
    ArrayList<Comment> cl;
String tokenx;
    ListView lv3;
    ArrayList<Change> chl;

    Context context = this;
    TextView pteam_name;
    TextView pteam_points;
    TextView pteam_comments;
    ImageView pteam_avatar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed2);
        url = this.getResources().getString(R.string.url);
        builder = new Retrofit.Builder()
                .baseUrl(url+"/")
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
        apiService = retrofit.create(PostClient.class);


        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);
        TabLayout tablayout = (TabLayout) findViewById(R.id.tabs);
        tablayout.setupWithViewPager(mViewPager);
        tablayout.getTabAt(0).setIcon(R.drawable.chart);
        tablayout.getTabAt(1).setIcon(R.drawable.profile_icon);
        settings2 = PreferenceManager.getDefaultSharedPreferences(context);
        String token = settings2.getString("token", "");

        if (!settings2.contains("team"))
        {

            SharedPreferences.Editor editor = settings2.edit();
            editor.putInt("team", -1);
            editor.commit();

        }

        tokenx = token;
        postlist(token);



        new MyAsyncTask().execute();

        Intent i = new Intent("Alarm");

    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            getTeam(tokenx);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            commentlist(tokenx);
        }
    }






private void setupViewPager(ViewPager viewPager)
{
   SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
    adapter.addFragment( new tab1_fragment(), "");
    adapter.addFragment(new tab2_fragment(), "");
    viewPager.setAdapter(adapter);
}

private void postlist(String token)
{

    Call<List<Post>> call = apiService.getPostList("Token "+token);
    Dialog progress_spinner;
    progress_spinner = LoadingSpinner.Spinner(this);
    progress_spinner.show();
    call.enqueue(new Callback<List<Post>>() {
        @Override
        public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
            if (response.isSuccessful())
            {
                progress_spinner.dismiss();
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
                lv = (ListView) findViewById(R.id.myList);
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
                al = new ArrayList<Post>();
                al.addAll(response.body());
               PostAdapter adapter = new PostAdapter(getApplicationContext(),R.layout.postrow,al);
                lv.setAdapter(adapter);
                swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
                swipeRefreshLayout.setOnRefreshListener(Feed2.this);
            }
            else
            {
                progress_spinner.dismiss();
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(Feed2.this, "fail", Toast.LENGTH_SHORT).show();
                settings2.edit().remove("token").commit();
                settings2.edit().remove("username").commit();
                Intent i = new Intent(Feed2.this, MainActivity.class);
                startActivity(i);
            }
        }

        @Override
        public void onFailure(Call<List<Post>> call, Throwable t) {
            progress_spinner.dismiss();
            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(false);
            lv = (ListView) findViewById(R.id.myList);
            noInternet = new ArrayList<String>();
            noInternet.add("Error loading, Check internet connection");
            PostAdapter_no adapter = new PostAdapter_no(getApplicationContext(),R.layout.no_internet,noInternet);
            lv.setAdapter(adapter);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(Feed2.this);
            Toast.makeText(Feed2.this, "error, check internet connection", Toast.LENGTH_SHORT).show();




        }
    });
}








    private void commentlist(String token)
    {


        int teampk = settings2.getInt("team", 0);
        if(teampk != -1) {
            Call<List<Comment>> call = apiService.getCommentList("Token " + token, teampk);

            call.enqueue(new Callback<List<Comment>>() {
                @Override
                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                    if (response.isSuccessful()) {


                        lv2 = (ListView) findViewById(R.id.myList2);
                        lv2.setOnTouchListener(new ListView.OnTouchListener() {
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
                        PostAdapter2 adapter = new PostAdapter2(getApplicationContext(), R.layout.commentrow, cl);
                        lv2.setAdapter(adapter);

                    } else {

                    }
                }

                @Override
                public void onFailure(Call<List<Comment>> call, Throwable t) {


                    lv2 = (ListView) findViewById(R.id.myList2);
                    noInternet = new ArrayList<String>();
                    noInternet.add("Error loading, Check internet connection");
                    PostAdapter_no adapter = new PostAdapter_no(getApplicationContext(), R.layout.no_internet, noInternet);
                    lv2.setAdapter(adapter);


                }
            });


            Call<List<Change>> call2 = apiService.getChangeList("Token " + token, teampk);

            call2.enqueue(new Callback<List<Change>>() {
                @Override
                public void onResponse(Call<List<Change>> call2, Response<List<Change>> response) {
                    if (response.isSuccessful()) {


                        lv3 = (ListView) findViewById(R.id.myList3);
                        lv3.setOnTouchListener(new ListView.OnTouchListener() {
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
                        chl = new ArrayList<Change>();
                        chl.addAll(response.body());
                        PostAdapter3 adapter = new PostAdapter3(getApplicationContext(), R.layout.changerow, chl);
                        lv3.setAdapter(adapter);

                    } else {

                    }
                }

                @Override
                public void onFailure(Call<List<Change>> call, Throwable t) {


                    lv3 = (ListView) findViewById(R.id.myList3);
                    noInternet = new ArrayList<String>();
                    noInternet.add("Error loading, Check internet connection");
                    PostAdapter_no adapter = new PostAdapter_no(getApplicationContext(), R.layout.no_internet, noInternet);
                    lv3.setAdapter(adapter);


                }
            });

        }
    }






    @Override
    public void onRefresh() {
        String token = settings2.getString("token", "");

        postlist(token);
        getTeam(token);
        commentlist(token);

    }




    private void getTeam(String token)
    {
        url = this.getResources().getString(R.string.url);



        Call<Post> call = apiService.getTeam("Token "+token);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful())
                {

                    t = response.body();

                    if (t.getAvatar() != null) {
                        Glide.with(context)
                                .load(url + t.getAvatar())
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into((ImageView) findViewById(R.id.pteam_avatar));
                    }
                    pteam_name = (TextView) findViewById(R.id.pteam_name);
                    pteam_points = (TextView) findViewById(R.id.pteam_points);
                    pteam_comments = (TextView) findViewById(R.id.textView4);
                    pteam_avatar = (ImageView) findViewById(R.id.pteam_avatar);
                    lv2 = (ListView) findViewById(R.id.myList2);
                    lv3 = (ListView) findViewById(R.id.myList3);
                    pteam_name.setText(t.getName());
                    pteam_points.setText(String.valueOf(t.getPoints()) + " Points");
                    if (t.getPoints() == null){
                        pteam_comments.setVisibility(TextView.INVISIBLE);
                        pteam_points.setVisibility(TextView.INVISIBLE);
                        pteam_avatar.setVisibility(ImageView.INVISIBLE);
                        lv2.setVisibility(ListView.INVISIBLE);
                        lv3.setVisibility(ListView.INVISIBLE);}
                    else {
                        pteam_avatar.setVisibility(ImageView.VISIBLE);
                        pteam_comments.setVisibility(TextView.VISIBLE);
                        pteam_points.setVisibility(TextView.VISIBLE);
                        lv2.setVisibility(ListView.VISIBLE);
                        lv3.setVisibility(ListView.VISIBLE);
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

                    Toast.makeText(Feed2.this, "Internal Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                pteam_name = (TextView) findViewById(R.id.pteam_name);
                pteam_points = (TextView) findViewById(R.id.pteam_points);
                pteam_comments = (TextView) findViewById(R.id.textView4);
                pteam_avatar = (ImageView) findViewById(R.id.pteam_avatar);
                lv2 = (ListView) findViewById(R.id.myList2);
                lv3 = (ListView) findViewById(R.id.myList3);



                pteam_avatar.setVisibility(ImageView.INVISIBLE);
                pteam_comments.setVisibility(TextView.INVISIBLE);
                pteam_name.setVisibility(TextView.INVISIBLE);
                pteam_points.setVisibility(TextView.INVISIBLE);
                lv2.setVisibility(ListView.INVISIBLE);
                lv3.setVisibility(ListView.INVISIBLE);

                Toast.makeText(Feed2.this, "error, check internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }





    public class PostAdapter extends ArrayAdapter{
    private List<Post> posts;
    private int resource;
    private LayoutInflater inflater;
    public PostAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Post> objects) {
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
        TextView team_rank;
        TextView team_name;
        TextView team_points;
        ImageView team_avatar;


        team_rank = (TextView) convertView.findViewById(R.id.team_rank);
        team_points = (TextView) convertView.findViewById(R.id.team_points);
        team_name = (TextView) convertView.findViewById(R.id.team_name);
        team_avatar = (ImageView) convertView.findViewById(R.id.team_avatar);


        if (posts.get(position).getAvatar() != null) {
            Glide.with(Feed2.this)
                    .load(url + posts.get(position).getAvatar())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(team_avatar);
        }
        team_rank.setText(String.valueOf(position+1));
        team_name.setText(posts.get(position).getName());
        team_points.setText(String.valueOf(posts.get(position).getPoints()));
        team_name.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(Feed2.this, Team_profile.class);
                myIntent.putExtra("teampk", posts.get(position).getPk());
                startActivity(myIntent);

            }
        });


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
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)   ;
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

    public class PostAdapter2 extends ArrayAdapter{
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
            TextView content;



            content = (TextView) convertView.findViewById(R.id.comment_content);

            content.setText(comments.get(position).getContent());


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
        moveTaskToBack(true);
    }



}
