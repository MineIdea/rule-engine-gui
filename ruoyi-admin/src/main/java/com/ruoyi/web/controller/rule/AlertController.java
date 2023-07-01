package com.ruoyi.web.controller.rule;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.ueba.rule.entity.Alert;
import com.ruoyi.ueba.rule.entity.RuleSource;
import com.ruoyi.ueba.rule.service.IAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ruoyi.common.utils.PageUtils.startPage;

/**
 * @author liutao
 */
@RestController
@RequestMapping("/rules/alert")
public class AlertController extends BaseController {
    @Autowired
    private IAlertService alertService;

    @GetMapping("/list")
    public TableDataInfo list(Alert alert) {
        startPage();
        List<Alert> ruleSources = alertService.listAlert(alert);
        return getDataTable(ruleSources);
    }

    @DeleteMapping("/{alertIds}")
    public AjaxResult delAlerts(@PathVariable Integer[] alertIds) {
        return toAjax(alertService.delAlerts(alertIds));
    }
}
