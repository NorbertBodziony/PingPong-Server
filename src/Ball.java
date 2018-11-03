import java.awt.*;
import  java.util.*;

public class Ball {
    Random rand= new Random();
    public double xVel,yVel,x,y;
    public int w,h;
    public Color color;

    @Override
    public String toString() {
        return "Ball{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public Ball(int x, int y, int w, int h)
    {
        this.x=x;
        this.y=y;
        xVel=1;
        yVel=-1;
        this.w=w;
        this.h=h;
    }

    public void move()
    {
        x+=xVel;
        y+=yVel;
        if(y<15)
        {
            yVel=-yVel;
           // yVel=-(yVel-rand.nextInt(2));
        }
        if(y>h-15)
        {   yVel=-yVel;
           // yVel=-(yVel+rand.nextInt(2));
        }
    }
    public void draw(Graphics g)
    {
        color=new Color(255);
        //color=new Color((int)(Math.random()*40000));
        g.setColor(color);
        g.fillOval((int)x-15,(int)y-15,30,30);
    }

}
