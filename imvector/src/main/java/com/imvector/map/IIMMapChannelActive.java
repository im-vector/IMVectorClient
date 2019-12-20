package com.imvector.map;

import io.netty.channel.ChannelHandlerContext;

/**
 * 连接活跃了之后
 * @author: vector.huang
 * @date: 2019/10/02 13:38
 */
public interface IIMMapChannelActive {

    void channelActive(ChannelHandlerContext ctx);

}
