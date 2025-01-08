package com.example.travelshooting.comment;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.poster.Poster;
import com.example.travelshooting.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor
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

    private String content;

    private boolean isDeleted;

    public Comment(User user, Poster poster, String content) {
        this.user = user;
        this.poster = poster;
        this.content = content;
    }

    public void updateComment(String content){
        this.content = content;

    }

}
