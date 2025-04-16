package hk.com.asl.egms2.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.RefreshResponse;
import hk.com.asl.egms2.model.FinalReportDoc;
import hk.com.asl.egms2.model.ReportWithAttachment;
import hk.com.asl.egms2.util.ResponseUtil;
import hk.com.asl.egms2.util.ResponseWrapper;
import hk.com.asl.egms2.model.ElasticDoc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocController {

    @Value("${elasticsearch.index}")
    private String indexName;

    @Value("${elasticsearch.pipeline}")
    private String pipelineName;

    private final ElasticsearchClient elasticsearchClient;

    public DocController(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<Object>> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("grant_no") String grantNo) {
        String responseMsg;
        ResponseEntity<Object> updatedList;
        try {
            byte[] fileBytes = file.getBytes();
            ReportWithAttachment document = new ReportWithAttachment(grantNo, fileBytes);

            // Create the index request
            IndexRequest<ReportWithAttachment> request = IndexRequest.of(i -> i
                    .index(indexName)
                    .pipeline(pipelineName)
                    .document(document));
            // Send the request
            IndexResponse response = elasticsearchClient.index(request);

            updatedList = listDocuments();

            responseMsg = "Document indexed with ID: " + response.id();

            // Use the custom utility method to return a structured response
            return ResponseUtil.ok(responseMsg, updatedList);
        } catch (IOException e) {
            responseMsg = "Failed to index document: " + e.getMessage();
            updatedList = listDocuments();

            return ResponseUtil.error(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    responseMsg,
                    updatedList);
        }
    }

    // READ
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDocumentById(@PathVariable("id") String id) {

        try {
            GetRequest request = GetRequest.of(g -> g
                    .index(indexName)
                    .id(id));

            GetResponse<FinalReportDoc> response = elasticsearchClient.get(request, FinalReportDoc.class);
            // GetResponse<ObjectNode> response = elasticsearchClient.get(request,
            // ObjectNode.class);

            if (response.found()) {
                return ResponseEntity.ok(response.source());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found with ID: " + id);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch document: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Object> listDocuments() {
        refreshIndex();

        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index(indexName));
            // .size(10));

            SearchResponse<FinalReportDoc> response = elasticsearchClient.search(request,
                    FinalReportDoc.class);

            if (response.hits().total().value() > 0) {
                List<Hit<FinalReportDoc>> hits = response.hits().hits();
                List<ElasticDoc<FinalReportDoc>> documents = new ArrayList<>();
                for (Hit<FinalReportDoc> hit : hits) {
                    documents.add(new ElasticDoc<>(hit.id(), hit.source()));
                }

                return ResponseEntity.ok(documents);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No documents found");
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch documents: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchDocuments(@RequestParam("query") String query) {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index(indexName)
                    .size(10)
                    .query(q -> q
                            .match(m -> m
                                    .field("attachment.content")
                                    .query(query))));

            SearchResponse<FinalReportDoc> response = elasticsearchClient.search(request, FinalReportDoc.class);
            List<Hit<FinalReportDoc>> hits = response.hits().hits();
            List<ElasticDoc<FinalReportDoc>> documents = new ArrayList<>();
            for (Hit<FinalReportDoc> hit : hits) {
                documents.add(new ElasticDoc<>(hit.id(), hit.source()));
            }
            return ResponseEntity.ok(documents);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch documents: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable("id") String id) {
        try {
            GetRequest getRequest = GetRequest.of(g -> g
                    .index(indexName)
                    .id(id));

            GetResponse<FinalReportDoc> getResponse = elasticsearchClient.get(getRequest, FinalReportDoc.class);

            if (getResponse.found()) {
                // Delete the document
                elasticsearchClient.delete(s -> s
                        .index(indexName)
                        .id(id));
                return ResponseEntity.ok("Document deleted with ID: " + id);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found with ID: " + id);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete document: " + e.getMessage());
        }
    }

    public ResponseEntity<?> refreshIndex() {
        try {
            RefreshResponse response = elasticsearchClient.indices().refresh(r -> r.index(indexName));

            if (response.shards() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to retrieve shard information from Elasticsearch.");
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("successful", response.shards().successful());
            responseBody.put("failed", response.shards().failed());
            responseBody.put("total", response.shards().total());

            return ResponseEntity.ok(responseBody);
        } catch (IOException e) {
            // Return an error response with a meaningful message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to refresh index: " + e.getMessage());
        }
    }
}
