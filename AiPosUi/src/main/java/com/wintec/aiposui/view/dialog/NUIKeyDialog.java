package com.wintec.aiposui.view.dialog;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.wintec.aiposui.R;
import com.wintec.aiposui.model.GoodsModel;
import com.wintec.aiposui.view.control.NUIKeyView;


import androidx.appcompat.app.AppCompatDialog;

public class NUIKeyDialog extends AppCompatDialog {
    public NUIKeyDialog(Context context) {
        this(context, R.style.QMUI_Dialog);
    }

    public NUIKeyDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    public static class Builder {
        private Context mContext;
        private int dialogWidth, dialogHeight;
        private int screenWidth, screenHeight;
        private int param = -1;
        private View contentView = null;
        private NUIKeyView keyView = null;

        public Builder(Context context) {
            mContext = context;
            Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
            //屏幕宽度
            screenWidth = QMUIDisplayHelper.getScreenWidth(mContext);
            screenHeight = QMUIDisplayHelper.getScreenHeight(mContext);
            if (mConfiguration.orientation==Configuration.ORIENTATION_LANDSCAPE){
                dialogWidth = (int) (screenWidth * 0.4);
            }
            else{
                dialogWidth = (int) (screenWidth * 0.85);
            }

            dialogHeight = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        private DialogListner dialogListner;

        public interface DialogListner {
            void onConfirm(String code, NUIKeyDialog dialog, int param);

            void onCancel(NUIKeyDialog dialog);
        }

        /**
         * 设置内容View
         *
         * @return
         */
        public Builder addKeyView(String title, int type) {
            keyView = new NUIKeyView(mContext, null);
            keyView.setKeyModel(title, type);
            return this;
        }


        public Builder addDialogListner(DialogListner listner) {
            dialogListner = listner;
            return this;
        }


        public Builder addView(View view) {
            contentView = view;
            return this;
        }


        /**
         * @return
         */
        public NUIKeyDialog create() {
            return create(true,false);
        }
        public NUIKeyDialog create(boolean isBlockIng) {
            return create(true,isBlockIng);
        }
        public NUIKeyDialog create(boolean cancelable,boolean isBlockIng) {
            NUIKeyDialog mDialog = new NUIKeyDialog(mContext) {
                @Override
                public void dismiss() {
                    keyView.clearInput();
                    keyView.goodModelDismiss();
                    super.dismiss();
                }

                @Override
                public void showWithParams(int p) {
                    param = p;
                    keyView.setInput(p+"");
                    show();
                }
                @Override
                public void showWithPlu(GoodsModel goodsModel) {
                    keyView.setGoodModel(goodsModel);
                    show();
                }
            };

            keyView.setConfirmBtnListner(new NUIKeyView.ConfirmListner() {
                @Override
                public void onConfirm(String code,int argment) {
                    if (dialogListner != null) {
                        dialogListner.onConfirm(code, mDialog, argment);
                    } else {
                        mDialog.dismiss();
                    }
                }

                @Override
                public void onCancel() {
                    if (dialogListner != null) {
                        dialogListner.onCancel(mDialog);
                    } else {
                        mDialog.dismiss();
                    }
                }
            });
            mDialog.addContentView(keyView, new ViewGroup.LayoutParams(dialogWidth, dialogHeight));
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            return mDialog;
        }
    }


    public void showWithParams(int param) {

    }

    public void showWithPlu(GoodsModel goodsModel) {

    }

}
