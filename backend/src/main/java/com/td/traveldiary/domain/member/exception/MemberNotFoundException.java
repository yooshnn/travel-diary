package com.td.traveldiary.domain.member.exception;

import com.td.traveldiary.global.exception.BusinessException;
import com.td.traveldiary.global.exception.ErrorCode;

public class MemberNotFoundException extends BusinessException {
    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
