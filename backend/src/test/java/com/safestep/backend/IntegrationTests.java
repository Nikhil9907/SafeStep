package com.safestep.backend;

import com.safestep.backend.model.Report;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTests {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void testSubmitAndGetReports() {
        String url = getBaseUrl() + "/api/reports";
        
        // Post a report
        Report report = new Report();
        report.setLatitude(28.6139);
        report.setLongitude(77.2090);
        report.setDescription("Unsafe street corner");
        report.setCategory("POOR_VISIBILITY");

        ResponseEntity<Report> postResponse = restTemplate.postForEntity(url, report, Report.class);
        assertEquals(200, postResponse.getStatusCode().value());
        assertNotNull(postResponse.getBody());
        assertNotNull(postResponse.getBody().getId());

        // Get all reports
        ResponseEntity<List> getResponse = restTemplate.getForEntity(url, List.class);
        assertEquals(200, getResponse.getStatusCode().value());
        assertNotNull(getResponse.getBody());
        assertFalse(getResponse.getBody().isEmpty());

        // Get nearby reports
        String nearbyUrl = url + "/nearby?latitude=28.6139&longitude=77.2090&radius=1000";
        ResponseEntity<List> getNearbyResponse = restTemplate.getForEntity(nearbyUrl, List.class);
        assertEquals(200, getNearbyResponse.getStatusCode().value());
        assertNotNull(getNearbyResponse.getBody());
    }

    @Test
    public void testMapDataEndpoints() {
        String baseUrl = getBaseUrl() + "/api/data";

        // CCTV
        ResponseEntity<List> cctvResponse = restTemplate.getForEntity(baseUrl + "/cctv?latitude=28.6139&longitude=77.2090&radius=1000", List.class);
        assertEquals(200, cctvResponse.getStatusCode().value());

        // Lighting
        ResponseEntity<List> lightingResponse = restTemplate.getForEntity(baseUrl + "/lighting?latitude=28.6139&longitude=77.2090&radius=1000", List.class);
        assertEquals(200, lightingResponse.getStatusCode().value());

        // Crime
        ResponseEntity<List> crimeResponse = restTemplate.getForEntity(baseUrl + "/crime?latitude=28.6139&longitude=77.2090&radius=1000", List.class);
        assertEquals(200, crimeResponse.getStatusCode().value());
    }

    @Test
    public void testChatExplainEndpoint() {
        String url = getBaseUrl() + "/api/chat/explain";
        
        Map<String, Object> request = Map.of(
            "dangerScore", 45.0,
            "crimeCount", 2,
            "cctvCount", 3,
            "lightingCount", 4,
            "reportCount", 1
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("explanation"));
    }

    @Test
    public void testRouteScoringEndpoint() {
        String url = getBaseUrl() + "/api/route/safe";

        Map<String, Object> request = Map.of(
            "startLatitude", 28.6139,
            "startLongitude", 77.2090,
            "endLatitude", 28.6200,
            "endLongitude", 77.2150
        );

        ResponseEntity<List> response = restTemplate.postForEntity(url, request, List.class);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        
        Map firstRoute = (Map) response.getBody().get(0);
        assertTrue(firstRoute.containsKey("coordinates"));
        assertTrue(firstRoute.containsKey("dangerScore"));
        assertTrue(firstRoute.containsKey("dangerLevel"));
        assertTrue(firstRoute.containsKey("explanation"));
    }
}
