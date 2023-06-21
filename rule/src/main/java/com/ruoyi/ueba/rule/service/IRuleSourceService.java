package com.ruoyi.ueba.rule.service;

import com.ruoyi.ueba.rule.entity.RuleSource;

import java.util.List;

/**
 * @author liutao
 */
public interface IRuleSourceService {
    public List<RuleSource> selectRuleSourceList(RuleSource ruleSource);

    public boolean changeStatus(RuleSource ruleSource);

    public boolean addSource(RuleSource ruleSource);

    public boolean updateSource(RuleSource ruleSource);

    public boolean delSource(Integer[] sourceIds);
}
