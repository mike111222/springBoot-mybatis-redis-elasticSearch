package com.wooyoo.learning.util;

        import org.elasticsearch.client.transport.TransportClient;
        import org.elasticsearch.common.settings.Settings;
        import org.elasticsearch.common.transport.TransportAddress;
        import org.elasticsearch.transport.client.PreBuiltTransportClient;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;

        import java.net.InetAddress;

/**
 * @author     ：xuesheng
 * @date       ：Created in 4/13/21 1:46 PM
 * @description：ES配置类
 * @modified By：
 * @version    ：1.0$
 */
@Configuration
public class ElasticSearchConfig {
    @Bean
    public TransportClient client() throws Exception {
        // 1.创建一个Settings对象
        Settings settings = Settings.builder().put("cluster.name", "PYZL").build();

        // 2.创建一个客户端Client对象
        TransportClient client = new PreBuiltTransportClient(settings);

        // 指定集群中节点的列表
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("10.116.2.32"), 9300));
        return client;
    }
}
