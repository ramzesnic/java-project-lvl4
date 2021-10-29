package hexlet.code.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.dto.UrlDetailDto;
import hexlet.code.dto.UrlDto;
import hexlet.code.exceptions.DuplicateUrlException;
import io.ebean.DuplicateKeyException;
import io.ebean.PagedList;

public final class UrlService {
    private static final String PAGE_EXIST_MSG = "Страница уже существует";
    private static final String DOCUMENT_FIELD = "document";
    private static final String STATUS_CODE_FIELD = "statusCode";

    public static Url create(String urlData) throws DuplicateUrlException {
        try {
            final Url url = new Url(urlData);
            url.save();
            return url;
        } catch (DuplicateKeyException exception) {
            throw new DuplicateUrlException(PAGE_EXIST_MSG);
        }
    }

    public static List<UrlDto> getList(int first, int max) {
        final PagedList<Url> pagedUrls = new QUrl()
                .setFirstRow(first)
                .setMaxRows(max)
                .orderBy().id.asc()
                        .findPagedList();

        return pagedUrls.getList()
                .stream()
                .map(UrlDto::new)
                .collect(Collectors.toList());
    }

    public static UrlDetailDto get(long id) {
        final Url url = new QUrl().id.equalTo(id)
                .findOne();
        return new UrlDetailDto(url);
    }

    private static UrlCheck makeUrlCheck(Map<String, ? extends Object> data, Url url) {
        final Document doc = (Document) data.get(DOCUMENT_FIELD);
        final int statusCode = (int) data.get(STATUS_CODE_FIELD);
        if (doc == null) {
            return new UrlCheck(statusCode, null, null, null, url);
        }
        final Element titleElement = doc.selectFirst("head > title");
        final String title = titleElement != null ? titleElement.text() : null;
        final Element descriptionElement = doc.selectFirst("meta[name='description']");
        final String description = descriptionElement != null ? descriptionElement.attr("content") : null;
        final Element h1Element = doc.selectFirst("body > h1");
        final String h1 = h1Element != null ? h1Element.text() : null;

        return new UrlCheck(statusCode, title, h1, description, url);
    }

    private static CompletableFuture<UrlCheck> runCheck(Url url) {
        final CompletableFuture<UrlCheck> checkTask = CompletableFuture.supplyAsync(() -> {
            try {
                final Response response = Jsoup.connect(url.getName()).execute();
                final int statusCode = response.statusCode();
                final Document doc = response.parse();
                return Map.of(STATUS_CODE_FIELD, statusCode, DOCUMENT_FIELD, doc);
            } catch (HttpStatusException e) {
                return Map.of(STATUS_CODE_FIELD, e.getStatusCode(), DOCUMENT_FIELD, null);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        })
                .thenApply((data) -> makeUrlCheck(data, url));

        return checkTask;
    }

    public static UrlDetailDto check(long id) throws Exception {
        final Url url = new QUrl().id.equalTo(id)
                .findOne();
        final UrlCheck newCheck = Optional.ofNullable(runCheck(url).get())
                .orElseThrow();

        url.addCheck(newCheck);
        url.save();

        return new UrlDetailDto(url);
    }
}
