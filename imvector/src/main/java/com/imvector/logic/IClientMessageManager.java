package com.imvector.logic;

import com.imvector.proto.IIMPacket;
import io.netty.channel.Channel;

/**
 * @author: vector.huang
 * @date: 2019/10/02 02:59
 */
public interface IClientMessageManager<T, P extends IIMPacket> {

    /**
     * 添加连接到业务处理层
     *
     * @param userDetail 用户详情
     * @param channel    连接
     */
    void setChannel(T userDetail, Channel channel);

    /**
     * 清空Channel
     */
    void clear();

    /**
     * 获取用户详情
     *
     * @return 用户详情
     */
    T getUserDetail();

    /**
     * 发送消息
     *
     * @param packet 发送的消息
     */
    void sendMessage(P packet);

}
