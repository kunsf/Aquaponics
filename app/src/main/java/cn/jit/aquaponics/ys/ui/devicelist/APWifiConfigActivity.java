package cn.jit.aquaponics.ys.ui.devicelist;

import static cn.jit.aquaponics.ys.ui.devicelist.AutoWifiNetConfigActivity.WIFI_PASSWORD;
import static cn.jit.aquaponics.ys.ui.devicelist.AutoWifiNetConfigActivity.WIFI_SSID;
import static cn.jit.aquaponics.ys.ui.devicelist.SeriesNumSearchActivity.BUNDE_SERIANO;
import static cn.jit.aquaponics.ys.ui.devicelist.SeriesNumSearchActivity.BUNDE_VERYCODE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.jit.aquaponics.utils.MyApp;
import cn.jit.aquaponics.ys.RootActivity;
import cn.jit.aquaponics.ys.ui.cameralist.EZCameraListActivity;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZHCNetDeviceSDK;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.wificonfig.APWifiConfig;

import ezviz.ezopensdk.R;

public class APWifiConfigActivity extends RootActivity {

    private String mVerifyCode;
    private String mDeviceSerial;
    private String mSSID;
    private String mPassword;

    private TextView mSSIDTv;
    private TextView mPasswordTv;
    private TextView mTipTv;

    private TextView mResetTv;
    private TextView mAddTv;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apwifi_config);
        Intent intent = getIntent();
        mDeviceSerial = intent.getStringExtra(BUNDE_SERIANO);
        mVerifyCode = intent.getStringExtra(BUNDE_VERYCODE);
        mSSID = getIntent().getStringExtra(WIFI_SSID);
        mPassword = getIntent().getStringExtra(WIFI_PASSWORD);
        mResetTv = (TextView) findViewById(R.id.reset);
        mAddTv = (TextView) findViewById(R.id.add_device);
        mProgress = (ProgressBar) findViewById(R.id.ap_progress);
        findViewById(R.id.back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mResetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAP();
            }
        });
        mAddTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddTv.getText().toString().trim().equalsIgnoreCase(getString(R.string.complete_txt))){
                    // TODO: 2018/6/6 ??????
                    Intent intent = new Intent(APWifiConfigActivity.this, EZCameraListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else{
                    showWaitDialog();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MyApp.getOpenSDK().addDevice(mDeviceSerial,mVerifyCode);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAddTv.setText(R.string.complete_txt);
                                        dismissWaitDialog();
                                    }
                                });
                            } catch (BaseException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissWaitDialog();
                                        mAddTv.setText(R.string.auto_wifi_add_device_failed2);
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });
        startAP();
    }


    public void startAP(){
        mProgress.setVisibility(View.VISIBLE);
        mResetTv.setVisibility(View.GONE);
        mAddTv.setVisibility(View.GONE);
        EZOpenSDK.getInstance().startAPConfigWifiWithSsid(mSSID, mPassword, mDeviceSerial, mVerifyCode,
            new APWifiConfig.APConfigCallback() {
                @Override
                public void onSuccess() {
                    Log.d("APWifiConfigActivity", "onSuccess");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setVisibility(View.GONE);
                            mResetTv.setVisibility(View.GONE);
                            mAddTv.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void OnError(final int code) {
                    Log.d("APWifiConfigActivity", "OnError");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setVisibility(View.GONE);
                            mResetTv.setVisibility(View.VISIBLE);
                            mAddTv.setVisibility(View.GONE);
                            switch (code) {
                                case 15:
                                    // TODO: 2018/7/24 ??????
                                    break;
                                case 1:
                                    // TODO: 2018/7/24 ????????????
                                    break;
                                case 2:
                                    // TODO: 2018/7/24 ??????ap??????????????????
                                    break;
                                case 3:
                                    // TODO: 2018/7/24  ??????ap????????????
                                    break;
                                case 4:
                                    // TODO: 2018/7/24 ??????WiFi????????????
                                    break;
                                default:
                                    // TODO: 2018/7/24 ????????????
                                    break;
                            }
                        }
                    });
                }
            });
    }


    @Override
    public void onBackPressed() {
        EZHCNetDeviceSDK.getInstance().stopAPConfigWifiWithSsid();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
