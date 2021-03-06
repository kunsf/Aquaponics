package cn.jit.aquaponics.uinew.second.rizhi;

//import cn.jit.aquaponics.model.response.TypeRizhiResponse;
import com.zxl.baselib.ui.base.BaseView;

/**
 * @author crazyZhangxl on 2018/9/27.
 * Describe: 获得所有塘口信息的View层
 */
public interface RizhiView extends BaseView {
    void deleteRizhiSuccess();
    void deleteRizhiFailure(String error);

    void getRizhiSuccess(TypeRizhiResponse typeRizhiResponse);
    void getRizhiFailure(String error);

}
