package com.ruoyi.web.controller.rule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ruoyi.common.utils.Utils;
import com.ruoyi.ueba.rule.entity.Alert;
import com.ruoyi.ueba.rule.mapper.AlertMapper;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author liutao
 */
@Component
public class AlertListener {
    private static Logger log = LoggerFactory.getLogger(AlertListener.class);
    @Autowired
    private AlertMapper alertMapper;

    @KafkaListener(topics = "ueba_rule_alert", groupId = "web_consumer")
    public void consume(String record) {
        try {
            Map<String, Object> map = Utils.jsonMapper.readValue(record, new TypeReference<Map<String, Object>>() {
            });
            Alert alert = new Alert();
            alert.setUuId((String) map.get("uuId"));
            alert.setName((String) map.get("name"));
            alert.setDesc(((String) map.get("desc")));
            alert.setData(map);
            this.alertMapper.insertAlert(alert);
            log.info("insert alert uuId[{}] name[{}] to db successfully",
                    alert.getUuId(),
                    alert.getName());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
