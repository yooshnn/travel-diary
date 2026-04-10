package com.td.traveldiary.domain.region.exception;

import com.td.traveldiary.global.exception.BusinessException;
import com.td.traveldiary.global.exception.ErrorCode;

public class SidoNotFoundException extends BusinessException {
    public SidoNotFoundException() {
        super(ErrorCode.SIDO_NOT_FOUND);
    }
}
