package com.ruoyi.web.controller.rule;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.ueba.rule.entity.RuleSource;
import com.ruoyi.ueba.rule.service.IRuleSourceService;
import com.ruoyi.web.controller.common.CommonController;
import com.ruoyi.web.controller.tool.Utils;
import com.ruoyi.web.core.config.FlinkConfig;
import com.ruoyi.web.core.config.HotSwappingConfig;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @author liutao
 */
@RestController
@RequestMapping("/rules/source")
public class RuleSourceController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(RuleSourceController.class);

    private IRuleSourceService ruleSourceService;

    @Autowired
    private FlinkConfig flinkConfig;
    @Autowired
    private HotSwappingConfig hotSwappingConfig;

    @GetMapping("/list")
    public TableDataInfo list(RuleSource ruleSource) {
        startPage();
        List<RuleSource> ruleSources = ruleSourceService.selectRuleSourceList(ruleSource);
        return getDataTable(ruleSources);
    }

    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody RuleSource ruleSource) {
        return toAjax(ruleSourceService.changeStatus(ruleSource));
    }

    @PutMapping("/add")
    public AjaxResult addSource(@RequestBody RuleSource ruleSource) {
        try {
            JobID jobID = this.submitNewJob(ruleSource);
            log.info("start job success, job id is {}", jobID);
        } catch (Exception e) {
            log.error("submit new job error", e);
            return toAjax(false);
        }
        boolean result = ruleSourceService.addSource(ruleSource);
        return toAjax(result);
    }

    @PutMapping("/update")
    public AjaxResult updateSource(@RequestBody RuleSource ruleSource) {
        return toAjax(ruleSourceService.updateSource(ruleSource));
    }

    @Autowired
    public void SetIRuleSourceService(IRuleSourceService ruleSourceService) {
        this.ruleSourceService = ruleSourceService;
    }

    public JobID submitNewJob(RuleSource ruleSource) throws Exception {
        Configuration configuration = new Configuration();
        configuration.setString(RestOptions.ADDRESS, this.flinkConfig.getServer());
        configuration.setInteger(RestOptions.PORT, this.flinkConfig.getPort());

        // Create a REST client to connect to the Flink cluster
        try (RestClusterClient<StandaloneClusterId> client =
                     new RestClusterClient<>(configuration, StandaloneClusterId.getInstance())) {
            Map<String, Object> data = ruleSource.getData();
            String dataJson = Utils.jsonMapper.writeValueAsString(data);
            dataJson = dataJson.replace("\"", "\\\"");
            String configTemplate = this.flinkConfig.getConfigTemplate();
            String config = String.format(configTemplate,
                    dataJson,
                    this.flinkConfig.getServer()+":"+ this.flinkConfig.getPort(),
                    Utils.jsonMapper.writeValueAsString(hotSwappingConfig)
            );

            String base64Config = Base64.getEncoder().encodeToString(config.getBytes());
            // Create a JobGraph for your Flink job
            PackagedProgram program = PackagedProgram.newBuilder()
                    .setJarFile(new File(this.flinkConfig.getJarFile()))
                    .setEntryPointClassName(this.flinkConfig.getEntryClass())
                    .setArguments("-c", base64Config)
                    .build();
            JobGraph jobGraph =
                    PackagedProgramUtils.createJobGraph(program, configuration, 1, false);

            // Submit the job to the Flink cluster
           return client.submitJob(jobGraph).get();
        }
    }

}
