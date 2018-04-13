package prefuse.util.force.yifanhu;

public class ForceLayoutData extends ForceVector {

    public float energy0;
    public float step;
    public int progress;

    public ForceLayoutData() {
        progress = 0;
        step = 0;
        energy0 = Float.POSITIVE_INFINITY;
    }
}
