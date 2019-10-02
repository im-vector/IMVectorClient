package com.imvector.map.impl;

import com.imvector.logic.IIMLogicHandler;
import com.imvector.map.IIMMapChannelActive;
import com.imvector.map.IIMMapHandler;
import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author: vector.huang
 * @date: 2019/10/02 01:13
 */
public class IMMapHandler implements IIMMapHandler {

    private final IIMLogicHandler logicHandler;
    private final IIMMapChannelActive channelActive;

    public IMMapHandler(IIMLogicHandler logicHandler,
                        IIMMapChannelActive channelActive) {
        this.logicHandler = logicHandler;
        this.channelActive = channelActive;
    }

    @Override
    public ChannelHandler[] getMapHandler() {
        return new ChannelHandler[]{
                //90 秒未完成登录，将会断开
                new IdleStateHandler(0, 90, 0, TimeUnit.SECONDS),
                //登录处理
                new LoginHandler(logicHandler, channelActive)
        };
    }
}
