import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UdpServer extends Thread {

    private DatagramSocket socket;

    private int stan=0;
    public static ArrayList<Player> players=new ArrayList<>();
    public int PlayerCounter=0;
    public byte[] bytes;
    public int type;
    public  int P;
    Ball ball1;
    public int width;
    public int height;
    private int multi=0;

    public  UdpServer()

    {

        try {
            this.socket=new DatagramSocket(1331);

        } catch (SocketException e) {
            e.printStackTrace();
        }

    }
    class SayHello extends TimerTask {
        public void run() {
            if(stan==1){

            System.out.println(players.get(0).TimeOut);
            if(players.get(0).TimeOut!=0||players.get(0).TimeOut!=0){
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteOut);
            try {

                dataOut.writeInt(5);
                dataOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = byteOut.toByteArray();
            sendDatatoAllClients(bytes);
                players.get(0).TimeOut-=1;
                players.get(1).TimeOut-=1;
        }
        else {
                stan=0;
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                DataOutputStream dataOut = new DataOutputStream(byteOut);
                try {
                    dataOut.writeInt(6);

                    dataOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bytes = byteOut.toByteArray();

                sendDatatoAllClients(bytes);
                System.out.println("Time out all");
                players.clear();
                PlayerCounter=0;
            }}
        }
    }
    public  void run()
    {

        while(true)
        {
           if(stan==0)
           {
               System.out.println("czekam na polaczenie");
           }

            byte[] data=new  byte[1024];
            DatagramPacket packet = new DatagramPacket(data,data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes=packet.getData();
            ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
            DataInputStream dataIn = new DataInputStream(byteIn);
            try {
                type=dataIn.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(type==1) // init palyers
            {
                System.out.println("new user::"+packet.getAddress().getHostAddress()+" port "+packet.getPort());
                for(Player p:players)
                {
                    if(p.socket==packet.getPort())
                    {
                         multi =1;
                        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                        DataOutputStream dataOut = new DataOutputStream(byteOut);
                        try {
                            dataOut.writeInt(2);
                            dataOut.writeBoolean(false);
                            dataOut.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bytes = byteOut.toByteArray();
                        sendData(bytes,packet.getAddress(),packet.getPort());
                    }
                }
                if(multi!=1){
                 players.add(new Player());
                try {
                    width=dataIn.readInt();
                    height=dataIn.readInt();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                players.get(PlayerCounter).ipAddress=packet.getAddress();
                players.get(PlayerCounter).socket=packet.getPort();
                PlayerCounter++;
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                DataOutputStream dataOut = new DataOutputStream(byteOut);
                try {
                    dataOut.writeInt(1);
                    dataOut.writeInt(PlayerCounter);
                    dataOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bytes = byteOut.toByteArray();
                sendData(bytes,packet.getAddress(),packet.getPort());

                if(PlayerCounter==2) // init game
                {   ball1=new Ball((int)width/2,(int)height/2,(int)width,(int)height);
                    System.out.println("Init game");
                     byteOut = new ByteArrayOutputStream();
                     dataOut = new DataOutputStream(byteOut);
                    try {
                        dataOut.writeInt(2);
                        dataOut.writeBoolean(true);
                        stan=1;
                        dataOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bytes = byteOut.toByteArray();
                    sendDatatoAllClients(bytes);
                    System.out.println(players.toString());
                    Timer timer = new Timer();
                    timer.schedule(new SayHello(), 0, 2000);
                }

            }multi=0;
            }
            if(type==3)
            {

                try {
                    P=dataIn.readInt();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                DataOutputStream dataOut = new DataOutputStream(byteOut);
                try {
                    dataOut.writeInt(3);
                    dataOut.writeInt(P);
                    dataOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(stan==1){
                if(packet.getPort()==players.get(0).socket)
                {
                    bytes = byteOut.toByteArray();
                    players.get(0).P=P;

                    sendData(bytes,players.get(1).ipAddress,players.get(1).socket);
                }
                else {
                    bytes = byteOut.toByteArray();
                    players.get(1).P=P;
                    sendData(bytes,players.get(0).ipAddress,players.get(0).socket);


                }}
            }
            if(type==5)
            {
                for(Player p : players)
                {
                    if (packet.getPort()==p.socket&&packet.getAddress().equals(p.ipAddress))
                    {
                        p.TimeOut=4;
                    }
                }
            }
            if(type==6)
            {
                stan=0;
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                DataOutputStream dataOut = new DataOutputStream(byteOut);
                try {
                    dataOut.writeInt(6);

                    dataOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bytes = byteOut.toByteArray();

                sendDatatoAllClients(bytes);
                System.out.println("Disconnected");
                players.clear();
                PlayerCounter=0;
            }
            if(type!=6&&type!=5&&type!=4&&type!=3&&type!=2&&type!=1)
            {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                DataOutputStream dataOut = new DataOutputStream(byteOut);
                try {
                    dataOut.writeChars("hello");


                    dataOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bytes = byteOut.toByteArray();

                sendData(bytes,packet.getAddress(),packet.getPort());
                System.out.println("Not known");
            }
            if(stan==1) {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                DataOutputStream dataOut = new DataOutputStream(byteOut);
                try {
                    dataOut.writeInt(4);
                    dataOut.writeInt((int) ball1.x);
                    dataOut.writeInt((int) ball1.y);
                    dataOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bytes = byteOut.toByteArray();
                sendDatatoAllClients(bytes);
                //System.out.println("ball data"+ball1.toString());
                ball1.move();
                colision();
            }




        }
    }
    public void sendData(byte[] data,InetAddress ipAddress,int port)
    {
        DatagramPacket packet= new DatagramPacket(data,data.length,ipAddress,port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendDatatoAllClients(byte[] data)
    {
        for (Player p : players)
        {
            sendData(data,p.ipAddress,p.socket);
        }
    }
    private void colision()
    {
        if(ball1.y>= players.get(0).P&&ball1.y<=players.get(0).P+200&&ball1.x>=(width-4*10))
        {   ball1.xVel++;
            ball1.xVel=-ball1.xVel;
        }
        if(ball1.y>=players.get(1).P&&ball1.y<=players.get(1).P+200&&ball1.x<=4*10)
        {   ball1.xVel--;
            ball1.xVel=-ball1.xVel;
        }
        if (ball1.x>width||ball1.x<0)
        {ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteOut);
            try {
                dataOut.writeInt(2);
                dataOut.writeBoolean(false);
                dataOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = byteOut.toByteArray();
            sendDatatoAllClients(bytes);
            stan=0;
            PlayerCounter=0;
            players.clear();
        }

    }
}
