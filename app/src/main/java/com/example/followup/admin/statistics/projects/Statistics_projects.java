package com.example.followup.admin.statistics.projects;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.followup.R;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Statistics_projects extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView all_projects_no, cat_projects_no, _237_projects_no;
    AnimatedPieView all_projects_chart, cat_projects_chart, _237_projects_chart;
    ProgressBar loading;
    WebserviceContext ws;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics_projects, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        getAllData();
    }

    private void getAllData() {
        getProjectsData(1,getFilterMap(""),"");
        getProjectsData(1,getFilterMap("1"),"1");
        getProjectsData(1,getFilterMap("2"),"2");
    }

    private void initFields(View view) {
        ws = new WebserviceContext(getActivity());
        loading = view.findViewById(R.id.loading);
        all_projects_no = view.findViewById(R.id.all_projects_no);
        cat_projects_no = view.findViewById(R.id.cat_projects_no);
        _237_projects_no = view.findViewById(R.id._237_projects_no);
        all_projects_chart = view.findViewById(R.id.all_projects_chart);
        cat_projects_chart = view.findViewById(R.id.cat_projects_chart);
        _237_projects_chart = view.findViewById(R.id._237_projects_chart);

    }

    public void getProjectsData(int pageNum, Map<String, String> filterMap, String country_id) {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getStatisticsProjects(UserUtils.getAccessToken(getContext()), pageNum, filterMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        JSONObject projectObject = new JSONObject(response.body().string());
                        setProjectsData(projectObject, country_id);
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
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                loading.setVisibility(View.GONE);
            }
        });

    }

    private void setProjectsData(JSONObject projectObject, String countryId) throws JSONException {

        Log.e("Object", countryId);

        int total_projects = projectObject.getInt("total_projects");
        int total_done_projects = projectObject.getInt("total_done_projects");
        int total_cancel_projects = projectObject.getInt("total_cancel_projects");
        int total_created_projects = projectObject.getInt("total_created_projects");
//        int total_havent_request = projectObject.getInt("total_haven\'t_request");
//        int total_haven_request = projectObject.getInt("total_haven_request");
        if (countryId.equals("")) {
            all_projects_no.setText(String.valueOf(total_projects));
            setAllProjectsChart(total_created_projects, total_done_projects, total_cancel_projects);
        } else if (countryId.equals("1")) {
            cat_projects_no.setText(String.valueOf(total_projects));
            setCatProjectsChart(total_created_projects, total_done_projects, total_cancel_projects);
        } else if (countryId.equals("2")) {
            _237_projects_no.setText(String.valueOf(total_projects));
            set237ProjectsChart(total_created_projects, total_done_projects, total_cancel_projects);
        }
    }

    public void setAllProjectsChart(int created, int done, int cancelled) {

        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        config
//                .startAngle(-90)// 起始角度偏移
                .animatePie(true)
                .strokeMode(true)
                .floatUpDuration(500)
                .floatDownDuration(500)
                .splitAngle(0.5f)
                .duration(1000)
//                .drawText(true)
                .textSize(34)
                .autoSize(true)
                .pieRadius(200)
                .legendsWith(getView().findViewById(R.id.all_projects_legends))
                .addData(new SimplePieInfo(created, getActivity().getColor(R.color.colorAccent), "Created :" + created))
                .addData(new SimplePieInfo(done, getActivity().getColor(R.color.green), "Done :" + done))
                .addData(new SimplePieInfo(cancelled, getActivity().getColor(R.color.red), "Cancelled :" + cancelled))

                .duration(500);// 持续时间

        all_projects_chart.applyConfig(config);
        all_projects_chart.start();


    }
    public void setCatProjectsChart(int created, int done, int cancelled) {

        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        config
//                .startAngle(-90)// 起始角度偏移
                .animatePie(true)
                .strokeMode(true)
                .floatUpDuration(500)
                .floatDownDuration(500)
                .splitAngle(0.5f)
                .duration(1000)
//                .drawText(true)
                .textSize(34)
                .autoSize(true)
                .pieRadius(200)
                .legendsWith(getView().findViewById(R.id.cat_projects_legends))
                .addData(new SimplePieInfo(created, getActivity().getColor(R.color.colorAccent), "Created :" + created))
                .addData(new SimplePieInfo(done, getActivity().getColor(R.color.green), "Done :" + done))
                .addData(new SimplePieInfo(cancelled, getActivity().getColor(R.color.red), "Cancelled :" + cancelled))

                .duration(500);// 持续时间

        cat_projects_chart.applyConfig(config);
        cat_projects_chart.start();


    }
    public void set237ProjectsChart(int created, int done, int cancelled) {

        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        config
//                .startAngle(-90)// 起始角度偏移
                .animatePie(true)
                .strokeMode(true)
                .floatUpDuration(500)
                .floatDownDuration(500)
                .splitAngle(0.5f)
                .duration(1000)
//                .drawText(true)
                .textSize(34)
                .autoSize(true)
                .pieRadius(200)
                .legendsWith(getView().findViewById(R.id._237_projects_legends))
                .addData(new SimplePieInfo(created, getActivity().getColor(R.color.colorAccent), "Created :" + created))
                .addData(new SimplePieInfo(done, getActivity().getColor(R.color.green), "Done :" + done))
                .addData(new SimplePieInfo(cancelled, getActivity().getColor(R.color.red), "Cancelled :" + cancelled))

                .duration(500);// 持续时间

        _237_projects_chart.applyConfig(config);
        _237_projects_chart.start();


    }

    public Map<String, String> getFilterMap(String country_id) {
        Map<String, String> map = new HashMap<>();
        map.put("per_page", "20");
        map.put("country_id", country_id);
//        map.put("user_ids", "3");

        return map;
    }
}