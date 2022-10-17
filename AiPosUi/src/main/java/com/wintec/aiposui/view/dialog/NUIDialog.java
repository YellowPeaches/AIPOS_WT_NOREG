package com.wintec.aiposui.view.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogView;
import com.wintec.aiposui.R;
import com.wintec.aiposui.view.control.NUIKeyView;

/**
 * @描述：自定义键盘Dialog
 * @文件名: AiPos.NUIKeyDialog
 * @作者: ningzhenyu
 * @邮箱： 348723352@qq.com
 * @创建时间: 2020/9/21 15:24
 * 使用
 * new NUIKeyDialog.Builder(context).addKeyView("请输入打秤码",NUIKeyView.KEY_TYPE_CODE).addDialogListner(new NUIKeyDialog.Builder.DialogListner() {
 *             @Override
 *             public void onConfirm(String code, NUIKeyDialog dialog) {
 *
 *             }
 *
 *             @Override
 *             public void onCancel(NUIKeyDialog dialog) {
 *
 *             }
 *         }).create().show();
 *
 */
public class NUIDialog extends AppCompatDialog {

    public NUIDialog(Context context) {
        this(context, R.style.QMUI_Dialog);
    }
    public NUIDialog(Context context, int theme) {
        super(context, theme);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        setCanceledOnTouchOutside(false);
//        Window window = getWindow();
//        WindowManager.LayoutParams P = window.getAttributes();
//        P.gravity = Gravity.CENTER;
//        P.width = QMUIDisplayHelper.getScreenWidth(context)/2;
//        P.height=QMUIDisplayHelper.getScreenHeight(context)/2;
//        window.setAttributes(P);
//        window.setBackgroundDrawableResource(android.R.color.transparent);// 去掉dialog的默认背景
//        setContentView(R.layout.dialog_key);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static class Builder {
        private Context mContext;
        private int dialogWidth,dialogHeight;
        private int screenWidth,screenHeight;
        private View contentView=null;
        private NUIKeyView keyView=null;
        public Builder(Context context) {
            mContext = context;
            //屏幕宽度
            screenWidth=QMUIDisplayHelper.getScreenWidth(mContext);
            screenHeight=QMUIDisplayHelper.getScreenHeight(mContext);
            dialogWidth= (int) (screenWidth*0.4);
            dialogHeight=ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        private DialogListner dialogListner;
        public interface DialogListner{
            void onConfirm(String code, NUIDialog dialog);
            void onCancel(NUIDialog dialog);
        }
        /**
         * 设置内容View
         * @return
         */
        public Builder addKeyView(String title,int type) {
            keyView=new NUIKeyView(mContext,null);
            keyView.setKeyModel(title,type);
            return this;
        }

        public Builder addDialogListner(DialogListner listner) {
            dialogListner=listner;
            return this;
        }


        public Builder addView(View view) {
            contentView=view;
            return this;
        }

        /**
         *
         * @return
         */
        public NUIDialog create() {
            return create(true);
        }
        public NUIDialog create(boolean cancelable) {
            NUIDialog mDialog=new NUIDialog(mContext);
            /*QMUIDialogView mDialogView = onCreateDialogView(mContext);
            if (contentView != null) {
                ConstraintLayout.LayoutParams lp = onCreateTitleLayoutParams();
                lp.topToTop=ConstraintLayout.LayoutParams.PARENT_ID;
                lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                lp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                lp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                mDialogView.addView(contentView, lp);
            }else if (keyView!=null){
                ConstraintLayout.LayoutParams lp = onCreateTitleLayoutParams();
                lp.topToTop=ConstraintLayout.LayoutParams.PARENT_ID;
                lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                lp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                lp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                mDialogView.addView(keyView, lp);
                keyView.setConfirmBtnListner(new NUIKeyView.ConfirmListner() {
                    @Override
                    public void onConfirm(String code) {
                        if (dialogListner!=null){
                            dialogListner.onConfirm(code,mDialog);
                        }else {
                            mDialog.dismiss();
                        }
                    }
                    @Override
                    public void onCancel() {
                        if (dialogListner!=null){
                            dialogListner.onCancel(mDialog);
                        }else {
                            mDialog.dismiss();
                        }
                    }
                });
            }*/
//            QMUIDialogRootLayout mRootView = new QMUIDialogRootLayout(mContext, mDialogView, new FrameLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            keyView.setConfirmBtnListner(new NUIKeyView.ConfirmListner() {
                @Override
                public void onConfirm(String code , int param) {
                    if (dialogListner!=null){
                        dialogListner.onConfirm(code,mDialog);
                    }else {
                        mDialog.dismiss();
                    }
                }
                @Override
                public void onCancel() {
                    if (dialogListner!=null){
                        dialogListner.onCancel(mDialog);
                    }else {
                        mDialog.dismiss();
                    }
                }
            });
            mDialog.addContentView(keyView, new ViewGroup. LayoutParams(dialogWidth, dialogHeight));
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            return mDialog;
        }
        protected QMUIDialogView onCreateDialogView(@NonNull Context context){
            QMUIDialogView dialogView = new QMUIDialogView(context);
            dialogView.setBackground(QMUIResHelper.getAttrDrawable(context, com.qmuiteam.qmui.R.attr.qmui_skin_support_dialog_bg));
            dialogView.setRadius(QMUIResHelper.getAttrDimen(context, com.qmuiteam.qmui.R.attr.qmui_dialog_radius));
            dialogView.setId(R.id.qmui_dialog_content_id);
            return dialogView;
        }


        protected ConstraintLayout.LayoutParams onCreateTitleLayoutParams() {
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            lp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            lp.verticalChainStyle = ConstraintLayout.LayoutParams.CHAIN_PACKED;
            return lp;
        }


        /**
         * 设置宽度百分比
         * @return
         */
        public Builder setWidthPercent(float mWidthPercent){
            dialogWidth= (int) (screenWidth*mWidthPercent);
            return this;
        }
        public Builder setWidthSize(int mWidthSize){
            dialogWidth=mWidthSize;
            return this;
        }
        /**
         * 设置高度百分比
         * @return
         */
        public Builder seHeightPercent(float mHeightPercent){
            dialogHeight=(int) (screenHeight*mHeightPercent);;
            return this;
        }
        public Builder seHeightSize(int mHeightSize){
            dialogHeight=mHeightSize;
            return this;
        }

    }

}
