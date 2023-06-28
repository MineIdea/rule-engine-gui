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
        this.sendChangeModel(model, EventType.add);
        return modelMapper.insertModel(model);
    }

    @Override
    public int updateModel(Model model) {
        this.sendChangeModel(model, EventType.update);
        return modelMapper.updateModel(model);
    }

    @Override
    public int delModel(Integer[] modelIds) {
        for (Integer modelId : modelIds) {
            Model model = new Model();
            model.setId(modelId);
            Model detail = this.selectModels(model).get(0);
            this.sendChangeModel(detail, EventType.delete);
        }
        return modelMapper.delModels(modelIds);
    }

    @Override
    public int getMaxModelId() {
        Integer maxModelId = modelMapper.getMaxModelId();
        if (maxModelId == null) {
            return 0;
        }
        return maxModelId;
    }

    public void sendChangeModel(Model model, EventType eventType) {
        String uuId = UUID.randomUUID().toString();
        if (model.getId() != null) {
            model.getData().put("mid", model.getId());
        }
        Map<String, Object> res = new HashMap<>();
        res.put("type", eventType.toString());
        res.put("model", model.getData());
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
