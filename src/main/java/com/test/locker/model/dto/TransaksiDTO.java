package com.test.locker.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class TransaksiDTO {
    private Integer userId;
    private List<Integer> lockerNo;
    private BigDecimal deposit;
}
