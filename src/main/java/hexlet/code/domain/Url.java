package hexlet.code.domain;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.ebean.Model;
import io.ebean.annotation.Identity;
import io.ebean.annotation.IdentityGenerated;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
public class Url extends Model {
    @Id
    @Identity(generated = IdentityGenerated.BY_DEFAULT)
    private long id;

    private String name;

    @WhenCreated
    private Instant createdAt;
}
