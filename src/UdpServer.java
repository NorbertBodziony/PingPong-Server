import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

public class UdpServer extends Thread {

    private DatagramSocket socket;

    private int stan = 0;
    public static ArrayList<Player> players = new ArrayList<>();
    public int PlayerCounter = 0;
    public byte[] bytes;
    public int type;
    public int width;
    public int height;
    public int CurrentGame;
    public int P;
    private int multi = 0;
    Executor e = Executors.newCachedThreadPool();
    public static ArrayList<GameSimulation> game = new ArrayList<>();
    public static ArrayList<FutureTask<String>> f = new ArrayList<>();
    private int GameNumber = 0;
    Connection connection;
    Map<String, Integer> results = new HashMap<>();

    public UdpServer()

    {

        try {
            this.socket = new DatagramSocket(1331);
            this.connection = DataBase.connect();

        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    /*
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
    */
    public void run() {

        while (true) {
            if (stan == 0) {

                System.out.println("czekam na polaczenie");
                ;
            }

            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = packet.getData();
            ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
            DataInputStream dataIn = new DataInputStream(byteIn);
            try {
                type = dataIn.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (type == 1) // init palyers
            {
                System.out.println("new user::" + packet.getAddress().getHostAddress() + " port " + packet.getPort());

                    players.add(new Player());
                    try {
                        int namesie=dataIn.readInt();
                        while (namesie>0){
                        players.get(PlayerCounter).Name+=dataIn.readChar();
                        namesie--;}
                        DataBase.InsertPlayer(connection,players.get(PlayerCounter).Name);
                        width = dataIn.readInt();
                        height = dataIn.readInt();
                        System.out.println(players.get(PlayerCounter));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                players.get(PlayerCounter).ipAddress = packet.getAddress();
                    players.get(PlayerCounter).socket = packet.getPort();
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
                    sendData(bytes, packet.getAddress(), packet.getPort());

                    if (PlayerCounter == 2) // init game
                    {    byteOut = new ByteArrayOutputStream();
                        dataOut = new DataOutputStream(byteOut);
                        try {
                            dataOut.writeInt(2);
                            dataOut.writeBoolean(true);
                            stan = 1;
                            dataOut.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bytes = byteOut.toByteArray();
                        System.out.println(players.toString());

                        game.add( new GameSimulation(players.get(0),players.get(1),width,height));
                         f.add( new FutureTask<>(game.get(GameNumber)));
                        e.execute(f.get(GameNumber));
                        sendDatatoAllClients(bytes,game.get(GameNumber));
                        GameNumber++;
                        players.clear();
                        PlayerCounter=0;
                    }


            }
            if(stan==1) {
                for (int i = 0; i < game.size(); i++) {
                    if ((packet.getAddress().equals(game.get(i).P1.ipAddress) && packet.getPort()==game.get(i).P1.socket) || (packet.getAddress().equals(game.get(i).P2.ipAddress) && packet.getPort() == game.get(i).P2.socket)) {
                        CurrentGame = i;
                    }
                }

                if (type == 3) {

                    try {
                        P = dataIn.readInt();

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
                    if (stan == 1) {
                        if (packet.getPort() == game.get(CurrentGame).P2.socket) {
                            bytes = byteOut.toByteArray();
                            game.get(CurrentGame).P1.P = P;

                            sendData(bytes, game.get(CurrentGame).P1.ipAddress, game.get(CurrentGame).P1.socket);
                        } else {
                            bytes = byteOut.toByteArray();
                            game.get(CurrentGame).P2.P = P;
                            sendData(bytes, game.get(CurrentGame).P2.ipAddress, game.get(CurrentGame).P2.socket);


                        }
                    }
                }
                if (type == 5) {
                    for (Player p : players) {
                        if (packet.getPort() == p.socket && packet.getAddress().equals(p.ipAddress)) {
                            p.TimeOut = 4;
                        }
                    }
                }
                if (type == 6) {

                    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                    DataOutputStream dataOut = new DataOutputStream(byteOut);
                    try {
                        dataOut.writeInt(6);

                        dataOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bytes = byteOut.toByteArray();

                    sendDatatoAllClients(bytes,game.get(CurrentGame));
                    System.out.println("Disconnected");
                    game.get(CurrentGame).interupted=true;
                    try {
                        DataBase.InsertGame(connection,game.get(CurrentGame).P1.Name,game.get(CurrentGame).P2.Name,"Interupted");
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }

                    f.get(CurrentGame).cancel(true);
                    if(f.size()==0)
                    {
                        stan=0;
                    }

                }
                if (type != 6 && type != 5 && type != 4 && type != 3 && type != 2 && type != 1) {
                    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                    DataOutputStream dataOut = new DataOutputStream(byteOut);
                    try {
                        dataOut.writeChars("hello");


                        dataOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bytes = byteOut.toByteArray();

                    sendData(bytes, packet.getAddress(), packet.getPort());
                    System.out.println("Not known");
                }

                    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                    DataOutputStream dataOut = new DataOutputStream(byteOut);
                    try {
                        dataOut.writeInt(4);
                        dataOut.writeInt((int) game.get(CurrentGame).ball1.x);
                        dataOut.writeInt((int) game.get(CurrentGame).ball1.y);
                        dataOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bytes = byteOut.toByteArray();
                    sendDatatoAllClients(bytes,game.get(CurrentGame));
                    if (f.get(CurrentGame).isDone()) {
                        try {
                            if(game.get(CurrentGame).Saved==false&& game.get(CurrentGame).interupted==false) {

                               /* BufferedWriter writer = new BufferedWriter(new FileWriter("Results.txt", true));
                                writer.append(f.get(CurrentGame).get());
                                writer.newLine();
                                writer.close();
                                */
                                DataBase.InsertGame(connection,game.get(CurrentGame).P1.Name,game.get(CurrentGame).P2.Name,f.get(CurrentGame).get());
                                game.get(CurrentGame).Saved=true;
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        byteOut = new ByteArrayOutputStream();
                         dataOut = new DataOutputStream(byteOut);
                        try {
                            dataOut.writeInt(2);
                            dataOut.writeBoolean(false);
                            dataOut.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bytes = byteOut.toByteArray();
                        sendDatatoAllClients(bytes,game.get(CurrentGame));
                        System.out.println("Game OVER  " + CurrentGame);
                        f.get(CurrentGame).cancel(true);
                        try {
                            SendScoreboard(game.get(CurrentGame));
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                        if(f.size()==0)
                        {
                            stan=0;
                        }

                    }


            }

        }
    }

    public void sendData(byte[] data, InetAddress ipAddress, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDatatoAllClients(byte[] data,GameSimulation game) {
        sendData(data,game.P1.ipAddress,game.P1.socket);
        sendData(data,game.P2.ipAddress,game.P2.socket);
    }

    public Map<String, Integer> results()
    {
        try {
            Scanner input = new Scanner(new FileReader("Results.txt"));
            results.clear();
            while (input.hasNextLine()) {
                String line=input.nextLine();
                if(results.containsKey(line))
                {
                     int wins=results.get(line);
                     results.put(line,++wins);
                }
                else
                {
                    results.put(line,1);
                }
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return results;
    }
    public void SendScoreboard(GameSimulation game) throws SQLException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOut);
        Map<String, Integer> results=DataBase.GetResults(connection);
        int resultsize;
        try {
            dataOut.writeInt(10);
            if(results.size()>10)
            {
               resultsize=10;
            }
            else
            {
                resultsize=results.size();

            }
            dataOut.writeInt(resultsize);
            for (int i = 0; i < resultsize; i++)
            {Object myKey = results.keySet().toArray()[i];

             int myValue = results.get(myKey);
             dataOut.writeInt(myKey.toString().length());
             dataOut.writeChars(myKey.toString());
             dataOut.writeInt(myValue);
             dataOut.close();
             bytes = byteOut.toByteArray();
             sendDatatoAllClients(bytes,game);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
