package ru.netology.diploma_cloud_storage;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.diploma_cloud_storage.domain.*;
import ru.netology.diploma_cloud_storage.repository.CloudRepository;
import ru.netology.diploma_cloud_storage.repository.UserRepository;

import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers(disabledWithoutDocker = true)
class ApplicationTests {

    private static final String TEST_INIT_FILENAME = "filename.test";
    private static final String TEST_NEW_FILENAME = "newName.test";
    private static final String TEST_USERNAME = "test_username";
    private static final int PORT = 8081;
    private static String rootPath;
    private static String token;
    private static int counter;
    private long time;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CloudRepository cloudRepository;
    public static GenericContainer<?> container =
            new GenericContainer<>("diploma:latest").withExposedPorts(PORT);

    @BeforeAll
    public static void init() {
        System.out.println("START CONTAINER");
        container.start();
        rootPath = "http://localhost:" + container.getMappedPort(PORT) + "/cloud";
        System.out.println("START TESTS");
    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("TEST #" + (++counter) + " at " + new Date());
        time = System.currentTimeMillis();
    }

    @AfterEach
    public void afterEach() {
        time = System.currentTimeMillis() - time;
        System.out.println("Test completed with " + time/1000d + " seconds");
    }

    @Test
    @Order(1)
    void loginTest() {
        final User user = new User(TEST_USERNAME, TEST_USERNAME.toUpperCase());
        final String endpoint = "/login";
        final boolean isResponseMustBeNull = false;
        configureRequestFactory();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<User> httpEntity = new HttpEntity<>(user, headers);
        final AuthToken body =
                getResponseBody(HttpMethod.POST, endpoint, httpEntity, isResponseMustBeNull, AuthToken.class);
        token = body.getAuthToken();
        System.out.println("New auth-token: " + token);
    }

    @Test
    @Order(2)
    void fileWriteTest() {
        final String endpoint = "/file?filename=" + TEST_INIT_FILENAME;
        final boolean isResponseMustBeNull = true;
        final String fileHash = "fileHASH";
        final String fileData = "10000001";
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("auth-token", token);
        final MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("hash", fileHash);
        request.add("file", fileData);
        final HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(request, headers);
        getResponseBody(HttpMethod.POST, endpoint, httpEntity, isResponseMustBeNull, ErrorMessage.class);
    }

    @Test
    @Order(3)
    void fileReadTest() {
        final String endpoint = "/file?filename=" + TEST_INIT_FILENAME;
        final boolean isResponseMustBeNull = false;
        final HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", token);
        final HttpEntity<MultiValueMap<String, String>> httpEntity =
                new HttpEntity<>(null, headers);
        final String body =
                getResponseBody(HttpMethod.GET, endpoint, httpEntity, isResponseMustBeNull, String.class);
        Assertions.assertTrue(body.contains("hash") && body.contains("file"));
    }

    @Test
    @Order(4)
    void fileRenameTest() {
        final String endpoint = "/file?filename=" + TEST_INIT_FILENAME;
        final boolean isResponseMustBeNull = true;
        final Name name = new Name(TEST_NEW_FILENAME);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("auth-token", token);
        final HttpEntity<Name> httpEntity = new HttpEntity<>(name, headers);
        getResponseBody(HttpMethod.PUT, endpoint, httpEntity, isResponseMustBeNull, ErrorMessage.class);
    }

    @Test
    @Order(5)
    void getListTest() {
        final int listSize = 3;
        final String endpoint = "/list?limit=" + listSize;
        final boolean isResponseMustBeNull = false;
        final HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", token);
        final HttpEntity<Name> httpEntity = new HttpEntity<>(null, headers);
        final ParameterizedTypeReference<List<FileSize>> ptr = new ParameterizedTypeReference<List<FileSize>>() {};
        final List<FileSize> body =
                getResponseBody(HttpMethod.GET, endpoint, httpEntity, isResponseMustBeNull, ptr);
        Assertions.assertEquals(body.size(), listSize);
        Assertions.assertTrue(body.toString().contains("size") &&
                body.toString().contains(TEST_NEW_FILENAME) &&
                !body.toString().contains(TEST_INIT_FILENAME));
    }

    @Test
    @Order(6)
    void fileDeleteTest() {
        final String endpoint = "/file?filename=" + TEST_NEW_FILENAME;
        final boolean isResponseMustBeNull = true;
        final HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", token);
        final HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        getResponseBody(HttpMethod.DELETE, endpoint, httpEntity, isResponseMustBeNull, ErrorMessage.class);
    }

    @Test
    @Order(7)
    void logoutTest() {
        final String endpoint = "/logout";
        final boolean isResponseMustBeNull = true;
        final HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", token);
        final HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        getResponseBody(HttpMethod.POST, endpoint, httpEntity, isResponseMustBeNull, ErrorMessage.class);
        cleanDatabase();
    }

    void configureRequestFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        restTemplate.getRestTemplate().setRequestFactory(requestFactory);
    }

    void cleanDatabase() {
        if (userRepository.existsByLogin(TEST_USERNAME))
            userRepository.deleteById(TEST_USERNAME);
        if (cloudRepository.existsById(TEST_INIT_FILENAME))
            cloudRepository.deleteById(TEST_INIT_FILENAME);
        if (cloudRepository.existsById(TEST_NEW_FILENAME))
            cloudRepository.deleteById(TEST_NEW_FILENAME);
    }

    private <T> T getResponseBody(HttpMethod httpMethod,
                                  String endpoint,
                                  HttpEntity httpEntity,
                                  boolean isResponseMustBeNull,
                                  Class responseClass) {
        final String path = rootPath + endpoint;
        final ResponseEntity<T> forEntity =
                restTemplate.exchange(path, httpMethod, httpEntity, responseClass);
        test(httpMethod, path, httpEntity, isResponseMustBeNull, forEntity);
        return forEntity.getBody();
    }

    private <T> T getResponseBody(HttpMethod httpMethod,
                                  String endpoint,
                                  HttpEntity httpEntity,
                                  boolean isResponseMustBeNull,
                                  ParameterizedTypeReference ptr) {
        final String path = rootPath + endpoint;
        final ResponseEntity<T> forEntity =
                restTemplate.exchange(path, httpMethod, httpEntity, ptr);
        test(httpMethod, path, httpEntity, isResponseMustBeNull, forEntity);
        return forEntity.getBody();
    }

    private <T> void test(HttpMethod httpMethod,
                       String path,
                       HttpEntity httpEntity,
                       boolean isResponseMustBeNull, ResponseEntity<T> forEntity) {
        final T body = forEntity.getBody();
        final int statusCodeValue = forEntity.getStatusCodeValue();
        System.out.println(httpMethod + "-request to " + path);
        System.out.println("Request body: " + httpEntity.getBody());
        System.out.println("Request headers: " + httpEntity.getHeaders().entrySet());
        System.out.println("RESPONSE: [" + statusCodeValue + "]\t" + body);
        if (isResponseMustBeNull) {
            Assertions.assertNull(body);
        } else {
            Assertions.assertNotNull(body);
        }
        Assertions.assertEquals(statusCodeValue, HttpStatus.OK.value());
    }
}
