package com.store.authentication.config;

public class KeywordsAndConstants {

    public static final String RABBIT_MQ_USER_NAME="alphaTanmoy";
    public static final String RABBIT_MQ_PASSWORD="password";
    public static final String RABBIT_MQ_HOST="localhost";
    public static final Integer RABBIT_MQ_PORT=5672;

    public static final String RABBIT_MQ_EXCHANGE="RABBIT_MQ_EXCHANGE";
    public static final String RABBIT_MQ_QUEUE_FOR_EVENTS = "RABBIT_MQ_QUEUE_FOR_EVENTS";
    public static final String RABBIT_MQ_ROUTE_KEY_FOR_EVENTS = "RABBIT_MQ_ROUTE_KEY_FOR_EVENTS";
    public static final String RABBIT_MQ_QUEUE_FOR_REQUEST_SANITATION = "RABBIT_MQ_QUEUE_FOR_REQUEST_SANITATION";
    public static final String RABBIT_MQ_ROUTE_KEY_FOR_REQUEST_SANITATION = "RABBIT_MQ_ROUTE_KEY_FOR_REQUEST_SANITATION";
    public static final String RABBIT_MQ_QUEUE_FOR_LOGIN_OR_SIGNUP_OTP = "RABBIT_MQ_QUEUE_FOR_LOGIN_OR_SIGNUP_OTP";
    public static final String RABBIT_MQ_ROUTE_KEY_FOR_LOGIN_OR_SIGNUP_OTP = "RABBIT_MQ_ROUTE_KEY_FOR_LOGIN_OR_SIGNUP_OTP";
    public static final String RABBIT_MQ_ROUTE_KEY_FOR_FOREX_DATA = "RABBIT_MQ_ROUTE_KEY_FOR_FOREX_DATA";
    public static final String RABBIT_MQ_QUEUE_FOR_FOREX_DATA = "RABBIT_MQ_QUEUE_FOR_FOREX_DATA";


    public static final Integer MAXIMUM_DEVICE_CAN_CONNECT = 3;
    public static final Integer OTP_EXPIRED_IN_MINUTES = 2;
    public static final String SECRET_KEY="wpembytrwcvnryxksdbqwjebrTANMOYuyGHyudqgwveytrtrCSnwifoesarjbwe";
    public static final String JWT_HEADER="Authorization";
    public static final String SIGNING_PREFIX="signing_";
    public static final String OTP_TEXT_FOR_LOGIN="your login otp is - ";
    public static final String OTP_SUBJECT_FOR_LOGIN="petStore Login/Signup Otp";
}
