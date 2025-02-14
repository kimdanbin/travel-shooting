package com.example.travelshooting.report.entity;

import com.example.travelshooting.common.BaseEntity;
import com.example.travelshooting.enums.DomainType;
import com.example.travelshooting.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "report")
@Getter
@NoArgsConstructor
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    private DomainType type;

    @Column(nullable = false)
    private Long fkId;

    @Column(nullable = false)
    private String reason;

    public Report(User user, DomainType domainType, Long posterId, String reason) {
        this.user = user;
        this.type = domainType;
        this.fkId = posterId;
        this.reason = reason;

    }
}
