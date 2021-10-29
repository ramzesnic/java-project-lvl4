package hexlet.code.dto;

import hexlet.code.domain.UrlCheck;
import hexlet.code.utils.Utils;
import lombok.Getter;

@Getter
public class UrlCheckDto {
    private long id;
    private int statusCode;
    private String title;
    private String description;
    private String h1;
    private String createdAt;

    public UrlCheckDto(UrlCheck urlCheck) {
        this.id = urlCheck.getId();
        this.statusCode = urlCheck.getStatusCode();
        this.title = urlCheck.getTitle();
        this.description = urlCheck.getDescription();
        this.h1 = urlCheck.getH1();

        final String pattern = "yyyy-MM-dd HH:mm";
        this.createdAt = Utils.formatDate(pattern, urlCheck.getCreatedAt());
    }
}
