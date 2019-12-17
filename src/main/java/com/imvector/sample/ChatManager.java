package com.imvector.sample;

import com.imvector.client.entity.UserDetail;
import com.imvector.client.proto.IMUtil;
import com.imvector.logic.IClientMessageManager;
import com.imvector.server.proto.Packet;
import com.imvector.server.proto.chat.Chat;
import com.imvector.utils.SpringUtils;

/**
 * @author: vector.huang
 * @date: 2019/10/06 10:54
 */
public class ChatManager {

    public static void sendTextMessage(int to, String content) {

        var message = SpringUtils.getBean(IClientMessageManager.class);
        UserDetail userDetail = (UserDetail) message.getUserDetail();

        var msgReq = Chat.MsgReq.newBuilder();
        msgReq.setChatType(Chat.ChatType.SINGLE);
        msgReq.setContent(content);
        msgReq.setFrom(userDetail.getUserId());
        msgReq.setTo(to);
        msgReq.setMsgId(userDetail.getMaxMsgId());

        var packet = IMUtil.newPacket(Packet.ServiceId.CHAT, Chat.CommandId.CHAT_MSG, msgReq);
        message.sendMessage(packet);
    }

}
