package org.udg.pds.todoandroid.rest;

import org.udg.pds.todoandroid.entity.Chat;
import org.udg.pds.todoandroid.entity.CreateChat;
import org.udg.pds.todoandroid.entity.Equipment;
import org.udg.pds.todoandroid.entity.FindFacebookFriends;
import org.udg.pds.todoandroid.entity.FindPhoneFriends;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Message;
import org.udg.pds.todoandroid.entity.NearRoutes;
import org.udg.pds.todoandroid.entity.Post;
import org.udg.pds.todoandroid.entity.Objective;
import org.udg.pds.todoandroid.entity.PostBody;
import org.udg.pds.todoandroid.entity.Route;
import org.udg.pds.todoandroid.entity.SendMessage;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserLogin;
import org.udg.pds.todoandroid.entity.UserRegister;
import org.udg.pds.todoandroid.entity.UserRegisterFacebook;
import org.udg.pds.todoandroid.entity.UserSignInFacebook;
import org.udg.pds.todoandroid.entity.UserUpdate;
import org.udg.pds.todoandroid.entity.Workout;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
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

    @POST("/users/signInFacebook")
    Call<User> signInFacebook(@Body UserSignInFacebook usFacebook);

    @POST("/users/registerFacebook")
    Call<String> registerFacebook(@Body UserRegisterFacebook usFacebook);

    @GET("/users/check/{token}")
    Call<String> check(@Path("token") String token);

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

    @POST("/workouts/{id}/points")
    Call<String> addPoints(@Path("id") String workoutId,@Body Double[][] points);

    @POST("/posts")
    Call<IdObject> createPost(@Body PostBody post);

    @GET("/posts")
    Call<List<Post>> getPosts();

    @GET("/users")
    Call<List<User>> searchUser(@Query("search") String name);


    @GET("/users/id/{uname}")
    Call<Long> getIdByUsername(@Path("uname") String uname);


    @DELETE("/workouts/{wid}")
    Call<String> deleteWorkout(@Path("wid") String workoutId);

    @POST("/routes/near")
    Call<List<Route>> getNearRoutes(@Body NearRoutes nearRoutes);

    @POST("/users/follow/{id}")
    Call <String>  followUser(@Path("id" )Long  id);

    @DELETE("/users/unfollow/{id}")
    Call <String>  unfollowUser(@Path("id" )Long  id);

    @GET ("/users/following")
    Call <List<User>> getOwnFollowing();

    @GET ("/users/followers")
    Call <List<User>> getOwnFollowers();

    @GET ("/users/following/{id}")
    Call <List<User>> getFollowing(@Path("id")Long id);

    @GET ("/users/followers/{id}")
    Call <List<User>> getFollowers(@Path("id")Long id);

    @POST("/users/findFacebookFriends")
    Call  <List<User>>  findFacebookFriends(@Body FindFacebookFriends findFacebookFriends);

    @POST("/users/findPhoneFriends")
    Call  <List<User>>  findPhoneFriends(@Body FindPhoneFriends findPhoneFriends);

    @GET("/equipment")
    Call<List<Equipment>> getEquipments();

    @POST("/equipment")
    Call<IdObject> addEquipment(@Body Equipment e);

    @DELETE("/equipment/{eid}")
    Call<String> deleteEquipment(@Path("eid") Long equipmentId);

    @GET("/users/private")
    Call <String> changePrivacy();

    @GET("/objectives")
    Call<List<Objective>> getObjectives();

    @DELETE("/objectives/{oid}")
    Call<String> deleteObjective(@Path("oid") Long objectiveId);

    @POST("/objectives")
    Call<IdObject> addObjective(@Body Objective o);

    @POST("/posts/like/{id}")
    Call <String>  likePost(@Path("id" )Long  id);

    @GET("/posts/likes/{id}")
    Call <List<User>> getLikes(@Path("id")Long id);

    @POST("/notifications/update-token")
    Call<String> updateToken(@Body String token);

    @DELETE("/posts/removelike/{id}")
    Call <String>  removeLikePost(@Path("id" )Long  id);

    @GET("/chats")
    Call<List<Chat>> getChats();

    @POST("/chats")
    Call<Long> createChat(@Body CreateChat chat);

    @GET("/chats/{id}/messages")
    Call<List<Message>> getMessages(@Path("id") Long chatId);


    @POST("/chats/{id}/messages")
    Call<Message> sendMessage(@Path("id") Long chatId,@Body SendMessage message);

    @GET("/users/searchUsersWithoutChat")
    Call<List<User>> searchUsersWithoutChat(@Query("search") String name);

}

