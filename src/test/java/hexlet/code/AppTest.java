package hexlet.code;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.DB;
import io.ebean.Transaction;
import io.javalin.Javalin;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;

import hexlet.code.utils.Utils;

class AppTest {
    private static final String SEED_SITE = "https://hexlet.io";
    private static final String FIXTURE_PATH = "src/test/resources/";
    private static Javalin app;
    private static String baseUrl;
    private static Url url;
    private static Transaction transaction;
    private static String fixture;
    private static MockWebServer mockServer;

    @BeforeAll
    public static void beforeAll() throws IOException {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        fixture = Utils.getFileContent(FIXTURE_PATH + "fixture.html");

        url = new Url(SEED_SITE);
        url.save();
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
        mockServer = new MockWebServer();
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void afterEach() throws IOException {
        mockServer.shutdown();
        transaction.rollback();
    }

    @Test
    void testIndex() {
        HttpResponse<String> response = Unirest.get(baseUrl).asString();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Анализатор страниц");
    }

    @Test
    void testGetList() {
        HttpResponse<String> response = Unirest.get(baseUrl + "/urls").asString();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(SEED_SITE);
    }

    @Test
    void testGetUrl() {
        HttpResponse<String> response = Unirest.get(baseUrl + "/urls/" + url.getId()).asString();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(SEED_SITE);
    }

    @Test
    void testAddUrl() {
        String newSite = "http://site.com";
        HttpResponse<?> responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", newSite)
                .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

        HttpResponse<String> responseGet = Unirest
                .get(baseUrl + "/urls")
                .asString();

        assertThat(responseGet.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(responseGet.getBody()).contains("Страница успешно добавлена");

        Url testurl = new QUrl().name.equalTo(newSite)
                .findOne();

        assertThat(testurl).isNotNull();
        assertThat(testurl.getName()).isEqualTo(newSite);
    }

    @Test
    void testAddDuplicateUrl() {
        Unirest
                .post(baseUrl + "/urls")
                .field("url", SEED_SITE)
                .asEmpty();

        HttpResponse<String> responseGet = Unirest
                .get(baseUrl + "/urls")
                .asString();

        assertThat(responseGet.getBody()).contains("Страница уже существует");

    }

    @Test
    void testAddIncorrectUrl() {
        Unirest
                .post(baseUrl + "/urls")
                .field("url", "fignya")
                .asEmpty();

        HttpResponse<String> responseGet = Unirest
                .get(baseUrl + "/urls")
                .asString();

        assertThat(responseGet.getBody()).contains("Некорректный URL");

    }

    @Test
    void testCheckUrl() {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setBody(fixture);
        mockServer.enqueue(mockResponse);

        String urlName = mockServer.url("/").toString();

        Unirest
                .post(baseUrl + "/urls")
                .field("url", urlName)
                .asEmpty();
        Url newUrl = new QUrl().name.equalTo(urlName.substring(0, urlName.length() - 1)).findOne();

        Unirest
                .post(baseUrl + "/urls/" + newUrl.getId() + "/checks")
                .asEmpty();

        HttpResponse<String> getResponse = Unirest
                .get(baseUrl + "/urls/" + newUrl.getId())
                .asString();

        assertThat(getResponse.getStatus()).isEqualTo(HttpStatus.OK);
        String body = getResponse.getBody();
        assertThat(body).contains("Страница успешно проверена");
        assertThat(body).contains("testTitle");
        assertThat(body).contains("testDescription");
        assertThat(body).contains("testH1");
    }
}
