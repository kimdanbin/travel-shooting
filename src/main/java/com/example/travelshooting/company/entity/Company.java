package com.example.travelshooting.company.entity;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leisure_company")
@Getter
@NoArgsConstructor
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private String description;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    public Company(User user, String name, String description) {
        this.user = user;
        this.name = name;
        this.description = description;
    }

    public void updateCompany(String description) {
        this.description = description;
    }

}
