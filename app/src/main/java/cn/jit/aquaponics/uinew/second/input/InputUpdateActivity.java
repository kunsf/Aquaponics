package cn.jit.aquaponics.uinew.second.input;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import cn.jit.aquaponics.R;
import cn.jit.aquaponics.base.AppConstant;
//import cn.jit.aquaponics.model.bean.InputUpdateBean;
//import cn.jit.aquaponics.model.response.InputResponse;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.ScreenUtils;
import com.zxl.baselib.ui.base.BaseActivity;
import com.zxl.baselib.util.NetworkHelper;
import com.zxl.baselib.util.ui.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class InputUpdateActivity extends BaseActivity<InputUpdateView,InputUpdatePresenter> implements InputUpdateView {

    @BindView(R.id.ivToolbarNavigation)
    ImageView mIvToolbarNavigation;
    @BindView(R.id.vToolbarDivision)
    View mVToolbarDivision;
    @BindView(R.id.tvToolbarTitle)
    TextView mTvToolbarTitle;
    @BindView(R.id.tv_publish_now)
    TextView mTvPublishNow;

    @BindView(R.id.inputType)
    TextView mInputType;
    @BindView(R.id.inputName)
    EditText mInputName;
    @BindView(R.id.manufacturer)
    EditText mManufacturer;
    @BindView(R.id.remarks)
    EditText mRemarks;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private InputResponse mInputResponse;

    private String type;
    private String name;
    private String manufacturer;
    private String remarks;

    private int mItemIndex;
    private int mInputId;
    private boolean isFromInputMain = false;

    private ArrayList<String> typeEnum = new ArrayList();

    private InputAddPictureAdapter mAdapter;

    List<LocalMedia> selectList = null;

    private final InputAddPictureAdapter.onAddPicClickListener onAddPicClickListener = new InputAddPictureAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            PictureSelector.create(InputUpdateActivity.this)
                    .openGallery(PictureMimeType.ofImage())
                    .maxSelectNum(3)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }
    };

    @Override
    protected void init() {
        if (getIntent()!=null){
            mInputResponse = (InputResponse) getIntent().getExtras().getSerializable(AppConstant.BUNDLE_INPUT_BEAN);

            type = mInputResponse.getType();
            name = mInputResponse.getName();
            manufacturer = mInputResponse.getManufacture();
            remarks = mInputResponse.getRemarks();

            mInputId = mInputResponse.getId();
            mItemIndex = getIntent().getIntExtra(AppConstant.EXTRA_ITEM_INDEX,-1);
            isFromInputMain = getIntent().getBooleanExtra(AppConstant.EXTRA_IS_FROM_INPUT_MAIN,false);
        }
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_input_add;
    }

    @Override
    protected InputUpdatePresenter createPresenter() {
        return new InputUpdatePresenter(this);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mTvToolbarTitle.setText("?????????????????????");
        mTvPublishNow.setVisibility(View.VISIBLE);
        mTvPublishNow.setText(R.string.submit);

        mInputType.setText(type);
        mInputName.setText(name);
        mManufacturer.setText(manufacturer);
        mRemarks.setText(remarks);

        mInputType.setOnClickListener(v ->
                new MaterialDialog.Builder(this)
                        .title("???????????????")
                        .items(typeEnum)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                mInputType.setText(text);
                            }
                        })
                        .positiveText(android.R.string.cancel)
                        .show()
        );

        FullyGridLayoutManager manager = new FullyGridLayoutManager(this,
                4, GridLayoutManager.VERTICAL, false);
        mRecycler.setLayoutManager(manager);
        mRecycler.addItemDecoration(new GridSpacingItemDecoration(4,
                ScreenUtils.dip2px(this, 8), false));
        mAdapter = new InputAddPictureAdapter(this,onAddPicClickListener);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("selectorList") != null) {
            mAdapter.setList(savedInstanceState.getParcelableArrayList("selectorList"));
        }
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((v,position) -> {
            List<LocalMedia> selectList =  mAdapter.getData();
            PictureSelector.create(InputUpdateActivity.this)
                    .themeStyle(R.style.picture_default_style) // xml????????????
                    .openExternalPreview(position, selectList);
        });
    }

    @Override
    protected void initData() {
        typeEnum.clear();
        typeEnum.add("??????");
        typeEnum.add("?????????");
    }

    private void requestSubmit() {
        checkFormatData();
    }

    @Override
    protected void initListener() {
        mIvToolbarNavigation.setOnClickListener(v -> onBackPressed());


        mTvPublishNow.setOnClickListener(v -> requestSubmit());
    }

    private void checkFormatData(){
        String inputType = mInputType.getText().toString();
        if(TextUtils.isEmpty(inputType)){
            UIUtils.showToast("????????????????????????");
            return;
        }

        String inputName = mInputName.getText().toString();
        if(TextUtils.isEmpty(inputName)){
            UIUtils.showToast("????????????????????????");
            return;
        }

        String manufacturer = mManufacturer.getText().toString();
        if(TextUtils.isEmpty(manufacturer)){
            UIUtils.showToast("??????????????????");
        }

        if (selectList == null){
            UIUtils.showToast("????????????????????????");
            return;
        }

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for(LocalMedia lm: selectList){
            File file = new File(lm.getPath());
            Log.d("test",lm.getPath());
            RequestBody requestBody = RequestBody.create(MediaType.parse("text/x-markdown;charset=utf-8"), file);
            builder.addFormDataPart("pictures",file.getName(),requestBody);
        }

        List<MultipartBody.Part> parts = builder.build().parts();

        String remarks = mRemarks.getText().toString();

        if(!NetworkHelper.isNetworkAvailable(this)){
            UIUtils.showToast(getString(R.string.no_net));
            return;
        }else {
            doInputAddCommit(inputType,inputName,manufacturer,parts,remarks);
        }
    }

    private void doInputAddCommit(String inputType, String inputName, String manufacturer, List<MultipartBody.Part> pictures, String remarks){
        mPresenter.updateInput(mInputId,inputType,inputName,manufacturer,pictures,remarks);
    }



    @Override
    public void updateInputSuccess() {
        InputUpdateBean inputUpdateBean = new InputUpdateBean();
        inputUpdateBean.setItemPosition(mItemIndex);

        mInputResponse.setName(mInputName.getText().toString());
        mInputResponse.setType(mInputType.getText().toString());
        mInputResponse.setManufacture(mManufacturer.getText().toString());
        mInputResponse.setRemarks(mRemarks.getText().toString());

        inputUpdateBean.setmFishPondResponse(mInputResponse);
        mRxManager.post(AppConstant.RX_UPDATE_INPUT,inputUpdateBean);
        UIUtils.showToast("????????????");
        finish();
    }

    @Override
    public void updateInputFailure(String error) {
        UIUtils.showToast(error);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //??????????????????
        if(requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK){
            selectList = PictureSelector.obtainMultipleResult(data);
            mAdapter.setList(selectList);
            mAdapter.notifyDataSetChanged();
        }
    }
}