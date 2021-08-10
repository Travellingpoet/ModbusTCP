package net.wimpi.modbustcp.bean;

public class MessageBean {
    //ACK字符
    private String ack_connect = "";

    public String getAck_connect() {
        return ack_connect;
    }

    public void setAck_connect(String ack){
        this.ack_connect = ack;
    }

    @Override
    public String toString() {
        return "{" +
                "ack_connect:" + ack_connect +
                '}';
    }
}
