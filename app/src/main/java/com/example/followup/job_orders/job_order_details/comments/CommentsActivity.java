package com.example.followup.job_orders.job_order_details.comments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.followup.R;
import com.example.followup.job_orders.list.Job_order_item;
import com.example.followup.job_orders.list.Job_orders_adapter;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity {

    ImageView back;
    RecyclerView recyclerView;
    ProgressBar loading;

    ArrayList<Comment_item> comments_list;
    Comments_adapter comments_adapter;

    int currentPageNum = 1;
    int lastPageNum;
    boolean mHasReachedBottomOnce = false;

    int jobOrderId;

    WebserviceContext ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        initFields();
        back.setOnClickListener(view -> onBackPressed());

        getComments(currentPageNum);
    }

    private void initFields() {
        ws = new WebserviceContext(this);
        back = findViewById(R.id.back);
        loading = findViewById(R.id.loading);
        recyclerView = findViewById(R.id.recycler_view);
        jobOrderId = getIntent().getIntExtra("job_order_id",0);
        comments_list = new ArrayList<>();
        initRecyclerView();
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


    public void getComments(int pageNum) {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getComments(UserUtils.getAccessToken(getBaseContext()), jobOrderId, pageNum).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
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


                comments_list.add(new Comment_item(id, user_id, comment, user_name, user_avatar, created_at));

            }

            comments_adapter.notifyDataSetChanged();
            mHasReachedBottomOnce = false;
            currentPageNum++;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}