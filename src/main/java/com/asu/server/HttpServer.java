package com.asu.server;

import com.asu.web.DispatcherServlet;
import com.asu.web.Request;
import com.asu.web.Response;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    private int port;
    private DispatcherServlet dispatcher;

    public HttpServer(int port, DispatcherServlet dispatcher) {
        this.port = port;
        this.dispatcher = dispatcher;
    }

    public void start() throws Exception {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) {

                        ch.pipeline()
                                .addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(65536))
                                .addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {

                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {

                                        Request request = buildRequest(req);
                                        Response response = new NettyResponse(ctx);

                                        dispatcher.dispatch(request, response);
                                    }
                                });
                    }
                });

        ChannelFuture future = bootstrap.bind(port).sync();

        System.out.println("🔥 Agni running on port " + port);

        future.channel().closeFuture().sync();
    }

    private Request buildRequest(FullHttpRequest req) {

        String uri = req.uri().split("\\?")[0];

        Map<String, String> params = new HashMap<>();

        Request request = new Request(uri, params);

        request.setBody(req.content().toString(io.netty.util.CharsetUtil.UTF_8));

        req.headers().forEach(h -> request.addHeader(h.getKey(), h.getValue()));

        return request;
    }
}
