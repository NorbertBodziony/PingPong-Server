import java.net.DatagramSocket;
import java.net.InetAddress;

public class Player {

    public InetAddress ipAddress;
    public int socket;
    public int P=0;
    public int TimeOut=4;

    @Override
    public String toString() {
        return "Player{" +
                "ipAddress=" + ipAddress +
                ", socket=" + socket +
                ", P=" + P +
                '}';
    }
}
