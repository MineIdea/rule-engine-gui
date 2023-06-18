package com.ruoyi.ueba.rule.service.impl;

import com.ruoyi.ueba.rule.entity.Model;
import com.ruoyi.ueba.rule.mapper.ModelMapper;
import com.ruoyi.ueba.rule.service.IModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liutao
 */
@Service
public class ModelServiceImpl implements IModelService {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ModelMapper modelMapper;

    @Override
    public List<Model> selectModels(Model model) {
        return modelMapper.selectModels(model);
    }

    @Override
    public int changeStatus(Model model) {
        return modelMapper.updateModels(model);
    }

    @Override
    public int addModel(Model model) {
        return modelMapper.insertModel(model);
    }

    @Override
    public int updateModel(Model model) {
        return modelMapper.updateModels(model);
    }
}
