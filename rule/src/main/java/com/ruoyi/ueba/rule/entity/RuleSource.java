package com.ruoyi.ueba.rule.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

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
    private Date createTime;
    @JsonProperty
    private Map<String, Object> data;
    @JsonProperty
    private int delFlag;
    @JsonProperty
    boolean active;
    @JsonCreator
    public RuleSource() {}

}
