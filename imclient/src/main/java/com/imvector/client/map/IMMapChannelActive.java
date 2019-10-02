package com.imvector.client.map;

import com.imvector.client.proto.IMUtil;
import com.imvector.map.IIMMapChannelActive;
import com.imvector.server.proto.Packet;
import com.imvector.server.proto.system.IMSystem;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

/**
 * @author: vector.huang
 * @date: 2019/10/02 13:41
 */
@Component
public class IMMapChannelActive implements IIMMapChannelActive {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 发送一个心跳请求
        // 心跳成功之后，就开始登录
        var header = IMUtil.newPacket(Packet.ServiceId.SYSTEM,
                IMSystem.CommandId.SYSTEM_NOON, null);
        ctx.writeAndFlush(header);
    }
}
