package com.example.travelshooting.like;

import com.example.travelshooting.poster.Poster;
import com.example.travelshooting.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likePoster")
@Getter
@NoArgsConstructor
public class LikePoster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "poster_id")
    private Poster poster;

}
