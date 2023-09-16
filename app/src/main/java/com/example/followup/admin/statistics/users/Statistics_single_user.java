package com.example.followup.admin.statistics.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

public class Statistics_single_user extends AppCompatActivity {

    ImageView back;
    TextView projects_no, requests_no, purchasing_no, printing_no, production_no, photography_no, extras_no, jo_no;
    AnimatedPieView projects_chart, requests_status_chart, requests_types_chart, purchasing_chart, printing_chart, production_chart, photography_chart, extras_chart, jo_chart;
    ProgressBar loading;
    WebserviceContext ws;
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_single_user);
        user_id = getIntent().getIntExtra("user_id", 0);
        initFields();
        getUserData(1, getFilterMap());
        back.setOnClickListener(v -> onBackPressed());

    }

    private void initFields() {
        ws = new WebserviceContext(this);
        loading = findViewById(R.id.loading);
        back = findViewById(R.id.back);
        projects_no = findViewById(R.id.projects_no);
        requests_no = findViewById(R.id.requests_no);
        purchasing_no = findViewById(R.id.purchasing_no);
        printing_no = findViewById(R.id.printing_no);
        production_no = findViewById(R.id.production_no);
        photography_no = findViewById(R.id.photography_no);
        extras_no = findViewById(R.id.extras_no);
        jo_no = findViewById(R.id.jo_no);

        projects_chart = findViewById(R.id.projects_chart);
        requests_status_chart = findViewById(R.id.requests_status_chart);
        requests_types_chart = findViewById(R.id.requests_types_chart);
        purchasing_chart = findViewById(R.id.purchasing_chart);
        printing_chart = findViewById(R.id.printing_chart);
        production_chart = findViewById(R.id.production_chart);
        photography_chart = findViewById(R.id.photography_chart);
        extras_chart = findViewById(R.id.extras_chart);
        jo_chart = findViewById(R.id.jo_chart);


    }

    public void getUserData(int pageNum, Map<String, String> filterMap) {
        loading.setVisibility(View.VISIBLE);

        ws.getApi().getStatisticsUsers(UserUtils.getAccessToken(getBaseContext()), pageNum, filterMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        JSONObject responseObject = new JSONObject(response.body().string());
                        JSONObject responseDataObject = responseObject.getJSONObject("result");
                        JSONArray usersArray = responseDataObject.getJSONArray("data");
                        JSONObject userObject = usersArray.getJSONObject(0);
                        setUserData(userObject);
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

    public Map<String, String> getFilterMap() {
        Map<String, String> map = new HashMap<>();
        map.put("per_page", "20");
        map.put("user_ids", String.valueOf(user_id));
//        map.put("user_ids", "3");

        return map;
    }

    private void setUserData(JSONObject userObject) throws JSONException {
        projects_no.setText(userObject.getString("projects_count"));
        requests_no.setText(userObject.getJSONObject("requests").getString("count"));
        jo_no.setText(userObject.getJSONObject("job_orders").getString("count"));
        purchasing_no.setText(userObject.getJSONObject("requests").getJSONObject("types").getJSONObject("Purchasing").getString("count"));
        printing_no.setText(userObject.getJSONObject("requests").getJSONObject("types").getJSONObject("Printing").getString("count"));
        production_no.setText(userObject.getJSONObject("requests").getJSONObject("types").getJSONObject("Production").getString("count"));
        photography_no.setText(userObject.getJSONObject("requests").getJSONObject("types").getJSONObject("Photography").getString("count"));
        extras_no.setText(userObject.getJSONObject("requests").getJSONObject("types").getJSONObject("Extras").getString("count"));

        JSONObject projectData = userObject.getJSONObject("project");
        setProjectsChart(projectData.getInt("created"), projectData.getInt("done"), projectData.getInt("canceled"));

        JSONObject requestsStatusData = userObject.getJSONObject("requests").getJSONObject("status");
        setRequestsStatusChart(requestsStatusData.getInt("CANCEL"), requestsStatusData.getInt("CREATED")
                , requestsStatusData.getInt("REJECT"), requestsStatusData.getInt("APPROVE"), requestsStatusData.getInt("JO"));

        JSONObject requestsTypesData = userObject.getJSONObject("requests").getJSONObject("types");
        JSONObject requestsPurchasing = requestsTypesData.getJSONObject("Purchasing");
        JSONObject requestsPrinting = requestsTypesData.getJSONObject("Printing");
        JSONObject requestsProduction = requestsTypesData.getJSONObject("Production");
        JSONObject requestsPhotography = requestsTypesData.getJSONObject("Photography");
        JSONObject requestsExtras = requestsTypesData.getJSONObject("Extras");

        setRequestsTypesChart(requestsPurchasing.getInt("count"),requestsPrinting.getInt("count")
                ,requestsProduction.getInt("count"),requestsPhotography.getInt("count"),
                requestsExtras.getInt("count"));

        setPurchasingChart(requestsPurchasing.getInt("created"),requestsPurchasing.getInt("canceled")
                ,requestsPurchasing.getInt("approved"),requestsPurchasing.getInt("have_jo"));

        setPrintingChart(requestsPrinting.getInt("created"),requestsPrinting.getInt("canceled")
                ,requestsPrinting.getInt("approved"),requestsPrinting.getInt("have_jo"));

        setProductionChart(requestsProduction.getInt("created"),requestsProduction.getInt("canceled")
                ,requestsProduction.getInt("approved"),requestsProduction.getInt("have_jo"));

        setPhotographyChart(requestsPhotography.getInt("created"),requestsPhotography.getInt("canceled")
                ,requestsPhotography.getInt("approved"),requestsPhotography.getInt("have_jo"));

        setExtrasChart(requestsExtras.getInt("created"),requestsExtras.getInt("canceled")
                ,requestsExtras.getInt("approved"),requestsExtras.getInt("have_jo"));


        JSONObject joData = userObject.getJSONObject("job_orders").getJSONObject("status");

        int create = joData.getInt("CREATE");
        int sales_reject = joData.getInt("SALES_REJECT");
        int sales_approve = joData.getInt("SALES_APPROVE");
        int magdy_hold = joData.getInt("MAGDY_HOLD");
        int magdy_approve = joData.getInt("MAGDY_APPROVE");
        int hesham_reject = joData.getInt("HESHAM_REJECT");
        int hesham_approve = joData.getInt("HESHAM_APPROVE");
        int send_to_co = joData.getInt("SEND_TO_CO");
        int co_reject = joData.getInt("CO_REJECT");
        int co_approve = joData.getInt("CO_APPROVE");
        int adel_paid = joData.getInt("ADEL_PAID");


        setJOChart(create,sales_reject,sales_approve,magdy_hold,magdy_approve,hesham_reject,hesham_approve
                ,send_to_co,co_reject,co_approve,adel_paid);

    }


    public void setProjectsChart(int created, int done, int cancelled) {

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
                .legendsWith(findViewById(R.id.projects_legends))
                .addData(new SimplePieInfo(created, getColor(R.color.green), "Created :" + created))
                .addData(new SimplePieInfo(done, getColor(R.color.colorAccent), "Done :" + done))
                .addData(new SimplePieInfo(cancelled, getColor(R.color.red), "Cancelled :" + cancelled))

                .duration(500);// 持续时间

        projects_chart.applyConfig(config);
        projects_chart.start();


    }

    public void setRequestsStatusChart(int cancel, int created, int reject, int approve, int jo) {

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
                .legendsWith(findViewById(R.id.requests_status_legends))
                .addData(new SimplePieInfo(cancel, getColor(R.color.more_red), "Cancel :" + cancel))
                .addData(new SimplePieInfo(created, getColor(R.color.colorPrimaryDark), "Created :" + created))
                .addData(new SimplePieInfo(reject, getColor(R.color.red), "Reject :" + reject))
                .addData(new SimplePieInfo(approve, getColor(R.color.green), "Approve :" + approve))
                .addData(new SimplePieInfo(jo, getColor(R.color.more_green), "JO :" + jo))

                .duration(500);// 持续时间

        requests_status_chart.applyConfig(config);
        requests_status_chart.start();


    }
    public void setRequestsTypesChart(int requestsPurchasing, int requestsPrinting, int requestsProduction, int requestsPhotography, int requestsExtras) {

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
                .legendsWith(findViewById(R.id.requests_types_legends))
                .addData(new SimplePieInfo(requestsPurchasing, getColor(R.color.colorPrimaryDark), "Purchasing :" + requestsPurchasing))
                .addData(new SimplePieInfo(requestsPrinting, getColor(R.color.colorAccent), "Printing :" + requestsPrinting))
                .addData(new SimplePieInfo(requestsProduction, getColor(R.color.green), "Production :" + requestsProduction))
                .addData(new SimplePieInfo(requestsPhotography, getColor(R.color.gray), "Photography :" + requestsPhotography))
                .addData(new SimplePieInfo(requestsExtras, getColor(R.color.text_light_blue), "Extras :" + requestsExtras))

                .duration(500);// 持续时间

        requests_types_chart.applyConfig(config);
        requests_types_chart.start();


    }
    public void setPurchasingChart(int created, int cancelled, int approved, int haveJo) {

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
                .legendsWith(findViewById(R.id.purchasing_legends))
                .addData(new SimplePieInfo(created, getColor(R.color.colorAccent), "Created :" + created))
                .addData(new SimplePieInfo(cancelled, getColor(R.color.red), "Cancelled :" + cancelled))
                .addData(new SimplePieInfo(approved, getColor(R.color.green), "Approved :" + approved))
                .addData(new SimplePieInfo(haveJo, getColor(R.color.more_green), "Have JO :" + haveJo))

                .duration(500);// 持续时间

        purchasing_chart.applyConfig(config);
        purchasing_chart.start();


    }
    public void setPrintingChart(int created, int cancelled, int approved, int haveJo) {

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
                .legendsWith(findViewById(R.id.printing_legends))
                .addData(new SimplePieInfo(created, getColor(R.color.colorAccent), "Created :" + created))
                .addData(new SimplePieInfo(cancelled, getColor(R.color.red), "Cancelled :" + cancelled))
                .addData(new SimplePieInfo(approved, getColor(R.color.green), "Approved :" + approved))
                .addData(new SimplePieInfo(haveJo, getColor(R.color.more_green), "Have JO :" + haveJo))

                .duration(500);// 持续时间

        printing_chart.applyConfig(config);
        printing_chart.start();


    }
    public void setProductionChart(int created, int cancelled, int approved, int haveJo) {

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
                .legendsWith(findViewById(R.id.production_legends))
                .addData(new SimplePieInfo(created, getColor(R.color.colorAccent), "Created :" + created))
                .addData(new SimplePieInfo(cancelled, getColor(R.color.red), "Cancelled :" + cancelled))
                .addData(new SimplePieInfo(approved, getColor(R.color.green), "Approved :" + approved))
                .addData(new SimplePieInfo(haveJo, getColor(R.color.more_green), "Have JO :" + haveJo))

                .duration(500);// 持续时间

        production_chart.applyConfig(config);
        production_chart.start();


    }
    public void setPhotographyChart(int created, int cancelled, int approved, int haveJo) {

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
                .legendsWith(findViewById(R.id.photography_legends))
                .addData(new SimplePieInfo(created, getColor(R.color.colorAccent), "Created :" + created))
                .addData(new SimplePieInfo(cancelled, getColor(R.color.red), "Cancelled :" + cancelled))
                .addData(new SimplePieInfo(approved, getColor(R.color.green), "Approved :" + approved))
                .addData(new SimplePieInfo(haveJo, getColor(R.color.more_green), "Have JO :" + haveJo))

                .duration(500);// 持续时间

        photography_chart.applyConfig(config);
        photography_chart.start();

    }
    public void setExtrasChart(int created, int cancelled, int approved, int haveJo) {

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
                .legendsWith(findViewById(R.id.extras_legends))
                .addData(new SimplePieInfo(created, getColor(R.color.colorAccent), "Created :" + created))
                .addData(new SimplePieInfo(cancelled, getColor(R.color.red), "Cancelled :" + cancelled))
                .addData(new SimplePieInfo(approved, getColor(R.color.green), "Approved :" + approved))
                .addData(new SimplePieInfo(haveJo, getColor(R.color.more_green), "Have JO :" + haveJo))

                .duration(500);// 持续时间

        extras_chart.applyConfig(config);
        extras_chart.start();

    }

    public void setJOChart(int created,int sales_reject, int sales_approve, int cc_hold, int cc_approve,
                           int fm_reject,int fm_approve,int pending_ceo,int ceo_reject,int ceo_approve,int adel_paid) {

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
                .legendsWith(findViewById(R.id.jo_legends))
                .addData(new SimplePieInfo(created, getColor(R.color.text_dark_blue), "Created :" + created))
                .addData(new SimplePieInfo(sales_reject, getColor(R.color.red), "Sales Reject :" + sales_reject))
                .addData(new SimplePieInfo(sales_approve, getColor(R.color.gray), "Sales Approve :" + sales_approve))
                .addData(new SimplePieInfo(cc_hold, getColor(R.color.colorAccent), "CC Hold :" + cc_hold))
                .addData(new SimplePieInfo(cc_approve, getColor(R.color.green), "CC Approve :" + cc_approve))
                .addData(new SimplePieInfo(fm_reject, getColor(R.color.more_red), "FM Reject :" + fm_reject))
                .addData(new SimplePieInfo(fm_approve, getColor(R.color.more_green), "FM Approve :" + fm_approve))
                .addData(new SimplePieInfo(pending_ceo, getColor(R.color.text_light_blue), "Pending ceo :" + fm_approve))
                .addData(new SimplePieInfo(ceo_approve, getColor(R.color.between_green), "Ceo Approved :" + ceo_approve))
                .addData(new SimplePieInfo(ceo_reject, getColor(R.color.between_red), "Ceo Reject :" + ceo_reject))
                .addData(new SimplePieInfo(adel_paid, getColor(R.color.text_request_gray), "Adel Paid :" + adel_paid))

                .duration(500);// 持续时间

        jo_chart.applyConfig(config);
        jo_chart.start();

    }
}