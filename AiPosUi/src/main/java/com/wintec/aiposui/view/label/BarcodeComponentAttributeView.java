package com.wintec.aiposui.view.label;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.wintec.aiposui.R;
import com.wintec.aiposui.view.AiPosLayout;

public class BarcodeComponentAttributeView extends AiPosLayout {
    private EditText edtX, edtY, edtWidth, edtHeight;
    private RadioGroup rgRotate;

    public BarcodeComponentAttributeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(View view, boolean isLandscape) {
        edtX = view.findViewById(R.id.edt_attribute_x);
        edtY = view.findViewById(R.id.edt_attribute_y);
        edtWidth = view.findViewById(R.id.edt_barcode_width);
        edtHeight= view.findViewById(R.id.edt_barcode_height);
        rgRotate = view.findViewById(R.id.rg_rotate);
    }

    @Override
    protected int getPortraitLayout() {
        return R.layout.view_barcode_attribute;
    }

    @Override
    protected int getLandscapeLayout() {
        return R.layout.view_barcode_attribute;
    }
}
