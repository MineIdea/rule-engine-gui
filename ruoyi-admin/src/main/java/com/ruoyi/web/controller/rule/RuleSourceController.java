package com.ruoyi.web.controller.rule;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.ueba.rule.entity.RuleSource;
import com.ruoyi.ueba.rule.service.IRuleSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liutao
 */
@RestController
@RequestMapping("/rules/source")
public class RuleSourceController extends BaseController {
    private IRuleSourceService ruleSourceService;

    @GetMapping("/list")
    public TableDataInfo list(RuleSource ruleSource) {
        startPage();
        List<RuleSource> ruleSources = ruleSourceService.selectRuleSourceList(ruleSource);
        return getDataTable(ruleSources);
    }

    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody RuleSource ruleSource) {
        return toAjax(ruleSourceService.changeStatus(ruleSource));
    }

    @Autowired
    public void SetIRuleSourceService(IRuleSourceService ruleSourceService) {
        this.ruleSourceService = ruleSourceService;
    }

}
