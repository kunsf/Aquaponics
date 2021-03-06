package cn.jit.aquaponics.uinew.second.reagent;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.xrecyclerview.XRecyclerView;
import cn.jit.aquaponics.R;
import cn.jit.aquaponics.base.AppConstant;
//import cn.jit.aquaponics.model.bean.ReagentInputUpdateBean;
//import cn.jit.aquaponics.model.cache.UserCache;
//import cn.jit.aquaponics.model.response.ReagentInputResponse;
import com.zxl.baselib.ui.base.BaseActivity;
import com.zxl.baselib.util.ui.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

public class ReagentInputListActivity extends BaseActivity<ReagentInputListView,ReagentInputListPresenter> implements ReagentInputListView {

    @BindView(R.id.ivToolbarNavigation)
    ImageView ivToolbarNavigation;
    @BindView(R.id.vToolbarDivision)
    View vToolbarDivision;
    @BindView(R.id.ibAddMenu)
    ImageButton ibAddMenu;
    @BindView(R.id.tvToolbarTitle)
    TextView tvToolbarTitle;
    @BindView(R.id.rvPond)
    XRecyclerView mRecCustom;

    int mPosition;

    private BaseQuickAdapter<ReagentInputResponse, BaseViewHolder> mAdapter;
    private List<ReagentInputResponse> mReagentInputResponse = new ArrayList<>();

    private int mPage;
    private String mUserName;

    private boolean mIsFirst = true;

    @Override
    protected void init() {
        mUserName = UserCache.getUserUsername();
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_guding;
    }

    @Override
    protected ReagentInputListPresenter createPresenter() {
        return new ReagentInputListPresenter(this);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ibAddMenu.setVisibility(View.VISIBLE);
        ivToolbarNavigation.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText("?????????????????????");

        initAdapter();

        mPage = 1;
        mPresenter.getUserReagentInput(mUserName,mPage,AppConstant.NORMAL_PAGE_SIZE);

    }

    @Override
    protected void initData() {
        mRxManager.on(AppConstant.RX_ADD_REAGENT_INPUT, (Consumer<ReagentInputResponse>) pondMainResponse -> {
            mReagentInputResponse.add(pondMainResponse);
            mAdapter.notifyDataSetChanged();
        });

        mRxManager.on(AppConstant.RX_UPDATE_REAGENT_INPUT, (Consumer<ReagentInputUpdateBean>) gudingUpdateBean -> {
            Log.e("test","yes");
            mReagentInputResponse.set(gudingUpdateBean.getItemPosition()-1,gudingUpdateBean.getReagentInputResponse());
            mAdapter.notifyItemChanged(gudingUpdateBean.getItemPosition());
        });
    }

    @Override
    protected void initListener() {
        ivToolbarNavigation.setOnClickListener(v -> onBackPressed());

        ibAddMenu.setOnClickListener(view -> {
            if (!UserCache.getUserRole().equals("ROLE_USER")){
                UIUtils.showToast("???????????????");
            }else {
                jumpToActivity(ReagentInputAddActivity.class);
            }
        });
        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            mPosition = position-1;
            if (!UserCache.getUserRole().equals("ROLE_USER")){
                UIUtils.showToast("???????????????");
            }else {
                showChooseView(position-1);
            }
            return false;
        });
    }

    private void showChooseView(int itemPosition) {
        Dialog dialog = new Dialog(this,R.style.MyDialog);
        View choose = LayoutInflater.from(this).inflate(R.layout.dialog_choose, null);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AnimBottom);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.setContentView(choose);
        setViewAndListener(dialog,choose,itemPosition);
        dialog.show();

    }

    private void setViewAndListener(Dialog dialog, View choose, int itemPosition) {
        View vEdit =  choose.findViewById(R.id.tvChooseEdit);
        View vCancel =  choose.findViewById(R.id.tvChooseCancel);
        View vDelete = choose.findViewById(R.id.tvChooseDel);
        vEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                jumpToPondEdit(itemPosition);
            }
        });

        vCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        vDelete.setOnClickListener(view -> {
            dialog.dismiss();
            mPresenter.deleteReagentInput(mReagentInputResponse.get(itemPosition).getId());
        });
    }

    public void jumpToPondEdit(int itemPosition){
        ReagentInputResponse reagentInputResponse = mReagentInputResponse.get(itemPosition);
        // reagentInputUpdate
        Intent intent = new Intent(this, ReagentInputUpdateActivity.class);
        intent.putExtra(AppConstant.EXTRA_ITEM_INDEX,itemPosition+1);
        intent.putExtra(AppConstant.EXTRA_IS_FROM_REAGENT_INPUT_MAIN,true);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstant.BUNDLE_REAGENT_INPUT_BEAN,reagentInputResponse);
        intent.putExtras(bundle);
        jumpToActivity(intent);
    }

    private void initAdapter() {
        mRecCustom.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mAdapter = new BaseQuickAdapter<ReagentInputResponse, BaseViewHolder>(R.layout.item_reagent_input,mReagentInputResponse) {
            @Override
            protected void convert(BaseViewHolder helper, ReagentInputResponse item) {
                helper.setText(R.id.pond,item.getPond());
                helper.setText(R.id.batchNumber,item.getBatchNumber());
                helper.setText(R.id.date,item.getTime());
                helper.setText(R.id.reagent,item.getReagent());
                helper.setText(R.id.amount,String.valueOf(item.getAmount())+item.getUnit());
                helper.setText(R.id.remarks,item.getRemarks());
            }
        };
        mRecCustom.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //???????????????
                mPage = 1;
                mPresenter.getUserReagentInput(mUserName,mPage,AppConstant.NORMAL_PAGE_SIZE);
            }

            @Override
            public void onLoadMore() {
                // ????????????
                mPage++;
                mPresenter.getUserReagentInput(mUserName,mPage,AppConstant.NORMAL_PAGE_SIZE);
            }
        });
        mRecCustom.setAdapter(mAdapter);

        // ???????????????
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_pond_main, null, false);
        TextView tvEmptyDes =  emptyView.findViewById(R.id.tvEmptyDes);
        tvEmptyDes.setText("????????????????????????");
        mAdapter.setEmptyView(emptyView);
    }

    public void remove(int position) {
        mReagentInputResponse.remove(position);
        mAdapter.notifyItemRemoved(position+1);
        mAdapter.notifyItemRangeChanged(position+1,mReagentInputResponse.size()-position);
    }

    @Override
    public void deleteReagentInputSuccess() {
        remove(mPosition);
        UIUtils.showToast("????????????");
    }

    @Override
    public void deleteReagentInputFailure(String error) {
        UIUtils.showToast(error);
    }

    @Override
    public void getReagentInputSuccess(List<ReagentInputResponse> reagentInputResponseList) {
        if (mIsFirst){
            if (reagentInputResponseList != null){
                mReagentInputResponse.clear();
                mReagentInputResponse.addAll(reagentInputResponseList);
                mAdapter.notifyDataSetChanged();
            }
            mIsFirst = false;
        }else {
            if (mPage == 1){
                if (reagentInputResponseList != null){
                    mReagentInputResponse.clear();
                    mReagentInputResponse.addAll(reagentInputResponseList);
                    mAdapter.notifyDataSetChanged();
                    // ????????????
                    mRecCustom.refreshComplete();
                }

            }else {
                if (reagentInputResponseList != null && reagentInputResponseList.size() > 0){
                    mReagentInputResponse.addAll(reagentInputResponseList);
                    mAdapter.notifyDataSetChanged();
                    mRecCustom.refreshComplete();
                }else {
                    mRecCustom.noMoreLoading();
                }
            }
        }
    }

    @Override
    public void getReagentInputFailure(String error) {
        UIUtils.showToast(error);
        mRecCustom.refreshComplete();
        if (mIsFirst){
            mIsFirst = false;
        }

        if (mPage > 1) {
            mPage--;
        }
    }
}