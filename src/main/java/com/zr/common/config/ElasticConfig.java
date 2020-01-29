package com.zr.common.config;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import java.net.InetAddress;


@Configuration
@PropertySource(value = "classpath:elasticsearch.properties")
public class ElasticConfig {
    private static final Logger logger = LoggerFactory.getLogger(ElasticConfig.class);
    @Value("${elasticsearch.hostName}")
    private String hostName;
    @Value("${elasticsearch.transport}")
    private Integer transport;
    @Value("${elasticsearch.clusterName}")
    private String clusterName;
    @Bean
    public TransportClient transportClient(){
        logger.info("初始化ES");
        TransportClient transportClient = null;
        try {
            TransportAddress transportAddress  = new TransportAddress(InetAddress.getByName(hostName),Integer.valueOf(transport));
            /*配置信息*/
            Settings es = Settings.builder().put("cluster.name",clusterName).build();
            /*配置信息settings自定义*/
            transportClient = new PreBuiltTransportClient(es);
            transportClient.addTransportAddress(transportAddress);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  transportClient;
    }
}
