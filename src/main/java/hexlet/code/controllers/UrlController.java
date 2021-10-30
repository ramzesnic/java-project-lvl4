package hexlet.code.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import hexlet.code.dto.UrlDetailDto;
import hexlet.code.dto.UrlDto;
import hexlet.code.exceptions.IncorrectUrlException;
import hexlet.code.services.UrlService;
import io.javalin.http.Context;

public final class UrlController {
    private static final String URL_WRONG_MSG = "Некорректный URL";
    private static final String URL_SUCCESS_MSG = "Страница успешно добавлена";
    private static final String URL_CHECKED_MSG = "Страница успешно проверена";

    private static String getUrlData(String urlSpec) throws IncorrectUrlException {
        try {
            final URL url = new URL(urlSpec);
            final String protocol = url.getProtocol();
            final String host = url.getHost();
            final Integer port = url.getPort();

            return port.equals(-1)
                    ? String.format("%s://%s", protocol, host)
                    : String.format("%s://%s:%d", protocol, host, port);
        } catch (MalformedURLException e) {
            throw new IncorrectUrlException(URL_WRONG_MSG);
        }
    }

    public static void create(Context ctx) {
        final String urlSpec = ctx.formParam("url");

        try {
            final String urlData = getUrlData(urlSpec);
            UrlService.create(urlData);
            ctx.sessionAttribute("flash", URL_SUCCESS_MSG);
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
        }
        ctx.redirect("/urls");
    }

    public static void getUrls(Context ctx) {
        final int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        final int rowsPerPage = 10;
        final int offset = (page - 1) * rowsPerPage;

        final List<UrlDto> urls = UrlService.getList(offset, rowsPerPage);

        ctx.attribute("page", page);
        ctx.attribute("urls", urls);
        ctx.render("urls/index.html");
    }

    public static void getUrl(Context ctx) {
        final long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        final UrlDetailDto url = UrlService.get(id);

        ctx.attribute("url", url);
        ctx.render("urls/show.html");
    }

    public static void checkUrl(Context ctx) throws Exception {
        final long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        final UrlDetailDto url = UrlService.check(id);

        ctx.sessionAttribute("flash", URL_CHECKED_MSG);
        ctx.attribute("url", url);
        ctx.redirect("/urls/" + id);
    }
}
