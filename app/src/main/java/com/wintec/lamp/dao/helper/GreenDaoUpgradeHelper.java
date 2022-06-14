package com.wintec.lamp.dao.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wintec.lamp.dao.CommdityDao;
import com.wintec.lamp.dao.DaoMaster;
import com.wintec.lamp.dao.PluDtoDao;
import com.wintec.lamp.dao.TagMiddleDao;
import com.wintec.lamp.dao.TraceabilityCodeDao;
import com.wintec.lamp.dao.entity.TagMiddle;

import org.greenrobot.greendao.database.Database;

/**
 * @author 赵冲
 * @description:
 * @date :2021/7/7 10:27
 */
public class GreenDaoUpgradeHelper extends DaoMaster.OpenHelper {
    public GreenDaoUpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    //这里重写onUpgrade方法
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
//        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
//            @Override
//            public void onCreateAllTables(Database db, boolean ifNotExists) {
//                DaoMaster.createAllTables(db, true);
//            }
//
//            @Override
//            public void onDropAllTables(Database db, boolean ifExists) {
//               // DaoMaster.dropAllTables(db, true);
//            }
//        }, TagMiddleDao.class);
        int currentVersion = 0;
        if (oldVersion == 2) {
            TagMiddleDao.createTable(db, true);
            currentVersion = newVersion;
        }
        if (oldVersion == 3) {
            MigrationHelper.getInstance().migrate(db, TagMiddleDao.class, CommdityDao.class, PluDtoDao.class);
            currentVersion = newVersion;
        }
        if (oldVersion == 4) {
            TraceabilityCodeDao.createTable(db, true);
            currentVersion = newVersion;
        }
        if (oldVersion == 5) {
            TraceabilityCodeDao.createTable(db, true);
            currentVersion = newVersion;
        }
        if (oldVersion == 6) {
            MigrationHelper.getInstance().migrate(db, PluDtoDao.class);
            currentVersion = newVersion;
        }
        if (oldVersion == 7) {
            MigrationHelper.getInstance().migrate(db, TagMiddleDao.class);
            currentVersion = newVersion;
        }
        if (currentVersion != newVersion) {
            super.onUpgrade(db, oldVersion, newVersion);
        }

    }
}

