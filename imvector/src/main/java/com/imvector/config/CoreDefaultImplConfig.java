package com.imvector.config;

import com.imvector.logic.IIMLogicHandler;
import com.imvector.logic.IClientMessageManager;
import com.imvector.logic.impl.IMLogicHandler;
import com.imvector.logic.impl.MemoryMessageManager;
import com.imvector.map.IIMMapChannelActive;
import com.imvector.map.IIMMapHandler;
import com.imvector.map.impl.IMMapHandler;
import com.imvector.proto.IIMProtocolCodec;
import com.imvector.proto.impl.IMProtocolCodec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: vector.huang
 * @date: 2019/10/02 04:00
 */
@Configuration
public class CoreDefaultImplConfig {

    @ConditionalOnMissingBean(IIMProtocolCodec.class)
    @Bean("defaultIMProtocolCodec")
    public IIMProtocolCodec defaultIMProtocolCodec() {
        return new IMProtocolCodec();
    }

    @ConditionalOnMissingBean(IIMLogicHandler.class)
    @Bean
    public IIMLogicHandler iIMLogicHandler() {
        return new IMLogicHandler();
    }

    @ConditionalOnMissingBean(IIMMapHandler.class)
    @Bean
    public IIMMapHandler iIMMapHandler(IIMLogicHandler logicHandler,
                                       IIMMapChannelActive channelActive) {

        return new IMMapHandler(logicHandler, channelActive);
    }

    @ConditionalOnMissingBean(IClientMessageManager.class)
    @Bean
    public IClientMessageManager iMessageManager() {
        return new MemoryMessageManager();
    }

}
