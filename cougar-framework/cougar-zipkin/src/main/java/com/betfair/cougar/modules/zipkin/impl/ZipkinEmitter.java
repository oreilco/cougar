package com.betfair.cougar.modules.zipkin.impl;

import com.betfair.cougar.modules.zipkin.api.ZipkinData;
import com.betfair.cougar.util.geolocation.RemoteAddressUtils;
import com.github.kristofa.brave.zipkin.ZipkinSpanCollector;
import com.twitter.zipkin.gen.Endpoint;
import com.twitter.zipkin.gen.Span;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.twitter.zipkin.gen.zipkinCoreConstants.*;

public class ZipkinEmitter {

    private static final int LOCALHOST_IP;

    private String serviceName;

    private ZipkinSpanCollector zipkinSpanCollector;


    static {
        LOCALHOST_IP = RemoteAddressUtils.getIPv4AsInteger();
    }

    public void emitServerReceive(@Nonnull ZipkinData zipkinData) {
        emitAnnotation(zipkinData, SERVER_RECV);
    }

    public void emitServerSend(@Nonnull ZipkinData zipkinData) {
        emitAnnotation(zipkinData, SERVER_SEND);
    }

    public void emitClientSend(@Nonnull ZipkinData zipkinData) {
        emitAnnotation(zipkinData, CLIENT_SEND);
    }

    public void emitClientReceive(@Nonnull ZipkinData zipkinData) {
        emitAnnotation(zipkinData, CLIENT_RECV);
    }

    /**
     * The returning object should be used to emit more than 1 annotation at once. After adding your annotations you
     * will need to call emitAnnotations in order to send the span with all the annotations to Zipkin.
     *
     * @param zipkinData Zipkin request data
     * @return Zipkin annotations storage capable of merging multiple annotations per emission
     */
    public ZipkinAnnotationsStore buildAnnotationsStore(@Nonnull ZipkinData zipkinData) {
        Objects.requireNonNull(zipkinData);
        Endpoint endpoint = generateEndpoint(zipkinData);
        return new ZipkinAnnotationsStore(zipkinData).defaultEndpoint(endpoint);
    }

    /**
     * Emit a pre-populated storage of annotations to Zipkin
     *
     * @param zipkinAnnotationsStore Zipkin annotations storage representing the entire list of annotations to emit
     */
    public void emitAnnotations(@Nonnull ZipkinAnnotationsStore zipkinAnnotationsStore) {
        Objects.requireNonNull(zipkinAnnotationsStore);
        zipkinSpanCollector.collect(zipkinAnnotationsStore.generate());
    }


    // Single annotation emission methods

    public void emitAnnotation(@Nonnull ZipkinData zipkinData, String s) {
        Objects.requireNonNull(s);
        Span span = generateZipkinAnnotationsStore(zipkinData).addAnnotation(s).generate();
        zipkinSpanCollector.collect(span);
    }

    public void emitAnnotation(@Nonnull ZipkinData zipkinData, @Nonnull String key, @Nonnull String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Span span = generateZipkinAnnotationsStore(zipkinData).addAnnotation(key, value).generate();
        zipkinSpanCollector.collect(span);
    }

    public void emitAnnotation(@Nonnull ZipkinData zipkinData, @Nonnull String key, int value) {
        Objects.requireNonNull(key);
        Span span = generateZipkinAnnotationsStore(zipkinData).addAnnotation(key, value).generate();
        zipkinSpanCollector.collect(span);
    }

    public void emitAnnotation(@Nonnull ZipkinData zipkinData, @Nonnull String key, long value) {
        Objects.requireNonNull(key);
        Span span = generateZipkinAnnotationsStore(zipkinData).addAnnotation(key, value).generate();
        zipkinSpanCollector.collect(span);
    }

    public void emitAnnotation(@Nonnull ZipkinData zipkinData, @Nonnull String key, double value) {
        Objects.requireNonNull(key);
        Span span = generateZipkinAnnotationsStore(zipkinData).addAnnotation(key, value).generate();
        zipkinSpanCollector.collect(span);
    }


    @Nonnull
    private ZipkinAnnotationsStore generateZipkinAnnotationsStore(@Nonnull ZipkinData zipkinData) {
        Objects.requireNonNull(zipkinData);

        Endpoint endpoint = generateEndpoint(zipkinData);
        return new ZipkinAnnotationsStore(zipkinData).defaultEndpoint(endpoint);
    }

    @Nonnull
    private Endpoint generateEndpoint(@Nonnull ZipkinData zipkinData) {
        return new Endpoint(LOCALHOST_IP, zipkinData.getPort(), serviceName);
    }

    public void setZipkinSpanCollector(@Nonnull ZipkinSpanCollector zipkinSpanCollector) {
        Objects.requireNonNull(zipkinSpanCollector);
        this.zipkinSpanCollector = zipkinSpanCollector;
    }

    public void setServiceName(@Nonnull String serviceName) {
        Objects.requireNonNull(serviceName);
        this.serviceName = serviceName;
    }
}
