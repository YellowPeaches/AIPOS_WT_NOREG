package com.wintec.lamp.contract;

import com.wintec.lamp.base.BaseMvpPresenter;
import com.wintec.lamp.base.BaseMvpView;
import com.wintec.lamp.dao.entity.PluDto;

import java.util.List;

/**
 * @author 赵冲
 * @description:
 * @date :2021/7/7 9:32
 */
public interface BarSettingContart {

    interface IView extends BaseMvpView {
        void showFile(String msg);

        void showSucces(String msg);

        void saveData(String s);
    }

    abstract class Presenter extends BaseMvpPresenter<BarSettingContart.IView> {

        public abstract void getUpdateBar(String branchId);

        public abstract void getUpdatePriceTar2(String posSn);

        public abstract void getUpdatePriceTar(String branchId, String posSn);

    }
}
