package com.ruoyi.web.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author liutao
 */
@Component
@ConfigurationProperties(prefix = "hot-swapping")
public class HotSwappingConfig {
    @JsonProperty("bootstrap_servers")
    private String bootstrapServers;
    @JsonProperty("group_id")
    private String groupId;
    @JsonProperty("topic")
    private String topic;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotSwappingConfig that = (HotSwappingConfig) o;
        return Objects.equals(bootstrapServers, that.bootstrapServers) && Objects.equals(groupId, that.groupId) && Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bootstrapServers, groupId, topic);
    }

    @Override
    public String toString() {
        return "HotSwappingConfig{" +
                "bootstrapServers='" + bootstrapServers + '\'' +
                ", groupId='" + groupId + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
