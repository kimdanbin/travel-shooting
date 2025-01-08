package com.example.travelshooting.file.entity;

import com.example.travelshooting.poster.Poster;
import com.example.travelshooting.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "poster_file")
@Getter
@NoArgsConstructor
public class PosterFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "poster_id")
    private Poster poster;

    @ManyToOne
    @JoinColumn(name = "leisure_product_id")
    private Product product;

    @JoinColumn(name = "file_name")
    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String url;

    @JoinColumn(name = "file_type")
    private String fileType;

    public PosterFile(String fileName, String url, String fileType) {
        this.fileName = fileName;
        this.url = url;
        this.fileType = fileType;
    }

}
