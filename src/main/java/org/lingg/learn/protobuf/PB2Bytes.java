package org.lingg.learn.protobuf;

import java.util.Arrays;

//官方源码：https://github.com/google/protobuf
//各版本的编译结果下载地址：https://github.com/google/protobuf/releases
//官方介绍：https://developers.google.com/protocol-buffers/

// protobuf的jar版本和exe版本需要对应，否则会报错
// java.lang.UnsupportedOperationException: This is supposed to be overridden by subclasses.

// protobuf 2.6之前的版本，同时为多个proto文件生成java或者c++代码时，是支持通配符的，比如
// protoc.exe --proto_path=custom_msg --cpp_out=build custom_msg/*.proto
// 但现在2.6已经不支持这种写法了，要同时指定多个proto文件名，必须追加文件名：
// protoc.exe --proto_path=custom_msg --cpp_out=build custom_msg/aaa.proto custom_msg/bbb.proto
public class PB2Bytes {
   public static void main(String[] args) throws Exception {
        byte[] bytes = toBytes();
        toPlayer(bytes);

    }
    public static byte[] toBytes(){
        PlayerModule.PBPlayer.Builder builder = PlayerModule.PBPlayer.newBuilder();
        builder.setAge(18).setName("jack").setPlayerId(288L).addSkills(33).addSkills(34);

        PlayerModule.PBPlayer player = builder.build();

        //序列化成字节数组
        byte[] byteArray = player.toByteArray();

        System.out.println(Arrays.toString(byteArray));

        return byteArray;
    }


    /**
     * 反序列化
     * @param bs
     * @throws Exception
     */
    public static void toPlayer(byte[] bs) throws Exception{

        PlayerModule.PBPlayer pbPlayer = PlayerModule.PBPlayer.parseFrom(bs);

        System.out.println("playerId:" + pbPlayer.getPlayerId());
        System.out.println("age:" + pbPlayer.getAge());
        System.out.println("name:" + pbPlayer.getName());
        System.out.println("skills:" + (Arrays.toString(pbPlayer.getSkillsList().toArray())));
    }
}
