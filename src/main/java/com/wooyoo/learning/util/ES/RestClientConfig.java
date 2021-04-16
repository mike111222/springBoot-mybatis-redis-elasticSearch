//package com.wooyoo.learning.util.ES;
//
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.TransportAddress;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
//import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
//import org.springframework.data.elasticsearch.client.RestClients;
//
//import java.net.InetAddress;
//
///**
// * @author     ：xuesheng
// * @date       ：Created in 4/13/21 1:46 PM
// * @description: High Level REST Client is the default client of Elasticsearch,
// * @modified By：
// * @version    ：1.0$
// */
//@Configuration
//public class RestClientConfig extends AbstractElasticsearchConfiguration {
//
//    @Override
//    @Bean
//    public RestHighLevelClient elasticsearchClient() {
//
//        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo("localhost:9200")
//                .build();
//
//        return RestClients.create(clientConfiguration).rest();
//    }
//}
