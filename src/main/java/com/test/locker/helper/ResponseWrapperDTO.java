package com.test.locker.helper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseWrapperDTO <T>{
    private T data;
    private String status;
    private String messages;
}
