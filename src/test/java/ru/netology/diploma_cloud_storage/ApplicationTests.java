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
import ru.netology.diploma_cloud_storage.db.entities.FileId;
import ru.netology.diploma_cloud_storage.db.entities.UserEntity;
import ru.netology.diploma_cloud_storage.domain.*;
import ru.netology.diploma_cloud_storage.repository.CloudRepository;
import ru.netology.diploma_cloud_storage.repository.UserRepository;

import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers(disabledWithoutDocker = true)
class ApplicationTests {

    static final String ROOT_ENTRYPOINT = "";
    static final String DOCKER_IMAGE_NAME = "diploma:latest";
    static final String TEST_INIT_FILENAME = "filename.test";
    static final String TEST_NEW_FILENAME = "newName.test";
    static final String TEST_USERNAME = "test_username";
    static final int PORT = 8081;
    static String rootPath;
    static String token;
    static int counter;
    private long time;
    private final Set<String> files = new HashSet<>();

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CloudRepository cloudRepository;
    public static GenericContainer<?> container =
            new GenericContainer<>(DOCKER_IMAGE_NAME).withExposedPorts(PORT);

    @BeforeAll
    public static void init() {
        System.out.println("START CONTAINER");
        container.start();
        rootPath = "http://localhost:" + container.getMappedPort(PORT) + ROOT_ENTRYPOINT;
        System.out.println("CONTAINER STARTED");
    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("\tTEST #" + (++counter) + " at " + new Date());
        time = System.currentTimeMillis();
    }

    @AfterEach
    public void afterEach() {
        time = System.currentTimeMillis() - time;
        System.out.println("\tTest completed with " + time/1000d + " seconds");
    }

    @Test
    @Order(1)
    void loginTest() {
        configureRequestFactory();
        login(TEST_USERNAME, HttpStatus.OK.value());
    }

    @Test
    @Order(2)
    void fileUploadTest() {
        fileUpload("10000001", true, true, HttpStatus.OK.value());
    }

    @Test
    @Order(3)
    void fileDownloadTest() {
        fileDownload(TEST_INIT_FILENAME, true, HttpStatus.OK.value());
    }

    @Test
    @Order(4)
    void fileRenameTest() {
        fileRename(TEST_INIT_FILENAME, TEST_NEW_FILENAME, true, true, HttpStatus.OK.value());
    }

    @Test
    @Order(5)
    void getListTest() {
        getList(1, true, HttpStatus.OK.value());
    }

    @Test
    @Order(6)
    void fileDeleteTest() {
        fileDelete(TEST_NEW_FILENAME, true, true, HttpStatus.OK.value());
    }

    @Test
    @Order(7)
    void logoutTest() {
        logout(true, true, HttpStatus.OK.value(), false);
    }

    @Test
    @Order(8)
    void loginErrorTest1() {
        login(TEST_USERNAME.substring(0, 2), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(9)
    void fileUploadErrorTest1() {
        fileUpload("10000002", false, false, HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @Order(10)
    void fileUploadErrorTest2() {
        fileUpload("10000002", true, false, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(11)
    void fileDownloadErrorTest1() {
        fileDownload(TEST_INIT_FILENAME, false, HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @Order(12)
    void fileDownloadErrorTest2() {
        fileDownload(TEST_INIT_FILENAME.substring(2), true, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(13)
    void fileRenameErrorTest1() {
        fileRename(TEST_INIT_FILENAME, TEST_NEW_FILENAME.substring(0, 2), false, false, HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @Order(14)
    void fileRenameErrorTest2() {
        fileRename(TEST_INIT_FILENAME, "name/?!", true, false, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(15)
    void fileGetListError1() {
        getList(3, false, HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @Order(16)
    void fileGetListError2() {
        getList(-2, true, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(17)
    void fileGetListError3() {
        getList(300, true, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @Order(18)
    void fileDeleteError1() {
        fileDelete(TEST_INIT_FILENAME, false, false, HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @Order(19)
    void fileDeleteError2() {
        fileDelete("?!/", true, false, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Order(20)
    void fileDeleteError3() {
        fileDelete(TEST_INIT_FILENAME, true, false, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @Order(21)
    void logoutErrorTest1() {
        logout(false, false, HttpStatus.UNAUTHORIZED.value(), true);
    }

    private void login(String username, int status) {
        final String endpoint = "/login";
        final User user = new User(username, username.toUpperCase());
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<User> httpEntity = new HttpEntity<>(user, headers);
        if (status == HttpStatus.OK.value()) {
            final AuthToken body =
                    getResponseBody(HttpMethod.POST, endpoint, httpEntity, false, AuthToken.class, status);
            token = body.getAuthToken();
        } else
            getResponseBody(HttpMethod.POST, endpoint, httpEntity, false, ErrorMessage.class, status);
    }

    private void fileUpload(String fileData,
                            boolean isAuthorized,
                            boolean isResponseMustBeNull,
                            int status) {
        final String filename = TEST_INIT_FILENAME;
        files.add(filename);
        final String endpoint = "/file?filename=" + filename;
        final String fileHash = String.valueOf(Objects.hash(fileData));
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        if (isAuthorized)
            headers.add("auth-token", token);
        final MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("hash", fileHash);
        request.add("file", fileData);
        final HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(request, headers);
        getResponseBody(HttpMethod.POST, endpoint, httpEntity, isResponseMustBeNull, ErrorMessage.class, status);
    }

    private void fileDownload(String filename,
                              boolean isAuthorized,
                              int status) {
        final String endpoint = "/file?filename=" + filename;
        final boolean isResponseMustBeNull = false;
        final HttpHeaders headers = new HttpHeaders();
        if (isAuthorized)
            headers.add("auth-token", token);
        final HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(null, headers);
        final String body =
                getResponseBody(HttpMethod.GET, endpoint, httpEntity, isResponseMustBeNull, String.class, status);
        if (status == HttpStatus.OK.value())
            Assertions.assertTrue(body.contains("hash") && body.contains("file"));
    }

    private void fileRename(String filename,
                            String newFilename,
                            boolean isAuthorized,
                            boolean isResponseMustBeNull,
                            int status) {
        files.add(filename);
        final String endpoint = "/file?filename=" + filename;
        final Name name = new Name(newFilename);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (isAuthorized)
            headers.add("auth-token", token);
        final HttpEntity<Name> httpEntity = new HttpEntity<>(name, headers);
        getResponseBody(HttpMethod.PUT, endpoint, httpEntity, isResponseMustBeNull, ErrorMessage.class, status);
    }

    private void getList(int listSize,
                         boolean isAuthorized,
                         int status) {
        final String endpoint = "/list?limit=" + listSize;
        final HttpHeaders headers = new HttpHeaders();
        if (isAuthorized)
            headers.add("auth-token", token);
        final HttpEntity<Name> httpEntity = new HttpEntity<>(null, headers);
        if (status == HttpStatus.OK.value()) {
            final ParameterizedTypeReference<List<FileSize>> ptr = new ParameterizedTypeReference<List<FileSize>>() {};
            final List<FileSize> body =
                    getResponseBody(HttpMethod.GET, endpoint, httpEntity, false, ptr, status);
            Assertions.assertEquals(body.size(), listSize);
            Assertions.assertTrue(body.toString().contains("size") &&
                    body.toString().contains(TEST_NEW_FILENAME) &&
                    !body.toString().contains(TEST_INIT_FILENAME));
        } else
            getResponseBody(HttpMethod.GET, endpoint, httpEntity, false, ErrorMessage.class, status);
    }

    void fileDelete(String filename,
                    boolean isAuthorized,
                    boolean isResponseMustBeNull,
                    int status) {
        final String endpoint = "/file?filename=" + filename;
        final HttpHeaders headers = new HttpHeaders();
        if (isAuthorized)
            headers.add("auth-token", token);
        final HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        getResponseBody(HttpMethod.DELETE, endpoint, httpEntity, isResponseMustBeNull, ErrorMessage.class, status);
    }

    private void logout(boolean isAuthorized,
                        boolean isResponseMustBeNull,
                        int status,
                        boolean isFinalizedDatabase) {
        final String endpoint = "/logout";
        final HttpHeaders headers = new HttpHeaders();
        if (isAuthorized)
            headers.add("auth-token", token);
        final HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        getResponseBody(HttpMethod.POST, endpoint, httpEntity, isResponseMustBeNull, ErrorMessage.class, status);
        if (isFinalizedDatabase)
            cleanDatabase();
    }

    void configureRequestFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        restTemplate.getRestTemplate().setRequestFactory(requestFactory);
    }

    void cleanDatabase() {
        cloudRepository.flush();
        userRepository.flush();
        if (userRepository.existsByLogin(TEST_USERNAME)) {
            final UserEntity user = userRepository.findByLogin(TEST_USERNAME);
            for (String filename : files) {
                final FileId fileId = new FileId(user, filename);
                if (cloudRepository.existsById(fileId))
                    cloudRepository.deleteById(fileId);
            }
            System.out.println("TRY TO DELETE USER");
            userRepository.deleteById(TEST_USERNAME);
            System.out.println("USER DELETED");
        }
    }

    private <T> T getResponseBody(HttpMethod httpMethod,
                                  String endpoint,
                                  HttpEntity httpEntity,
                                  boolean isResponseMustBeNull,
                                  Class responseClass,
                                  int status) {
        final String path = rootPath + endpoint;
        final ResponseEntity<T> forEntity =
                restTemplate.exchange(path, httpMethod, httpEntity, responseClass);
        test(httpMethod, path, httpEntity, isResponseMustBeNull, forEntity, status);
        return forEntity.getBody();
    }

    private <T> T getResponseBody(HttpMethod httpMethod,
                                  String endpoint,
                                  HttpEntity httpEntity,
                                  boolean isResponseMustBeNull,
                                  ParameterizedTypeReference ptr,
                                  int status) {
        final String path = rootPath + endpoint;
        final ResponseEntity<T> forEntity =
                restTemplate.exchange(path, httpMethod, httpEntity, ptr);
        test(httpMethod, path, httpEntity, isResponseMustBeNull, forEntity, status);
        return forEntity.getBody();
    }

    private <T> void test(HttpMethod httpMethod,
                       String path,
                       HttpEntity httpEntity,
                       boolean isResponseMustBeNull,
                       ResponseEntity<T> forEntity,
                       int status) {
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
        Assertions.assertEquals(statusCodeValue, status);
    }
}
