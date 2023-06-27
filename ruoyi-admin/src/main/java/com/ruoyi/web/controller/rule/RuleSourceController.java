package com.ruoyi.web.controller.rule;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.ueba.rule.entity.RuleSource;
import com.ruoyi.ueba.rule.service.IRuleSourceService;
import com.ruoyi.common.utils.Utils;
import com.ruoyi.common.config.FlinkConfig;
import com.ruoyi.common.config.HotSwappingConfig;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobStatus;
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
import java.util.concurrent.ExecutionException;

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
    @Autowired
    private RestClusterClient<StandaloneClusterId> client;

    @GetMapping("/list")
    public TableDataInfo list(RuleSource ruleSource) {
        startPage();
        List<RuleSource> ruleSources = ruleSourceService.selectRuleSourceList(ruleSource);
        return getDataTable(ruleSources);
    }

    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody RuleSource ruleSource) {
        Boolean active = ruleSource.getActive();
        if (active != null) {
            RuleSource detail = (RuleSource) this.list(ruleSource).getRows().get(0);
            boolean jobRunning = false;
            try {
                jobRunning = isJobRunning(detail.getJobId());
            } catch (Exception e) {
                log.warn("check job status error", e);
                return AjaxResult.error("check job status error", e);
            }
            if (active) {
                if (jobRunning) {
                    return AjaxResult.success("job is running, do not need update");
                } else {
                    JobID jobID;
                    try {
                        jobID = this.submitNewJob(detail);
                    } catch (Exception e) {
                        log.warn("start job error", e);
                        return AjaxResult.error("start job error", e);
                    }
                    log.info("start job success, job id is {}", jobID);
                    ruleSource.setJobId(jobID.toHexString());
                }
            } else {
                if (jobRunning) {
                    try {
                        client.cancel(JobID.fromHexString(detail.getJobId())).get();
                    } catch (InterruptedException | ExecutionException e) {
                        log.warn("cancel job error", e);
                        return AjaxResult.error("cancel job error", e);
                    }
                    ruleSource.setJobId(" ");
                    log.info("cancel job success, job id is {}", ruleSource.getJobId());
                }
            }
        }

        return toAjax(ruleSourceService.changeStatus(ruleSource));
    }

    @PutMapping("/add")
    public AjaxResult addSource(@RequestBody RuleSource ruleSource) {
        JobID jobID;
        try {
            jobID = this.submitNewJob(ruleSource);
            log.info("start job success, job id is {}", jobID);
        } catch (Exception e) {
            log.error("submit new job error", e);
            return toAjax(false);
        }
        ruleSource.setJobId(jobID.toHexString());
        boolean result = ruleSourceService.addSource(ruleSource);
        return toAjax(result);
    }

    @PutMapping("/update")
    public AjaxResult updateSource(@RequestBody RuleSource ruleSource) {
        Boolean active = ruleSource.getActive();
        if (active != null) {
            if (active) {
                try {
                    if (isJobRunning(ruleSource.getJobId())) {
                        return AjaxResult.success("job is running, do not need update");
                    } else {
                        JobID jobID = this.submitNewJob(ruleSource);
                        log.info("start job success, job id is {}", jobID);
                        ruleSource.setJobId(jobID.toHexString());
                    }
                } catch (Exception e) {
                    log.warn("check job status error", e);
                    return toAjax(false);
                }
            } else {
                try {
                    if (isJobRunning(ruleSource.getJobId())) {
                        client.cancel(JobID.fromHexString(ruleSource.getJobId())).get();
                        log.info("cancel job success, job id is {}", ruleSource.getJobId());
                    }
                } catch (Exception e) {
                    log.warn("cancel job error", e);
                    return toAjax(false);
                }
            }
        }

        return toAjax(ruleSourceService.updateSource(ruleSource));
    }

    @DeleteMapping("/{sourceIds}")
    public AjaxResult delSource(@PathVariable Integer[] sourceIds) {
        RuleSource ruleSource = new RuleSource();
        for (Integer sourceId : sourceIds) {
            ruleSource.setId(sourceId);
            TableDataInfo list = this.list(ruleSource);
            for (Object row : list.getRows()) {
                RuleSource source = (RuleSource) row;
                try {
                    if (isJobRunning(source.getJobId())) {
                        client.cancel(JobID.fromHexString(ruleSource.getJobId())).get();
                        log.info("cancel job success, job id is {}", ruleSource.getJobId());
                    }
                } catch (Exception e) {
                    log.warn("cancel job error.Ignore this job.", e);
                }
            }
        }

        return toAjax(ruleSourceService.delSource(sourceIds));
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
        Map<String, Object> data = ruleSource.getData();
        String dataJson = Utils.jsonMapper.writeValueAsString(data);
        dataJson = dataJson.replace("\"", "\\\"");
        String configTemplate = this.flinkConfig.getConfigTemplate();
        String config = String.format(configTemplate,
                dataJson,
                this.flinkConfig.getServer() + ":" + this.flinkConfig.getPort(),
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

    public boolean isJobRunning(String jobId) throws Exception {
        if (jobId == null || jobId.isEmpty() || jobId.equals(" ")) {
            return false;
        }
        JobStatus jobStatus = client.getJobStatus(JobID.fromHexString(jobId)).get();
        return jobStatus == JobStatus.INITIALIZING || jobStatus == JobStatus.CREATED || jobStatus == JobStatus.RUNNING;
    }

}
