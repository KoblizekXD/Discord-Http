package com.discord.api;

public enum APIVersion {
    V10,
    V9,
    V8(Status.DEPRECATED),
    V7(Status.DEPRECATED),
    V6(Status.DEPRECATED, true),
    V5(Status.DISCONTINUED),
    V4(Status.DISCONTINUED),
    V3(Status.DISCONTINUED);

    private final Status status;
    private final boolean isDefault;

    APIVersion(Status status, boolean isDefault) {
        this.status = status;
        this.isDefault = isDefault;
    }

    APIVersion() {
        this(Status.AVAILABLE, false);
    }
    APIVersion(Status status) {
        this(status, false);
    }

    public boolean isDefault() {
        return isDefault;
    }

    public Status getStatus() {
        return status;
    }
    public int asInteger() {
        return Integer.parseInt(this.toString().substring(1));
    }
}
