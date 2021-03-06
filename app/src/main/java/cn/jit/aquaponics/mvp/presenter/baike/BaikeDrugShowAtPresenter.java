package cn.jit.aquaponics.mvp.presenter.baike;

import android.annotation.SuppressLint;

//import cn.jit.aquaponics.api.ApiRetrofit;
import cn.jit.aquaponics.base.AppConstant;
import cn.jit.aquaponics.mvp.ui.view.baike.BaikeDrugShowAtView;
import com.zxl.baselib.ui.base.BaseActivity;
import com.zxl.baselib.ui.base.BasePresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author crazyZhangxl on 2018/10/15.
 * Describe:
 */
public class BaikeDrugShowAtPresenter extends BasePresenter<BaikeDrugShowAtView> {

    public BaikeDrugShowAtPresenter(BaseActivity context) {
        super(context);
    }


    @SuppressWarnings("AlibabaMethodTooLong")
    @SuppressLint("CheckResult")
    public void queryBaikeDetail(int index) {

                getView().showLoadingDialog();
                ApiRetrofit.getInstance().queryDetailYaoBaikeInfo(index)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(baikeShowResponseBaseResponse -> {
                            getView().hideLoadingDialog();
                            if (baikeShowResponseBaseResponse.getCode() == AppConstant.REQUEST_SUCCESS){
                                getView().queryBaikeShowSuccess(baikeShowResponseBaseResponse.getData());
                            }else {
                                getView().queryBaikeShowFailure(baikeShowResponseBaseResponse.getMsg());
                            }
                        }, throwable -> {
                            getView().hideLoadingDialog();
                            getView().queryBaikeShowFailure(throwable.getLocalizedMessage());
                        });
    }
}
