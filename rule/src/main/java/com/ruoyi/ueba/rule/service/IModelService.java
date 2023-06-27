package com.ruoyi.ueba.rule.service;

import com.ruoyi.ueba.rule.entity.Model;
import java.util.List;
/**
 * @author liutao
 */
public interface IModelService {
    List<Model> selectModels(Model model);

    int changeStatus(Model model);

    int addModel(Model model);

    int updateModel(Model model);

    int delModel(Integer[] modelIds);
}
