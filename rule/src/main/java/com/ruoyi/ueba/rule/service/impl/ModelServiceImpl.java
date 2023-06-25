package com.ruoyi.ueba.rule.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruoyi.common.config.HotSwappingConfig;
import com.ruoyi.common.utils.Utils;
import com.ruoyi.ueba.rule.entity.Model;
import com.ruoyi.ueba.rule.mapper.ModelMapper;
import com.ruoyi.ueba.rule.service.IModelService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author liutao
 */
@Service
public class ModelServiceImpl implements IModelService {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ModelMapper modelMapper;
    @Autowired
    private KafkaProducer<String, String> hotSwappingProducer;
    @Autowired
    private HotSwappingConfig hotSwappingConfig;

    @Override
    public List<Model> selectModels(Model model) {
        return modelMapper.selectModels(model);
    }

    @Override
    public int changeStatus(Model model) {
        return modelMapper.updateModel(model);
    }

    @Override
    public int addModel(Model model) {
        this.sendChangeModel(model.getData(), EventType.add);
        return modelMapper.insertModel(model);
    }

    @Override
    public int updateModel(Model model) {
        this.sendChangeModel(model.getData(), EventType.update);
        return modelMapper.updateModel(model);
    }

    public void sendChangeModel(Map<String, Object> model, EventType eventType) {
        String uuId = UUID.randomUUID().toString();
        Map<String, Object> res = new HashMap<>();
        res.put("type", eventType.toString());
        res.put("model", model);
        try {
            this.hotSwappingProducer.send(new ProducerRecord<>(
                    hotSwappingConfig.getTopic(),
                    uuId,
                    Utils.jsonMapper.writeValueAsString(res))
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public enum EventType {
        add,
        update,
        delete
    }
}
