package com.divitbui.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES)
public enum ChannelType {

    LEVEL2;

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
