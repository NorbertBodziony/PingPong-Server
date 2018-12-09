


public class MainServer {

    private static  UdpServer udpServer;


    public static void main(String[] args) {

        DataBase.init();
        udpServer = new UdpServer();
        udpServer.start();

    }
}