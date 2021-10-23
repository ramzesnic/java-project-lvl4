package hexlet.code.services;

import java.util.List;
import java.util.stream.Collectors;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import hexlet.code.dto.UrlDto;
import hexlet.code.exceptions.DuplicateUrlException;
import io.ebean.DuplicateKeyException;
import io.ebean.PagedList;

public final class UrlService {
    public static Url create(String urlData) throws DuplicateUrlException {
        try {
            final Url url = new Url(urlData);
            url.save();
            return url;
        } catch (DuplicateKeyException exception) {
            throw new DuplicateUrlException("Страница уже существует");
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

    public static UrlDto get(long id) {
        final Url url = new QUrl().id.equalTo(id)
                .findOne();
        return new UrlDto(url);
    }
}
