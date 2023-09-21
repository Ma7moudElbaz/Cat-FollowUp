package com.example.followup.admin.statistics.requests;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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


public class Statistics_requests extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics_requests, container, false);
    }

    TextView purchasing_no, printing_no, production_no, photography_no, extras_no;
    AnimatedPieView purchasing_chart, printing_chart, production_chart, photography_chart, extras_chart;
    ProgressBar loading;
    RadioGroup country_select_group;
    WebserviceContext ws;
    String country_id = "";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);

        country_select_group.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.all:
                    country_id = "";
                    break;
                case R.id.country_cat:
                    country_id = "1";
                    break;
                case R.id.country_237:
                    country_id = "2";
                    break;
            }
            getRequestsData(1, getFilterMap());
        });

        getRequestsData(1, getFilterMap());
    }

    private void initFields(View view) {
        purchasing_no = view.findViewById(R.id.purchasing_no);
        printing_no = view.findViewById(R.id.printing_no);
        production_no = view.findViewById(R.id.production_no);
        photography_no = view.findViewById(R.id.photography_no);
        extras_no = view.findViewById(R.id.extras_no);

        purchasing_chart = view.findViewById(R.id.purchasing_chart);
        printing_chart = view.findViewById(R.id.printing_chart);
        production_chart = view.findViewById(R.id.production_chart);
        photography_chart = view.findViewById(R.id.photography_chart);
        extras_chart = view.findViewById(R.id.extras_chart);

        loading = view.findViewById(R.id.loading);

        country_select_group = view.findViewById(R.id.country_select);


        ws = new WebserviceContext(getActivity());
    }

    public Map<String, String> getFilterMap() {
        Map<String, String> map = new HashMap<>();
        map.put("country_id", country_id);

        return map;
    }

    public void getRequestsData(int pageNum, Map<String, String> filterMap) {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getStatisticsRequests(UserUtils.getAccessToken(getContext()), pageNum, filterMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        JSONObject responseObject = new JSONObject(response.body().string());
                        JSONObject responseDataObject = responseObject.getJSONObject("types");
                        setRequestsData(responseDataObject);
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


    private void setRequestsData(JSONObject requestsObject) throws JSONException {


        JSONObject requestsPurchasing = requestsObject.getJSONObject("Purchasing");
        JSONObject requestsPrinting = requestsObject.getJSONObject("Printing");
        JSONObject requestsProduction = requestsObject.getJSONObject("Production");
        JSONObject requestsPhotography = requestsObject.getJSONObject("Photography");
        JSONObject requestsExtras = requestsObject.getJSONObject("Extras");


        purchasing_no.setText(requestsPurchasing.getString("count"));
        setPurchasingChart(requestsPurchasing.getInt("created"), requestsPurchasing.getInt("canceled")
                , requestsPurchasing.getInt("approved"), requestsPurchasing.getInt("have_jo"));

        printing_no.setText(requestsPrinting.getString("count"));
        setPrintingChart(requestsPrinting.getInt("created"), requestsPrinting.getInt("canceled")
                , requestsPrinting.getInt("approved"), requestsPrinting.getInt("have_jo"));

        production_no.setText(requestsProduction.getString("count"));
        setProductionChart(requestsProduction.getInt("created"), requestsProduction.getInt("canceled")
                , requestsProduction.getInt("approved"), requestsProduction.getInt("have_jo"));

        photography_no.setText(requestsPhotography.getString("count"));
        setPhotographyChart(requestsPhotography.getInt("created"), requestsPhotography.getInt("canceled")
                , requestsPhotography.getInt("approved"), requestsPhotography.getInt("have_jo"));

        extras_no.setText(requestsExtras.getString("count"));
        setExtrasChart(requestsExtras.getInt("created"), requestsExtras.getInt("canceled")
                , requestsExtras.getInt("approved"), requestsExtras.getInt("have_jo"));

    }


    public void setPurchasingChart(int created, int cancel, int approve, int jo) {

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
                .pieRadius(170)
                .legendsWith(getView().findViewById(R.id.purchasing_legends))
                .addData(new SimplePieInfo(cancel, getActivity().getColor(R.color.more_red), "Cancel :" + cancel))
                .addData(new SimplePieInfo(created, getActivity().getColor(R.color.colorPrimaryDark), "Created :" + created))
                .addData(new SimplePieInfo(approve, getActivity().getColor(R.color.green), "Approve :" + approve))
                .addData(new SimplePieInfo(jo, getActivity().getColor(R.color.more_green), "JO :" + jo))

                .duration(500);// 持续时间

        purchasing_chart.applyConfig(config);
        purchasing_chart.start();


    }

    public void setPrintingChart(int created, int cancel, int approve, int jo) {

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
                .pieRadius(170)
                .legendsWith(getView().findViewById(R.id.printing_legends))
                .addData(new SimplePieInfo(cancel, getActivity().getColor(R.color.more_red), "Cancel :" + cancel))
                .addData(new SimplePieInfo(created, getActivity().getColor(R.color.colorPrimaryDark), "Created :" + created))
                .addData(new SimplePieInfo(approve, getActivity().getColor(R.color.green), "Approve :" + approve))
                .addData(new SimplePieInfo(jo, getActivity().getColor(R.color.more_green), "JO :" + jo))

                .duration(500);// 持续时间

        printing_chart.applyConfig(config);
        printing_chart.start();

    }

    public void setProductionChart(int created, int cancel, int approve, int jo) {

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
                .pieRadius(170)
                .legendsWith(getView().findViewById(R.id.production_legends))
                .addData(new SimplePieInfo(cancel, getActivity().getColor(R.color.more_red), "Cancel :" + cancel))
                .addData(new SimplePieInfo(created, getActivity().getColor(R.color.colorPrimaryDark), "Created :" + created))
                .addData(new SimplePieInfo(approve, getActivity().getColor(R.color.green), "Approve :" + approve))
                .addData(new SimplePieInfo(jo, getActivity().getColor(R.color.more_green), "JO :" + jo))

                .duration(500);// 持续时间

        production_chart.applyConfig(config);
        production_chart.start();

    }

    public void setPhotographyChart(int created, int cancel, int approve, int jo) {

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
                .pieRadius(170)
                .legendsWith(getView().findViewById(R.id.photography_legends))
                .addData(new SimplePieInfo(cancel, getActivity().getColor(R.color.more_red), "Cancel :" + cancel))
                .addData(new SimplePieInfo(created, getActivity().getColor(R.color.colorPrimaryDark), "Created :" + created))
                .addData(new SimplePieInfo(approve, getActivity().getColor(R.color.green), "Approve :" + approve))
                .addData(new SimplePieInfo(jo, getActivity().getColor(R.color.more_green), "JO :" + jo))

                .duration(500);// 持续时间

        photography_chart.applyConfig(config);
        photography_chart.start();

    }

    public void setExtrasChart(int created, int cancel, int approve, int jo) {

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
                .pieRadius(170)
                .legendsWith(getView().findViewById(R.id.extras_legends))
                .addData(new SimplePieInfo(cancel, getActivity().getColor(R.color.more_red), "Cancel :" + cancel))
                .addData(new SimplePieInfo(created, getActivity().getColor(R.color.colorPrimaryDark), "Created :" + created))
                .addData(new SimplePieInfo(approve, getActivity().getColor(R.color.green), "Approve :" + approve))
                .addData(new SimplePieInfo(jo, getActivity().getColor(R.color.more_green), "JO :" + jo))

                .duration(500);// 持续时间

        extras_chart.applyConfig(config);
        extras_chart.start();

    }


}