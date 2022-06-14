package com.wintec.lamp.bean;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 * @since 2022-05-07 13:46:05
 */
public class MapDepot {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
//    private Integer id;

    /**
     * 图片url
     */
//    private String url;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品plu
     */
    private String productCode;

    /**
     * 租户号
     */
    private String organ;

    /**
     * 设备SN
     */
    private String sn;

    /**
     * 门店编码
     */
    private String branchCode;

//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

//    public LocalDateTime getCreateData() {
//        return createData;
//    }
//
//    public void setCreateData(LocalDateTime createData) {
//        this.createData = createData;
//    }

    /**
     * 上传时间
     */
//    private LocalDateTime createData;

    /**
     * 是否匹配
     */
    private Integer isDelete;

}
