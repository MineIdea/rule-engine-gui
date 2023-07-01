package com.ruoyi.ueba.rule.service.impl;

import com.ruoyi.ueba.rule.entity.Alert;
import com.ruoyi.ueba.rule.mapper.AlertMapper;
import com.ruoyi.ueba.rule.service.IAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liutao
 */
@Service
public class AlertServiceImpl implements IAlertService {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private AlertMapper alertMapper;

    @Override
    public List<Alert> listAlert(Alert alert) {
        return alertMapper.selectAlerts(alert);
    }

    @Override
    public int delAlerts(Integer[] alertIds) {
        return alertMapper.delAlerts(alertIds);
    }
}
