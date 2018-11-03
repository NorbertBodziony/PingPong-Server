import java.net.DatagramSocket;
import java.net.InetAddress;

public class Player {
    public String Name="";
    public InetAddress ipAddress;
    public int socket;
    public int P=0;
    public int TimeOut=4;
    public GameSimulation game;

    @Override
    public String toString() {
        return "Player{" +
                "Name='" + Name + '\'' +
                ", ipAddress=" + ipAddress +
                ", socket=" + socket +
                '}';
    }
}
