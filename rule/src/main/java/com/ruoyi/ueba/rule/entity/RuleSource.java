package com.ruoyi.ueba.rule.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author liutao
 */
public class RuleSource {
    @JsonProperty
    private Integer id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String format;
    @JsonProperty
    private String jobId;
    @JsonProperty
    private Date createTime;
    @JsonProperty
    private Map<String, Object> data;
    @JsonProperty
    private Integer delFlag;
    @JsonProperty
    private Boolean active;
    @JsonCreator
    public RuleSource() {}

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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
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

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleSource that = (RuleSource) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(format, that.format) && Objects.equals(createTime, that.createTime) && Objects.equals(data, that.data) && Objects.equals(delFlag, that.delFlag) && Objects.equals(active, that.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, format, createTime, data, delFlag, active);
    }
}
