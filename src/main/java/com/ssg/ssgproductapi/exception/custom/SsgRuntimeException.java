package com.ssg.ssgproductapi.exception.custom;

//BaseException
public abstract class SsgRuntimeException extends RuntimeException{
    public SsgRuntimeException(String msg){
        super(msg);
    }
}
