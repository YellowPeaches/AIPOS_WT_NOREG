package com.wintec.lamp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wintec.lamp.base.BaseConstance;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Android Studio.
 * User: hxw
 * Date: 21/3/2
 * Time: 下午4:29
 */
public class NavigatorFragment extends Fragment {
    private String TAG = "NavigatorFragment";
    private PublishSubject<Boolean> resultSubject;
    private PublishSubject<Boolean> cancleSubject;
    private PublishSubject<Boolean> attachSubject = PublishSubject.create();

    public Single<Boolean> startLoginForResult(Context context) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(TAG);
        if (fragment == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(this, TAG).commitNowAllowingStateLoss();
            return startLoginSingle();
        } else {
            return ((NavigatorFragment) fragment).startLoginSingle();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachSubject.onNext(true);
        attachSubject.onComplete();
    }

    public Single<Boolean> startLoginSingle() {
        resultSubject = PublishSubject.create();
        cancleSubject = PublishSubject.create();
        startLogin();
        return resultSubject.takeUntil(cancleSubject)
                .single(false);
    }

    @SuppressLint("CheckResult")
    private void startLogin() {
        if (!isAdded()) {
            attachSubject.subscribe(__ -> startLoginForResult());
        } else {
            startLoginForResult();
        }
    }

    private void startLoginForResult() {
        if (null != getActivity()) {
//            UserManager.getInstance().logOut(getActivity());
            Toast.makeText(getContext(), "请重新登录", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent();
        intent.setAction(BaseConstance.TOKEN_FAIL);
        intent.addCategory(BaseConstance.TOKEN_CATEGORY);
        startActivityForResult(intent, 77);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == BaseConstance.LOGIN_RESULT) {
            if (resultSubject == null) {
                resultSubject = PublishSubject.create();
            }
            resultSubject.onNext(true);
            resultSubject.onComplete();

        } else {
            if (cancleSubject == null) {
                cancleSubject = PublishSubject.create();
            }
            cancleSubject.onNext(true);
        }
    }
}
