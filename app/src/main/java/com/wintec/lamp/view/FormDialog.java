package com.wintec.lamp.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.wintec.aiposui.view.dialog.AiTipDialog;
import com.wintec.lamp.R;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.dao.entity.Commdity;
import com.wintec.lamp.dao.helper.CommdityHelper;
import com.wintec.lamp.network.NetWorkManager;
import com.wintec.lamp.network.response.ResponseTransformer;
import com.wintec.lamp.network.schedulers.SchedulerProvider;

import java.util.HashMap;

import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;

/**
 * @author 赵冲
 * @description:
 * @date :2021/2/24 14:19
 */
public class FormDialog {
    private Dialog dialog;
    Context context;
    View rootLayout;
    AiTipDialog aiTipDialog;
    EditText itemCode;
    EditText name;
    EditText price;
    Spinner unitSpinner;
    Button insertButtun;
    Button quitButtun;

    public FormDialog(Context context, View rootLayout) {
        super();
        this.context = context;
        this.rootLayout = rootLayout;
    }

    public void initDialog() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.insert_commdity, null);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        dialog = new Dialog(context);
        dialog.addContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (LinearLayout.LayoutParams.MATCH_PARENT)));
        dialog.setCanceledOnTouchOutside(false);
        itemCode = view.findViewById(R.id.commdity_itemcode);
        name = view.findViewById(R.id.commdity_name);
        price = view.findViewById(R.id.commdity_price);
        unitSpinner = view.findViewById(R.id.commdity_unit);
        insertButtun = view.findViewById(R.id.commdity_insert);
        quitButtun = view.findViewById(R.id.commdity_quit);
        aiTipDialog = new AiTipDialog();
        insertButtun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
                //数据上传

                RequestBody body = getBody();
                if (body == null) {
                    return;
                }
                aiTipDialog.showLoading("正在上传", context);
                Disposable disposable = NetWorkManager
                        .getRequest()
                        .insertSku(body)
                        .compose(ResponseTransformer.handleResult())
                        .compose(SchedulerProvider.getInstance().applySchedulers())
                        .subscribe(result -> {
                            // 处理数据

                            CommdityHelper.insertCommdity(result);

                            aiTipDialog.dismiss();
                            aiTipDialog.showSuccess("上传成功", rootLayout);
                            dialog.dismiss();
                            clear();
                        }, throwable -> {
                            // 处理异常
                            aiTipDialog.dismiss();
                            aiTipDialog.showFail("上传失败", rootLayout);
                            dialog.dismiss();
                            clear();
                        });
            }
        });
        quitButtun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public void show() {
        dialog.show();
    }

    //整理数据
    public RequestBody getBody() {
        String s = null;
        if ("".equals(itemCode.getText().toString()) || itemCode.getText().toString() == null) {
            aiTipDialog.showFail("商品编码不能为空", rootLayout);
            return null;
        } else {
            s = String.format("%0" + Const.getSettingValue(Const.KEY_ITEMCODE_SIZE) + "d", Integer.valueOf(itemCode.getText().toString()));
            //TODO 第四处
            Commdity commdityByItemCode = CommdityHelper.getCommdityByScalesCode(s);
            if (commdityByItemCode != null) {
                aiTipDialog.showFail("商品编码已存在", rootLayout);
                return null;
            }
        }
        if ("".equals(price.getText().toString().trim()) || price.getText().toString().trim() == null) {
            aiTipDialog.showFail("商品价格不能为空", rootLayout);
            return null;
        }
        if ("".equals(name.getText().toString().trim()) || name.getText().toString().trim() == null) {
            aiTipDialog.showFail("商品名称不能为空", rootLayout);
            return null;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("itemCode", s);
        map.put("price", price.getText().toString().trim());
        map.put("name", name.getText().toString().trim());
        map.put("branchId", Const.getSettingValue(Const.KEY_BRANCH_ID));
        if (unitSpinner.getSelectedItem().toString().equals("计重") || unitSpinner.getSelectedItem().toString() == "计重") {
            map.put("unitId", "4");
        } else {
            map.put("unitId", "9");
        }
        Gson gson = new Gson();
        String strEntity = gson.toJson(map);
        Log.e("=====json串", strEntity);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        return body;
    }

    public void clear() {
        itemCode.setText("");
        name.setText("");
        price.setText("");
    }
}
