package com.imvector.logic.impl;

import com.imvector.logic.IIMLogicHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author: vector.huang
 * @date: 2019/10/02 01:50
 */
public class IMLogicHandler<T> implements IIMLogicHandler<T> {

    private int noonTime = 5 * 60;

    public IMLogicHandler() {
    }

    public IMLogicHandler(int noonTime) {
        this.noonTime = noonTime;
    }

    @Override
    public ChannelHandler[] getLogicHandler(T userDetail, Channel channel) {

        //登录成功，这个处理器就会替换为业务处理处理器
        IMServiceHandler imServerHandler = new IMServiceHandler<>(userDetail, channel);
        return new ChannelHandler[]{
                new IdleStateHandler(noonTime, 0, 0, TimeUnit.SECONDS),
                imServerHandler,
        };
    }
}
