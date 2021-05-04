package org.udg.pds.todoandroid.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserSignInFacebook;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseRegisterLogin extends AppCompatActivity {

    CallbackManager callbackManager;
    LoginButton loginButton;
    TodoApi mTodoService;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_register_login);

        callbackManager = CallbackManager.Factory.create();
        mTodoService = ((TodoApp) this.getApplication()).getAPI();
        context = this;

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPermissions("email");
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Profile p = Profile.getCurrentProfile();
                String facebookToken = AccessToken.getCurrentAccessToken().getToken();
                String facebookId = p.getId();

                UserSignInFacebook userSignInFacebook = new UserSignInFacebook();
                userSignInFacebook.facebookId = facebookId;
                userSignInFacebook.facebookToken = facebookToken;

                Call<User> call = mTodoService.signInFacebook(userSignInFacebook);
                call.enqueue(new Callback<User>() {
                 @Override
                 public void onResponse(Call<User> call, Response<User> response) {
                     if(response.isSuccessful()){
                         ChooseRegisterLogin.this.startActivity(new Intent(ChooseRegisterLogin.this, NavigationActivity.class));
                         ChooseRegisterLogin.this.finish();
                     }
                     else{
                         try {
                             JSONObject jObjError = new JSONObject(response.errorBody().string());
                             String error = jObjError.getString("message");
                             if(error.equals("Facebook id does not exists")){
                                 System.out.println(error);

                                 //Fem la crida a google dels parametres que necesitarem
                                 GraphRequest request = GraphRequest.newMeRequest(
                                     loginResult.getAccessToken(),
                                     new GraphRequest.GraphJSONObjectCallback() {
                                         @Override
                                         public void onCompleted(
                                             JSONObject object,
                                             GraphResponse response) {
                                             // Application code
                                             try {
                                                 String email = object.getString("email");
                                                 Intent i = new Intent(ChooseRegisterLogin.this,Register.class);
                                                 i.putExtra("email",email);
                                                 i.putExtra("facebookId",facebookId);
                                                 i.putExtra("facebookToken",facebookToken);
                                                 startActivity(i);

                                             } catch (JSONException e) {
                                                 Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                             }
                                         }
                                     });
                                 Bundle parameters = new Bundle();
                                 parameters.putString("fields", "id,name,link,email,birthday,first_name,last_name");
                                 request.setParameters(parameters);
                                 request.executeAsync();


                             }
                             else Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                         } catch (Exception e) {
                             Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                         }
                     }
                 }

                 @Override
                 public void onFailure(Call<User> call, Throwable t) {
                     Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                 }
             });

                    //System.out.println("facebook token"+loginResult.getAccessToken().getApplicationId());
                    System.out.println("facebook token" + AccessToken.getCurrentAccessToken().getToken());
                GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                            // Application code
                            System.out.println(object.toString());
                        }
                    });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,birthday,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        Button b1 = findViewById(R.id.buttonChooseLogin);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseRegisterLogin.this, Login.class));
                ChooseRegisterLogin.this.finish();
            }
        });

        Button b2 = findViewById(R.id.buttonChooseRegister);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseRegisterLogin.this, Register.class));
                ChooseRegisterLogin.this.finish();
            }
        });
    }

    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
