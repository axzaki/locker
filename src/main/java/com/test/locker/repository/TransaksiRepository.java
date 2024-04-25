package com.test.locker.repository;

import com.test.locker.model.entity.Locker;
import com.test.locker.model.entity.Transaksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransaksiRepository extends JpaRepository<Transaksi,String> {
    @Query("SELECT t FROM Transaksi t JOIN t.lockerList l WHERE l.noLoker = :noLoker")
    Transaksi findFirstByLockerNoLoker(Integer noLoker);

    Transaksi findById(Integer id);
}
