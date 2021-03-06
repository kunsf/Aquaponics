package cn.jit.aquaponics.mvp.ui.view.config;

////import cn.jit.aquaponics.model.response.ConfigMainResponse;
////import cn.jit.aquaponics.model.response.PondMainResponse;
////import cn.jit.aquaponics.model.response.RobotPageResponse;
import com.zxl.baselib.ui.base.BaseView;

import java.util.List;

/**
 * @author crazyZhangxl on 2018/9/29.
 * Describe:
 */
public interface ConfigAddAtView extends BaseView {

    void addConfigSuccess(ConfigMainResponse configMainResponse);

    void addConfigFailure(String error);

    void getPondInfoSuccess(List<PondMainResponse> pondBeanList, List<String> pondStrList);

    void getPondInfoFailure(String error);

    void getRobotInfoSuccess(RobotPageResponse robotPageResponse);

    void getRobotInfoFailure(String error);


    void queryMyRobotSuccess(RobotPageResponse robotPageResponse);

    void queryMyRobotFailure(String error);
}
