package com.ruoyi.ueba.rule.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author liutao
 */
public class Model {
    @JsonProperty
    private Integer id;
    @JsonProperty
    private String name;
    @JsonProperty
    private Date createTime;
    @JsonProperty
    private Map<String, Object> data;
    @JsonProperty
    private Integer delFlag;
    @JsonProperty
    private Boolean active;
    @JsonCreator
    public Model() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return Objects.equals(id, model.id) && Objects.equals(name, model.name) && Objects.equals(createTime, model.createTime) && Objects.equals(data, model.data) && Objects.equals(delFlag, model.delFlag) && Objects.equals(active, model.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createTime, data, delFlag, active);
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                ", data=" + data +
                ", delFlag=" + delFlag +
                ", active=" + active +
                '}';
    }
}
