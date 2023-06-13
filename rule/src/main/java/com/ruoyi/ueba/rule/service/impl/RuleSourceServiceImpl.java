package com.ruoyi.ueba.rule.service.impl;

import com.ruoyi.ueba.rule.entity.RuleSource;
import com.ruoyi.ueba.rule.mapper.RuleSourceMapper;
import com.ruoyi.ueba.rule.service.IRuleSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liutao
 */
@Service
public class RuleSourceServiceImpl implements IRuleSourceService {
    @Autowired
    private RuleSourceMapper ruleSourceMapper;

    @Override
    public List<RuleSource> selectRuleSourceList(RuleSource ruleSource) {
        return ruleSourceMapper.selectRuleSources(ruleSource);
    }
}
