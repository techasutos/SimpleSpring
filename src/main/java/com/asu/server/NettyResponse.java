package com.asu.server;

import com.asu.web.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class NettyResponse extends Response {

    private ChannelHandlerContext ctx;

    public NettyResponse(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void write(Object body) {

        byte[] bytes = body.toString().getBytes();

        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1,
                HttpResponseStatus.OK,
                io.netty.buffer.Unpooled.wrappedBuffer(bytes)
        );

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);

        ctx.writeAndFlush(response);
    }
}
