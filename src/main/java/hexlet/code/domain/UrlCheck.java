package hexlet.code.domain;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import io.ebean.Model;
import io.ebean.annotation.Identity;
import io.ebean.annotation.IdentityGenerated;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class UrlCheck extends Model {
    @Id
    @Identity(generated = IdentityGenerated.BY_DEFAULT)
    private long id;

    @NotNull
    private final int statusCode;
    private final String title;
    private final String h1;

    @Lob
    private final String description;

    @ManyToOne
    @NotNull
    private final Url url;

    @WhenCreated
    private Instant createdAt;
}
