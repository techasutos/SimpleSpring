package com.asu.web.negotiation;

import com.asu.web.Request;


public class ContentNegotiationManager {

    public String resolveContentType(Request request) {

        String accept = request.getHeader("Accept");

        if (accept != null && accept.contains("application/json")) {
            return "application/json";
        }

        return "text/plain";
    }
}
