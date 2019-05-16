package com.PhotoGridMaker;

import android.app.Application;
import com.PhotoGridMaker.utils.ToastUtil;

/**
 * @author HanXueFeng
 * @ProjectName project： CutPicToNine
 * @class package：com.wisdom
 * @class describe：
 * @time 2019/3/5 16:51
 * @change
 */
public class APPApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.init(this);
    }
}
