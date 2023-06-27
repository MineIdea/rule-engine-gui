package com.ruoyi.ueba.rule.service.impl;

import com.ruoyi.common.utils.Utils;
import com.ruoyi.ueba.rule.entity.RuleSource;
import com.ruoyi.ueba.rule.mapper.RuleSourceMapper;
import com.ruoyi.ueba.rule.service.IRuleSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @author liutao
 */
@Service
public class RuleSourceServiceImpl implements IRuleSourceService {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private RuleSourceMapper ruleSourceMapper;

    @Override
    public List<RuleSource> selectRuleSourceList(RuleSource ruleSource) {
        return ruleSourceMapper.selectRuleSources(ruleSource);
    }

    @Override
    public boolean changeStatus(RuleSource ruleSource) {
        return ruleSourceMapper.updateSource(ruleSource) > 0;
    }

    @Override
    public boolean addSource(RuleSource ruleSource) {
        return ruleSourceMapper.insertSource(ruleSource) > 0;
    }

    @Override
    public boolean updateSource(RuleSource ruleSource) {
        return ruleSourceMapper.updateSource(ruleSource) > 0;
    }

    @Override
    public boolean delSource(Integer[] sourceIds) {
        return ruleSourceMapper.delSource(sourceIds) > 0;
    }
}
