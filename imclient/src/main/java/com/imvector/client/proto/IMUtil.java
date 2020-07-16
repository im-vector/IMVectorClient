package com.imvector.client.proto;

import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.ProtocolMessageEnum;
import com.imvector.proto.impl.IMPacket;
import com.imvector.proto.Packet;

/**
 * @author: vector.huang
 * @date: 2019/05/25 11:52
 */
public final class IMUtil {

    /**
     * 创建新的Header，没有 Version 和 Seq
     *
     * @param serviceId        服务Id
     * @param commandId        命令Id
     * @param messageOrBuilder 消息体
     * @return 新的Header
     */
    public static IMPacket newPacket(Packet.ServiceId serviceId,
                                     ProtocolMessageEnum commandId,
                                     MessageOrBuilder messageOrBuilder) {

        byte[] bytes = {};
        if (messageOrBuilder instanceof Message) {
            bytes = ((Message) messageOrBuilder).toByteArray();
        } else if (messageOrBuilder instanceof Message.Builder) {
            bytes = ((Message.Builder) messageOrBuilder).build().toByteArray();
        }

        IMPacket msg = new IMPacket((short) serviceId.getNumber(),
                (short) commandId.getNumber(), bytes);
        return msg;
    }

    /**
     * 复制成新的Header，同时复制Version 和 Seq
     *
     * @param srcHeader        从这里复制
     * @param messageOrBuilder 消息体
     * @return 新的Header
     */
    public static IMPacket copyHeader(IMPacket srcHeader,
                                      MessageOrBuilder messageOrBuilder) {

        Message message;
        if (messageOrBuilder instanceof Message) {
            message = (Message) messageOrBuilder;
        } else {
            message = ((Message.Builder) messageOrBuilder).build();
        }
        var msg = new IMPacket(srcHeader.getServiceId(),
                srcHeader.getCommandId(),
                message.toByteArray());
        msg.setVersion(srcHeader.getVersion());
        msg.setSeq(srcHeader.getSeq());
        return msg;
    }

    public static short getServiceId(int cmdId) {
        return (short) (cmdId >> 16);
    }

    public static short getCommandId(int cmdId) {
        return (short) (cmdId & 0xffff0000);
    }

    public static int getCmdId(short serviceId, short commandId) {

        return serviceId << 16 | commandId;
    }

    /**
     * 将int数值转换为占四个字节的byte数组
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) (value & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[3] = (byte) ((value >> 24) & 0xFF);
        return src;
    }

    /**
     * 将short数值转换为占四个字节的byte数组
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] shortToBytes(short value) {
        byte[] src = new byte[4];
        src[0] = (byte) (value & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        return src;
    }

}
