package com.wintec.lamp.utils.wintecLable;

import java.util.List;


public class Handler extends ScaleApiHandler {

    @Override
    public void CommodityList(List<Commodity> list) {
        for (Commodity commodity:list
        ) {
            System.out.println(commodity.toString());
        }
    }

    @Override
    public void returnMe() {

    }
}
