package com.ssg.ssgproductapi.exception.custom;

// 동아리를 이미 가입했거나 중복값 예외
public class AlreadyExistsException extends SsgRuntimeException {
    public AlreadyExistsException(String msg){super(msg);}
}
