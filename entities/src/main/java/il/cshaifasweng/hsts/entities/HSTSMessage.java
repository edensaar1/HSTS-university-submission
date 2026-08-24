package il.cshaifasweng.hsts.entities;


import il.cshaifasweng.hsts.entities.enums.MessageType;

import java.io.Serializable;

public class HSTSMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageType type;
    private Object payload;

    public HSTSMessage(MessageType type, Object payload){
        this.type = type;
        this.payload = payload;
    }

    public MessageType getType() {
        return type;
    }

    public Object getPayload()  {
        return payload;
    }

}
