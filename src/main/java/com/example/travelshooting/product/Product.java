package com.example.travelshooting.product;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.company.Company;
import com.example.travelshooting.file.File;
import com.example.travelshooting.poster.Poster;
import com.example.travelshooting.reservation.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leisure_product")
@Getter
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "leisure_company_id")
    private Company company;

    private String name;

    private String description;

    private int price;

    private LocalTime openAt;

    private LocalTime closeAt;

    private String address;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Poster> posters = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

}
