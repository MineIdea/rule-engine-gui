package com.ruoyi.ueba.rule.mapper;

import com.ruoyi.ueba.rule.entity.Alert;

import java.util.List;

/**
 * @author liutao
 */
public interface AlertMapper {
    public List<Alert> selectAlerts(Alert alert);
}
