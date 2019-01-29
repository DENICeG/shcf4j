package com.imperva.shcf4j.request.body.multipart;

import java.io.InputStream;
import java.util.Objects;

public class InputStreamPartBuilder extends PartBuilder<InputStreamPartBuilder> {


    protected InputStream inputStream;
    protected long contentLength = -1;


    public InputStreamPartBuilder inputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public InputStreamPartBuilder contentLength(int contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    @Override
    public Part build() {
        Objects.requireNonNull(inputStream, "inputStream");
        return new InputStreamPart(this);
    }
}
