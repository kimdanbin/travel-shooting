package com.example.travelshooting.poster;

import com.example.travelshooting.comment.Comment;
import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.file.File;
import com.example.travelshooting.like.LikePoster;
import com.example.travelshooting.product.Product;
import com.example.travelshooting.restaurant.Restaurant;
import com.example.travelshooting.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poster")
@Getter
@NoArgsConstructor
public class Poster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "leisure_product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private String title;

    private String content;

    private LocalDateTime travelStartAt;

    private LocalDateTime travelEndAt;

    private boolean isDeleted;

    @OneToMany(mappedBy = "poster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikePoster> likePosters = new ArrayList<>();

    @OneToMany(mappedBy = "poster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "poster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

}
