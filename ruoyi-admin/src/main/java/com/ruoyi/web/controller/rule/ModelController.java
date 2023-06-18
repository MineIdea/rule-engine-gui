package com.ruoyi.web.controller.rule;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.ueba.rule.entity.Model;
import com.ruoyi.ueba.rule.service.IModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liutao
 */
@RestController
@RequestMapping("/rules/model")
public class ModelController extends BaseController {
    @Autowired
    private IModelService ruleService;

    @GetMapping("/list")
    public TableDataInfo listModels(Model model) {
        startPage();
        List<Model> ruleSources = ruleService.selectModels(model);
        return getDataTable(ruleSources);
    }

    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody Model model) {
        return toAjax(ruleService.changeStatus(model));
    }

    @PutMapping("/add")
    public AjaxResult addModel(@RequestBody Model model) {
        return toAjax(ruleService.addModel(model));
    }

    @PutMapping("/update")
    public AjaxResult updateModel(@RequestBody Model model) {
        return toAjax(ruleService.updateModel(model));
    }
}
