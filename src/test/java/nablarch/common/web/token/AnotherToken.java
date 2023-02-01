package nablarch.common.web.token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "DB_TOKEN")
public class AnotherToken {
    public AnotherToken() {
    }

    public AnotherToken(String value, Timestamp createdAt) {
        this.value = value;
        this.createdAt = createdAt;
    }

    @Id
    @Column(name = "VALUE_COL", nullable = false)
    public String value;

    @Column(name = "CREATED_AT_COL")
    public Timestamp createdAt;
}
