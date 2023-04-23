package com.discord.util;

public @interface Docs {
    String value();
    boolean borrowed() default false;
}
