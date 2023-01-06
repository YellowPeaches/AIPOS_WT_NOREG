package com.wintec.lamp.utils.wintecLable;

import java.net.Socket;

/**
 * @description:
 * @projectName:winteclabel
 * @see:com.wintec.hundler
 * @author:赵冲
 * @createTime:2022/10/25 13:18
 * @version:1.0
 */
public interface ObjectHander extends Runnable{
    void analysis(String request);
    void returnMe();
/*    void deletePlus(String request);
    void deleteAllPlus(String request);
    void downPlus(String request);
    void responseEnd(Integer id);
    void responsePlu(Commodity commodity);*/
    //void getList(String result);
    //List<Commodity> getCommodityList(String ret) throws ParseException;

    void setSocket(Socket socket);
}
