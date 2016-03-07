package tv.theautismchannel.amazon;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PreLoadingActivity extends Activity {

    Handler handler;
    TextView mSwitcher;
    TextView mainText;
    RelativeLayout mainLayout;
    Animation in;
    Animation out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pre_loading);
        Intent receiveIntent = getIntent();
        //Get series title and set it as a main loading text
        String seriesTitle = receiveIntent.getStringExtra("seriesTitle");
        String epiTextColor = receiveIntent.getStringExtra("epiTextColor");
        mainText = (TextView) findViewById(R.id.main_text);
        mSwitcher = (TextView) findViewById(R.id.loading_text);
        if (seriesTitle==null) {
            mainText.setText(R.string.app_name);
        }else {
            mainText.setText(seriesTitle);
        }
        // Set text color
        if (epiTextColor!=null) {
            int textColor;
            try {
                textColor = Color.parseColor(epiTextColor);
                System.out.println("try "+epiTextColor);
            } catch (RuntimeException e) {
                textColor = Color.parseColor("#0" + epiTextColor.substring(1));
                System.out.println("catch "+epiTextColor);
            }
        mainText.setTextColor(textColor);
        mSwitcher.setTextColor(textColor);
        }
        mSwitcher.setText(R.string.downloading);
        //Get background color
        String epi_bkg_color = receiveIntent.getStringExtra("epi_bkg_color");
        if (epi_bkg_color==null) {
//            Do nothing
        }else {
            mainLayout = (RelativeLayout) findViewById(R.id.pre_loading);
            try {
                mainLayout.setBackgroundColor(Color.parseColor(epi_bkg_color));
            } catch (RuntimeException e){
                epi_bkg_color = "#0"+epi_bkg_color.substring(1);
                mainLayout.setBackgroundColor(Color.parseColor(epi_bkg_color));
            }
        }

        // Create fade in and fade out animation of "loading content" text
        handler = new Handler();

        in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(2000);

        out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(2000);
        in.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                mSwitcher.startAnimation(out);
                handler.postDelayed(mFadeOut, 2000);
//                }
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }
        });
        mSwitcher.startAnimation(in);
        registerReceiver(broadcast_reciever, new IntentFilter("finish_activity"));

    }
//    @Override
//    public void onBackPressed() {
//        // do nothing, just wait for content loading
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast_reciever);

    }
    private Runnable mFadeOut =new Runnable(){

        @Override
        public void run() {
            mSwitcher.startAnimation(in);
        }
    };
    BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            if (action.equals("finish_activity")) {
                finish();
                // Add fadeout animation when Activity is closing
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                // DO WHATEVER YOU WANT.
            }
        }
    };

}
