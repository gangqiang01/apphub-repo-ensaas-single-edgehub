package com.m2m.management.former;

import com.m2m.management.former.MsgFormer;
public class Response {
    /**
     * success
     * @param object
     * @return
     */
    public static MsgFormer success(Object object){
        MsgFormer msg=new MsgFormer();
        msg.setStatus("success");
        msg.setData(object);
        return msg;
    }

    public static MsgFormer success(Object object, long count){
        MsgFormer msg=new MsgFormer();
        msg.setStatus("success");
        msg.setData(object);
        msg.setCount(count);
        return msg;
    }
    public static MsgFormer success(){
        return success(null);
    }

    public static MsgFormer error(String resultmsg){
        MsgFormer msg=new MsgFormer();
        msg.setStatus("error");
        msg.setData(resultmsg);
        return msg;
    }


}
