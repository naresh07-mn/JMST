package com.travel_track.solution.apihandler;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.Query;

public class StringConverterFactory extends Converter.Factory {

    private final Converter.Factory delegateFactory;

    StringConverterFactory(Converter.Factory delegateFactory) {
        super();
        this.delegateFactory = delegateFactory;
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit
            retrofit) {
        for (final Annotation annotation : annotations) {
            if (annotation instanceof Query) {

                final Converter<?, RequestBody> delegate = delegateFactory
                        .requestBodyConverter(type, annotations, new Annotation[0], retrofit);
                return new DelegateToStringConverter<>(delegate);
            }
        }
        return null;
    }


    static class DelegateToStringConverter<T> implements Converter<T, String> {
        private final Converter<T, RequestBody> delegate;

        DelegateToStringConverter(Converter<T, RequestBody> delegate) {
            this.delegate = delegate;
        }

        @Override
        public String convert(T value) throws IOException {
            final okio.Buffer buffer = new okio.Buffer();
            delegate.convert(value).writeTo(buffer);
            if(value instanceof String){
                return value.toString();
            }
            else {
                return buffer.readUtf8();
            }
        }
    }
}