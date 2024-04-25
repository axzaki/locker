package com.test.locker.repository;

import com.test.locker.model.entity.Locker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LockerRepository extends JpaRepository<Locker,String> {
    List<Locker> findByStatus(@Param("status") String status);
    List<Locker> findByNoLokerInAndStatus(List<Integer> idList, String status);
    Locker findByNoLokerAndStatus(Integer noLoker, String status);

    @Query("SELECT l FROM Locker l JOIN l.transaksiList t WHERE t.id = :trxId AND l.status = :status")
    List<Locker> findByTransaksiIdAndStatus(Integer trxId, String status);
}
