package org.chatloop.user_training.model.progress;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.chatloop.user_training.jpa.StringSetConverter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Encapulates the qualifications which users are earning while they progress through courses.
 */
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Entity
@Table(name = "earned_qualifications")
public class EarnedQualifications {
    @Id
    private String userId;

    @Column
    @Convert(converter = StringSetConverter.class)
    private Set<String> qualifications;

    @Override
    public String toString() {
        return new StringJoiner(", ", EarnedQualifications.class.getSimpleName() + "[", "]")
                .add("userId='" + userId + "'")
                .add("qualifications=" + qualifications)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EarnedQualifications that = (EarnedQualifications) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(qualifications, that.qualifications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, qualifications);
    }
}
