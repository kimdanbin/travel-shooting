package com.example.travelshooting.file.entity;

import com.example.travelshooting.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leisure_file")
@Getter
@NoArgsConstructor
public class LeisureFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "leisure_product_id")
    private Product product;

    @JoinColumn(name = "file_name")
    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String url;

    @JoinColumn(name = "file_type")
    private String fileType;

    // 임시
    public LeisureFile(String fileName, String url, String fileType) {
        this.fileName = fileName;
        this.url = url;
        this.fileType = fileType;
    }

    public LeisureFile(Product product, String fileName, String url, String fileType) {
        this.product = product;
        this.fileName = fileName;
        this.url = url;
        this.fileType = fileType;
    }

}
