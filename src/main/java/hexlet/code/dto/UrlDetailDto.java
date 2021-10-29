package hexlet.code.dto;

import java.util.List;
import java.util.stream.Collectors;

import hexlet.code.domain.Url;
import hexlet.code.utils.Utils;
import lombok.Getter;

@Getter
public class UrlDetailDto {
    private long id;
    private String name;
    private String createdAt;
    private List<UrlCheckDto> urlChecks;

    public UrlDetailDto(Url url) {
        this.id = url.getId();
        this.name = url.getName();
        final String pattern = "yyyy-MM-dd HH:mm";
        this.createdAt = Utils.formatDate(pattern, url.getCreatedAt());
        this.urlChecks = url.getUrlChecks()
                .stream()
                .map(UrlCheckDto::new)
                .collect(Collectors.toList());
    }
}
