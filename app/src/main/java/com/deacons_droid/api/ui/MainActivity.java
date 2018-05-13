package com.deacons_droid.api.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.deacons_droid.R;
import com.deacons_droid.api.model.Login;
import com.deacons_droid.api.model.User;
import com.deacons_droid.api.service.PostClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Retrofit.Builder builder;

    Retrofit retrofit;

   PostClient postClient;

    Context context = this;
    EditText username ;
    EditText password ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        builder = new Retrofit.Builder()
                .baseUrl(this.getResources().getString(R.string.url) +"/")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();
        postClient = retrofit.create(PostClient.class);

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (settings.contains("token"))
        {
            startActivity(new Intent(MainActivity.this, Feed2.class));
        }
        findViewById(R.id.btn_login).setOnClickListener((view)->{login();  });
        findViewById(R.id.btn_signup).setOnClickListener((view)->{ startActivity(new Intent(MainActivity.this, Register.class));  });
    }
    private static String token;
    private void login()
    {
        username =(EditText) findViewById(R.id.username_txt);
       String usernamestr = username.getText().toString();
       password = (EditText) findViewById(R.id.password_txt);
        String passwordstr = password.getText().toString();
        Login login = new Login(usernamestr,passwordstr);
        Call<User> call = postClient.login(login);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful())
                {
                    token = response.body().getToken();
                    SharedPreferences settings = PreferenceManager
                            .getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("token", token);
                    editor.putString("username", usernamestr);
                    editor.commit();
                    startActivity(new Intent(MainActivity.this, Feed2.class));
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
}
