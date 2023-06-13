package com.ruoyi.ueba.rule.service;

import com.ruoyi.ueba.rule.entity.RuleSource;

import java.util.List;

/**
 * @author liutao
 */
public interface IRuleSourceService {
    public List<RuleSource> selectRuleSourceList(RuleSource ruleSource);
}
