package nablarch.common.web.token;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "DOUBLE_SUBMISSION")
public class Token {
    public Token() {
    }

    public Token(String value, Timestamp createdAt) {
        this.value = value;
        this.createdAt = createdAt;
    }

    @Id
    @Column(name = "TOKEN", nullable = false)
    public String value;

    @Column(name = "CREATED_AT")
    public Timestamp createdAt;
}
