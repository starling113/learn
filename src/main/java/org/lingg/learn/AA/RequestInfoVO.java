package org.lingg.learn.AA;

import lombok.Data;

@Data
public class RequestInfoVO {

    int magic;  //校验用固定值0x0CAFFEE0

    byte version;  //版本号

    byte type;     //类型，请求或者响应

    int sequence;     //序号标记一对请求响应

    int length;  //body长度

    String body;

    public int getLength(){
        return body.length();
    }
}
