package com.example.travelshooting.file;

import com.example.travelshooting.poster.Poster;
import com.example.travelshooting.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file")
@Getter
@NoArgsConstructor
public class File {

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

    private String url;

    @JoinColumn(name = "file_type")
    private String fileType;

}
