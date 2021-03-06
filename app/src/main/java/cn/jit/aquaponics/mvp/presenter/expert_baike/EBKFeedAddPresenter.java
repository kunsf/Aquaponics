package cn.jit.aquaponics.mvp.presenter.expert_baike;

import android.annotation.SuppressLint;

//import cn.jit.aquaponics.api.ApiRetrofit;
import cn.jit.aquaponics.base.AppConstant;
//import cn.jit.aquaponics.model.response.BaikeFeedBean;
import cn.jit.aquaponics.mvp.ui.view.expert_baike.EBKAddAtView;
import com.zxl.baselib.ui.base.BaseActivity;
import com.zxl.baselib.ui.base.BasePresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author crazyZhangxl on 2018/10/30.
 * Describe:
 */
public class EBKFeedAddPresenter extends BasePresenter<EBKAddAtView> {
    public EBKFeedAddPresenter(BaseActivity context) {
        super(context);
    }


    @SuppressLint("CheckResult")
    public void doFeedAddRequest( String name,String category, String source, String content){
        BaikeFeedBean baikeFeedBean = new BaikeFeedBean();
        baikeFeedBean.setName(name);
        baikeFeedBean.setCategory(category);
        baikeFeedBean.setSource(source);
        baikeFeedBean.setContent(content);
        ApiRetrofit.getInstance().addFeedBaike(baikeFeedBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                    getView().hideLoadingDialog();
                    if (baseResponse.getCode() == AppConstant.REQUEST_SUCCESS){
                        getView().ebkAddSuccess();
                    }else {
                        getView().ebkAddFailure(baseResponse.getMsg());
                    }
                }, throwable -> {
                    getView().hideLoadingDialog();
                    getView().ebkAddFailure(throwable.getLocalizedMessage());
                });
    }
}
