package com.travel_track.solution.views.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.travel_track.solution.R;
import com.travel_track.solution.data.PreferenceManager;

import androidx.annotation.Nullable;
import butterknife.BindView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.core.content.ContextCompat.startActivity;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.tryagain)
    @Nullable
    TextView mTextViewTryAgain;
    @BindView(R.id.fetching_details)
    @Nullable
    TextView mTextViewFetchingDetails;
    @BindView(R.id.progressbar)
    @Nullable
    ProgressBar mProgress;

    private SplashTaskBg splashTaskBg;

    @Override
    void onBuildUserInterface() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View pageContent = inflater.inflate(R.layout.activity_splash, null);
        setViewContents(pageContent);

        splashTaskBg = new SplashTaskBg();
        splashTaskBg.execute((Void) null);
    }


    public class SplashTaskBg extends AsyncTask<Void, Void, Boolean> {
        SplashTaskBg() {}

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            splashTaskBg = null;
            if (success) {
                if (PreferenceManager.getInstance(SplashActivity.this).getUserInfo() != null
                        && PreferenceManager.getInstance(SplashActivity.this).getUserInfo().getUserid() != null) {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            } else {
                mProgress.setVisibility(View.GONE);
                mTextViewFetchingDetails.setVisibility(View.GONE);
                mTextViewTryAgain.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled() {
            splashTaskBg = null;
        }
    }

}
