package hexlet.code.domain;

import java.time.Instant;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import io.ebean.Model;
import io.ebean.annotation.Identity;
import io.ebean.annotation.IdentityGenerated;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
public final class Url extends Model {
    @Id
    @Identity(generated = IdentityGenerated.BY_DEFAULT)
    private long id;

    @Column(unique = true)
    @NotNull
    private final String name;

    @OneToMany(cascade = CascadeType.ALL)
    @OrderBy("createdAt desc")
    private List<UrlCheck> urlChecks;

    @WhenCreated
    private Instant createdAt;

    public void addCheck(UrlCheck check) {
        urlChecks.add(check);
    }
}
