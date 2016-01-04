
/**
 * Created by sesshoumaru on 02.01.16.
 */
public class Circle {
    float x;
    float y;
    float radius;

    Circle next;

    public Circle(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;

    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }


    public Circle getNext() {
        return next;
    }

    public void setNext(Circle next) {
        this.next = next;
    }


    public double distanceTo(Circle circle) {
        double x = this.x - circle.getX();
        double y = this.y - circle.getY();
        return Math.sqrt(x * x + y * y);
    }


}
