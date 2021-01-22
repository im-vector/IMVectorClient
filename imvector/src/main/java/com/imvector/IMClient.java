package com.imvector;

import com.imvector.config.NettyConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: vector.huang
 * @date: 2019/03/18 13:13
 */
@Component
public class IMClient implements ApplicationListener<ApplicationEvent> {

    private final NettyConfig nettyConfig;
    private final InitChannel initChannel;

    private Logger logger = LoggerFactory.getLogger(IMClient.class);

    private ChannelFuture channelFuture;
    private EventLoopGroup workerGroup;

    private IMClientStatus status;

    private List<OnIMClientStatusChangedListener> listeners;

    @Autowired
    public IMClient(NettyConfig nettyConfig, InitChannel initChannel) {
        this.nettyConfig = nettyConfig;
        this.initChannel = initChannel;
        status = IMClientStatus.CONNECT_FAIL;
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {
        if (event instanceof ApplicationStartedEvent) {
            ConfigurableApplicationContext context = ((ApplicationStartedEvent) event).getApplicationContext();
            if (context instanceof AnnotationConfigApplicationContext) {
                start();
            }
        } else if (event instanceof ContextClosedEvent) {
            //SpringBoot 关闭的同时，关闭Netty
            if (channelFuture.channel().isOpen()) {
                channelFuture.channel().close();
            }
        }
    }

    public void addImClientStatusChangedListener(OnIMClientStatusChangedListener listener) {
        if (listener == null) {
            return;
        }
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        listeners.add(listener);
    }


    public void removeImClientStatusChangedListener(OnIMClientStatusChangedListener listener) {
        if (listener == null) {
            return;
        }
        if (listeners == null) {
            return;
        }
        listeners.remove(listener);
    }


    public void clearImClientStatusChangedListener() {
        if (listeners == null) {
            return;
        }
        listeners.clear();
    }


    /**
     * 关闭事件
     */
    private void shutdownGracefully() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
    }

    public void start() {
        if (status != IMClientStatus.CONNECT_FAIL) {
            return;
        }
        try {
            //不可以阻塞，否则SpringBoot 就无法启动了
            run();
        } catch (Exception e) {
            shutdownGracefully();
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 运行IM服务
     */
    private void run() throws Exception {

        logger.info("开始启动IMClient...");

        /*
         * NioEventLoopGroup 是用来处理I/O操作的多线程事件循环器，
         * Netty 提供了许多不同的 EventLoopGroup 的实现用来处理不同的传输。
         * 在这个例子中我们实现了一个客户端的应用，因此会有1个 NioEventLoopGroup 会被使用。
         * 经常被叫做‘worker’，用来处理和服务端的连接。
         * 如何知道多少个线程已经被使用，如何映射到已经创建的 Channel上都需要依赖于 EventLoopGroup 的实现，
         * 并且可以通过构造函数来配置他们的关系。
         */
        //worker 多线程事件循环器
        workerGroup = new NioEventLoopGroup();

        /*
         * 客户端中，把ServerBootstrap 改为Bootstrap
         */
        Bootstrap b = new Bootstrap();
        //加入事件循环器
        b.group(workerGroup);
        /*
         * 当客户端连接上的时候
         * NioSocketChannel 类指定了该新的 Channel 如何连接进来
         * Nio就是异步连接进来
         */
        b.channel(NioSocketChannel.class);

        /*
         * 当有新的Channel 连接进来之后就会调用该ChannelInitializer来初始化该Channel
         * 主要工作就是添加很多ChannelHandler 到PipelineChannel上，用来处理数据
         * 当你的程序变的复杂时，可能你会增加更多的处理类到 pipeline 上
         */
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                /*
                 * 当有新的Channel需要处理的时候
                 * 把Handler 添加到PipelineChannel 上
                 */
                initChannel.initChannel(ch);
            }
        });
        /*
         * 你关注过 option() 和 childOption() 吗？
         * option() 是提供给NioServerSocketChannel 用来接收进来的连接。
         * childOption() 是提供给由父管道 ServerChannel 接收到的连接，
         * 在这个例子中也是 NioServerSocketChannel。
         */
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.TCP_NODELAY, true);

        setStatus(IMClientStatus.CONNECTING);

        //绑定端口，开始接收进来的连接
        //sync 等待绑定成功
        ChannelFuture f = b.connect(nettyConfig.getHost(), nettyConfig.getPort());
        f.addListener(future -> {
            if (future.isSuccess()) {
                logger.info("IMClient 启动成功");
                setStatus(IMClientStatus.CONNECTED);
            } else {
                //例如，端口被占用了:Address already in use
                logger.info("IMServer 启动失败：" + future.cause().getMessage());
                setStatus(IMClientStatus.CONNECT_FAIL);
            }
        });
        /*
         * 等待服务器  socket 关闭 。
         * 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
         */
        channelFuture = f.channel().closeFuture();
        channelFuture.addListener(future -> {
            shutdownGracefully();
            if (future.isSuccess()) {
                logger.info("成功关闭IMClent");
            } else {
                logger.info("关闭IMClent 失败：" + future.cause().getMessage());
            }
            setStatus(IMClientStatus.CONNECT_FAIL);
        });
    }

    public IMClientStatus getStatus() {
        return status;
    }

    public void setStatus(IMClientStatus status) {
        this.status = status;
        if (listeners != null) {
            for (OnIMClientStatusChangedListener listener : listeners) {
                listener.onChanged(status);
            }
        }
    }
}
