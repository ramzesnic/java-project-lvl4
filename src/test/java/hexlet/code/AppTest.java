package hexlet.code;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.DB;
import io.ebean.Transaction;
import io.javalin.Javalin;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;

class AppTest {
    private static Javalin app;
    private static String baseUrl;
    private static Url url;
    private static Transaction transaction;
    private static String seedSite = "https://hexlet.io";

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;

        url = new Url(seedSite);
        url.save();
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void afterEach() {
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
        assertThat(response.getBody()).contains(seedSite);
    }

    @Test
    void testGetUrl() {
        HttpResponse<String> response = Unirest.get(baseUrl + "/urls/" + url.getId()).asString();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(seedSite);
    }

    @Test
    void testAddUrl() {
        String newSite = "http://site.com";
        HttpResponse<String> responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", newSite)
                .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

        HttpResponse<String> responseGet = Unirest
                .get(baseUrl + "/urls")
                .asString();

        assertThat(responseGet.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(responseGet.getBody()).contains("Страница успашно добавлена");

        Url testurl = new QUrl().name.equalTo(newSite)
                .findOne();

        assertThat(testurl).isNotNull();
        assertThat(testurl.getName()).isEqualTo(newSite);
    }
}
