package com.test.locker.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CompleteTransaksiDTO {
    private Integer transaksiId;
    private Integer userId;
    private List<LockerDTO> lockerDTOList;
    private BigDecimal deposit;
    private BigDecimal denda;
    private BigDecimal kurangBayar;
    private String status;
}
