package com.example.travelshooting.product.entity;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.company.entity.Company;
import com.example.travelshooting.file.entity.LeisureFile;
import com.example.travelshooting.part.entity.Part;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leisure_product")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE leisure_product SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "leisure_company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, length = 200)
    private String address;

//    @Column(nullable = false)
//    private Integer quantity;

    @Column(nullable = false)
    private LocalDate saleStartAt;

    @Column(nullable = false)
    private LocalDate saleEndAt;

    private boolean isDeleted = false;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeisureFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Part> parts = new ArrayList<>();

    public Product(String name, String description, Integer price, String address, LocalDate saleStartAt, LocalDate saleEndAt, Company company) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.address = address;
        this.saleStartAt = saleStartAt;
        this.saleEndAt = saleEndAt;
        this.company = company;
    }

    public void updateProduct(String description, Integer price, String address, LocalDate saleStartAt, LocalDate saleEndAt) {
        this.description = description;
        this.price = price;
        this.address = address;
        this.saleStartAt = saleStartAt;
        this.saleEndAt = saleEndAt;
    }
}
