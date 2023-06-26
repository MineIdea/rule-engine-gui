package com.ruoyi.ueba.rule.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author liutao
 */
public class Alert extends BaseEntity {
    @JsonProperty
    private Integer id;
    @JsonProperty
    private String uuId;
    @JsonProperty
    private String name;
    @JsonProperty
    private String desc;
    @JsonProperty
    private Date createTime;
    @JsonProperty
    private Map<String, Object> data;

    public Alert() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alert alert = (Alert) o;
        return Objects.equals(id, alert.id) && Objects.equals(uuId, alert.uuId) && Objects.equals(name, alert.name) && Objects.equals(desc, alert.desc) && Objects.equals(createTime, alert.createTime) && Objects.equals(data, alert.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuId, name, desc, createTime, data);
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", uuId='" + uuId + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", createTime=" + createTime +
                ", data=" + data +
                '}';
    }
}
