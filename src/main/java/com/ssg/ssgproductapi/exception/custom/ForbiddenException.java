package com.ssg.ssgproductapi.exception.custom;

public class ForbiddenException extends SsgRuntimeException {
    public ForbiddenException(String msg){
        super(msg);
    }

}
