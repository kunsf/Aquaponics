package cn.jit.aquaponics.uinew.fourth;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.allen.library.SuperTextView;
import cn.jit.aquaponics.R;
////import cn.jit.aquaponics.model.cache.UserCache;
import cn.jit.aquaponics.mvp.presenter.user.PersonalAtPresenter;
import cn.jit.aquaponics.mvp.ui.activity.user.LoginActivity;
import cn.jit.aquaponics.mvp.ui.view.user.IPersonalAtView;
import com.luck.picture.lib.entity.LocalMedia;
import com.zxl.baselib.baseapp.AppManager;
import com.zxl.baselib.ui.base.BaseActivity;
import com.zxl.baselib.ui.base.BaseFragment;
import com.zxl.baselib.util.image.GlideLoaderUtils;
import com.zxl.baselib.util.ui.UIUtils;
import com.zxl.baselib.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yuanyuan on 2019/3/11.
 */

public class ProfileFragment extends BaseFragment<IPersonalAtView, PersonalAtPresenter> implements IPersonalAtView {

    @BindView(R.id.img_avater)
    CircleImageView imgAvater;
    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.stv_Modify)
    SuperTextView mStvModify;
    @BindView(R.id.stv_Opinion)
    SuperTextView mStvOpinion;
    @BindView(R.id.stv_about_robot)
    SuperTextView stvAboutRobot;
    @BindView(R.id.stv_logout)
    SuperTextView stvLogout;
    @BindView(R.id.stv_robot_pwd)
    SuperTextView mStvRobotPwd;
    @BindView(R.id.stvDrive)
    SuperTextView stvDrive;
    private View mExitView;
    private View mUserChooseView;
    private CustomDialog mUserChooseDialog;
    private CustomDialog mExitDialog;
    private boolean isChecked = false;

    public List<LocalMedia> mSingleSelectList = new ArrayList<>();

    @Override
    public void init() {

    }

    public static ProfileFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_profilel;
    }

    @Override
    protected PersonalAtPresenter createPresenter() {
        return new PersonalAtPresenter((BaseActivity) getActivity());
    }

    @Override
    public void initView(View rootView) {
        initHeadView();
    }

    /**
     * ??????????????????head
     */
    private void initHeadView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
//        if (UserCache.getUserRealname().equals("")){
//            tvName.setText(UserCache.getUserUsername());
//        }else {
//            tvName.setText(UserCache.getUserRealname());
//        }

        tvName.setText(UserCache.getUserUsername());
        // ????????????
        GlideLoaderUtils.display(getActivity(), imgAvater, UserCache.getUserImage());
    }

    @Override
    public void initListener() {
        // ?????????????????????????????????
//        imgAvater.setOnClickListener(view -> PictureSHelper.getInstance().
//                chooseSinglePictureEvent((BaseActivity) getActivity(), mSingleSelectList, PictureConfig.SINGLE));

        stvAboutRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToActivity(VideoConfirmActivity.class);
            }
        });

        imgAvater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //PhotoPicker.builder().setPhotoCount(1).setShowCamera(true).setPreviewEnabled(false).start(FeedingActivity.this,PhotoPicker.REQUEST_CODE);
            }
        });

        mStvOpinion.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
//                if (mUserChooseView == null) {
//                    mUserChooseView = View.inflate(getActivity(), R.layout.dialog_userchoose, null);
//                    mUserChooseDialog = new CustomDialog(getActivity(), mUserChooseView, R.style.MyDialog);
//                    mUserChooseView.findViewById(R.id.user1).setOnClickListener((View v) -> {
//                        mUserChooseDialog.dismiss();
//                        UserCache.setUserName("yuwang1");
//                        UIUtils.showToast("??????????????????1");
//                    });
//
//                    mUserChooseView.findViewById(R.id.user2).setOnClickListener(v -> {
//                        mUserChooseDialog.dismiss();
//                        UserCache.setUserName("yuwang2");
//                        UIUtils.showToast("??????????????????2");
//                    });
//
//                    mUserChooseView.findViewById(R.id.user3).setOnClickListener(v -> {
//                        mUserChooseDialog.dismiss();
//                        UserCache.setUserName("yuwang3");
//                        UIUtils.showToast("??????????????????3");
//                    });
//                }
//                mUserChooseDialog.show();
            }
        });

        mStvModify.setOnSuperTextViewClickListener(superTextView -> jumpToActivity(SelfInfoActivity.class));

        stvLogout.setOnSuperTextViewClickListener(superTextView -> {
            if (mExitView == null) {
                mExitView = View.inflate(getActivity(), R.layout.dialog_exit, null);
                mExitDialog = new CustomDialog(getActivity(), mExitView, R.style.MyDialog);
                mExitView.findViewById(R.id.tvExitAccount).setOnClickListener((View v) -> {
                    mExitDialog.dismiss();
                    mPresenter.doLogout();
                });

                mExitView.findViewById(R.id.tvExitApp).setOnClickListener(v -> {
                    mExitDialog.dismiss();
                    AppManager.getAppManager().finishAllActivity();
                });
            }
            mExitDialog.show();
        });

        // ?????????????????????
//        mStvRobotPwd.setOnSuperTextViewClickListener(superTextView -> jumpToActivity(ChangePwdActivity.class));

        stvDrive.setCheckBoxCheckedChangeListener(new SuperTextView.OnCheckBoxCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });


        stvDrive.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //?????????????????????????????????????????????setChecked()???????????????listener
                if(!compoundButton.isPressed()){
                    return;
                }else {
                    String title = isChecked?"??????":"??????";
                    showMaterialDialog(null, "??????" + title + "?????????????", "??????", "??????", new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            float height;
                            isChecked = !isChecked;
                        }
                    }, new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            stvDrive.setSwitchIsChecked(isChecked);
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

    }








    /**
     * ????????????
     *
     * @param imagePath
     */
    @Override
    public void postHeadSuccess(String imagePath) {
        UIUtils.showToast("????????????");
        UserCache.setUserImage(imagePath);
        mSingleSelectList.clear();
        GlideLoaderUtils.display(getActivity(), imgAvater, imagePath);
    }

    @Override
    public void postHeadFailure(String error) {
        UIUtils.showToast(error);
    }

    /**
     * ??????????????????
     */
    @Override
    public void logoutSuccess() {
        AppManager.getAppManager().finishAllActivity();
        jumpToActivityAndClearTask(LoginActivity.class);
    }
}
