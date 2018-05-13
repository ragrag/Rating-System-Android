package com.deacons_droid.api.service;

import com.deacons_droid.api.model.Change;
import com.deacons_droid.api.model.Comment;
import com.deacons_droid.api.model.Login;
import com.deacons_droid.api.model.Post;
import com.deacons_droid.api.model.User;
import com.deacons_droid.api.model.UserStaff;

import java.math.BigInteger;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Raggi on 8/22/2017.
 */


    public interface PostClient{
        // Request method and URL specified in the annotation
        // Callback for the parsed response is the last parameter



    @POST("api/auth/token/")
    Call<User> login(@Body Login login);


    @GET("api/teams")
    Call<List<Post>> getPostList(@Header("Authorization") String token);

    @GET("api/team/{id}")
    Call<Post> getTeamid(@Header("Authorization") String token,@Path("id") Integer id);

    @GET("api/user/team")
    Call<Post> getTeam(@Header("Authorization") String token);


    @GET("api/user/staff")
    Call<UserStaff> getStaff(@Header("Authorization") String token);

    @GET("api/comments/{id}")
    Call<List<Comment>> getCommentList(@Header("Authorization") String token,@Path("id") Integer id);

    @GET("api/changes/{id}")
    Call<List<Change>> getChangeList(@Header("Authorization") String token, @Path("id") Integer id);

    @GET("api/comment/delete/{id}")
    Call<ResponseBody> deleteComment(@Header("Authorization") String token, @Path("id") Integer id);


    @FormUrlEncoded
    @POST("api/comment/create/{id}")
    Call<ResponseBody> createComment(@Header("Authorization") String token,@Path("id") int teamid,@Field("content") String content,@Field("public") Boolean p);


    @FormUrlEncoded
    @POST("api/points/{id}/{val}")
    Call<ResponseBody> updatePoints(@Header("Authorization") String token,@Path("id") int teamid,@Field("value") Integer value,@Field("note") String note,@Path("val") Integer val);

    @FormUrlEncoded
    @POST("api/user/create/")
    Call<ResponseBody> register(@Field("username") String username,@Field("password") String password);

    }

