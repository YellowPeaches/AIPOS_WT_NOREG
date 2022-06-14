package com.wintec.lamp.view;

import android.content.Context;
import android.view.View;

import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.wintec.lamp.R;

import androidx.core.content.ContextCompat;

public class NUIBottomSheet extends QMUIBottomSheet {
    private Context context;
    public ClickListener clickListener;

    public interface ClickListener {
        void onItemClick(QMUIBottomSheet dialog, View itemView, int position, String tag);
    }

    public NUIBottomSheet(Context context) {
        super(context);
        this.context = context;
    }

    public QMUIBottomSheet createQuitBottomSheet(ClickListener clickListener, String title) {
        QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(context);
        builder.setGravityCenter(true)
                .setSkinManager(QMUISkinManager.defaultInstance(context))
                .setTitle(title)
                .setAddCancelBtn(false)
                .setAllowDrag(false)
                .setNeedRightMark(false)
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        clickListener.onItemClick(dialog, itemView, position, tag);
                    }
                });
        builder.addItem("确定");
        builder.addItem("取消");
        return builder.build();
    }


}
