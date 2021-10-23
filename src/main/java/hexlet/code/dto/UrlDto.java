package hexlet.code.dto;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import hexlet.code.domain.Url;
import lombok.Getter;

@Getter
public class UrlDto {
    private long id;
    private String name;
    private String createdAt;

    public UrlDto(Url url) {
        this.id = url.getId();
        this.name = url.getName();
        final String pattern = "yyyy-MM-dd HH:mm";
        this.createdAt = DateTimeFormatter.ofPattern(pattern)
                .withZone(ZoneId.systemDefault())
                .format(url.getCreatedAt());
    }
}
