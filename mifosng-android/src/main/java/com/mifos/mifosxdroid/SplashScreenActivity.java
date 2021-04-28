/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.mifos.api.BaseUrl;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.login.LoginActivity;
import com.mifos.mifosxdroid.passcode.NewPassCodeActivity;
import com.mifos.mifosxdroid.passcode.PassCodeActivity;
import com.mifos.mobile.passcode.utils.PassCodeConstants;
import com.mifos.utils.PrefManager;


/**
 * This is the First Activity which can be used for initial checks, inits at app Startup
 */
public class SplashScreenActivity extends MifosBaseActivity {

    ImageView splashImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashImage = findViewById(R.id.iv_splash_image);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!PrefManager.isAuthenticated()) {
                    PrefManager.setInstanceUrl(BaseUrl.PROTOCOL_HTTPS
                            + BaseUrl.API_ENDPOINT + BaseUrl.API_PATH);
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this,
                            NewPassCodeActivity.class);
                    intent.putExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN, false);
                    startActivity(intent);
                }
                finish();
            }
        },2000);

        Animation myAnimation = AnimationUtils.loadAnimation(this,R.anim.animation);
        if(splashImage != null) splashImage.startAnimation(myAnimation);
    }
}
