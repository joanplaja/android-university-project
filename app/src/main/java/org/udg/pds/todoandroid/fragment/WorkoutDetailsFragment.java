package org.udg.pds.todoandroid.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.UpdateProfileActivity;
import org.udg.pds.todoandroid.entity.DictionaryImages;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.PostBody;
import org.udg.pds.todoandroid.entity.Workout;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Workout Details: ";

    DictionaryImages dictionaryImages = new DictionaryImages();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TodoApi mTodoService;
    private Long id;
    private Workout workout;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button cancel, delete;

    //Coses relatives al dialeg del post
    private AlertDialog dialogPost;
    private Button cancelPostButton, postButton, choosImageButton;
    private ImageView postImage;
    private EditText postDescription;
    Uri selectedImageUri = null;
    String storedImageUri = "";

    private BroadcastReceiver mMessageReceiver;

    public WorkoutDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkoutDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutDetailsFragment newInstance(String param1, String param2) {
        WorkoutDetailsFragment fragment = new WorkoutDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String title = intent.getExtras().getString("title");
                String body = intent.getExtras().getString("body");

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_personalizado,
                    (ViewGroup) getActivity().findViewById(R.id.custom_toast_container));

                TextView messageToast = (TextView) layout.findViewById(R.id.text);

                String text = title + body;

                messageToast.setText(text);

                Toast toast = new Toast(getActivity());

                toast.setGravity(Gravity.TOP, 0, 0);

                toast.setDuration(Toast.LENGTH_LONG);

                toast.setView(layout);

                toast.show();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        id = getArguments().getLong("id");
        Log.i(TAG, id.toString());
        View v = inflater.inflate(R.layout.fragment_workout_details, container, false);
        Button deleteButton;
        deleteButton = v.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDelete();
            }
        });
        Button postButton;
        postButton = v.findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { handlePost(); }
        });
        return v;
    }

    private void handlePost() {
        createNewPostDialog();
    }

    private void createNewPostDialog() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View view = getLayoutInflater().inflate(R.layout.post_dialog, null);
        cancelPostButton = (Button)view.findViewById(R.id.cancelButton);
        postButton = (Button)view.findViewById(R.id.postButton);
        postDescription = (EditText)view.findViewById(R.id.description);
        postImage = (ImageView) view.findViewById(R.id.image);

        postImage.setImageResource(R.drawable.ic_menu_camera);
        postDescription = (EditText)view.findViewById(R.id.description);

        choosImageButton = (Button)view.findViewById(R.id.chooseImageButton);

        dialogBuilder.setView(view);
        dialogPost = dialogBuilder.create();
        dialogPost.show();

        choosImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Primer fer la crida a la api per guarda la imatge a la base de dades i que en retorni la uri com a resposta
                if (selectedImageUri != null) {
                    try {
                        InputStream is = getActivity().getContentResolver().openInputStream(selectedImageUri);
                        String extension = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(getActivity().getContentResolver().getType(selectedImageUri));
                        File tempFile = File.createTempFile("upload", extension, getActivity().getCacheDir());
                        FileOutputStream outs = new FileOutputStream(tempFile);
                        IOUtils.copy(is, outs);
                        // create RequestBody instance from file
                        RequestBody requestFile =
                            RequestBody.create(
                                MediaType.parse(getActivity().getContentResolver().getType(selectedImageUri)),
                                tempFile
                            );

                        // MultipartBody.Part is used to send also the actual file name
                        MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", tempFile.getName(), requestFile);

                        Call<String> call = mTodoService.uploadImage(body);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    Picasso.get().load(response.body()).fit().centerCrop().into(postImage);
                                    storedImageUri = response.body();
                                    makeCreatePostCall();
                                }
                                else {

                                    Log.i(TAG, "Ha anat malament la crida per guarda la imatge...");
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(getActivity(), "Ha fallat la crida a la API per guardar la imatge...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    makeCreatePostCall();
                }

                NavDirections action =
                    WorkoutDetailsFragmentDirections.actionWorkoutDetailsFragmentToActionWorkoutList();
                Navigation.findNavController(getView()).navigate(action);
                dialogPost.dismiss();

            }
        });


        cancelPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPost.dismiss();
            }
        });
    }

    private void makeCreatePostCall() {
        PostBody body = new PostBody();
        body.workoutId = id;
        body.description = postDescription.getText().toString();
        body.imageUrl = storedImageUri;
        Call<IdObject> call = mTodoService.createPost(body);
        call.enqueue(new Callback<IdObject>() {
            @Override
            public void onResponse(Call<IdObject> call, Response<IdObject> response) {
                if (response.isSuccessful()) {
                }
                else{
                    Toast toast = Toast.makeText(getActivity(), "Something went wrong with the API call.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<IdObject> call, Throwable t) {
                Toast toast = Toast.makeText(getActivity(), "Something went wrong with the API call.", Toast.LENGTH_SHORT);
                toast.show();
            }

        });
    }

    public void createNewDeleteDialog() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View view = getLayoutInflater().inflate(R.layout.delete_popup, null);
        cancel = (Button)view.findViewById(R.id.cancelButton);
        delete = (Button)view.findViewById(R.id.deleteButton);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<String> call = mTodoService.deleteWorkout(id.toString());

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            Log.i(TAG, "onResponse: " + response.body());
                            NavDirections action =
                                WorkoutDetailsFragmentDirections.actionWorkoutDetailsFragmentToActionWorkoutList();
                            Navigation.findNavController(getView()).navigate(action);
                            dialog.dismiss();

                        } else {
                            Toast.makeText(WorkoutDetailsFragment.this.getContext(), "Error deleting the Workout", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(WorkoutDetailsFragment.this.getContext(), "Error making call to delete the Workout", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: Aqui fariem el cancel");
                dialog.dismiss();
            }
        });
    }

    private void handleDelete() {
        createNewDeleteDialog();
    }

    @Override
    public void onStart(){
        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((mMessageReceiver),
            new IntentFilter("Notification Data")
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    public void loadDetails() {
        Call<Workout> call = mTodoService.getWorkout(id.toString());

        call.enqueue(new Callback<Workout>() {
            @Override
            public void onResponse(Call<Workout> call, Response<Workout> response) {
                if (response.isSuccessful()) {
                    //Emplenem el workout amb el que rebem de la crida.
                    workout = response.body();
                    //Recuperem els camps que els interessen i els guardem a variables.
                    String type = workout.type;
                    Integer icon = dictionaryImages.images.get(type);
                    Double initialLatitude = workout.route.initialLatitude;
                    Double initialLongitude = workout.route.initialLongitude;
                    Double lastLatitude;
                    Double lastLongitude;
                    if(workout.route.points.size() != 0) {
                        lastLatitude = workout.route.points.get(workout.route.points.size()-1).latitude;
                        lastLongitude = workout.route.points.get(workout.route.points.size()-1).longitude;
                    } else {
                        lastLatitude = 0.0;
                        lastLongitude = 0.0;
                    }
                    Double latitudeDiff = lastLatitude - initialLatitude;
                    Double longitudeDiff = lastLongitude - initialLongitude;

                    //Emplenem els valors de les Views
                    TextView typeView = WorkoutDetailsFragment.this.getView().findViewById(R.id.type);
                    typeView.setText(type.toUpperCase());

                    ImageView iconView = WorkoutDetailsFragment.this.getView().findViewById(R.id.icon);
                    iconView.setImageResource(icon);

                    TextView initialLatitudeView = WorkoutDetailsFragment.this.getView().findViewById(R.id.initialLatitude);
                    initialLatitudeView.setText(initialLatitude.toString());

                    TextView initialLongitudeView = WorkoutDetailsFragment.this.getView().findViewById(R.id.initialLongitude);
                    initialLongitudeView.setText(initialLongitude.toString());

                    TextView lastLatitudeView = WorkoutDetailsFragment.this.getView().findViewById(R.id.lastLatitude);
                    lastLatitudeView.setText(lastLatitude.toString());

                    TextView lastLongitudeView = WorkoutDetailsFragment.this.getView().findViewById(R.id.lastLongitude);
                    lastLongitudeView.setText(lastLongitude.toString());

                    TextView latitudeDiffView = WorkoutDetailsFragment.this.getView().findViewById(R.id.latitudeDiff);
                    if(latitudeDiff > 0) {
                        //hem anat cap a l'est
                        latitudeDiffView.setText(latitudeDiff.toString() + " East");
                    } else {
                        //hem anat cap a l'oest
                        latitudeDiff = -latitudeDiff;
                        latitudeDiffView.setText(latitudeDiff.toString() + " West");
                    }

                    TextView longitudeDiffView = WorkoutDetailsFragment.this.getView().findViewById(R.id.longitudeDiff);
                    if(longitudeDiff > 0) {
                        //hem anat cap al nord
                        longitudeDiffView.setText(longitudeDiff.toString() + " North");
                    } else {
                        //hem anat cap al sud
                        longitudeDiff = -longitudeDiff;
                        longitudeDiffView.setText(longitudeDiff.toString() + " South");
                    }


                } else {
                    Toast.makeText(WorkoutDetailsFragment.this.getContext(), "Error reading specific Workout", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Workout> call, Throwable t) {
                Toast.makeText(WorkoutDetailsFragment.this.getContext(), "Error making call", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 1) {
            selectedImageUri = data.getData();
            postImage.setImageURI(selectedImageUri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.loadDetails();
    }
}
