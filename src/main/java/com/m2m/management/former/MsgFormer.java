package com.m2m.management.former;

import lombok.Data;

@Data
public class MsgFormer<T> {

    /*提示信息 */
    private String status;

    /*具体内容*/
    private  T data;

//    all list count
    private long count =-1;

}
