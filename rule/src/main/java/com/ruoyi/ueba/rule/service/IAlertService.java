package com.ruoyi.ueba.rule.service;

import com.ruoyi.ueba.rule.entity.Alert;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liutao
 */
@Service
public interface IAlertService {
    List<Alert> listAlert(Alert alert);
}
