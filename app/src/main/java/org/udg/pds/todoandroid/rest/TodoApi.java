package org.udg.pds.todoandroid.rest;

import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserLogin;
import org.udg.pds.todoandroid.entity.UserRegister;
import org.udg.pds.todoandroid.entity.UserUpdate;
import org.udg.pds.todoandroid.entity.Workout;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by imartin on 13/02/17.
 */
public interface TodoApi {
    @POST("/users/login")
    Call<User> login(@Body UserLogin login);

    @POST("users/logout")
    Call<String> logout();

    @POST ("users/register")
    Call<String> register(@Body UserRegister register);

    @GET("/users/check")
    Call<String> check();

    @POST("/tasks")
    Call<IdObject> addTask(@Body Task task);

    @GET("/tasks")
    Call<List<Task>> getTasks();

    @GET("/tasks/{id}")
    Call<Task> getTask(@Path("id") String id);

    @POST("/images")
    @Multipart
    Call<String> uploadImage(@Part MultipartBody.Part file);

    @GET("/users/{id}")
    Call<User> getUser(@Path("id") Long id);

    @GET("/users/me")
    Call<User> getUserMe();

    @PUT("/users/me")
    Call<String> updateUserMe(@Body UserUpdate updateUser);

    @GET("/workouts")
    Call<List<Workout>> getWorkouts();

    @GET("/workouts/{wid}")
    Call<Workout> getWorkout(@Path("wid") String workoutId);

    @POST("/workouts")
    Call<IdObject> createWorkout(@Body Workout workout);

    //@POST("/workouts/{id}/routes")
    //Call<IdObject> createRoute(@Path("id") String workoutId,@Body Route route);

    @POST("/routes/{id}/points")
    Call<IdObject> addPoints(@Path("id") String workoutId,@Body Double[][] points);

    @GET("/users")
    Call<List<User>> searchUser(@Query("search") String name);


    @GET("/users/id/{uname}")
    Call<Long> getIdByUsername(@Path("uname") String uname);


    @DELETE("/workouts/{wid}")
    Call<String> deleteWorkout(@Path("wid") String workoutId);


}

