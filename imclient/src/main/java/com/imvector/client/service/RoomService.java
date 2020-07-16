package com.imvector.client.service;

import com.imvector.client.entity.UserDetail;
import com.imvector.logic.PacketInboundHandler;
import com.imvector.proto.Packet;
import com.imvector.proto.chat.Chat;
import com.imvector.proto.chat.room.Room;
import com.imvector.proto.impl.IMPacket;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author: vector.huang
 * @date: 2020/07/16 13:04
 */
@Service("Service" + Packet.ServiceId.ROOM_VALUE)
public class RoomService implements PacketInboundHandler<UserDetail, IMPacket> {

    private final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Override
    public void packetRead(UserDetail userDetail, ChannelHandlerContext ctx, IMPacket packet) throws Exception {

        switch (packet.getCommandId()) {
            case Room.CommandId.CREATE_ROOM_VALUE: {
                var resp = Room.CreateRoomResp.parseFrom(packet.getBody());
                logger.info("创建聊天室：status = {},roomNo = {},roomId = {}, msg = {}",
                        resp.getStatusValue() == Packet.Status.OK_VALUE, resp.getRoomNo(), resp.getRoomId(), resp.getMsg());
                break;
            }
            case Room.CommandId.JOIN_ROOM_VALUE: {
                var resp = Room.JoinRoomResp.parseFrom(packet.getBody());
                logger.info("加入聊天室：status = {},roomNo = {},roomId = {}, msg = {}",
                        resp.getStatusValue() == Packet.Status.OK_VALUE, resp.getRoomNo(), resp.getRoomId(), resp.getMsg());
                break;
            }
            default:
                logger.info("未知聊天消息：{}", packet.getCommandId());
                break;
        }

    }
}
