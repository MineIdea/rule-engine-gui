package com.ruoyi.web.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @author liutao
 */
@Configuration
@ConfigurationProperties(prefix = "flink")
public class FlinkConfig {
    private String server;
    private int port;
    private String jarFile;

    private String entryClass;

    private String configTemplate;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getJarFile() {
        return jarFile;
    }

    public void setJarFile(String jarFile) {
        this.jarFile = jarFile;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public void setEntryClass(String entryClass) {
        this.entryClass = entryClass;
    }

    public String getConfigTemplate() {
        return configTemplate;
    }

    public void setConfigTemplate(String configTemplate) {
        this.configTemplate = configTemplate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlinkConfig that = (FlinkConfig) o;
        return port == that.port && Objects.equals(server, that.server) && Objects.equals(jarFile, that.jarFile) && Objects.equals(entryClass, that.entryClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, port, jarFile, entryClass);
    }

    @Override
    public String toString() {
        return "FlinkConfig{" +
                "server='" + server + '\'' +
                ", port=" + port +
                ", jarFile='" + jarFile + '\'' +
                ", entryClass='" + entryClass + '\'' +
                '}';
    }
}
