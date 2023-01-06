package com.wintec.lamp.dao.helper;

import com.wintec.lamp.base.MyApp;
import com.wintec.lamp.dao.TraceabilityCodeDao;
import com.wintec.lamp.dao.entity.TraceabilityCode;

public class TraceabilityCodeHelper {

    public static String selectByCode(String pluNo, String itemNo) {
        TraceabilityCode traceabilityCode = MyApp.getDaoInstant().getTraceabilityCodeDao().queryBuilder()
                .where(
                        TraceabilityCodeDao.Properties.PluNo.eq(pluNo),
                        TraceabilityCodeDao.Properties.ItemNo.eq(itemNo)
                ).unique();

        if (traceabilityCode != null && traceabilityCode.getTraceabilityCode() != null) {
            return traceabilityCode.getTraceabilityCode();
        } else {
            return "当前追溯不可用";
        }
    }

    public static TraceabilityCode selectByPLU(String pluNo) {
        if (pluNo == null) {
            return null;
        }
        return MyApp.getDaoInstant().getTraceabilityCodeDao().queryBuilder()
                .where(TraceabilityCodeDao.Properties.PluNo.eq(pluNo))
        .unique();
    }
    public static void insert(TraceabilityCode traceabilityCode) {
        TraceabilityCode temp = MyApp.getDaoInstant().getTraceabilityCodeDao().queryBuilder().where(
                TraceabilityCodeDao.Properties.MrySymbol.eq(traceabilityCode.getMrySymbol())
        ).unique();
        if (temp == null) {
            MyApp.getDaoInstant().getTraceabilityCodeDao().insert(traceabilityCode);
        } else {
            traceabilityCode.set_id(temp.get_id());
            MyApp.getDaoInstant().getTraceabilityCodeDao().update(traceabilityCode);
        }
    }

    public static TraceabilityCode selectByCodeOrPlu(String pluNo, String itemNo) {
        TraceabilityCode traceabilityCode = MyApp.getDaoInstant().getTraceabilityCodeDao().queryBuilder()
                .where(
                        TraceabilityCodeDao.Properties.PluNo.eq(pluNo),
                        TraceabilityCodeDao.Properties.ItemNo.eq(itemNo)
                ).unique();

        return traceabilityCode;
    }

    public static void update(TraceabilityCode traceabilityCode) {
        MyApp.getDaoInstant().getTraceabilityCodeDao().update(traceabilityCode);
    }
}
