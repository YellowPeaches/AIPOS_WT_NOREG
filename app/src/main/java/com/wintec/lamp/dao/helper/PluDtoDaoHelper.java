package com.wintec.lamp.dao.helper;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.room.util.StringUtil;

import com.google.zxing.common.StringUtils;
import com.wintec.ThreadCacheManager;
import com.wintec.lamp.base.Const;
import com.wintec.lamp.base.MyApp;
import com.wintec.lamp.dao.AccDtoDao;
import com.wintec.lamp.dao.PluDtoDao;
import com.wintec.lamp.dao.entity.PluDto;
import com.wintec.lamp.utils.CommUtils;
import com.wintec.lamp.utils.DBUtil;
import com.wintec.lamp.utils.NetWorkUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class PluDtoDaoHelper {

    private Context getContext() {
        return getContext();
    }

    public static PluDto getCommdity(Long downloadId) {
        return MyApp.getDaoInstant().getPluDtoDao().load(downloadId);
//        return MyApp.getDaoInstant().getCommdityDao().queryBuilder().where(TbAppUpdateDao.Properties.DownloadId.eq(downloadId)).unique();
    }

    public static PluDto getCommdityByItemCode(String downloadId) {
        return MyApp.getDaoInstant().getPluDtoDao().queryBuilder().where(
                PluDtoDao.Properties.PluNo.eq(downloadId)
        ).unique();
    }

    //用于在线取值
    public static PluDto getCommdityByScalesCodeOnline(String scalesCode) {
        if (scalesCode == null) {
            return null;
        }
        if ("在线取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE))) {
            DBUtil dbUtil = new DBUtil();
            try {
                if ("oracle".equals(Const.getSettingValue(Const.KEY_GET_DATA_DB))) {
                    Future<PluDto> submit = ThreadCacheManager.getExecutorService().submit(new Callable<PluDto>() {
                        @Override
                        public PluDto call() throws Exception {
                            String inputQuerySQL = Const.getSettingValue(Const.KEY_GET_DATA_SQL);
                            String queryOneByPLU;
                            if (inputQuerySQL.contains("where") || inputQuerySQL.contains("WHERE")) {
                                queryOneByPLU = inputQuerySQL + " AND PLU = " + scalesCode;
                            } else {
                                queryOneByPLU = inputQuerySQL + " WHERE PLU = " + scalesCode;
                            }
                            long beginTime = System.currentTimeMillis();
                            List query = DBUtil.Query(queryOneByPLU);
                            long endTime = System.currentTimeMillis();
                            dbUtil.logWriteData("完成根据plu查询  花费 "+(endTime-beginTime)+" ms  "+queryOneByPLU);
                            if (query != null || query.size() == 2) {
                                List<HashMap<String, Object>> data = (List<HashMap<String, Object>>) query.get(1);
                                if (data.size() == 1) {
                                    return new PluDto(data.get(0));
                                }
                            }
                            return null;
                        }
                    });
                    return submit.get();
                } else {
                    Future<PluDto> submit = ThreadCacheManager.getExecutorService().submit(new Callable<PluDto>() {
                        @Override
                        public PluDto call() throws Exception {
                            List query = DBUtil.Query("SELECT * FROM dbo.v_sk_item where plu_no = " + scalesCode);
                            if (query != null || query.size() == 2) {
                                List<HashMap<String, Object>> data = (List<HashMap<String, Object>>) query.get(1);
                                if (data.size() == 1) {
                                    return new PluDto(data.get(0));
                                }
                            }
                            return null;
                        }
                    });
                    return submit.get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return MyApp.getDaoInstant().getPluDtoDao().queryBuilder().where(
                        PluDtoDao.Properties.PluNo.eq(scalesCode)
                ).unique();
            } catch (ExecutionException e) {
                e.printStackTrace();
                return MyApp.getDaoInstant().getPluDtoDao().queryBuilder().where(
                        PluDtoDao.Properties.PluNo.eq(scalesCode)
                ).unique();
            }
        } else {
            return MyApp.getDaoInstant().getPluDtoDao().queryBuilder().where(
                    PluDtoDao.Properties.PluNo.eq(scalesCode)
            ).unique();
        }
    }

    public static PluDto getCommdityByScalesCode(String scalesCode) {
        if (scalesCode == null) {
            return null;
        }
        return MyApp.getDaoInstant().getPluDtoDao().queryBuilder().where(
                PluDtoDao.Properties.PluNo.eq(scalesCode)
        ).unique();

    }

    public static PluDto getCommdityByScalesCodeLocal(String scalesCode) {
        if (scalesCode == null) {
            return null;
        }
        return MyApp.getDaoInstant().getPluDtoDao().queryBuilder().where(
                PluDtoDao.Properties.PluNo.eq(scalesCode)
        ).unique();
    }

    public static PluDto getCommdityByScalesCodeWithOutPrice(int scalesCode) {
        return MyApp.getDaoInstant().getPluDtoDao().queryBuilder().where(
                PluDtoDao.Properties.PluNo.eq(scalesCode)
        ).unique();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<PluDto> getCommdityBySearchKey(String key, int limit, int offset) {

        List<PluDto> list = MyApp.getDaoInstant().getPluDtoDao().queryBuilder()
                .where(
                        PluDtoDao.Properties.Initials.like("%" + key + "%")
                )
                .orderRaw("length (initials)")
                .offset(offset * limit)
                .limit(limit)
                .list();
//        if ("在线取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE))) {
//            if (list != null && list.size() > 0) {
//                List<String> collect = list.stream().map(PluDto::getPluNo).collect(Collectors.toList());
//                String join = String.join(",", collect);
//                try {
//                    Future<List<PluDto>> submit = ThreadCacheManager.getExecutorService().submit(new Callable<List<PluDto>>() {
//                        @Override
//                        public List<PluDto> call() throws Exception {
//                            List<PluDto> pluDtos = new ArrayList<>();
//                            List query = DBUtil.Query("SELECT * FROM dbo.v_sk_item where plu_no in( " + join + ")");
//                            if (query != null || query.size() == 2) {
//                                List<HashMap<String, Object>> data = (List<HashMap<String, Object>>) query.get(1);
//                                data.forEach(item -> {
//                                    pluDtos.add(new PluDto(item));
//                                });
//                            }
//                            return pluDtos;
//                        }
//                    });
//                    return submit.get();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//                return new ArrayList<>();
//            } else {
//                return new ArrayList<>();
//            }
//        } else {
        return list;
//        }

    }

    public static List<PluDto> getCommdityByItemCode() {
        return MyApp.getDaoInstant().getPluDtoDao().loadAll();
    }


    /**
     * 新增
     */
    public static long insertCommdity(PluDto pluDto) {
        return MyApp.getDaoInstant().getPluDtoDao().insertOrReplace(pluDto);
    }


    public static List<PluDto> queryRecommondCommdityByClick() {
        return MyApp.getDaoInstant().getPluDtoDao().queryBuilder()
                .where(
                        PluDtoDao.Properties.UnitPriceA.notEq(0),
                        PluDtoDao.Properties.Click.notEq(0)
                ).orderDesc(
                        PluDtoDao.Properties.Click
                ).limit(5).list();
    }

    public static void deleteAll() {
        MyApp.getDaoInstant().getPluDtoDao().deleteAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<PluDto> getCommdityByScalesCode2(String scalesCode, int limit, int offset) {
        List<PluDto> list = MyApp.getDaoInstant().getPluDtoDao().queryBuilder()
                .where(PluDtoDao.Properties.PluNo.like("%" + scalesCode + "%"))
                .orderRaw("length (plu_no)")
                .offset(offset * limit)
                .limit(limit)
                .list();

//        if ("在线取数".equals(Const.getSettingValue(Const.KEY_GET_DATA_MODE))) {
//            if (list != null && list.size() > 0) {
//                List<String> collect = list.stream().map(PluDto::getPluNo).collect(Collectors.toList());
//                String join = String.join(",", collect);
//
//                try {
//                    Future<List<PluDto>> submit = ThreadCacheManager.getExecutorService().submit(new Callable<List<PluDto>>() {
//                        @Override
//                        public List<PluDto> call() throws Exception {
//                            List<PluDto> pluDtos = new ArrayList<>();
//                            List query = DBUtil.Query("SELECT * FROM dbo.v_sk_item where plu_no in( " + join + ")");
//                            if (query != null || query.size() == 2) {
//                                List<HashMap<String, Object>> data = (List<HashMap<String, Object>>) query.get(1);
//                                data.forEach(item -> {
//                                    pluDtos.add(new PluDto(item));
//                                });
//                            }
//                            return pluDtos;
//                        }
//                    });
//                    return submit.get();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//                return new ArrayList<>();
//            } else {
//                return new ArrayList<>();
//            }
//        } else {
        return list;
//        }
    }

    /**
     * 查询包含当前打秤码的总条数
     *
     * @param scalesCode
     * @return
     */
    public static int getCommdityTotalByScalesCode2(String scalesCode) {
        return (int) MyApp.getDaoInstant().getPluDtoDao().queryBuilder()
                .where(PluDtoDao.Properties.PluNo.like("%" + scalesCode + "%"))
                .count();
    }

    public static int getCommdityTotalByInitials(String key) {
        return (int) MyApp.getDaoInstant().getPluDtoDao().queryBuilder()
                .where(PluDtoDao.Properties.Initials.like("%" + key + "%"))
                .count();
    }

    /**
     * 更新
     *
     * @param commdity
     */
    public static void updateCommdity(PluDto commdity) {
        MyApp.getDaoInstant().getPluDtoDao().update(commdity);
    }
}
