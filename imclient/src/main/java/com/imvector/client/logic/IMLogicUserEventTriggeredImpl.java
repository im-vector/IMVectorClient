package com.imvector.client.logic;

import com.imvector.client.entity.UserDetail;
import com.imvector.client.proto.IMUtil;
import com.imvector.logic.impl.IMLogicUserEventTriggered;
import com.imvector.proto.Packet;
import com.imvector.proto.system.IMSystem;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author: vector.huang
 * @date: 2019/10/02 17:01
 */
@Component
public class IMLogicUserEventTriggeredImpl implements IMLogicUserEventTriggered<UserDetail> {

    private final Logger logger = LoggerFactory.getLogger(IMLogicUserEventTriggeredImpl.class);

    @Override
    public void userEventTriggered(UserDetail userDetail, ChannelHandlerContext ctx, Object evt) {

        if (evt instanceof IdleStateEvent) {
            //发送心跳
            logger.debug("因为空闲太久，发送一个心跳");
            var header = IMUtil.newPacket(Packet.ServiceId.SYSTEM,
                    IMSystem.CommandId.SYSTEM_NOON, null);
            userDetail.setStart(System.currentTimeMillis());
            ctx.writeAndFlush(header);
        }

    }
}
