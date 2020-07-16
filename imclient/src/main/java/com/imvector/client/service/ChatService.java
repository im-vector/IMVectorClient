package com.imvector.client.service;

import com.imvector.client.entity.UserDetail;
import com.imvector.logic.PacketInboundHandler;
import com.imvector.proto.impl.IMPacket;
import com.imvector.proto.Packet;
import com.imvector.proto.chat.Chat;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author: vector.huang
 * @date: 2019/10/06 10:53
 */
@Service("Service" + Packet.ServiceId.CHAT_VALUE)
public class ChatService implements PacketInboundHandler<UserDetail, IMPacket> {

    private final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Override
    public void packetRead(UserDetail userDetail, ChannelHandlerContext ctx, IMPacket packet) throws Exception {

        switch (packet.getCommandId()) {
            case Chat.CommandId.CHAT_MSG_VALUE:
                var resp = Chat.MsgResp.parseFrom(packet.getBody());
                logger.info("消息发送：status = {},msgId = {}, msg = {}",
                        resp.getStatusValue() == Packet.Status.OK_VALUE, resp.getMsgId(), resp.getMsg());
                break;
            case Chat.CommandId.CHAT_MSG_OUT_VALUE:
                var outRep = Chat.MsgOut.parseFrom(packet.getBody());
                logger.info("收到消息：from = {},chatType = {}, content = {}",
                        outRep.getFrom(), outRep.getChatType(),outRep.getContent());
                break;
            default:
                logger.info("未知聊天消息：{}", packet.getCommandId());
                break;
        }

    }
}
