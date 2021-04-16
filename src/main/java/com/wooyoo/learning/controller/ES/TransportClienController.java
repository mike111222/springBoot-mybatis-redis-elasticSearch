package com.wooyoo.learning.controller.ES;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author     ：xuesheng
 * @date       ：Created in 4/13/21 11:25 AM
 * @description：es测试
 * @modified By：
 * @version    ：1.0$
 */
@RestController
public class TransportClienController {
//    ./showdoc_api.sh /Users/xuesheng/Desktop/worksapce/xuesheng/spring-boot-mybatis-with-redis-master

    private static Log logger = LogFactory.getLog(TransportClienController.class);


    @Autowired
    private TransportClient client;

    /**
     * showdoc
     * @catalog 接口文档/ElasticSearch
     * @title 1.新增index
     * @method post
     * @url 127.0.0.1:9988/addIndex
     * @param index 必选 string 索引
     * @json_param {"index": "flb_cust_info_ses"}
     * @return {"code":"200","msg":"success","data":"flb_cust_info_ses"}
     * @return_param code string 状态，200：成功
     * @return_param msg string 信息
     * @return_param data string 新增的index
     * @number 1
     */
    @PostMapping("addIndex")
    @ResponseBody
    public Map<String,String> addIndex(@RequestBody String param) {
        Map<String,String> res=new HashMap<>();
        JSONObject json = JSONObject.parseObject(param);
        String index=json.getString("index");
        CreateIndexResponse createIndexResponse=this.client.admin().indices().prepareCreate(index).get();

        logger.info(createIndexResponse.hashCode());
        logger.info(createIndexResponse.index());
        logger.info(createIndexResponse.toString());
        if(StringUtils.isNotBlank(createIndexResponse.index())){
            res.put("code","200");
            res.put("msg","success");
            res.put("data",createIndexResponse.index());
        }else {
            res.put("msg","新增索引失败");
        }
        return res;
    }
    /**
     * showdoc
     * @catalog 接口文档/ElasticSearch
     * @title 2.删除index
     * @method post
     * @url 127.0.0.1:9988/deleteIndex
     * @param index 必选 string 索引
     * @json_param {"index": "flb_cust_info_ses"}
     * @return {"code":"200","msg":"删除索引flb_cust_info_ses1成功"}
     * @return_param code string 状态，200：成功
     * @return_param msg string 信息
     * @number 2
     */
    @PostMapping("deleteIndex")
    @ResponseBody
    public Map<String,String> deleteIndex(@RequestBody String param) {
        Map<String,String> res=new HashMap<>();
        JSONObject json = JSONObject.parseObject(param);
        String index=json.getString("index");
        AcknowledgedResponse dResponse=this.client.admin().indices().prepareDelete(index).get();

        if (dResponse.isAcknowledged()) {
            logger.info(dResponse.isAcknowledged());
            res.put("code","200");
            res.put("msg","删除索引"+index+"成功");
        }else {
            res.put("msg","删除索引" + index+"失败");
        }
        return res;
    }
    /**
     * showdoc
     * @catalog 接口文档/ElasticSearch
     * @title 3.插入document
     * @method post
     * @url 127.0.0.1:9988/addDocument
     * @param index 必选 string 索引
     * @param type 非必选 string 类型，默认为_doc
     * @param columns 必选 string 插入document的列
     * @json_param {"index":"flb_cust_info_ses","columns":{"title0":"s0","title1":"s1"}}
     * @return {"code":"200","msg":"success","data":"lzNZ2XgBHGZEls7JpxTg"}
     * @return_param code string 状态，200：成功
     * @return_param msg string 信息
     * @return_param data string 插入document的id
     * @number 3
     */
    @PostMapping("addDocument")
    @ResponseBody
    public Map<String,String> addDocument(@RequestBody String param) {
        Map<String,String> res=new HashMap<>();
        JSONObject json = JSONObject.parseObject(param);
        String index=json.getString("index");
        String type=StringUtils.isNotBlank(json.getString("type"))?json.getString("type"):"_doc";
        String columns=json.getString("columns");

        LinkedHashMap<String,String> columnMap=JSONObject.parseObject(columns,LinkedHashMap.class);
        try{
            XContentBuilder content=XContentFactory.jsonBuilder().startObject();

            for(Map.Entry<String, String> entry : columnMap.entrySet()){
                String mapKey = entry.getKey();
                String mapValue = entry.getValue();
                content.field(mapKey,mapValue);
            }
            content.endObject();
            IndexResponse result=this.client.prepareIndex(index,type).setSource(content).get();
            if(StringUtils.isNotBlank(result.getId())){
                res.put("code","200");
                res.put("msg","success");
                res.put("data",result.getId());
            }
        }catch (Exception e){
            res.put("code","500");
            e.printStackTrace();
        }
        return res;
    }
    /**
     * showdoc
     * @catalog 接口文档/ElasticSearch
     * @title 4.批量插入document
     * @description 批量插入document，批量插入的数据从文件中获得，此处模拟从文件中获得数据并批量插入
     * @method post
     * @url 127.0.0.1:9988/batchAdd
     * @param index 必选 string 索引
     * @json_param {"index": "flb_alarm_info_ses"}
     * @return {"msg": "success","code": "200"}
     * @return_param code string 状态，200：成功
     * @return_param msg string 信息
     * @number 4
     */
    @PostMapping("batchAdd")
    public Map<String,String> batchAdd(@RequestBody String param){
        Map<String,String> res=new HashMap<>();

        try{
            String data0="2000064447!^浦发2000064447!^DGGLFX999912!^客户的主要股东、关联企业发生严重亏损，重大经济纠纷，涉嫌重大经济案件，关停倒闭，破产等重大变化!^人行重要信息提示显示有 {保证人一代二代征信人行指标表.新增逾期60天保证人数} 人新增逾期60天，有 {保证人一代二代征信人行指标表.新增逾期90天保证人数} 人新增逾期90天。数据显示有 {保证人一代二代征信人行指标表.他行未结清欠息笔数保证人数} 个保证人他行存在未结清欠息；有 {保证人一代二代征信人行指标表.他行未结清垫款笔数保证人数} 个保证人他行存在未结清垫款；有 {保证人一代二代征信人行指标表.他行未结清逾期笔数保证人数} 个保证人他行存在未结清逾期；有 {保证人一代二代征信人行指标表.他行未结清借新还旧笔数保证人数} 个保证人他行存在未结清借新还旧；有 {保证人一代二代征信人行指标表.他行未结清资产重组笔数保证人数} 个保证人他行存在资产重组；有 {保证人一代二代征信人行指标表.垫款客户标志保证人数} 个保证人本行存在垫款；有 {保证人一代二代征信人行指标表.欠息客户标志保证人数} 个保证人本行存在欠息；有 {保证人一代二代征信人行指标表.逾期客户标志保证人数} 个保证人本行存在逾期；有 {保证人一代二代征信人行指标表.展期贷款客户标志保证人数} 个保证人本行存在展期； !^1!^02!^0!^!^3!^!^20200728!^!^!^F5161!^7!^!^!^!^!^!^";
            String data1="2000064447!^浦发2000064447!^DGRZFX0004!^当前借款企业他行当前有未结清垫款笔数>1!^企业他行未结清业务出现垫款：他行未结清垫款业务数 {企业客户征信指标.他行未结清垫款业务数} ；他行未结清垫款业务余额 {企业客户征信指标.他行未结清垫款余额} 。  !^1!^02!^0!^!^2!^!^20200906!^!^!^F5161!^7!^!^!^!^!^!^";
            String data2="2000064447!^好好学习!^DGRZFX0004!^当前借款企业他行当前有未结清垫款笔数>1!^企业他行未结清业务出现垫款：他行未结清垫款业务数 {企业客户征信指标.他行未结清垫款业务数} ；他行未结清垫款业务余额 {企业客户征信指标.他行未结清垫款余额} 。  !^1!^02!^0!^!^2!^!^20200906!^!^!^F5161!^7!^!^!^!^!^!^";
            String data3="2000064447!^学什么习!^DGGLFX999912!^当前借款企业他行当前有未结清垫款笔数>1!^企业他行未结清业务出现垫款：他行未结清垫款业务数 {企业客户征信指标.他行未结清垫款业务数} ；他行未结清垫款业务余额 {企业客户征信指标.他行未结清垫款余额} 。  !^1!^02!^0!^!^2!^!^20200906!^!^!^F5161!^7!^!^!^!^!^!^";
            JSONObject json = JSONObject.parseObject(param);
            String index=json.getString("index");
            logger.info("index"+":"+index);

            List<String[]> dataList=new ArrayList<>();
            dataList.add(data0.split("\\!\\^",-1));
            dataList.add(data1.split("\\!\\^",-1));
            dataList.add(data2.split("\\!\\^",-1));
            dataList.add(data3.split("\\!\\^",-1));

            for(int i=0;i<dataList.size();i++){
                XContentBuilder content=XContentFactory.jsonBuilder().startObject();
                for(int j=0;j<data0.split("\\!\\^",-1).length;j++){
                    content.field("title"+j,dataList.get(i)[j]);
                }
                content.endObject();
                IndexResponse result=this.client.prepareIndex(index,"_doc").setSource(content).get();
                logger.info("id"+i+":"+result.getId());
            }
            res.put("code","200");
            res.put("msg","success");
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    /**
     * showdoc
     * @catalog 接口文档/ElasticSearch
     * @title 5.根据id搜document
     * @method post
     * @url 127.0.0.1:9988/queryById
     * @param index 必选 string 索引
     * @param id 必选 string document的id
     * @json_param {"index": "flb_alarm_info_ses","id":"ijNd2HgBHGZEls7J0hSr"}
     * @return {"msg":"success","code":"200","data":{"title20":"","title10":"","title21":"","title0":"2000064447","title15":"7","title1":"好好学习","title16":"","title2":"DGRZFX0004","title17":"","title3":"当前借款企业他行当前有未结清垫款笔数>1","title18":"","title4":"企业他行未结清业务出现垫款：他行未结清垫款业务数 {企业客户征信指标.他行未结清垫款业务数} ；他行未结清垫款业务余额 {企业客户征信指标.他行未结清垫款余额} 。  ","title11":"20200906","title5":"1","title12":"","title6":"02","title13":"","title7":"0","title14":"F5161","title8":"","title9":"2","title19":""}}
     * @return_param code string 状态，200：成功
     * @return_param msg string 信息
     * @return_param data 搜到的document
     * @number 5
     */
    @PostMapping("queryById")
    public Map<String,Object> queryById(@RequestBody String param){
        Map<String,Object> res=new HashMap<>();
        JSONObject json = JSONObject.parseObject(param);
        String index=json.getString("index");
        String id=json.getString("id");
        if(id.isEmpty()){
            res.put("code","404");
            return res;
        }
        GetResponse response=this.client.prepareGet(index,"_doc",id).get();
        if(!response.isExists()){
            res.put("code","404");
            return res;
        }
        res.put("code","200");
        res.put("msg","success");
        res.put("data",response.getSourceAsMap());
        return  res;
    }
    /*
    复合查询
    must:文档必须匹配这些条件才能被包含进来。相当于sql中的 and
	must_not:文档必须不匹配这些条件才能被包含就来。相当于sql中的 not
	should:如果满足这些语句中的任意语句，将增加_score,否则无任何影响，主要用于修正每个文档的相关性得分。相当于sql中的or
	filter:必须匹配，但他以不评分、过滤模式来进行，这些语句对评分没有贡献

	matchQuery：会将搜索词分词，再与目标查询字段进行匹配，若分词中的任意一个词与目标字段匹配上，则可查询到。
    termQuery：不会对搜索词进行分词处理，而是作为一个整体与目标字段进行匹配，若完全匹配，则可查询到。
    eg.学习
    QueryBuilders.matchQuery("title1", "好好学习")，好好学习、学什么习
    QueryBuilders.termQuery("title1.keyword", "好好学习")，好好学习
        elasticsearch版本7.3.1，这里必须加.keyword才能正常使用termQuery，有人说6.5的版本都有这个问题，可能不是bug，有人说原因是
        elasticsearch 里默认的IK分词器是会将每一个中文都进行了分词的切割，所以你直接想查一整个词，或者一整句话是无返回结果的。https://ask.csdn.net/questions/267605
    QueryBuilders.matchPhraseQuery("title1", "学习"))，好好学习
    QueryBuilders.matchPhrasePrefixQuery("title1", "学习"))，好好学习
    */

    /**
     * showdoc
     * @catalog 接口文档/ElasticSearch
     * @title 6.复合查询document
     * @method post
     * @url 127.0.0.1:9988/compoundQuery
     * @param index 必选 string 索引
     * @json_param {"index": "flb_alarm_info_ses"}
     * @return {"msg":"200","date":[{"title20":"","title10":"","title21":"","title0":"2000064447","title15":"7","title1":"浦发2000064447","title16":"","title2":"DGGLFX999912","title17":"","title3":"客户的主要股东、关联企业发生严重亏损，重大经济纠纷，涉嫌重大经济案件，关停倒闭，破产等重大变化","title18":"","title4":"人行重要信息提示显示有 {保证人一代二代征信人行指标表.新增逾期60天保证人数} 人新增逾期60天，有 {保证人一代二代征信人行指标表.新增逾期90天保证人数} 人新增逾期90天。数据显示有 {保证人一代二代征信人行指标表.他行未结清欠息笔数保证人数} 个保证人他行存在未结清欠息；有 {保证人一代二代征信人行指标表.他行未结清垫款笔数保证人数} 个保证人他行存在未结清垫款；有 {保证人一代二代征信人行指标表.他行未结清逾期笔数保证人数} 个保证人他行存在未结清逾期；有 {保证人一代二代征信人行指标表.他行未结清借新还旧笔数保证人数} 个保证人他行存在未结清借新还旧；有 {保证人一代二代征信人行指标表.他行未结清资产重组笔数保证人数} 个保证人他行存在资产重组；有 {保证人一代二代征信人行指标表.垫款客户标志保证人数} 个保证人本行存在垫款；有 {保证人一代二代征信人行指标表.欠息客户标志保证人数} 个保证人本行存在欠息；有 {保证人一代二代征信人行指标表.逾期客户标志保证人数} 个保证人本行存在逾期；有 {保证人一代二代征信人行指标表.展期贷款客户标志保证人数} 个保证人本行存在展期； ","title11":"20200728","title5":"1","title12":"","title6":"02","title13":"","title7":"0","title14":"F5161","title8":"","title9":"3","title19":""}],"code":"200"}
     * @return_param code string 状态，200：成功
     * @return_param msg string 信息
     * @return_param data 搜到的document
     * @number 6
     */
    @PostMapping("compoundQuery")
    public Map<String,Object> compoundQuery(@RequestBody String param){
        Map<String,Object> res=new HashMap<>();
        try{
            JSONObject json = JSONObject.parseObject(param);
            String index=json.getString("index");

            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("title1", "浦发"))
                    .mustNot(QueryBuilders.termQuery("title2.keyword", "DGRZFX0004"));
//                    .must(QueryBuilders.matchPhraseQuery("title1", "学习"));
//                    .must(QueryBuilders.matchPhrasePrefixQuery("title1", "学习"));
//                    .should(QueryBuilders.matchPhraseQuery("title1","学习"));
//                    .should(QueryBuilders.rangeQuery("age").gte("24").lte("32"))
//                    .filter(QueryBuilders.rangeQuery("age").gte("24").lte("32"));

            SearchResponse searchResponse = this.client.prepareSearch(index).setQuery(queryBuilder).get();
            int i=0;
            List hitList=new ArrayList();
            for(SearchHit hit:searchResponse.getHits()) {
                hitList.add(hit.getSourceAsMap());
                logger.info(hit.getSourceAsString());
            }
            res.put("code","200");
            res.put("msg","success");
            res.put("date",hitList);

        }catch (Exception e){
            e.printStackTrace();
            logger.error("e",e);
            logger.error(e.getMessage());
        }
        return  res;
    }
    /**
     * showdoc
     * @catalog 接口文档/ElasticSearch
     * @title 7.按id删除document
     * @method post
     * @url 127.0.0.1:9988/deleteById
     * @param index 必选 string 索引
     * @param type 非必选 string 类型，默认为_doc
     * @param id 必选 string 要删除的document的id
     * @json_param {"index": "flb_alarm_info_ses","id":"kzN42HgBHGZEls7J1RTx"}
     * @return {"msg":"success","code":"200","data":"kzN42HgBHGZEls7J1RTx"}
     * @return_param code string 状态，200：成功
     * @return_param msg string 信息
     * @return_param data 删除了的document的id
     * @number 7
     */
    @PostMapping("deleteById")
    public Map<String,String> deleteById(@RequestBody String param){
        Map<String,String> res=new HashMap<>();
        JSONObject json = JSONObject.parseObject(param);
        String index=json.getString("index");
        String type=StringUtils.isNotBlank(json.getString("type"))?json.getString("type"):"_doc";
        String id=json.getString("id");

        if(StringUtils.isNotBlank(index)&&StringUtils.isNotBlank(id)){
            logger.info("删除");
            logger.info("index"+index);
            logger.info("type"+type);
            logger.info("id"+id);
            DeleteResponse response=this.client.prepareDelete(index,type,id).get();
            if(StringUtils.isNotBlank(response.getId())){
                res.put("code","200");
                res.put("msg","success");
                res.put("data",response.getId());
            }
        }else {
            res.put("msg","索引或id不可为空！");
        }
        return  res;
    }

    /**
     * showdoc
     * @catalog 接口文档/ElasticSearch
     * @title 8.批量删除document
     * @method post
     * @url 127.0.0.1:9988/batchDelete
     * @param index 必选 string 索引
     * @param type 非必选 string 类型，默认为_doc
     * @param ids 必选 string 要删除的document的id，以逗号分隔
     * @json_param {"index": "flb_alarm_info_ses","ids":"jjNz2HgBHGZEls7JJBRl,jzNz2HgBHGZEls7JJBR3,kDNz2HgBHGZEls7JJBSC,kTNz2HgBHGZEls7JJBSd"}
     * @return {"msg":"success","code":"200","data":["jjNz2HgBHGZEls7JJBRl","jzNz2HgBHGZEls7JJBR3","kDNz2HgBHGZEls7JJBSC","kTNz2HgBHGZEls7JJBSd"]}
     * @return_param code string 状态，200：成功
     * @return_param msg string 信息
     * @return_param data 删除了的document的id
     * @number 8
     */
    @PostMapping("batchDelete")
    public Map<String,Object> batchDelete(@RequestBody String param){
        Map<String,Object> res=new HashMap<>();
        JSONObject json = JSONObject.parseObject(param);
        String index=json.getString("index");
        String type=StringUtils.isNotBlank(json.getString("type"))?json.getString("type"):"_doc";
        String ids=json.getString("ids");

        if(StringUtils.isNotBlank(index)&&StringUtils.isNotBlank(ids)){
            logger.info("删除");
            logger.info("index"+index);
            logger.info("type"+type);
            logger.info("ids"+ids);
            String[] idArray=ids.split(",");

            List deletedList=new ArrayList();
            DeleteResponse response=null;
            for(String idItem:idArray){
                response=this.client.prepareDelete(index,type,idItem).get();
                deletedList.add(response.getId());
            }
            res.put("code","200");
            res.put("msg","success");
            res.put("data",deletedList);
        }else {
            res.put("msg","索引或ids不可为空！");
        }
        return  res;
    }

    /**
     * showdoc
     * @catalog 接口文档/ElasticSearch
     * @title 9.根据id修改document
     * @method post
     * @url 127.0.0.1:9988/updateById
     * @param index 必选 string 索引
     * @param type 非必选 string 类型，默认为_doc
     * @param id 必选 string 被修改document的id
     * @param columns 必选 Object document被修改的列及对应值
     * @json_param {"index": "flb_alarm_info_ses","id":"kjN42HgBHGZEls7J1RTi","columns":{"title1":"测试1111","title2":"测试2222"}}
     * @return {"code":"200","msg":"success"}
     * @return_param code string 状态，200：成功
     * @return_param msg string 信息
     * @number 9
     */
    @PostMapping("updateById")
    public Map<String,String> updateById(@RequestBody String param){
        Map<String,String> res=new HashMap<>();
        JSONObject json = JSONObject.parseObject(param);
        String index=json.getString("index");
        String type=StringUtils.isNotBlank(json.getString("type"))?json.getString("type"):"_doc";
        String id=json.getString("id");
        String columns=json.getString("columns");

        LinkedHashMap<String,String> columnMap=JSONObject.parseObject(columns,LinkedHashMap.class);

        if(StringUtils.isNotBlank(index)&&StringUtils.isNotBlank(id)&&StringUtils.isNotBlank(columns)){
            try{
                UpdateRequest updateRequest=new UpdateRequest(index,type,id);
                XContentBuilder builder=XContentFactory.jsonBuilder().startObject();

                for(Map.Entry<String, String> entry : columnMap.entrySet()){
                    String mapKey = entry.getKey();
                    String mapValue = entry.getValue();
                    builder.field(mapKey,mapValue);
                }
                builder.endObject();
                updateRequest.doc(builder);

                UpdateResponse updateResponse=this.client.update(updateRequest).get();
                if(StringUtils.isNotBlank(updateResponse.getResult().toString())){
                    res.put("code","200");
                    res.put("msg","success");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            res.put("msg","索引、id或修改值不可为空！");
        }
        return  res;
    }
    /**
     * showdoc
     * @catalog 接口文档/ElasticSearch
     * @title 10.批量修改document
     * @method post
     * @url 127.0.0.1:9988/batchUpdate
     * @param index 必选 string 索引
     * @param type 非必选 string 类型，默认为_doc
     * @param document 必选 Object 需要修改的document，kjN42HgBHGZEls7J1RTi为document的id，title1、title2为列名，测试1、测试2为document被修改列的值
     * @json_param {"index": "flb_alarm_info_ses","document": {"kjN42HgBHGZEls7J1RTi": {"title1": "测试1","title2": "测试2"},"kzN42HgBHGZEls7J1RTx":{"title3": "测试3","title4": "测试4"}}}
     * @return {"msg":"success","code":"200","data":["kjN42HgBHGZEls7J1RTi","kzN42HgBHGZEls7J1RTx"]}
     * @return_param code string 状态，200：成功
     * @return_param msg string 信息
     * @return_param data Array 被更新document的id
     * @number 10
     */
    @PostMapping("batchUpdate")
    public Map<String,Object> batchUpdate(@RequestBody String param){
        Map<String,Object> res=new HashMap<>();
        JSONObject json = JSONObject.parseObject(param);
        String index=json.getString("index");
        String type=StringUtils.isNotBlank(json.getString("type"))?json.getString("type"):"_doc";
        String document=json.getString("document");
        Map<String,Map<String,String>> documentMap=JSONObject.parseObject(document,Map.class);

        if(StringUtils.isNotBlank(index)&&StringUtils.isNotBlank(document)){
            try{
                List updatedList=new ArrayList();
                for(Map.Entry<String, Map<String,String>> entry : documentMap.entrySet()){
                    String id = entry.getKey();
                    Map<String,String> columnMap = entry.getValue();

                    UpdateRequest updateRequest=new UpdateRequest(index,type,id);
                    XContentBuilder builder=XContentFactory.jsonBuilder().startObject();

                    for(Map.Entry<String, String> columnentry : columnMap.entrySet()){
                        String mapKey = columnentry.getKey();
                        String mapValue = columnentry.getValue();
                        builder.field(mapKey,mapValue);
                    }
                    builder.endObject();
                    updateRequest.doc(builder);
                    UpdateResponse updateResponse=this.client.update(updateRequest).get();

                    if(StringUtils.isNotBlank(updateResponse.getResult().toString())){
                        updatedList.add(updateResponse.getId());
                    }
                }
                res.put("code","200");
                res.put("msg","success");
                res.put("data",updatedList);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            res.put("msg","索引、id或修改值不可为空！");
        }
        return  res;
    }

}
