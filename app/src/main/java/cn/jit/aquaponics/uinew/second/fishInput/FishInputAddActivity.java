package cn.jit.aquaponics.uinew.second.fishInput;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import cn.jit.aquaponics.R;
import cn.jit.aquaponics.base.AppConstant;
////import cn.jit.aquaponics.model.cache.UserCache;
////import cn.jit.aquaponics.model.response.FishPondResponse;
import com.zxl.baselib.bean.response.BaseResponse;
import com.zxl.baselib.ui.base.BaseActivity;
import com.zxl.baselib.util.NetworkHelper;
import com.zxl.baselib.util.time.TimeUtil;
import com.zxl.baselib.util.ui.UIUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class FishInputAddActivity extends BaseActivity<FishInputAddView,FishInputAddPresenter> implements FishInputAddView {

    @BindView(R.id.ivToolbarNavigation)
    ImageView mIvToolbarNavigation;
    @BindView(R.id.vToolbarDivision)
    View mVToolbarDivision;
    @BindView(R.id.tvToolbarTitle)
    TextView mTvToolbarTitle;
    @BindView(R.id.tv_publish_now)
    TextView mTvPublishNow;

    @BindView(R.id.fishType)
    EditText mFishType;
    @BindView(R.id.fishInputAmount)
    EditText mFishInputAmount;
    @BindView(R.id.unit)
    TextView mUnit;
    @BindView(R.id.selectedTime)
    TextView mSelectedTime;
    @BindView(R.id.ponds)
    TextView mPonds;
    @BindView(R.id.batchNumber)
    EditText mBatchNumber;

    private Integer[] isCheckedPonds;
    private List<String> selectedPonds = new ArrayList<>();

    private ArrayList<String> units =  new ArrayList<>();

    String fishType = "";
    String time = "";

    @Override
    protected void init() {

    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_fish_input_add;
    }

    @Override
    protected FishInputAddPresenter createPresenter() {
        return new FishInputAddPresenter(this);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mTvToolbarTitle.setText("??????????????????");
        mTvPublishNow.setVisibility(View.VISIBLE);
        mTvPublishNow.setText(R.string.submit);
    }

    @Override
    protected void initData() {
//        pond.clear();
//        pond.add("??????A");
//        pond.add("??????B");

        units.clear();
        units.add("???");
        units.add("???");
        units.add("???");

        time = mSelectedTime.getText().toString();
    }

    @Override
    protected void initListener() {
        mIvToolbarNavigation.setOnClickListener(v -> onBackPressed());

        mTvPublishNow.setOnClickListener(v -> requestSubmit());

        mSelectedTime.setOnClickListener(l -> showCustomDatePicker());

        mPonds.setOnClickListener(v -> mPresenter.getAllUserPonds(UserCache.getUserUsername()));

        mUnit.setOnClickListener(v ->
                new MaterialDialog.Builder(this)
                        .title("???????????????")
                        .items(units)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                mUnit.setText(text);
                            }
                        })
                        .positiveText(android.R.string.cancel)
                        .show());

//        mFishType.setOnFocusChangeListener((v, hasFocus) -> {
//            if(hasFocus){
//
//            }else {
//                fishType = mFishType.getText().toString();
//                String bN = fishType+time;
//                mBatchNumber.setText(bN);
//            }
//        });

        mFishType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fishType = s.toString();
                String bN = fishType+time;
                mBatchNumber.setText(bN);
            }
        });

        mSelectedTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                time = s.toString();
                String bN = fishType+time;
                mBatchNumber.setText(bN);
            }
        });

        mSelectedTime.setText(TimeUtil.date2String
                (new Date(System.currentTimeMillis()), "yyyy-MM-dd"));
    }

    @Override
    public void addFishInputSuccess(BaseResponse baseResponse) {
        if (baseResponse != null) {
            mRxManager.post(AppConstant.RX_ADD_FISH_INPUT, baseResponse);
            UIUtils.showToast("????????????");
            Log.e("test",baseResponse.toString());
        } else {
            UIUtils.showToast("??????????????????");
        }

        finish();
    }

    @Override
    public void addFishInputFailure(String error) {
        UIUtils.showToast(error);
    }

    @Override
    public void getAllUserPondsSuccess(List<FishPondResponse> baseResponseList) {
        ArrayList<String> names = new ArrayList<>();
        for (FishPondResponse fpr: baseResponseList){
            names.add(fpr.getName());
        }
        new MaterialDialog.Builder(this)
                .title("???????????????")
                .items(names)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        isCheckedPonds = which;
                        return true;
                    }
                })
                .alwaysCallMultiChoiceCallback()
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        StringBuilder sb = new StringBuilder();
                        selectedPonds.clear();
                        for(int i=0;i<isCheckedPonds.length;i++){
                            if(i!=0){
                                sb.append(";");
                            }
                            sb.append(names.get(isCheckedPonds[i]));
                            selectedPonds.add(names.get(isCheckedPonds[i]));
                        }
                        mPonds.setText(sb.toString());
                    }
                })
                .positiveText("??????")
                .show();
    }

    @Override
    public void getAllUserPondsFailure(String error) {
        UIUtils.showToast(error);
    }

    private void requestSubmit() {
        checkFormatData();
    }

    private void checkFormatData(){
        String type = mFishType.getText().toString();
        if(TextUtils.isEmpty(type)){
            UIUtils.showToast("?????????????????????");
            return;
        }

        String amount = mFishInputAmount.getText().toString();
        if(TextUtils.isEmpty(amount)){
            UIUtils.showToast("??????????????????");
        }

        String unit = mUnit.getText().toString();
        if (TextUtils.isEmpty(unit)){
            UIUtils.showToast("???????????????");
        }

        String date = mSelectedTime.getText().toString();
        if(TextUtils.isEmpty(date)){
            UIUtils.showToast("?????????????????????");
        }

        if (selectedPonds.size()==0){
            UIUtils.showToast("???????????????");
        }

        String batchNumber = mBatchNumber.getText().toString();
        if(TextUtils.isEmpty(batchNumber)){
            UIUtils.showToast("??????????????????");
        }

        if(!NetworkHelper.isNetworkAvailable(this)){
            UIUtils.showToast(getString(R.string.no_net));
            return;
        }else {
            doFishInputAddCommit(type,Double.parseDouble(amount),unit,date,selectedPonds,batchNumber);
        }

    }

    private void doFishInputAddCommit(String type,double amount,String unit,String date,List<String> pond,String batchNumber){
        mPresenter.addFishInput(type, amount, unit, date, pond, batchNumber);
    }

    public void showCustomDatePicker() {
        new MaterialDialog.Builder(this)
                .title("????????????")
                .customView(R.layout.dialog_datepicker, false)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog, which) -> {
                    DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
                    datePicker.setBackgroundColor(1);
                    mSelectedTime.setText(String.format("%d-%02d-%d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth()));
                })
                .show();
    }
}