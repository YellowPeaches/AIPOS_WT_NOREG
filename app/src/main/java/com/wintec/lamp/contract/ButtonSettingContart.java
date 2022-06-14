package com.wintec.lamp.contract;

import com.wintec.lamp.base.BaseMvpPresenter;
import com.wintec.lamp.base.BaseMvpView;

/**
 * @author
 * @description:
 * @date :2022/3/24 11:32
 */
public interface ButtonSettingContart {

    interface IView extends BaseMvpView {
        void showFile(String msg);

        void showSucces(String msg);

        void saveData(String s);
    }

    abstract class Presenter extends BaseMvpPresenter<ButtonSettingContart.IView> {

        public abstract void getUpdateBar(String branchId);

        public abstract void getUpdatePriceTar2(String posSn);

        public abstract void getUpdatePriceTar(String branchId, String posSn);

    }
}
