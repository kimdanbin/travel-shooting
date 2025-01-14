package com.example.travelshooting.product.entity;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.company.entity.Company;
import com.example.travelshooting.file.entity.LeisureFile;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.reservation.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "leisure_company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int quantity;

    @OneToMany(mappedBy = "product")
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeisureFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Part> parts = new ArrayList<>();

    public Product(String name, String description, int price, String address, int quantity, Company company) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.address = address;
        this.quantity = quantity;
        this.company = company;
    }

    public void updateProduct(String description, int price, String address, int quantity) {
        this.description = description;
        this.price = price;
        this.address = address;
        this.quantity = quantity;
    }
}
