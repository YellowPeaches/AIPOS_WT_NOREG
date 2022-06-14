package com.wintec.lamp.dao.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wintec.lamp.base.MyApp;
import com.wintec.lamp.dao.CommdityDao;
import com.wintec.lamp.dao.entity.Commdity;
import com.wintec.lamp.utils.CommUtils;
import com.wintec.lamp.utils.PriceUtils;

import java.util.ArrayList;
import java.util.List;

public class CommdityHelper {
    public static Commdity getCommdity(Long downloadId) {
        return MyApp.getDaoInstant().getCommdityDao().load(downloadId);
//        return MyApp.getDaoInstant().getCommdityDao().queryBuilder().where(TbAppUpdateDao.Properties.DownloadId.eq(downloadId)).unique();
    }

    public static Commdity getCommdityByItemCode(String downloadId) {
        return MyApp.getDaoInstant().getCommdityDao().queryBuilder().where(
                CommdityDao.Properties.ItemCode.eq(downloadId)
        ).unique();
    }

    public static Commdity getCommdityByScalesCode(String scalesCode) {
        if (scalesCode == null) {
            return null;
        }
        scalesCode = CommUtils.toOldPLU(scalesCode);
        return MyApp.getDaoInstant().getCommdityDao().queryBuilder().where(
                CommdityDao.Properties.Id.eq(scalesCode)
        ).unique();
    }

    public static Commdity getCommdityByScalesCodeWithOutPrice(int scalesCode) {
        return MyApp.getDaoInstant().getCommdityDao().queryBuilder().where(
                CommdityDao.Properties.Id.eq(scalesCode)
        ).unique();
    }

    public static List<Commdity> getCommdityBySearchKey(String key) {
        List<Commdity> list = new ArrayList<>();
        CommdityDao commdityDao = MyApp.getDaoInstant().getCommdityDao();
        long fromId = -1;
        String strSql = "select * from Commdity  where initials like" + "'%" + key + "%'" + " order by length (initials) limit 0,5 ";
        Cursor cursor = commdityDao.getDatabase().rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            Commdity commdity = new Commdity();
            commdity.set_id(cursor.getLong(0));
            commdity.setId(cursor.getString(1));
            commdity.setItemCode(cursor.getString(2));
            commdity.setName(cursor.getString(3));
            commdity.setPrice(cursor.getFloat(4));
            commdity.setUnitId(cursor.getInt(5));
            commdity.setClassifyId(cursor.getInt(6));
            commdity.setClassifyName(cursor.getString(7));
            commdity.setPreviewImage(cursor.getString(8));
            commdity.setSearchKey(cursor.getString(9));
            commdity.setParentClassifyId(cursor.getInt(10));
            commdity.setParentClassifyName(cursor.getString(11));
            commdity.setUnitPrint(cursor.getString(12));
            commdity.setClick(cursor.getInt(13));
            commdity.setInitials(cursor.getString(14));
            commdity.setCreateTime(cursor.getString(15));
            commdity.setNetFlag(cursor.getInt(16));
            commdity.setBranchId(cursor.getInt(17));
            list.add(commdity);
        }
        return list;
    }

    public static List<Commdity> getCommdityByItemCode() {
        return MyApp.getDaoInstant().getCommdityDao().loadAll();
    }

    public static List<Commdity> getCommdityListByClassName(String classifyName) {
        return MyApp.getDaoInstant().getCommdityDao().queryBuilder()
                .where(
                        CommdityDao.Properties.ClassifyName.eq(classifyName),
                        CommdityDao.Properties.Price.notEq(0)
                ).orderDesc(
                        CommdityDao.Properties.Click
                ).limit(3).list();
    }

    public static List<Commdity> getCommdityListByNetFlag(int flag) {
        return MyApp.getDaoInstant().getCommdityDao().queryBuilder()
                .where(
                        CommdityDao.Properties.NetFlag.eq(flag)
                ).orderDesc(
                        CommdityDao.Properties.Click
                ).list();
    }

    /**
     * 新增
     */
    public static long insertCommdity(Commdity commdity) {
        return MyApp.getDaoInstant().getCommdityDao().insertOrReplace(commdity);
    }

    public static void insertCommdityList(List<Commdity> commditys) {
        MyApp.getDaoInstant().getCommdityDao().insertOrReplaceInTx(commditys);
    }

    /**
     * 更新
     *
     * @param commdity
     */
    public static void updateCommdity(Commdity commdity) {
        MyApp.getDaoInstant().getCommdityDao().update(commdity);
    }

    /**
     * 根据ID删除
     */
    public static void deleteCommdityByKey(Long id) {
        MyApp.getDaoInstant().getCommdityDao().deleteByKey(id);
    }

    /**
     * 根据识别率排序
     *
     * @return
     */
    public static List<Commdity> queryRecommondCommdity() {
        return MyApp.getDaoInstant().getCommdityDao().queryBuilder()
                .where(
                        MyApp.getDaoInstant().getCommdityDao().queryBuilder()
                                .or(
                                        CommdityDao.Properties.ErrorClick.notEq(0),
                                        CommdityDao.Properties.Top1Click.notEq(0),
                                        CommdityDao.Properties.Top2T5Click.notEq(0)
                                )
                ).orderAsc(
                        CommdityDao.Properties.Accuracy
                ).limit(5).list();
    }

    public static List<Commdity> queryRecommondCommdityByClick() {
        return MyApp.getDaoInstant().getCommdityDao().queryBuilder()
                .where(
                        CommdityDao.Properties.Price.notEq(0),
                        CommdityDao.Properties.Click.notEq(0)
                ).orderDesc(
                        CommdityDao.Properties.Click
                ).limit(5).list();
    }

    public static void deleteAll() {
        MyApp.getDaoInstant().getCommdityDao().deleteAll();
    }

    public static List<Commdity> getCommdityByScalesCode2(String scalesCode) {
        List<Commdity> list = new ArrayList<>();
        CommdityDao commdityDao = MyApp.getDaoInstant().getCommdityDao();
        long fromId = -1;
        String strSql = "select * from Commdity  where id like" + "'%" + scalesCode + "%'" + " order by length (id) limit 0,5 ";
        Cursor cursor = commdityDao.getDatabase().rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            Commdity commdity = new Commdity();
            commdity.set_id(cursor.getLong(0));
            commdity.setId(cursor.getString(1));
            commdity.setItemCode(cursor.getString(2));
            commdity.setName(cursor.getString(3));
            commdity.setPrice(cursor.getFloat(4));
            commdity.setUnitId(cursor.getInt(5));
            commdity.setClassifyId(cursor.getInt(6));
            commdity.setClassifyName(cursor.getString(7));
            commdity.setPreviewImage(cursor.getString(8));
            commdity.setSearchKey(cursor.getString(9));
            commdity.setParentClassifyId(cursor.getInt(10));
            commdity.setParentClassifyName(cursor.getString(11));
            commdity.setUnitPrint(cursor.getString(12));
            commdity.setClick(cursor.getInt(13));
            commdity.setInitials(cursor.getString(14));
            commdity.setCreateTime(cursor.getString(15));
            commdity.setNetFlag(cursor.getInt(16));
            commdity.setBranchId(cursor.getInt(17));
            list.add(commdity);
        }
        return list;
    }
}
