package ru.netology.diploma_cloud_storage;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.impl.classic.HttpClients;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.message.BasicHeader;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.diploma_cloud_storage.domain.AuthToken;
import ru.netology.diploma_cloud_storage.domain.CloudFile;
import ru.netology.diploma_cloud_storage.domain.User;

import javax.accessibility.AccessibleStateSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers(disabledWithoutDocker = true)
class ApplicationTests {

//    @Value("${server.port}")
    private static int PORT = 8081;
    private String token;
    private static int counter = 0;

    @Autowired
    private TestRestTemplate restTemplate;
    public static GenericContainer<?> container =
            new GenericContainer<>("diploma:latest").withExposedPorts(PORT);

    @BeforeAll
    public static void init() {
        System.out.println("START CONTAINER");
        container.start();
        System.out.println("COUNTER = " + (++counter));
        System.out.println("START TESTS");
    }

    @Test
    @Order(1)
    void loginTest() {
        System.out.println("COUNTER = " + (++counter));
        final String username = "test_username";
        final User user = new User(username, username.toUpperCase());
//        ResponseEntity<Object> forEntity = test(HttpMethod.POST, user, "/login");
        final String path = "http://localhost:" +
                container.getMappedPort(PORT) + "/cloud" + "/login";
//        final HttpHeaders headers = new HttpHeaders();
//        headers.add("auth-token", token);
        ResponseEntity<AuthToken> forEntity =
                restTemplate.postForEntity(path, user, AuthToken.class);
        System.out.println(HttpMethod.POST + "-request to " + path + " at " + new Date());
        Assertions.assertNotNull(forEntity.getBody());
        token = forEntity.getBody().getAuthToken();
        System.out.println("SUCCESS: " + token);
        Assertions.assertNotNull(token);
        Assertions.assertEquals(forEntity.getStatusCodeValue(), HttpStatus.OK.value());
    }

    @Test
    @Order(2)
    void fileWriteTest() {
        System.out.println("COUNTER = " + (++counter));
        final String fileName = "fileName.test";
        final String fileHash = "fileHASH";
        final String fileData = "10000001";
//        final String request = "{\r\n" +
//                                "\t\"hash\": \"" + fileHash + "\",\r\n" +
//                                "\t\"file\": \"" + fileData + "\"\r\n" +
//                                "}";
        final CloudFile request = new CloudFile(fileHash, fileData);
//        Object obj = test(HttpMethod.POST, request, "/file?filename=" + fileName);
//        System.out.println("TOKEN: " + ((AuthToken) obj).getAuthToken());
        final String path = "http://localhost:" +
                container.getMappedPort(PORT) + "/cloud" + "/file?filename=" + fileName;
//        HttpRequest req = new MockClientHttpRequest(HttpMethod.POST, path);
//        restTemplate.getRestTemplate().setInterceptors(
//                Collections.singletonList((req, body, execution) -> {
//                    req.getHeaders()
//                            .add("auth-token", token);
//                    return execution.execute(req, body);
//                }));

        final HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", token);
        HttpEntity<CloudFile> httpEntity = new HttpEntity<>(request, headers);
        ResponseEntity<String> forEntity = null;
        System.out.println(HttpMethod.POST + "-request to " + path + " at " + new Date());
//        forEntity = restTemplate.exchange(path, HttpMethod.POST,
//                new HttpEntity<>(headers), String.class);
        forEntity = restTemplate.postForEntity(path, request, String.class);
        System.out.println("RESPONSE: " + forEntity.getBody());

        Assertions.assertEquals(forEntity.getStatusCodeValue(), HttpStatus.OK.value());
    }

    @Test
    void fileRenameTest() {
        System.out.println("COUNTER = " + (++counter));
        final String newName = "newName.test";
//        test(HttpMethod.PUT, null, "/file?name=" + newName);
        Assertions.assertTrue(false);
    }

    @Test
    void fileReadTest() {
//        test(HttpMethod.GET, null, "?limit=3");
        Assertions.assertTrue(false);
    }

    @Test
    void getListTest() {
//        test(HttpMethod.GET, null, "/list?limit=3");
        Assertions.assertTrue(false);
    }

    @Test
    void logoutTest() {
//        test(HttpMethod.GET, null, "?limit=3");
        Assertions.assertTrue(false);
    }

    @Test
    void fileDeleteTest() {
//        test(HttpMethod.GET, null, "?limit=3");
        Assertions.assertTrue(false);
    }

    private ResponseEntity<Object> test(HttpMethod httpMethod,
                                        Object request,
                                        String endpoint) {
        final String path = "http://localhost:" +
                container.getMappedPort(PORT) + "/cloud" + endpoint;
        final HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", token);
        ResponseEntity<Object> forEntity = null;
        System.out.println(httpMethod + "-request to " + path);
//        forEntity = restTemplate.exchange(path, httpMethod,
//                new HttpEntity<>(headers), String.class);
        switch (httpMethod) {
            case GET:
                forEntity = restTemplate.getForEntity(path, Object.class);
                break;
            case DELETE:
                restTemplate.delete(path);
                break;
            case POST:
                forEntity = restTemplate.postForEntity(path, request, Object.class);
                break;
            case PUT:
                restTemplate.put(path, request);
                break;
        }
        System.out.println("Request: " + request);
        final Object response = forEntity.getBody();
        System.out.println("Response: " + response);
        return forEntity;
//        Assertions.assertEquals(response, message);
    }
}
