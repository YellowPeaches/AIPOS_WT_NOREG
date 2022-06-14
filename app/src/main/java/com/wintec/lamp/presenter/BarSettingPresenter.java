package com.wintec.lamp.presenter;

import com.wintec.lamp.api.ComModel;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.dao.entity.TagMiddle;
import com.wintec.lamp.bean.TagRules;
import com.wintec.lamp.contract.BarSettingContart;
import com.wintec.lamp.dao.helper.TagMiddleHelper;
import com.wintec.lamp.network.ModelRequestCallBack;
import com.wintec.lamp.result.HttpResponse;
import com.wintec.lamp.service.WintecService;
import com.wintec.lamp.service.WintecServiceSingleton;
import com.wintec.lamp.utils.log.Logging;

import java.util.List;
import java.util.Map;

/**
 * @author 赵冲
 * @description:
 * @date :2021/7/7 9:32
 */
public class BarSettingPresenter extends BarSettingContart.Presenter {
    private ComModel comModel;
    private Logging logging;

    @Override
    public void getUpdateBar(String branchId) {
        comModel.getBarCode(branchId, new ModelRequestCallBack<TagRules>() {
            @Override
            public void onSuccess(HttpResponse<TagRules> response) {
                //todo  处理条码数据
                if ("1".equals(Const.getSettingValue(Const.BAR_CODE_MULTI_PRICE_SIGN))) {
                    getUpdatePriceTar2(Const.SN);
                } else {
                    getUpdatePriceTar(branchId, Const.SN);
                }
                String[] listItems = new String[]{
                        "前缀-PLU-总价",
                        "前缀-PLU-重量",
                        "前缀-PLU-总价-重量",
                        "前缀-PLU-单价-重量",
                        "前缀-PLU-重量-单价",
                        "前缀-PLU-重量-总价",
                        "货号-重量-总价",
//                        "货号-总价-重量",

                };
                TagRules tagRules = response.getData();
                if (tagRules == null) {
                    getView().showFile("请先设置条码格式");
                    return;
                }
                if ("0".equals(tagRules.getBarNumber())) {
                    Const.setSettingValue(Const.BAR_CODE_LENGTH, "13位");
                } else {
                    Const.setSettingValue(Const.BAR_CODE_LENGTH, "18位");
                }
                Const.setSettingValue(Const.BAR_CODE_PREFIX, tagRules.getPrefix());
                switch (tagRules.getDigits()) {
                    case 0:
                        Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "3");
                        Const.setSettingValue(Const.BAR_CODE_PLU_LENGTH, "五位");
                        break;
                    case 1:
                        Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "2");
                        Const.setSettingValue(Const.BAR_CODE_PLU_LENGTH, "六位");
                        break;
                    case 2:
                        Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "1");
                        Const.setSettingValue(Const.BAR_CODE_PLU_LENGTH, "七位");
                        break;
                    case 3:
                        Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "1");
                        Const.setSettingValue(Const.BAR_CODE_PLU_LENGTH, "九位");
                        break;
                    default:
                        Const.setSettingValue(Const.BAR_CODE_PLU_COORDINATE, "3");
                        Const.setSettingValue(Const.BAR_CODE_PLU_LENGTH, "五位");
                        break;
                }
                try{
                    Const.setSettingValue(Const.BAR_CODE_FORMAT, listItems[tagRules.getFormat()]);
                    getView().saveData(Const.getSettingValue(Const.BAR_CODE_FORMAT));
                }catch (Exception e){
                    logging.i(e.getMessage());
                }

                if (tagRules.getNumberBits() == 0) {
                    Const.setSettingValue(Const.BAR_CODE_PIECT_FLAG, "个位开始");
                } else if (tagRules.getNumberBits() == 1) {
                    Const.setSettingValue(Const.BAR_CODE_PIECT_FLAG, "千位开始");
                } else {
                    Const.setSettingValue(Const.BAR_CODE_PIECT_FLAG, "十位开始");

                }
                if (tagRules.getCheckDigit() == 0) {
                    Const.setSettingValue(Const.KEY_ODD_EVEN_CHECK, "奇校验");
                } else {
                    Const.setSettingValue(Const.KEY_ODD_EVEN_CHECK, "偶校验");
                }
                if (tagRules.getPrint() == 0) {
                    Const.setSettingValue(Const.BAR_CODE_IS_CHECK, "1");
                } else {
                    Const.setSettingValue(Const.BAR_CODE_IS_CHECK, "0");
                }
            }

            @Override
            public void onFail() {
                getView().showFile("下载条码格式失败");
            }
        });
    }

    @Override
    public void getUpdatePriceTar2(String posSn) {
        comModel.getPriceTag2(posSn, new ModelRequestCallBack<Map<Integer, List<TagMiddle>>>() {
            @Override
            public void onSuccess(HttpResponse<Map<Integer, List<TagMiddle>>> response) {
                Map<Integer, List<TagMiddle>> data = response.getData();
                if (data != null || data.size() > 0) {
                    getView().showSucces("下载价签格式成功");
                    TagMiddleHelper.deleteAll();
                    data.forEach((labelNo, tagMiddles) -> {
                        float offsetX = 2.01f;
                        float offsetY = 2.01f;
                        float tarOffsetX = 0.688f;
                        float textOffset = 0.78f;
                        //  float offset = 1f;
                        tagMiddles.forEach(item -> {
                            int leftOffset = 0;
                            item.setLabelNo(labelNo);
                            if (item.getFontSize() != null) {
                                item.setFontSize((int) (item.getFontSize() * textOffset));
                            }
                            item.setAbscissa(new Float(item.getAbscissa() * offsetX).intValue());
                            item.setOrdinate(new Float(item.getOrdinate() * offsetY).intValue());
                            if (item.getLength() != null && item.getBreadth() != null) {
                                item.setLength(new Float(item.getLength() * tarOffsetX).intValue());
                                item.setBreadth(new Float(item.getBreadth() * tarOffsetX).intValue());
                            }
                        });
                        TagMiddleHelper.insertList(tagMiddles);
                    });
                } else {
                    getView().showFile("请先设置价签格式");
                }
            }

            @Override
            public void onFail() {
                getView().showFile("下载价签格式失败");
            }
        });
    }

    @Override
    public void getUpdatePriceTar(String branchId, String posSn) {
        comModel.getPriceTag(branchId, posSn, new ModelRequestCallBack<List<TagMiddle>>() {
            @Override
            public void onSuccess(HttpResponse<List<TagMiddle>> response) {
                //todo  处理条码格式
                List<TagMiddle> data = response.getData();
                if (data == null || data.size() == 0) {
                    getView().showFile("请先设置价签格式");
                } else {
                    getView().showSucces("下载价签格式成功");
                    float offsetX = 2.01f;
                    float offsetY = 2.01f;
                    float tarOffsetX = 0.688f;
                    float textOffset = 0.78f;
                    //  float offset = 1f;
                    data.forEach(item -> {
                        int leftOffset = 0;
                        if (item.getFontSize() != null) {
                            item.setFontSize((int) (item.getFontSize() * textOffset));
                        }
                        item.setAbscissa(new Float(item.getAbscissa() * offsetX).intValue());
                        item.setOrdinate(new Float(item.getOrdinate() * offsetY).intValue());
                        if (item.getLength() != null && item.getBreadth() != null) {
                            item.setLength(new Float(item.getLength() * tarOffsetX).intValue());
                            item.setBreadth(new Float(item.getBreadth() * tarOffsetX).intValue());
                        }
                    });
                    TagMiddleHelper.deleteAll();
                    TagMiddleHelper.insertList(data);
                    //刷新打印缓存
                    //WintecServiceSingleton.getInstance().cacheLableTag();
                }

            }

            @Override
            public void onFail() {
                getView().showFile("下载价签格式失败");

            }
        });
    }

    @Override
    protected void createModel() {
        comModel = new ComModel(this);
    }
}
