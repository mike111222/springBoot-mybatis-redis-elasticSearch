//package com.wooyoo.learning.util.ES;
//
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.TransportAddress;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
///**
// * @author     ：xuesheng
// * @date       ：Created in 4/13/21 1:46 PM
// * @description：ES配置类
// * @modified By：
// * @version    ：1.0$
// */
//@Configuration
//public class TransportClientConfig extends ElasticsearchConfigurationSupport {
//
//    @Bean
//    public Client elasticsearchClient() throws UnknownHostException {
//        Settings settings = Settings.builder().put("cluster.name", "PYZL").build();
//        TransportClient client = new PreBuiltTransportClient(settings);
//        client.addTransportAddress(new TransportAddress(InetAddress.getByName("10.116.2.32"), 9300));
//        return client;
//    }
//
////    @Bean(name = { "elasticsearchOperations", "elasticsearchTemplate" })
////    public ElasticsearchTemplate elasticsearchTemplate() throws UnknownHostException {
////
////        ElasticsearchTemplate template = new ElasticsearchTemplate(elasticsearchClient, elasticsearchConverter);
////        template.setRefreshPolicy(refreshPolicy());
////
////        return template;
////    }
//}
//
