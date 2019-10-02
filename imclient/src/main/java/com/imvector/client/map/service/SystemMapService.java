package com.imvector.client.map.service;

import com.imvector.client.entity.UserDetail;
import com.imvector.client.proto.IMUtil;
import com.imvector.map.MapInboundHandler;
import com.imvector.proto.impl.IMPacket;
import com.imvector.server.proto.Packet;
import com.imvector.server.proto.system.IMSystem;
import com.imvector.server.proto.user.IMUser;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author: vector.huang
 * @date: 2019/04/25 13:57
 */
@Service("MapService" + Packet.ServiceId.SYSTEM_VALUE)
public class SystemMapService implements MapInboundHandler<UserDetail, IMPacket> {

    private Logger logger = LoggerFactory.getLogger(SystemMapService.class);

    private UserDetail userDetail;

    @Override
    public UserDetail packetRead(ChannelHandlerContext ctx, IMPacket msg) throws Exception {

        //处理心跳
        if (msg.getCommandId() == IMSystem.CommandId.SYSTEM_NOON_VALUE) {

            userDetail = new UserDetail();
            var userId = new Random(System.currentTimeMillis()).nextInt();
            if (userId < 0) {
                userId = -userId;
            }
            userDetail.setUserId(userId);
            // 心跳成功，开始登录
            var loginReq = IMSystem.LoginReq.newBuilder();
            loginReq.setToken(userId + "");

            var packet = IMUtil.newPacket(Packet.ServiceId.SYSTEM, IMSystem.CommandId.SYSTEM_LOGIN, loginReq);
            ctx.writeAndFlush(packet);
            logger.debug("已发送登录请求");
            return null;
        }

        // 处理登录成功
        if (msg.getCommandId() == IMSystem.CommandId.SYSTEM_LOGIN_VALUE) {

            var resp = IMSystem.LoginResp.parseFrom(msg.getBody());
            if (resp.getStatus() == Packet.Status.OK) {
                // 登录成功
                userDetail.setMaxMsgId(resp.getMaxMsgId());
                logger.info("登录成功：{}-{}", userDetail.getUserId(), resp.getMsg());
                return userDetail;
            }

            // 登录失败
            logger.error("登录失败：{}", resp.getMsg());
            ctx.close();
        }

        return null;
    }
}
