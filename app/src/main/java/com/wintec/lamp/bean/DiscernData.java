package com.wintec.lamp.bean;

import java.io.Serializable;
import java.util.Date;

public class DiscernData implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column discern_data.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column discern_data.posSn
     *
     * @mbg.generated
     */
    private String possn;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column discern_data.branch_code
     *
     * @mbg.generated
     */
    private String branchCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column discern_data.tenant_code
     *
     * @mbg.generated
     */
    private String tenantCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column discern_data.version
     *
     * @mbg.generated
     */
    private Integer version;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column discern_data.is_delete
     *
     * @mbg.generated
     */
    private Integer isDelete;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column discern_data.data_url
     *
     * @mbg.generated
     */
    private String dataUrl;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column discern_data.create_date
     *
     * @mbg.generated
     */
    private Date createDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column discern_data.delete_date
     *
     * @mbg.generated
     */
    private Date deleteDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column discern_data.reserved1
     *
     * @mbg.generated
     */
    private String reserved1;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column discern_data.reserved2
     *
     * @mbg.generated
     */
    private String reserved2;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table discern_data
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column discern_data.id
     *
     * @return the value of discern_data.id
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column discern_data.id
     *
     * @param id the value for discern_data.id
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column discern_data.posSn
     *
     * @return the value of discern_data.posSn
     * @mbg.generated
     */
    public String getPossn() {
        return possn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column discern_data.posSn
     *
     * @param possn the value for discern_data.posSn
     * @mbg.generated
     */
    public void setPossn(String possn) {
        this.possn = possn == null ? null : possn.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column discern_data.branch_code
     *
     * @return the value of discern_data.branch_code
     * @mbg.generated
     */
    public String getBranchCode() {
        return branchCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column discern_data.branch_code
     *
     * @param branchCode the value for discern_data.branch_code
     * @mbg.generated
     */
    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode == null ? null : branchCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column discern_data.tenant_code
     *
     * @return the value of discern_data.tenant_code
     * @mbg.generated
     */
    public String getTenantCode() {
        return tenantCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column discern_data.tenant_code
     *
     * @param tenantCode the value for discern_data.tenant_code
     * @mbg.generated
     */
    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode == null ? null : tenantCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column discern_data.version
     *
     * @return the value of discern_data.version
     * @mbg.generated
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column discern_data.version
     *
     * @param version the value for discern_data.version
     * @mbg.generated
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column discern_data.is_delete
     *
     * @return the value of discern_data.is_delete
     * @mbg.generated
     */
    public Integer getIsDelete() {
        return isDelete;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column discern_data.is_delete
     *
     * @param isDelete the value for discern_data.is_delete
     * @mbg.generated
     */
    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column discern_data.data_url
     *
     * @return the value of discern_data.data_url
     * @mbg.generated
     */
    public String getDataUrl() {
        return dataUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column discern_data.data_url
     *
     * @param dataUrl the value for discern_data.data_url
     * @mbg.generated
     */
    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl == null ? null : dataUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column discern_data.create_date
     *
     * @return the value of discern_data.create_date
     * @mbg.generated
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column discern_data.create_date
     *
     * @param createDate the value for discern_data.create_date
     * @mbg.generated
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column discern_data.delete_date
     *
     * @return the value of discern_data.delete_date
     * @mbg.generated
     */
    public Date getDeleteDate() {
        return deleteDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column discern_data.delete_date
     *
     * @param deleteDate the value for discern_data.delete_date
     * @mbg.generated
     */
    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column discern_data.reserved1
     *
     * @return the value of discern_data.reserved1
     * @mbg.generated
     */
    public String getReserved1() {
        return reserved1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column discern_data.reserved1
     *
     * @param reserved1 the value for discern_data.reserved1
     * @mbg.generated
     */
    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1 == null ? null : reserved1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column discern_data.reserved2
     *
     * @return the value of discern_data.reserved2
     * @mbg.generated
     */
    public String getReserved2() {
        return reserved2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column discern_data.reserved2
     *
     * @param reserved2 the value for discern_data.reserved2
     * @mbg.generated
     */
    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2 == null ? null : reserved2.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table discern_data
     *
     * @mbg.generated
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        DiscernData other = (DiscernData) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getPossn() == null ? other.getPossn() == null : this.getPossn().equals(other.getPossn()))
                && (this.getBranchCode() == null ? other.getBranchCode() == null : this.getBranchCode().equals(other.getBranchCode()))
                && (this.getTenantCode() == null ? other.getTenantCode() == null : this.getTenantCode().equals(other.getTenantCode()))
                && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
                && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()))
                && (this.getDataUrl() == null ? other.getDataUrl() == null : this.getDataUrl().equals(other.getDataUrl()))
                && (this.getCreateDate() == null ? other.getCreateDate() == null : this.getCreateDate().equals(other.getCreateDate()))
                && (this.getDeleteDate() == null ? other.getDeleteDate() == null : this.getDeleteDate().equals(other.getDeleteDate()))
                && (this.getReserved1() == null ? other.getReserved1() == null : this.getReserved1().equals(other.getReserved1()))
                && (this.getReserved2() == null ? other.getReserved2() == null : this.getReserved2().equals(other.getReserved2()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table discern_data
     *
     * @mbg.generated
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPossn() == null) ? 0 : getPossn().hashCode());
        result = prime * result + ((getBranchCode() == null) ? 0 : getBranchCode().hashCode());
        result = prime * result + ((getTenantCode() == null) ? 0 : getTenantCode().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        result = prime * result + ((getDataUrl() == null) ? 0 : getDataUrl().hashCode());
        result = prime * result + ((getCreateDate() == null) ? 0 : getCreateDate().hashCode());
        result = prime * result + ((getDeleteDate() == null) ? 0 : getDeleteDate().hashCode());
        result = prime * result + ((getReserved1() == null) ? 0 : getReserved1().hashCode());
        result = prime * result + ((getReserved2() == null) ? 0 : getReserved2().hashCode());
        return result;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table discern_data
     *
     * @mbg.generated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", possn=").append(possn);
        sb.append(", branchCode=").append(branchCode);
        sb.append(", tenantCode=").append(tenantCode);
        sb.append(", version=").append(version);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", dataUrl=").append(dataUrl);
        sb.append(", createDate=").append(createDate);
        sb.append(", deleteDate=").append(deleteDate);
        sb.append(", reserved1=").append(reserved1);
        sb.append(", reserved2=").append(reserved2);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}