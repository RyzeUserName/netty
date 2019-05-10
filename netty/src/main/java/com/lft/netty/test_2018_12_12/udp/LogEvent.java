package com.lft.netty.test_2018_12_12.udp;

import java.net.InetSocketAddress;

/**
 * 日志实体
 * @author Ryze
 * @date 2018-12-12 19:30
 */
public final class LogEvent {
    public static final byte SEPARATOR = (byte) ':';
    private final InetSocketAddress inetSocketAddress;
    private final String logfile;
    private final String msg;
    private final Long received;

    public LogEvent(String logfile, String msg) {
        this(null, logfile, msg, -1L);
    }

    public LogEvent(InetSocketAddress inetSocketAddress, String logfile, String msg, Long received) {
        this.inetSocketAddress = inetSocketAddress;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public String getLogfile() {
        return logfile;
    }

    public String getMsg() {
        return msg;
    }

    public Long getReceived() {
        return received;
    }
}
