package com.northcoders.tatooine.ui.addpost;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.northcoders.tatooine.R;
import com.northcoders.tatooine.databinding.ActivityAddPostBinding;
import com.northcoders.tatooine.model.Tattoo;
import com.northcoders.tatooine.ui.main.MainActivity;
import com.northcoders.tatooine.ui.main.MainViewModel;
import com.northcoders.tatooine.ui.userprofileview.UserProfileViewActivity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    boolean[] selectedStyles;
    ArrayList<Integer> stylesList = new ArrayList<>();
    String[] stylesArray = {"REALISM", "WATERCOLOUR", "WILDCARD"};
    BottomNavigationView bottomNavigationView;
    AppCompatImageView uploadImagePreview;
    MaterialButton uploadButton;
    MainViewModel viewModel;
    ActivityAddPostBinding binding;
    final String[] requestId = new String[1];
    Uri imageUri;
    AddPostActivityClickHandlers handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Tattoo post = new Tattoo();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_post);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set unbound views
        TextView selectStylesView = findViewById(R.id.selectStylesLayout);

        handler = new AddPostActivityClickHandlers(post, viewModel, this, imageUri, selectStylesView);

        binding.setClickHandler(handler);
        binding.setPost(post);

        // Bottom navigation bar functionality
        bottomNavigationView = findViewById(R.id.bottomNavBarView);
        bottomNavigationView.setSelectedItemId(R.id.addPost);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                if (item.getItemId() == R.id.profile) {
                    startActivity(new Intent(getApplicationContext(), UserProfileViewActivity.class));
                    return true;
                }
                if (item.getItemId() == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    return true;
                }
                return item.getItemId() == R.id.addPost;
            }
        });

        // Upload image functionality
        AppCompatImageView uploadImagePreview = findViewById(R.id.uploadImagePreview);
        MaterialButton uploadButton = findViewById(R.id.uploadImageButton);
        Uri[] imageUri = new Uri[1];

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        imageUri[0] = uri;
                        uploadImagePreview.setImageURI(uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                }
        );

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        MaterialButton submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaManager.init(getApplicationContext());
                String requestId = MediaManager.get()
                        .upload(imageUri[0])
                        .unsigned("preset_1")
                        .callback(new UploadCallback() {
                            @Override
                            public void onStart(String requestId) {
                                Toast.makeText(getApplicationContext(), "Upload started...", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onProgress(String requestId, long bytes, long totalBytes) {
                                double progress = (double) bytes / totalBytes;
                                Toast.makeText(getApplicationContext(), "Uploading:" + progress, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                Toast.makeText(getApplicationContext(), "Upload complete!", Toast.LENGTH_SHORT).show();

                                viewModel.addPost(new Tattoo(2212L, "Price here", "Time taken here", "Image URI here", null, null));
                                Toast.makeText(getApplicationContext(), "Post added!", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }

                            @Override
                            public void onError(String requestId, ErrorInfo error) {
                                Toast.makeText(getApplicationContext(), "Upload failed!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onReschedule(String requestId, ErrorInfo error) {

                            }
                        }).dispatch();

            };
        });
    }

}
