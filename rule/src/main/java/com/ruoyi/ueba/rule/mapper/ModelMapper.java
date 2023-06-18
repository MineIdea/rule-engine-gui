package com.ruoyi.ueba.rule.mapper;

import com.ruoyi.ueba.rule.entity.Model;

import java.util.List;

/**
 * @author liutao
 */
public interface ModelMapper {
    public List<Model> selectModels(Model model);

    public int updateModels(Model model);

    public int insertModel(Model model);
}
