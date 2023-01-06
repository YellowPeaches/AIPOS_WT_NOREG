package com.wintec.lamp.utils.wintecLable;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;


public abstract class ScaleApiHandler implements ObjectHander {

    private Socket socket;
    private OutputStream os = null;
    private PrintWriter pw = null;

    /**
     * DL_LBW 1
     */
    private final static String DL_LBW = "44 4c 5f 4c 42 57";
    /**
     * [label
     */
    private final static String DOWN_LABEL1 = "5b 6c 61 62 65 6c";
    /**
     * DL_PLU 2
     */
    private final static String DL_PLU = "44 4c 5f 50 4c 55";
    /**
     * DL_SLD 3
     */
    private final static String DL_SLD = "44 4c 5f 53 4c 44";
    /**
     * DL_BCCFG 4
     */
    private final static String DL_BCCFG = "44 4c 5f 42 43 43";
    /**
     * DL_SITE 5
     */
    private final static String DL_SITE = "44 4c 5f 53 49 54";
    /**
     * DL_DEPT 6
     */
    private final static String DL_DEPT = "44 4c 5f 44 45 50";
    /**
     * DL_CATEGORY 7
     */
    private final static String DL_CATEGORY = "44 4c 5f 43 41 54";
    /**
     * DL_UNIT 8
     */
    private final static String DL_UNIT = "44 4c 5f 55 4e 49";
    /**
     * DL_OPERATION 9
     */
    private final static String DL_OPERATION = "44 4c 5f 4f 50 45";
    /**
     * DL_IMGSALE 10
     */
    private final static String DL_IMGSALE = "44 4c 5f 49 4d 47";
    /**
     * DL_IMGLOOP 11
     */
    private final static String DL_IMGLOOP = "44 4c 5f 49 4d 47";
    /**
     * DL_IMGGUEST 12
     */
    private final static String DL_IMGGUEST = "44 4c 5f 49 4d 47";
    /**
     * CONN_NAK
     */
    private final static String CONN_NAK = "43 4f 4e 4e 5f 4e";
    /**
     * CONN_ACK
     */
    private final static String CONN_ACK = "43 4f 4e 4e 5f 41";
    /**
     * ERROR_NAK
     */
    private final static String ERROR_NAK = "45 52 52 4f 52 5f";
    /**
     * UPL_LAB 20
     */
    private final static String UPL_LAB = "55 50 4c 5f 4c 41";
    /**
     * UPL_USRLOG 21
     */
    private final static String UPL_USRLOG = "55 50 4c 5f 55 53";
    /**
     * UPL_LBLDETAIL 22
     */
    private final static String UPL_LBLDETAIL = "55 50 4c 5f 4c 42";
    /**
     * 命令类型
     */
    private static int CMD_TYPE = 0;
    /**
     * 数据长度
     */
    private static int DATA_LEN = 0;
    /**
     * 文件名称
     */
    private static String FILE_NAME;

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            byte[] bytes = new byte[1];
            String request = "";
            while (dis.read(bytes) != -1) {
                request += Hex2BytesUtils.bytesToHexString(bytes) + " ";
                if (dis.available() == 0) {
                    //System.out.println(request);
                    analysis(request);
                    request = "";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void analysis(String request) {
//        System.out.println("接收数据为：" + request);
        if (request.contains(DL_LBW)) {
            //下载标签
            CMD_TYPE = 1;
            result_ACK();
            return;
        } else if (request.contains(DL_PLU)) {
            CMD_TYPE = 2;
            result_ACK();
            return;
        } else if (request.contains(CONN_ACK)) {
            result_ACK();
            return;
        } else if (request.contains(CONN_NAK)) {
            return;
        }

        switch (CMD_TYPE) {
            //下载标签
            case 1:
                String[] s = request.split(" ");
                String sb;
                sb = s[0];
                for (int i = 1; i < s.length; i++) {
                    sb = sb + s[i];
                }
//                System.out.println("接收数据为0："+(sb));
                System.out.println("接收数据为1："+hexToStringUni(sb));
                TransToLocal.processBarcodes(hexToStringUni(sb));
                CMD_TYPE = 0;
                result_ACK();
                break;
            case 2:
                String[] splu = request.split(" ");
                String sbplu;
                sbplu = splu[0];
                for (int i = 1; i < splu.length; i++) {
                    sbplu = sbplu + splu[i];
                }
//                System.out.println("接收数据为：" + hexToStringUni(sbplu));
                //CMD_TYPE = 0;
                result_ACK();
                break;
            case 3:
                break;
            default:
                break;
        }


    }

    public void result_ACK() {
        OutputStream socketWriter = null;
        try {
            socketWriter = socket.getOutputStream();
            byte[] results = {0x41, 0x43, 0x4B};
            socketWriter.write(results);
            socketWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String hexToStringUni(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        try {
            s = new String(baKeyword, "UTF8");
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
        return s;
    }

    public void result_NAK() {
        OutputStream socketWriter = null;
        try {
            socketWriter = socket.getOutputStream();
            byte[] results = {0x4E, 0x41, 0x4B};
            socketWriter.write(results);
            socketWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public abstract void CommodityList(List<Commodity> commodityList);

}

/*
        switch (request.substring(0, 17)) {
                case DOWN_LABEL0: //todo  解析数据
                result_ACK();
                break;
                case DOWN_LABEL1:
                String[] s = request.split(" ");
                String sb;
                sb = s[0];
                for(int i=1;i<s.length;i++){
        sb = sb + s[i];
        }
        System.out.println("接收数据为："+hexToStringUni(sb));
        result_ACK();
        break;
default:
        break;
        }*/
