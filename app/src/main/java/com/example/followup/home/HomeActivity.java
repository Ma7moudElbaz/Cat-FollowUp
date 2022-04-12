package com.example.followup.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.example.followup.R;
import com.example.followup.home.notifications.NotificationsFragment;
import com.example.followup.home.profile.ProfileFragment;
import com.example.followup.home.projects.ProjectsFragment;
import com.example.followup.home.settings.SettingsFragment;
import com.example.followup.utils.UserUtils;
import com.example.followup.webservice.WebserviceContext;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends LocalizationActivity implements NavigationBarView.OnItemSelectedListener {

    public void setProjects() {
        if (bottomNavigationView.getSelectedItemId() != R.id.navigation_projects) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_projects);
        }
    }

    public void setContentFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contentLayout, fragment);
        fragmentTransaction.commit();
    }

    public void setBadgeNumber(int notificationsNo) {
        badge.setNumber(notificationsNo);
        badge.setVisible(true);

    }

    public void resetBadge() {
        badge.setNumber(0);
        badge.setVisible(false);
    }


    BottomNavigationView bottomNavigationView;
    BadgeDrawable badge;

    WebserviceContext ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initFields();

        badge = bottomNavigationView.getOrCreateBadge(R.id.navigation_notifications);
        getNotificationNumber();

        setContentFragment(new ProjectsFragment());

    }

    private void initFields() {
        ws = new WebserviceContext(this);
        bottomNavigationView = findViewById(R.id.btm_nav);
        bottomNavigationView.setOnItemSelectedListener(this);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navigation_projects) {
            setContentFragment(new ProjectsFragment());
        } else if (id == R.id.navigation_profile) {
            setContentFragment(new ProfileFragment());
        } else if (id == R.id.navigation_notifications) {
            setContentFragment(new NotificationsFragment());
        } else if (id == R.id.navigation_settings) {
            setContentFragment(new SettingsFragment());
        }
        return true;
    }

    public void getNotificationNumber() {
        ws.getApi().getNotificationsNumber(UserUtils.getAccessToken(getBaseContext())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {
                        JSONObject res = new JSONObject(response.body().string());
                        int notificationNo = res.getInt("unreadNotificationsNumber");
                        if (notificationNo == 0) {
                            resetBadge();
                        } else {
                            setBadgeNumber(notificationNo);
                        }


                    } else {
                        Toast.makeText(getBaseContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });

    }

}