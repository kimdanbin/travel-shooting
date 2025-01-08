package com.example.travelshooting.comment;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.poster.Poster;
import com.example.travelshooting.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE comment SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "poster_id")
    private Poster poster;

    @Column(nullable = false)
    private String content;

    private boolean isDeleted = false;

    public Comment(User user, Poster poster, String content) {
        this.user = user;
        this.poster = poster;
        this.content = content;
    }

    public void updateComment(String content){
        this.content = content;
    }
}
