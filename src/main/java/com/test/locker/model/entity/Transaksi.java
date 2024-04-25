package com.test.locker.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "T3_TRANSACTION")
@Where(clause = "IS_DELETED = 'N' ")
@Data
public class Transaksi {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_TRANSAKSI", unique = true)
    private int id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToMany
    @JoinTable(name = "T2_TRX_LOCKER",
            joinColumns = @JoinColumn(name = "ID_TRANSAKSI"),
            inverseJoinColumns = @JoinColumn(name = "NO_LOCKER"))
    private List<Locker> lockerList;

    @Column(name = "DENDA")
    private BigDecimal denda;

    @Column(name = "KURANG_BAYAR")
    private BigDecimal kurangBayar;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "IS_DELETED", length = 1)
    private String isDeleted;
}
