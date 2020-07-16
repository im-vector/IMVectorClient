package com.imvector.sample;

import com.imvector.client.proto.IMUtil;
import com.imvector.logic.IClientMessageManager;
import com.imvector.proto.Packet;
import com.imvector.proto.chat.room.Room;
import com.imvector.utils.SpringUtils;

/**
 * @author: vector.huang
 * @date: 2020/07/16 13:12
 */
public class RoomManager {

    public static void create(String roomName) {

        var message = SpringUtils.getBean(IClientMessageManager.class);

        var msgReq = Room.CreateRoomReq.newBuilder();
        msgReq.setRoomName(roomName);

        var packet = IMUtil.newPacket(Packet.ServiceId.ROOM, Room.CommandId.CREATE_ROOM, msgReq);
        message.sendMessage(packet);
    }

    public static void join(String roomNo) {

        var message = SpringUtils.getBean(IClientMessageManager.class);

        var msgReq = Room.JoinRoomReq.newBuilder();
        msgReq.setRoomNo(roomNo);

        var packet = IMUtil.newPacket(Packet.ServiceId.ROOM, Room.CommandId.JOIN_ROOM, msgReq);
        message.sendMessage(packet);
    }
}
