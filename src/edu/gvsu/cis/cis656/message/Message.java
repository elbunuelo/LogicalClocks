package edu.gvsu.cis.cis656.message;

import edu.gvsu.cis.cis656.clock.VectorClock;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Message {


    public int type;                // See MessageTypes
    public int pid;                 // unique client pid of the sender
    public String sender;           // name of the sender
    public VectorClock ts;          // clock piggy backed on message
    public String message;          // the message being sent
    public int tag;                 // useful for debugging - see notes


    public Message(int type, String sender, int pid, VectorClock ts, String message) {
        this.type = type;
        this.sender = sender;
        this.ts = ts;
        this.message = message;
        this.pid = pid;
        this.tag = 0;
    }

    public static Message parseMessage(String msg) {
        JSONObject obj = new JSONObject(msg);
        int type = obj.getInt("type");
        int pid = obj.getInt("pid");
        String sender = obj.getString("sender");
        String ts = obj.getString("ts");
        String content = obj.getString("message");

        VectorClock clk = new VectorClock();
        clk.setClockFromString(ts);

        Message message = new Message(type, sender, pid, clk, content);
        return message;
    }

    public String toString()
    {
        JSONObject msg = new JSONObject();
        msg.put("type", type);
        msg.put("pid", pid);
        msg.put("sender", sender);
        msg.put("ts", ts==null ? "{}" : ts.toString());
        msg.put("message", message);

        return msg.toString();
    }

    public static void sendMessage(Message m, DatagramSocket socket, InetAddress address, int port) {
        String data = m.toString();
        byte[] buf = data.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Message receiveMessage(DatagramSocket socket)
    {
        Message msg = null;
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf,buf.length);
        try {
            socket.receive(packet);
            String recvd = new String(packet.getData(), 0, packet.getLength());
            msg = Message.parseMessage(recvd);
        } finally {
            return msg;
        }
    }
}


/*
{
    "type" : 1,
    "sender" : "bob"
    "pid" : 10,
    "ts" : "{\"0\":2,\"1\":0,\"2\":0 }",
    "message" : "Hello Alice!"
}

 */