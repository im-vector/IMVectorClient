package com.imvector.logic.impl;

import com.imvector.logic.IClientMessageManager;
import com.imvector.proto.IIMPacket;
import io.netty.channel.Channel;

/**
 * @author: vector.huang
 * @date: 2019/10/02 02:57
 */
public class MemoryMessageManager<T, P extends IIMPacket> implements IClientMessageManager<T, P> {

    /**
     * 管理本地全部的Channel
     */
    private Channel channel;
    private T userDetail;


    @Override
    public void setChannel(T userDetail, Channel channel) {
        this.channel = channel;
        this.userDetail = userDetail;
    }

    @Override
    public void clear() {
        channel = null;
        userDetail = null;
    }

    @Override
    public T getUserDetail() {
        return userDetail;
    }

    @Override
    public void sendMessage(P packet) {

        // 直接发送，如果不在线消息将会被忽略
        if (channel != null) {
            channel.writeAndFlush(packet);
        }

    }
}
