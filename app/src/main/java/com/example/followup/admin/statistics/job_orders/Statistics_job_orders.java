package com.example.followup.admin.statistics.job_orders;

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

public class Statistics_job_orders extends Fragment {

    TextView  jo_no, eg_jo_no, ksa_jo_no;
    AnimatedPieView eg_jo_chart, ksa_jo_chart;
    ProgressBar loading;
    WebserviceContext ws;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics_job_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initFields(view);

        getJoData(1, getFilterMap());
    }

    private void initFields(View view) {
        ws = new WebserviceContext(getActivity());
        loading = view.findViewById(R.id.loading);

        jo_no = view.findViewById(R.id.jo_no);
        eg_jo_no = view.findViewById(R.id.eg_jo_no);
        ksa_jo_no = view.findViewById(R.id.ksa_jo_no);

        eg_jo_chart = view.findViewById(R.id.eg_jo_chart);
        ksa_jo_chart = view.findViewById(R.id.ksa_jo_chart);
    }

    public void getJoData(int pageNum, Map<String, String> filterMap) {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getStatisticsJO(UserUtils.getAccessToken(getContext()), pageNum, filterMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        JSONObject responseObject = new JSONObject(response.body().string());
//                        JSONObject responseDataObject = responseObject.getJSONObject("result");
//                        JSONArray usersArray = responseDataObject.getJSONArray("data");
//                        JSONObject userObject = usersArray.getJSONObject(0);
                        setJoData(responseObject);
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

    public Map<String, String> getFilterMap() {
        Map<String, String> map = new HashMap<>();
//        map.put("per_page", "20");
//        map.put("user_ids", String.valueOf(user_id));
//        map.put("user_ids", "3");

        return map;
    }


    private void setJoData(JSONObject joObject) throws JSONException {
        int jobOrdersTotal = joObject.getJSONObject("EGY").getInt("total")
                + joObject.getJSONObject("KSA").getInt("total");
        jo_no.setText(String.valueOf(jobOrdersTotal));



        JSONObject ksaJo = joObject.getJSONObject("KSA");
        JSONObject egJo = joObject.getJSONObject("EGY");

        eg_jo_no.setText(egJo.getString("total"));
        ksa_jo_no.setText(ksaJo.getString("total"));


        int eg_pending_sales = egJo.getInt("pending_sales");
        int eg_pending_cost_control = egJo.getInt("pending_cost_control");
        int eg_pending_finance_manager = egJo.getInt("pending_finance_manager");
        int eg_pending_ceo = egJo.getInt("pending_ceo");
        int eg_rejected = ksaJo.getInt("rejected");


        int ksa_pending_sales = ksaJo.getInt("pending_sales");
        int ksa_pending_cost_control = ksaJo.getInt("pending_cost_control");
        int ksa_pending_finance_manager = ksaJo.getInt("pending_finance_manager");
        int ksa_pending_ceo = ksaJo.getInt("pending_ceo");
        int ksa_pending_payment = ksaJo.getInt("pending_payment");
        int ksa_rejected = ksaJo.getInt("rejected");

        setEgJoChart(eg_pending_sales,eg_pending_cost_control,eg_pending_finance_manager,eg_pending_ceo,eg_rejected);
        setKsaJoChart(ksa_pending_sales,ksa_pending_cost_control,ksa_pending_finance_manager,ksa_pending_ceo,ksa_pending_payment,ksa_rejected);
    }

    public void setEgJoChart(int pending_sales, int pending_cost_control, int pending_finance_manager, int pending_ceo, int rejected) {


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
                .legendsWith(getView().findViewById(R.id.eg_jo_legends))
                .addData(new SimplePieInfo(pending_sales, getActivity().getColor(R.color.text_dark_blue), "Pending Sales :" + pending_sales))
                .addData(new SimplePieInfo(pending_cost_control, getActivity().getColor(R.color.red), "Pending Cost Control :" + pending_cost_control))
                .addData(new SimplePieInfo(pending_finance_manager, getActivity().getColor(R.color.gray), "Pending Finance Manager :" + pending_finance_manager))
                .addData(new SimplePieInfo(pending_ceo, getActivity().getColor(R.color.colorAccent), "Pending CEO :" + pending_ceo))
                .addData(new SimplePieInfo(rejected, getActivity().getColor(R.color.more_red), "Rejected  :" + rejected))

                .duration(500);// 持续时间

        eg_jo_chart.applyConfig(config);
        eg_jo_chart.start();

    }
    public void setKsaJoChart(int pending_sales, int pending_cost_control, int pending_finance_manager, int pending_ceo,int pending_payment, int rejected) {


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
                .legendsWith(getView().findViewById(R.id.ksa_jo_legends))
                .addData(new SimplePieInfo(pending_sales, getActivity().getColor(R.color.text_dark_blue), "Pending Sales :" + pending_sales))
                .addData(new SimplePieInfo(pending_cost_control, getActivity().getColor(R.color.red), "Pending Cost Control :" + pending_cost_control))
                .addData(new SimplePieInfo(pending_finance_manager, getActivity().getColor(R.color.gray), "Pending Finance Manager :" + pending_finance_manager))
                .addData(new SimplePieInfo(pending_ceo, getActivity().getColor(R.color.colorAccent), "Pending CEO :" + pending_ceo))
                .addData(new SimplePieInfo(pending_payment, getActivity().getColor(R.color.green), "Pending Payment  :" + pending_payment))
                .addData(new SimplePieInfo(rejected, getActivity().getColor(R.color.more_red), "Rejected  :" + rejected))

                .duration(500);// 持续时间

        ksa_jo_chart.applyConfig(config);
        ksa_jo_chart.start();

    }

}