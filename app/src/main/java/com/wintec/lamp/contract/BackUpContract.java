package com.wintec.lamp.contract;

import com.wintec.lamp.base.BaseMvpPresenter;
import com.wintec.lamp.base.BaseMvpView;
import com.wintec.lamp.bean.DiscernData;


/**
 * @author 赵冲
 * @description:
 * @date :2021/7/21 11:29
 */
public interface BackUpContract {
    interface IView extends BaseMvpView {

        void showFail(String mag);

        void showSuccess(String mag);

        void downLoadData(DiscernData discernData);

        void loading();

    }

    abstract class Presenter extends BaseMvpPresenter<BackUpContract.IView> {

        public abstract void exportData();

        public abstract void importData();

        public abstract void importDataByCloud();

        public abstract void upCloud();

        public abstract void exportDataCloud();

    }
}
