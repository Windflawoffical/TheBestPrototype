package com.example.thebestprototype.API;

import com.example.thebestprototype.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface UserAPI {

    @POST("/user/sign-up")
    Call<User> signUp(@Body User user);
    @POST("/user/sign-in")
    Call<User> signIn(@Body User user);
    @POST("/user/update-location")
    Call<User> updateLocation(@Body User user);
    @GET("/user/get-all")
    Call<List<User>> getAllUsers();
}
