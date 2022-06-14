package com.wintec.lamp.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.wintec.aiposui.model.GoodsModel;
import com.wintec.lamp.R;
import com.wintec.lamp.dao.entity.Commdity;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.entity.Total;
import com.wintec.lamp.utils.CommUtils;


/**
 * @author 赵冲
 * @description:
 * @date :2021/2/22 14:12
 */
public class PrintDialog {
    private Dialog dialog;
    Context context;
    PrintDialogListener positiveListener;
    ImageView imageView;
    TextView name_id;
    TextView price_id;
    TextView unitPrice_id;
    TextView weigth_id;
    TextView unitName;
    TextView unitPriceText;
    Button quit_btn;
    Button print_btn;
    CountDownTimer timer;
    private Boolean isPrint = false;
    private PluDto commdity;
    private Total total;


    private int dialogWidth, dialogHeight;
    private int screenWidth, screenHeight;

    public PrintDialog(Context context) {
        super();
        this.context = context;
    }

    public void setPositiveListener(PrintDialogListener positiveListener) {
        this.positiveListener = positiveListener;
    }


    @SuppressLint("WrongViewCast")
    public void initDialog() {
        //屏幕宽度
        View view = LayoutInflater.from(context).inflate(R.layout.print_price_tag, null);
        screenWidth = QMUIDisplayHelper.getScreenWidth(context);
        screenHeight = QMUIDisplayHelper.getScreenHeight(context);
        if (screenWidth > screenHeight) {
            dialogWidth = (int) (screenWidth * 0.35);
        } else {
            dialogWidth = (int) (screenWidth * 0.8);
        }
        dialogHeight = ViewGroup.LayoutParams.MATCH_PARENT;

        dialog = ResultDialog.creatAlertDialog(context, view);
        dialog.setContentView(view, new ViewGroup.LayoutParams(dialogWidth, dialogHeight));
        dialog.setCancelable(false);
        imageView = (ImageView) view.findViewById(R.id.img_id);
        name_id = (TextView) view.findViewById(R.id.name_id);
        price_id = (TextView) view.findViewById(R.id.price_id);
        unitPrice_id = (TextView) view.findViewById(R.id.unit_price_id);
        weigth_id = (TextView) view.findViewById(R.id.weight_id);
        quit_btn = (Button) view.findViewById(R.id.btn_quit);
        print_btn = (Button) view.findViewById(R.id.btn_print);
        unitName = (TextView) view.findViewById(R.id.unit_name);
        unitPriceText = view.findViewById(R.id.unit_text);
        quit_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveListener.dismissClick();
                dialog.dismiss();
                setPrint(false);
            }
        });
        print_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveListener.pintOnClick();
            }
        });

    }

    public void show(PluDto commdity, Total total, boolean isKg) {
        this.commdity = commdity;
        this.total = total;
        String price = "";
        if (!isKg) {
            price = CommUtils.Float2String(Float.parseFloat(total.getPrice()) / 2, 2);
        } else {
            price = total.getPrice();
        }
        if (commdity.getPriceUnitA() == 0) {
            if (isKg) {
                unitPriceText.setText("单价(元/kg)");
                unitName.setText("重量(kg)");
            } else {
                unitPriceText.setText("单价(元/500g)");
                unitName.setText("重量(kg)");
            }
        } else {
            unitPriceText.setText("单价(元/件)");
            unitName.setText("件数(个)");
        }
        name_id.setText(commdity.getNameTextA());
        unitPrice_id.setText(price);
        Glide.with(context).load(commdity.getPreviewImage()).placeholder(R.drawable.img_def).error(R.drawable.img_def).into(imageView);
        isPrint = true;
        dialog.show();
    }

    public void setTotalandWeight(String weight, String price, String total) {
        weigth_id.setText(weight);
        //计算总价
        unitPrice_id.setText(price);
        price_id.setText(total);

    }

    public interface PrintDialogListener {
        void pintOnClick();

        void dismissClick();
    }

    public Boolean getPrint() {
        return isPrint;
    }

    public void setPrint(Boolean print) {
        isPrint = print;
    }

    public PluDto getCommdity() {
        return commdity;
    }

    public void setCommdity(PluDto commdity) {
        this.commdity = commdity;
    }

    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }
}
