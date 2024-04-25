package com.test.locker.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Entity
@Table(name = "T1_USER")
@Where(clause = "IS_DELETED = 'N' ")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID", unique = true)
    private int id;
    @Column(name = "PHONE_NO")
    private String phoneNumber;
    @Column(name = "ID_CARD")
    private String idCard;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "DEPOSIT")
    private BigDecimal deposit;
    @Column(name = "IS_DELETED", length = 1)
    private String isDeleted;
}
