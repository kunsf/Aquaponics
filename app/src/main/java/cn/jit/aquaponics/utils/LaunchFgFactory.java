package cn.jit.aquaponics.utils;

import cn.jit.aquaponics.mvp.ui.fragment.DetFragment;
import cn.jit.aquaponics.mvp.ui.fragment.MeFragment;
import cn.jit.aquaponics.mvp.ui.fragment.MsgFragment;
import cn.jit.aquaponics.mvp.ui.fragment.NewsFragment;

/**
 * @author zxl on 2018/8/17.
 *         discription: 登录活动的工程类
 */

public class LaunchFgFactory {

    public static LaunchFgFactory getFactoryInstance(){
        return LaunchHolder.F_INSTANCE;
    }

    private LaunchFgFactory(){}

    private static class LaunchHolder{
        private static final LaunchFgFactory F_INSTANCE = new LaunchFgFactory();
    }


    private DetFragment mDetFgInstance = null;
    private MeFragment mMeFgInstance = null;
    private MsgFragment mMsgInstance = null;
    private NewsFragment mNewFgInstance = null;


    public DetFragment getDetFgInstance(){
        if (mDetFgInstance == null){
            synchronized (LaunchFgFactory.class){
                if (mDetFgInstance == null){
                    mDetFgInstance = new DetFragment();
                }
            }
        }
        return mDetFgInstance;
    }

    public MeFragment getMeFgInstacne(){
        if (mMeFgInstance == null){
            synchronized (LaunchFgFactory.class){
                if (mMeFgInstance == null){
                    mMeFgInstance = new MeFragment();
                }
            }
        }
        return mMeFgInstance;
    }

    public MsgFragment getMsgInstance(){
        if (mMsgInstance == null){
            synchronized (LaunchFgFactory.class){
                if (mMsgInstance == null){
                    mMsgInstance = new MsgFragment();
                }
            }
        }
        return mMsgInstance;
    }

    public NewsFragment getNewFgInstance(){
        if (mNewFgInstance == null){
            synchronized (LaunchFgFactory.class){
                if (mNewFgInstance == null){
                    mNewFgInstance = new NewsFragment();
                }
            }
        }
        return mNewFgInstance;
    }

}