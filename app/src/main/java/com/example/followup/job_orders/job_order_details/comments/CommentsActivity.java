package com.example.followup.job_orders.job_order_details.comments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.followup.R;
import com.example.followup.job_orders.job_order_details.comments.mentions.User;
import com.example.followup.job_orders.job_order_details.comments.mentions.UserPresenter;
import com.example.followup.utils.RealPathUtil;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.otaliastudios.autocomplete.AutocompletePolicy;
import com.otaliastudios.autocomplete.AutocompletePresenter;
import com.otaliastudios.autocomplete.CharPolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity {

    ImageView back, add_comment, add_attach;
    EditText et_comment;
    private Autocomplete mentionsAutocomplete;
    TextView filesChosen;
    RecyclerView recyclerView;
    ProgressBar loading;
    private ProgressDialog dialog;

    ArrayList<Comment_item> comments_list;
    Comments_adapter comments_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    int jobOrderId, projectId;

    WebserviceContext ws;
    List<String> filesSelected;

    private static final int FILES_REQUEST_CODE = 764546;


    ArrayList<User> users_list;
    ArrayList<String> users_mentioned_ids_list;

    private void setupMentionsAutocomplete() {
        float elevation = 6f;
        Drawable backgroundDrawable = new ColorDrawable(Color.WHITE);
        AutocompletePolicy policy = new CharPolicy('@'); // Look for @mentions
        AutocompletePresenter<User> presenter = new UserPresenter(this, users_list);
        AutocompleteCallback<User> callback = new AutocompleteCallback<User>() {
            @Override
            public boolean onPopupItemClicked(@NonNull Editable editable, @NonNull User item) {
                // Replace query text with the full name.
                int[] range = CharPolicy.getQueryRange(editable);
                if (range == null) return false;
                int start = range[0];
                int end = range[1];
                String replacement = item.getUsername();
                editable.replace(start, end, replacement);
                // This is better done with regexes and a TextWatcher, due to what happens when
                // the user clears some parts of the text. Up to you.
                editable.setSpan(new StyleSpan(Typeface.BOLD), start, start + replacement.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                users_mentioned_ids_list.add(String.valueOf(item.getUser_id()));
                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {
            }
        };

        mentionsAutocomplete = Autocomplete.<User>on(et_comment)
                .with(elevation)
                .with(backgroundDrawable)
                .with(policy)
                .with(presenter)
                .with(callback)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        initFields();
        back.setOnClickListener(view -> onBackPressed());
        add_comment.setOnClickListener(view -> {
            if (validateFields()) {
                addComment();
            }
        });
        add_attach.setOnClickListener(v -> Dexter.withContext(getBaseContext())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            pickFromGallery();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check());

        getComments(currentPageNum);
    }

    private void initFields() {
        ws = new WebserviceContext(this);

        filesSelected = new ArrayList<>();
        back = findViewById(R.id.back);
        add_comment = findViewById(R.id.add_comment);
        add_attach = findViewById(R.id.add_attach);
        et_comment = findViewById(R.id.et_comment);
        filesChosen = findViewById(R.id.files_chosen);
        loading = findViewById(R.id.loading);
        recyclerView = findViewById(R.id.recycler_view);
        jobOrderId = getIntent().getIntExtra("job_order_id", 0);
        projectId = getIntent().getIntExtra("project_id", 0);
        comments_list = new ArrayList<>();
        users_list = new ArrayList<>();
        users_mentioned_ids_list = new ArrayList<>();
        getMentionUsers();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please, Wait...");
        dialog.setCancelable(false);

        initRecyclerView();
    }

    public void getMentionUsers() {

        ws.getApi().getMentionUsers(UserUtils.getAccessToken(getBaseContext()), projectId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        JSONObject responseObject = new JSONObject(response.body().string());
                        JSONArray usersArray = responseObject.getJSONArray("data");
                        setMentionsUsersList(usersArray);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d("Error Throw", t.toString());
                Log.d("commit Test Throw", t.toString());
                Log.d("Call", t.toString());
                Toast.makeText(getBaseContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setMentionsUsersList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {

                JSONObject currentObject = list.getJSONObject(i);
                final int user_id = currentObject.getInt("id");
                final String user_name = currentObject.getString("name");

                users_list.add(new User(user_id, user_name));
            }

            setupMentionsAutocomplete();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        comments_adapter = new Comments_adapter(getBaseContext(), comments_list);
        recyclerView.setAdapter(comments_adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !mHasReachedBottomOnce) {
                    mHasReachedBottomOnce = true;

                    if (currentPageNum <= lastPageNum)
                        getComments(currentPageNum);

                }
            }
        });
    }

    private boolean validateFields() {
        if (et_comment.length() == 0) {
            et_comment.setError("This is required field");
            return false;
        }
        return true;
    }

    public void getComments(int pageNum) {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getComments(UserUtils.getAccessToken(getBaseContext()), jobOrderId, pageNum).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        JSONObject responseObject = new JSONObject(response.body().string());
                        JSONArray commentsArray = responseObject.getJSONArray("data");
                        setCommentsList(commentsArray);
                        JSONObject metaObject = responseObject.getJSONObject("meta");
                        lastPageNum = metaObject.getInt("last_page");
                    }

                    loading.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d("Error Throw", t.toString());
                Log.d("commit Test Throw", t.toString());
                Log.d("Call", t.toString());
                Toast.makeText(getBaseContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCommentsList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {

                JSONObject currentObject = list.getJSONObject(i);
                final int id = currentObject.getInt("id");
                final int user_id = currentObject.getInt("user_id");
                final String comment = currentObject.getString("body");
                final String user_name = currentObject.getJSONObject("user").getString("name");
                final String user_avatar = currentObject.getJSONObject("user").getString("avatar");
                final String created_at = currentObject.getString("created_at");
                final JSONArray attachment_arr = currentObject.getJSONArray("attaches");
                String attachment = "";
                if (attachment_arr.length() != 0) {
                    JSONObject currentAttach = attachment_arr.getJSONObject(0);
                    attachment = currentAttach.getString("file");
                }
                comments_list.add(new Comment_item(id, user_id, comment, user_name, user_avatar, created_at, attachment));

            }

            comments_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addComment() {
        Map<String, RequestBody> map = setCommentMapRequestBody();
        List<MultipartBody.Part> fileToUpload = addAttaches(filesSelected);
        dialog.show();
        ws.getApi().addComment(UserUtils.getAccessToken(getBaseContext()), fileToUpload, map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        resetAddCommentAndRefreshList();

                    } else {
                        assert response.errorBody() != null;
                        JSONObject res = new JSONObject(response.errorBody().string());
                        Toast.makeText(getBaseContext(), res.getString("error"), Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private Map<String, RequestBody> setCommentMapRequestBody() {
        Map<String, RequestBody> map = new HashMap<>();
        map.put("body", RequestBody.create(MediaType.parse("text/plain"), et_comment.getText().toString()));
        map.put("job_order_id", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jobOrderId)));
        map.put("comment_id", RequestBody.create(MediaType.parse("text/plain"), ""));
        map.put("user_ids", RequestBody.create(MediaType.parse("text/plain"), TextUtils.join(",", users_mentioned_ids_list)));

        return map;
    }

    private List<MultipartBody.Part> addAttaches(List<String> files) {
        List<MultipartBody.Part> list = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            File file = new File(files.get(i));
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
            list.add(part);
        }
        return list;
    }

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // Launching the Intent
        startActivityForResult(intent, FILES_REQUEST_CODE);
    }

    private void resetAddCommentAndRefreshList() {
        et_comment.setText("");
        filesSelected.clear();
        filesChosen.setVisibility(View.GONE);

        comments_list.clear();
        currentPageNum = 1;
        getComments(currentPageNum);

    }


    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == FILES_REQUEST_CODE) {
                filesSelected.clear();
                //data.getData returns the content URI for the selected files
                if (data == null) {
                    return;
                } else if (data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        filesSelected.add(RealPathUtil.getRealPath(getBaseContext(), uri));
                    }
                } else {
                    Uri uri = data.getData();
                    filesSelected.add(RealPathUtil.getRealPath(getBaseContext(), uri));
                }
                filesChosen.setVisibility(View.VISIBLE);
                filesChosen.setText(filesSelected.size() + " Files Selected");

                Log.e("Data selected", filesSelected.toString());
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}