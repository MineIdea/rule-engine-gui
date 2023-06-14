package com.ruoyi.ueba.rule.mapper;

import com.ruoyi.ueba.rule.entity.RuleSource;

import java.util.List;

/**
 * @author liutao
 */
public interface RuleSourceMapper {
    public List<RuleSource> selectRuleSources(RuleSource ruleSource);

    public int updateSource(RuleSource ruleSource);

    public int insertSource(RuleSource ruleSource);
}
