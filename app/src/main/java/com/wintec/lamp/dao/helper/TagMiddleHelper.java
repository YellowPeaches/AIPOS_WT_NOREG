package com.wintec.lamp.dao.helper;

import com.alibaba.fastjson.asm.Label;
import com.wintec.lamp.base.MyApp;
import com.wintec.lamp.dao.CommdityDao;
import com.wintec.lamp.dao.TagMiddleDao;
import com.wintec.lamp.dao.entity.TagMiddle;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * @author 赵冲
 * @description:
 * @date :2021/7/9 8:32
 */
public class TagMiddleHelper {

    public static long insert(TagMiddle tagMiddle) {
        return MyApp.getDaoInstant().getTagMiddleDao().insert(tagMiddle);
    }

    public static void insertList(List<TagMiddle> list) {
        list.forEach(item -> {
            MyApp.getDaoInstant().getTagMiddleDao().insert(item);
        });
    }

    public static void deleteAll() {
        MyApp.getDaoInstant().getTagMiddleDao().deleteAll();
    }

    public static List<TagMiddle> selectToLable() {
        List<TagMiddle> list = MyApp.getDaoInstant().getTagMiddleDao().queryBuilder()
                .where(
                        TagMiddleDao.Properties.DivId.notEq("drag21"),
                        TagMiddleDao.Properties.DivId.notEq("drag11")
                        , TagMiddleDao.Properties.Bz2.eq(0)
                ).list();
        return list;
    }

    public static List<TagMiddle> selectToLable(Integer labelNo) {
        List<TagMiddle> list = MyApp.getDaoInstant().getTagMiddleDao().queryBuilder()
                .where(
                        TagMiddleDao.Properties.DivId.notEq("drag21"),
                        TagMiddleDao.Properties.DivId.notEq("drag11")
                        , TagMiddleDao.Properties.Bz2.eq(0)
                        , TagMiddleDao.Properties.LabelNo.eq(labelNo)
                ).list();
        return list;
    }

    public static List<TagMiddle> selectBarCode() {
        List<TagMiddle> list = MyApp.getDaoInstant().getTagMiddleDao().queryBuilder()
                .where(
                        TagMiddleDao.Properties.DivId.eq("drag21")
                        , TagMiddleDao.Properties.Bz2.eq(0)
                ).list();
        return list;
    }

    public static List<TagMiddle> selectBarCode(Integer labelNo) {
        List<TagMiddle> list = MyApp.getDaoInstant().getTagMiddleDao().queryBuilder()
                .where(
                        TagMiddleDao.Properties.DivId.eq("drag21")
                        , TagMiddleDao.Properties.Bz2.eq(0)
                        , TagMiddleDao.Properties.LabelNo.eq(labelNo)
                ).list();
        return list;
    }

    public static List<TagMiddle> selectMuchToLable() {
        List<TagMiddle> list = MyApp.getDaoInstant().getTagMiddleDao().queryBuilder()
                .where(
                        TagMiddleDao.Properties.DivId.notEq("drag27"),
                        TagMiddleDao.Properties.DivId.notEq("drag11")
                        , TagMiddleDao.Properties.Bz2.eq(1)
                ).list();
        return list;
    }

    public static List<TagMiddle> selectMuchToLable(Integer labelNo) {
        List<TagMiddle> list = MyApp.getDaoInstant().getTagMiddleDao().queryBuilder()
                .where(
                        TagMiddleDao.Properties.DivId.notEq("drag27"),
                        TagMiddleDao.Properties.DivId.notEq("drag11")
                        , TagMiddleDao.Properties.Bz2.eq(1)
                        , TagMiddleDao.Properties.LabelNo.eq(labelNo)
                ).list();
        return list;
    }

    public static List<TagMiddle> selectMuchBarCode() {
        List<TagMiddle> list = MyApp.getDaoInstant().getTagMiddleDao().queryBuilder()
                .where(
                        TagMiddleDao.Properties.DivId.eq("drag27")
                        , TagMiddleDao.Properties.Bz2.eq(1)
                ).list();
        return list;
    }

    public static List<TagMiddle> selectMuchBarCode(Integer labelNo) {
        List<TagMiddle> list = MyApp.getDaoInstant().getTagMiddleDao().queryBuilder()
                .where(
                        TagMiddleDao.Properties.DivId.eq("drag27")
                        , TagMiddleDao.Properties.Bz2.eq(1)
                        , TagMiddleDao.Properties.LabelNo.eq(labelNo)
                ).list();
        return list;
    }


    public static List<TagMiddle> selectTraceabilityCode() {

        List<TagMiddle> list = MyApp.getDaoInstant().getTagMiddleDao().queryBuilder()
                .where(
                        TagMiddleDao.Properties.DivId.eq("drag11")
                        , TagMiddleDao.Properties.Bz2.eq(0)
                ).list();
        return list;
    }
}
