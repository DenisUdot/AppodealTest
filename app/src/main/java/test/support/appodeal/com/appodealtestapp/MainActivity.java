package test.support.appodeal.com.appodealtestapp;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.Native;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private long startTime;
    private TextView mCounterTextView;
    private boolean cancelPressed, firstShow;
    private CountDownTimer countDownTimer;
    private List<NativeAd> nativeAdList = new ArrayList<NativeAd>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCounterTextView = findViewById(R.id.textViewCounter);

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new CancelButtonListener());
        //set launch time
        startTime = System.currentTimeMillis();

        String appKey = "600cf4bc1fca015b6ab0f05661f5f42816bc9840ea1bf1c6";

        Appodeal.setAutoCacheNativeIcons(true);
        Appodeal.setAutoCacheNativeMedia(false);
        Appodeal.setNativeAdType(Native.NativeAdType.NoVideo);
        Appodeal.disableLocationPermissionCheck();
        Appodeal.setTesting(true);
        Appodeal.initialize(this, appKey, Appodeal.INTERSTITIAL | Appodeal.BANNER | Appodeal.NATIVE);

        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {
                Log.d("Appodeal", "onInterstitialLoaded");
            }
            @Override
            public void onInterstitialFailedToLoad() {
                Log.d("Appodeal", "onInterstitialFailedToLoad");
            }
            @Override
            public void onInterstitialShown() {
                countDownTimer.cancel();
                 Log.d("Appodeal", "onInterstitialShown");
            }
            @Override
            public void onInterstitialClicked() {
                Log.d("Appodeal", "onInterstitialClicked");
            }
            @Override
            public void onInterstitialClosed() {
                countDownTimer.start();
                Log.d("Appodeal", "onInterstitialClosed");
            }
        });

        countDownTimer = new CountDownTimer(30000, 500) {
            public void onTick(long millisUntilFinished) {
                mCounterTextView.setText("seconds remaining: " + (millisUntilFinished+1000) / 1000);
            }
            public void onFinish() {
                Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firstShow == false) {
            Appodeal.show(this, Appodeal.BANNER_TOP);
            Timer bannerCloseTimer = new Timer();
            bannerCloseTimer.schedule(new BannerCloseTimerTask(), 5000);
            firstShow = !firstShow;
        }
        if(cancelPressed == false) {
            countDownTimer.start();
        }
    }

    class BannerCloseTimerTask extends TimerTask{
        @Override
        public void run() {
            Appodeal.hide(MainActivity.this, Appodeal.BANNER);
            Appodeal.destroy(Appodeal.BANNER);
        }
    }

    class CancelButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            //check if the button was pressed before the first 30 seconds have elapsed
            if (System.currentTimeMillis() < (startTime + 30000)){
                if((!cancelPressed) && (countDownTimer != null)){
                    countDownTimer.cancel();
                    mCounterTextView.setText(R.string.cancel_pressed);
                    cancelPressed = !cancelPressed;
                }
            }
            NativeAdViewNewsFeed nav_nf = (NativeAdViewNewsFeed) findViewById(R.id.native_ad_view_news_feed);
            Appodeal.hide(MainActivity.this, Appodeal.BANNER);
            nativeAdList.addAll(Appodeal.getNativeAds(2));
            for(NativeAd na : nativeAdList) {
                nav_nf.setNativeAd(na);
            }
        }
    }
}
