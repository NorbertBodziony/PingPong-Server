


public class MainServer {

    private static  UdpServer udpServer;


    public static void main(String[] args) {


        udpServer = new UdpServer();
        udpServer.start();

    }
}