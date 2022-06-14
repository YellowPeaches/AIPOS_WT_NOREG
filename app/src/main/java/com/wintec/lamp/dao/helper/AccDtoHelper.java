package com.wintec.lamp.dao.helper;

import android.database.Cursor;

import com.wintec.ThreadCacheManager;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.base.MyApp;
import com.wintec.lamp.dao.AccDtoDao;
import com.wintec.lamp.dao.CommdityDao;
import com.wintec.lamp.dao.PluDtoDao;
import com.wintec.lamp.dao.entity.AccDto;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.utils.DBUtil;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AccDtoHelper {

    public static void insert(AccDto accDto) {
        MyApp.getDaoInstant().getAccDtoDao().insertOrReplace(accDto);
    }

    public static void deleteAll() {
        MyApp.getDaoInstant().getAccDtoDao().deleteAll();
    }

    public static void deleteByAccNo(int accNo) {
        AccDto accDto = selectByAccNoLocal(accNo);
        if (accDto != null) {
            MyApp.getDaoInstant().getAccDtoDao().delete(accDto);
        }
    }

    private static AccDto selectByAccNoLocal(int accNo) {
        return MyApp.getDaoInstant().getAccDtoDao().queryBuilder().where(AccDtoDao.Properties.AccNo.eq(accNo)).unique();
    }

    public static AccDto selectByAccNo(int accNo) {
//        if ("在线取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE))) {
//            try {
//                Future<AccDto> submit = ThreadCacheManager.getExecutorService().submit(new Callable<AccDto>() {
//                    @Override
//                    public AccDto call() throws Exception {
//                        List query = DBUtil.Query("SELECT * FROM dbo.v_sk_extratext where et_no = " + accNo);
//                        if (query != null || query.size() == 2) {
//                            List<HashMap<String, Object>> data = (List<HashMap<String, Object>>) query.get(1);
//                            if (data.size() == 1) {
//                                return new AccDto(data.get(0));
//                            }
//                        }
//                        return null;
//                    }
//                });
//                return submit.get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            return null;
//        } else {
        return MyApp.getDaoInstant().getAccDtoDao().queryBuilder().where(AccDtoDao.Properties.AccNo.eq(accNo)).unique();
//        }
    }

    public static void uptate(AccDto accDto) {
        MyApp.getDaoInstant().getAccDtoDao().update(accDto);
    }
}
