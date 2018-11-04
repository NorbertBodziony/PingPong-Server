import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.Callable;

public class GameSimulation implements Callable<String> {
    public  Player P1;
    public  Player P2;
    Ball ball1;
    public int width;
    public int height;
    int stan;
    public byte[] bytes;
    private String Winner="";
    public boolean Saved=false;
    public  boolean interupted=false;
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(byteOut);

    public GameSimulation(Player p1, Player p2, int width, int height) {
        P1 = p1;
        P2 = p2;
        this.width = width;
        this.height = height;
        ball1=new Ball((int)width/2,(int)height/2,(int)width,(int)height);
        System.out.println("Init game");
    }

    @Override

    public String call() throws Exception {

        while(true) {
            ball1.move();
            if (colision() == false) {
                return Winner;
            }
            Thread.sleep(10);


        }
    }
    private boolean colision()
    {
        if(ball1.y>= P2.P&&ball1.y<=P2.P+200&&ball1.x>=(width-4*10))
        {   ball1.xVel++;
            ball1.xVel=-ball1.xVel;
        }
        if(ball1.y>=P1.P&&ball1.y<=P1.P+200&&ball1.x<=4*10)
        {   ball1.xVel--;
            ball1.xVel=-ball1.xVel;
        }
        if (ball1.x>width||ball1.x<0)
        {  if(ball1.x>width)
            {Winner=P1.Name;}
            else { Winner=P2.Name;}
            return false;

        }
        return true;

    }
}
