package hexlet.code.dto;

import java.util.Optional;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.utils.Utils;
import lombok.Getter;

@Getter
public class UrlDto {
    private long id;
    private String name;
    private String lastCheck;
    private Integer lastCheckCode;

    public UrlDto(Url url) {
        this.id = url.getId();
        this.name = url.getName();
        final String pattern = "yyyy-MM-dd HH:mm";
        final Optional<UrlCheck> lastUrlCheck = url.getUrlChecks()
                .stream()
                .findFirst();
        this.lastCheck = lastUrlCheck
                .map(lc -> Utils.formatDate(pattern, lc.getCreatedAt()))
                .orElse(null);
        this.lastCheckCode = lastUrlCheck
                .map(lu -> lu.getStatusCode())
                .orElse(null);
    }
}
