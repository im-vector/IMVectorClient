package com.imvector.logic.impl;

import io.netty.channel.ChannelHandlerContext;

/**
 * 协议未知的前提下是不知道怎么处理这个消息的
 *
 * @author: vector.huang
 * @date: 2019/10/02 16:54
 */
public interface IMLogicUserEventTriggered<T> {

    void userEventTriggered(T userDetail, ChannelHandlerContext ctx, Object evt);
}
