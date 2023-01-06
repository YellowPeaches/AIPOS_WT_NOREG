package com.wintec.lamp.utils.wintecLable;

import com.elvishew.xlog.XLog;
import com.wintec.detection.utils.StringUtils;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.dao.entity.TagMiddle;
import com.wintec.lamp.dao.helper.TagMiddleHelper;

import java.util.ArrayList;
import java.util.List;


public class TransToLocal {

    static int lableNo = -1;

    public static void processBarcodes(String barcodeInfo) {

        List<TagMiddle> tagData = new ArrayList<>();
        //获得每行数据
        final String[] barcodes = barcodeInfo.split("\r\n");
        int widgetsNum = barcodes.length; //组件数量
        //拿到标签号
        for (int i = 2; i < widgetsNum; i++) {
            if (barcodes[i].contains("=LabelNumber,")) {
                String[] temp = barcodes[i].split(",");
                lableNo = Integer.valueOf(temp[8]);
                break;
            }
        }

        String lableName = barcodes[0];    //第一行作为标签名
        //获得标签大小数据
        final String[] lableSize = barcodes[1].split("=")[1].split(",");

        if (widgetsNum > 2) {
            for (int i = 2; i < widgetsNum; i++) {
                if (barcodes[i].contains("=LabelNumber")) {
                    continue;
                }
                String[] oneAttribute = barcodes[i].split(",");
                //处理一个组件
                TagMiddle widgets = organizeData(oneAttribute, generateCommonPart(lableName, Integer.valueOf(lableSize[0]), Integer.valueOf(lableSize[1])));
                if (widgets != null) {
                    tagData.add(widgets);
                }
            }
        }
        //保存价签
        saveLable(tagData);
    }

    /**
     * 生成TagMiddle，共同部分相同
     *
     * @param lableName 标签名称
     * @param length    标签长
     * @param width     标签宽
     * @return 默认通用部分TagMiddle
     */
    private static TagMiddle generateCommonPart(String lableName, int length, int width) {
        TagMiddle tagMiddle = new TagMiddle();
        tagMiddle.setTemplateId(1);
        tagMiddle.setName(lableName);
        tagMiddle.setLengths((int) (length / 7.18 + 0.5));
        tagMiddle.setLength((int) (length / 7.18 + 0.5));
        tagMiddle.setBreadth((int) (width / 7.18 + 0.5));
        tagMiddle.setBreadths((int) (width / 7.18 + 0.5));
        tagMiddle.setUnderline(0);
        tagMiddle.setItalic(0);
        tagMiddle.setBz2("0");
        tagMiddle.setOverstriking(1);//设置加粗
        tagMiddle.setLabelNo(lableNo);

        return tagMiddle;
    }

    /**
     * 价签存储到本地
     *
     * @param tagData
     */
    private static void saveLable(List<TagMiddle> tagData) {
        float offsetX = 2.01f;
        float offsetY = 2.01f;
        float tarOffsetX = 0.688f;
        float textOffset = 0.78f;
        tagData.forEach(item -> {
            if (item.getFontSize() != null) {
                item.setFontSize((int) (item.getFontSize() * textOffset));
            }
            item.setAbscissa(new Float(item.getAbscissa() * offsetX).intValue());
            item.setOrdinate(new Float(item.getOrdinate() * offsetY).intValue());
        });
        try {
            if (!"1".equals(Const.getSettingValue(Const.BAR_CODE_MULTI_PRICE_SIGN))) {
                TagMiddleHelper.deleteAll();
            } else {
                List<TagMiddle> deleteTagData = TagMiddleHelper.selectBarCodebyLableNo(-1);
                TagMiddleHelper.deleteOneLable(deleteTagData);
                deleteTagData.clear();
                deleteTagData = TagMiddleHelper.selectBarCodebyLableNo(lableNo);
                TagMiddleHelper.deleteOneLable(deleteTagData);
            }
            TagMiddleHelper.insertList(tagData);
        } catch (Exception e) {
            XLog.e(e);
        }
    }

    /**
     * @param oneTagInfo     本地软件下发的一条组件信息
     * @param labelAttribute 默认组件
     * @return 处理好的组件信息
     */
    private static TagMiddle organizeData(String oneAttribute[], TagMiddle labelAttribute) {
        TagMiddle oneLabelAttribute = labelAttribute;

        oneLabelAttribute.setAbscissa((int) (Integer.valueOf(oneAttribute[1]) / 1.86));
        oneLabelAttribute.setOrdinate((int) (Integer.valueOf(oneAttribute[2]) / 1.98));
        oneLabelAttribute.setFontSize((int) (Integer.valueOf(oneAttribute[6]) + 8));
        oneLabelAttribute.setCodeSystem(0); //是否打印内容   0打印标题与内容,1仅打印内容,2仅打印标题

        int angle = rotate(oneAttribute[5]);
        oneLabelAttribute.setUnderline(angle);
        if (angle == 0) {
            oneLabelAttribute.setLength(Integer.valueOf(oneAttribute[3]));
            oneLabelAttribute.setBreadth(Integer.valueOf(oneAttribute[4]));
        } else {
            oneLabelAttribute.setLength(Integer.valueOf(oneAttribute[4]));
            oneLabelAttribute.setBreadth(Integer.valueOf(oneAttribute[3]));
        }
        //商品名
        if (oneAttribute[0].endsWith("=article")) {
            oneLabelAttribute.setCodeSystem(1);
            oneLabelAttribute.setDivId("drag1");
            oneLabelAttribute.setTagName(oneAttribute[8]);   //标签标题

        }
        //通用   单位解析，固定文本
        if (oneAttribute[0].endsWith("fixedText")) {
            oneLabelAttribute.setCodeSystem(2); //是否打印内容   0打印标题与内容,1仅打印内容,2仅打印标题
            oneLabelAttribute.setDivId("drag100");
            oneLabelAttribute.setTagName(oneAttribute[8]);   //标签标题
            oneLabelAttribute.setOverstriking(0);
        }
        //门店名
        if (oneAttribute[0].endsWith("=store")) {
            oneLabelAttribute.setCodeSystem(1); //是否打印内容   0打印标题与内容,1仅打印内容,2仅打印标题
            oneLabelAttribute.setDivId("drag12");
            oneLabelAttribute.setTagName(oneAttribute[8]);   //标签标题
            Const.setSettingValue(Const.KEY_BRANCH_NAME, oneAttribute[8]);
        }
        //单价
        if (oneAttribute[0].endsWith("=price")) {
            oneLabelAttribute.setCodeSystem(1); //是否打印内容   0打印标题与内容,1仅打印内容,2仅打印标题
            oneLabelAttribute.setDivId("drag19");
            oneLabelAttribute.setTagName("单价");
            oneLabelAttribute.setUnit(" ");
        }
        //总价
        if (oneAttribute[0].endsWith("=amount")) {
            oneLabelAttribute.setCodeSystem(1); //是否打印内容   0打印标题与内容,1仅打印内容,2仅打印标题
            oneLabelAttribute.setDivId("drag20");
            oneLabelAttribute.setTagName("总价");
            oneLabelAttribute.setUnit(" ");
            oneLabelAttribute.setFontSize(oneLabelAttribute.getFontSize() + 10);
        }
        //条码   条形码和二维码
        if (oneAttribute[0].endsWith("=barcodeCode128") || oneAttribute[0].endsWith("=barcodeEAN13")
                || oneAttribute[0].endsWith("=qrCode")) {
            //二维码
            if (oneAttribute[0].endsWith("=qrCode")) {
                oneLabelAttribute.setTagName("二维码");
                Const.setSettingValue(Const.BAR_CODE_OR_QRCODE_FLAG, (1 + ""));
            } else {
                oneLabelAttribute.setTagName("商品条码");
                Const.setSettingValue(Const.BAR_CODE_OR_QRCODE_FLAG, (0 + ""));
            }
            oneLabelAttribute.setCodeSystem(0); //是否打印内容   0打印标题与内容,1仅打印内容,2仅打印标题
            oneLabelAttribute.setDivId("drag21");
        }
        //包装日期
        if (oneAttribute[0].endsWith("=packDate")) {
            oneLabelAttribute.setCodeSystem(1); //是否打印内容   0打印标题与内容,1仅打印内容,2仅打印标题
            oneLabelAttribute.setDivId("drag23");
            oneLabelAttribute.setTagName("称重日期");
            oneLabelAttribute.setDateFormat("yyyy/MM/dd");
        }
        //保质日期yyyy/MM/dd
        if (oneAttribute[0].endsWith("=bestDate")) {
            oneLabelAttribute.setCodeSystem(1); //是否打印内容   0打印标题与内容,1仅打印内容,2仅打印标题
            oneLabelAttribute.setDivId("drag29");
            oneLabelAttribute.setTagName("保质日期");
            oneLabelAttribute.setDateFormat("yyyy/MM/dd");
        }
        //秤号
        if (oneAttribute[0].endsWith("=scaleNumber")) {
            oneLabelAttribute.setCodeSystem(1); //是否打印内容   0打印标题与内容,1仅打印内容,2仅打印标题
            oneLabelAttribute.setDivId("drag3");
            oneLabelAttribute.setTagName("秤号");
        }
        //重量
        if (oneAttribute[0].endsWith("weight")) {
            oneLabelAttribute.setDivId("drag4");
            oneLabelAttribute.setCodeSystem(1);
            oneLabelAttribute.setUnit(" ");
            oneLabelAttribute.setUnits("PCS");
            oneLabelAttribute.setTagName("重量数字");
        }
        //plu码
        if (oneAttribute[0].endsWith("=pluNumber")) {
            oneLabelAttribute.setDivId("drag6");
            oneLabelAttribute.setCodeSystem(1);
            oneLabelAttribute.setTagName("商品编码");
        }
        //时间HH:mm:ss
        if (oneAttribute[0].endsWith("=time")) {
            oneLabelAttribute.setDivId("drag24");
            oneLabelAttribute.setCodeSystem(1);
            oneLabelAttribute.setTagName("时间");
            oneLabelAttribute.setDateFormat("HH:mm:ss");
        }
        if (oneAttribute[0].endsWith("=ingredients")) {
            oneLabelAttribute.setDivId("drag9");
            oneLabelAttribute.setCodeSystem(1);
            oneLabelAttribute.setTagName("配料");
        }
        //附加文本1
        if (oneAttribute[0].endsWith("=extraTextA")) {
            oneLabelAttribute.setDivId("drag30");
            oneLabelAttribute.setCodeSystem(1);
            oneLabelAttribute.setTagName(oneAttribute[8]);
        }
        if (oneAttribute[0].endsWith("=extraTextB")) {
            oneLabelAttribute.setDivId("drag31");
            oneLabelAttribute.setCodeSystem(1);
            oneLabelAttribute.setTagName(oneAttribute[8]);
        }
        if (oneAttribute[0].endsWith("=extraTextC")) {
            oneLabelAttribute.setDivId("drag32");
            oneLabelAttribute.setCodeSystem(1);
            oneLabelAttribute.setTagName(oneAttribute[8]);
        }
        if (oneAttribute[0].endsWith("=extraTextD")) {
            oneLabelAttribute.setDivId("drag33");
            oneLabelAttribute.setCodeSystem(1);
            oneLabelAttribute.setTagName(oneAttribute[8]);
        }
        if (oneAttribute[0].endsWith("=weightUnit") || oneAttribute[0].endsWith("=priceUnit") || oneAttribute[0].endsWith("=amountUnit")) {
            oneLabelAttribute.setCodeSystem(2); //是否打印内容   0打印标题与内容,1仅打印内容,2仅打印标题
            oneLabelAttribute.setDivId("drag101");
            oneLabelAttribute.setTagName(oneAttribute[8]);   //标签标题
        }

        return oneLabelAttribute;
    }

    /**
     * @param angle
     * @description: 调整组件方向角度
     * @return: int      组件方向  0 正常，1 顺时针旋转90度，2 顺时针旋转270度
     * @author: dean
     * @time: 2022/11/7 11:29
     */
    private static int rotate(String angle) {
        //默认返回0
        int ans = 0;
        if (StringUtils.isNotEmpty(angle)) {
            if ("0".equals(angle))
                ans = 0;
            else if ("1".equals(angle))
                ans = 1;
            else if ("2".equals(angle))
                ans = 3;
        }
        return ans;
    }
}

