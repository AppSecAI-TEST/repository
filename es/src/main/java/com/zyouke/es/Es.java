package com.zyouke.es;

import java.net.UnknownHostException;
import java.util.List;

import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyouke.bean.Area;

public class Es {

    private static final String INDEX = "db"; 
    private static final String TYPE = "area";
    
    public static void creatIndexByEs(List<Area> list,ConnectionPool pool) {
	TransportClient client = pool.getTransportClient();
	System.out.println("��ʼ������.....");
	long start = System.currentTimeMillis();
	try {
	    new XContentFactory();// ����ӳ��
	    isExists(client);
	    XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(TYPE)
		    		      .startObject("properties").startObject("id").field("type", "long").field("store", "yes").endObject()
		    		      .startObject("code").field("type", "string").field("store", "yes").endObject()
		    		      .startObject("level").field("type", "long").field("store", "yes").endObject()
		    		      .startObject("value").field("type", "string").field("store", "yes").endObject()
		    		      .startObject("fullName").field("type", "string").field("store", "yes").field("analyzer", "ik").field("search_analyzer", "ik").endObject()
		    		      .endObject()
		    		      .endObject();
	    PutMappingRequest mapping = Requests.putMappingRequest(INDEX).type(TYPE).source(builder);
	    client.admin().indices().putMapping(mapping).actionGet();
	    ObjectMapper mapper = new ObjectMapper();
	    BulkRequestBuilder bulkRequest = client.prepareBulk();
	    for (Area area : list) {
		bulkRequest.add(client.prepareIndex(INDEX, TYPE, area.getId() + "").setSource(mapper.writeValueAsBytes(area)));
	    }
	    // ִ����������request
	    BulkResponse bulkResponse = bulkRequest.get();
	    // �鿴����
	    if (bulkResponse.hasFailures()) {
		for (BulkItemResponse bulkItemResponse : bulkResponse) {
		    System.out.println("��������� ����idΪ : " + bulkItemResponse.getId() + " ��������ϢΪ��" + bulkItemResponse.getFailureMessage());
		}
	    } else {
		System.out.println("���������ɹ�.....����ʱ" + (System.currentTimeMillis() - start) / 1000);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    pool.close(client);
	}

    }

    public static void search(ConnectionPool pool){
	TransportClient client = pool.getTransportClient();
	MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
	SearchResponse searchResponse = client.prepareSearch(INDEX)
	      .setTypes(TYPE)
	      .setQuery(matchAllQuery)
	      .setFrom(0)
	      .setSize(10)
	      .get();
	long totalHits = searchResponse.getHits().getTotalHits();
	SearchHit[] hits = searchResponse.getHits().hits();
	for (SearchHit searchHit : hits) {
	    System.out.println(searchHit.getSourceAsString());
	}
	pool.close(client);
    }
    
    
    
    
    
    /**
     * ɾ�����е�����
     * 
     * @throws UnknownHostException
     */
    public static void deleteIndex(ConnectionPool pool) {
	TransportClient client = pool.getTransportClient();
	try {
	    ClusterStateResponse response = client.admin().cluster().prepareState().execute().actionGet();
	    // ��ȡ��������
	    String[] indexs = response.getState().getMetaData().getConcreteAllIndices();
	    for (String index : indexs) {
		// �������������
		DeleteIndexResponse deleteIndexResponse = client.admin().indices().prepareDelete(index).execute().actionGet();

	    }
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	}finally{
	    pool.close(client);
	}
    }
    
    
    private synchronized static TransportClient isExists(TransportClient client){
	if (!client.admin().indices().prepareExists(INDEX).get().isExists()) {
		client.admin().indices().prepareCreate(INDEX).get();
	    }
	return client;
    }
    
    
    
    
    
    
    
    
}