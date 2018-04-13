package prefuse.util.force.yifanhu;

public class ForceVector {

    protected float x;
    protected float y;
	
    public ForceVector(ForceVector vector) {
        this.x = vector.x();
        this.y = vector.y();
    }
    
    public ForceVector(float x, float y) {
        this.x = x;
        this.y = y;
    }
	
    public ForceVector() {
        this.x = 0;
        this.y = 0;
    }
    

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void add(ForceVector f) {
        if (f != null) {
            x += f.x();
            y += f.y();
        }
    }

    public void multiply(float s) {
        x *= s;
        y *= s;
    }

    public void subtract(ForceVector f) {
        if (f != null) {
            x -= f.x();
            y -= f.y();
        }
    }

    public float getEnergy() {
        return x * x + y * y;
    }

    public float getNorm() {
        return (float) Math.sqrt(getEnergy());
    }

    public ForceVector normalize() {
        float norm = getNorm();
        return new ForceVector(x / norm, y / norm);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
}
