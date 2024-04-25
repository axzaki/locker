package com.test.locker.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "T1_LOCKER")
@Where(clause = "IS_DELETED = 'N' ")
@Data
public class Locker {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "NO_LOCKER", unique = true)
    private int noLoker;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "DATE_PINJAM")
    private Date datePinjam;
    @Column(name = "DATE_KEMBALI")
    private Date dateKembali;
    @Column(name = "USAGE_COUNT_PASSWORD")
    private Integer usageCountPassword;
    @Column(name = "WRONG_COUNT_PASSWORD")
    private Integer wrongCountPassword;
    @Column(name = "IS_PASSWORD_LOCKED", length = 1)
    private String isPasswordLocked;
    @Column(name = "IS_DELETED", length = 1)
    private String isDeleted;

    @ManyToMany(mappedBy = "lockerList")
    private List<Transaksi> transaksiList;
}
