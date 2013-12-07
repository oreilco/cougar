package com.betfair.cougar.transport.impl;

import com.betfair.cougar.transport.api.RequestTimeResolver;

import java.util.Date;

/**
 *
 */
public abstract class SimpleRequestTimeResolver<I, O> implements RequestTimeResolver<I, O> {
    private boolean clientTimeSynchronizedWithServer;

    public SimpleRequestTimeResolver(boolean clientTimeSynchronizedWithServer) {
        this.clientTimeSynchronizedWithServer = clientTimeSynchronizedWithServer;
    }

    protected abstract Date readRequestTime(I input);

    @Override
    public Date resolveRequestTime(I input) {
        if (clientTimeSynchronizedWithServer) {
            return readRequestTime(input);
        }
        return new Date();
    }
}