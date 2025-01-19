package com.example.travelshooting.part.entity;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_part")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE part SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Part extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "leisure_product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalTime openAt;

    @Column(nullable = false)
    private LocalTime closeAt;

    @Column(nullable = false)
    private Integer headCount;

    private boolean isDeleted = false;

    @OneToMany(mappedBy = "part")
    private List<Reservation> reservations = new ArrayList<>();

    public Part(LocalTime openAt, LocalTime closeAt,  Integer headCount, Product product) {
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.headCount = headCount;
        this.product = product;
    }

    public void updatePart(LocalTime openAt, LocalTime closeAt,  int headCount) {
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.headCount = headCount;
    }

}
