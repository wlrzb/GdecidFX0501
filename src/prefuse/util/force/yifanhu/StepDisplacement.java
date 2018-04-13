package prefuse.util.force.yifanhu;

import prefuse.visual.VisualItem;

public class StepDisplacement implements Displacement {

    private float step;

    public StepDisplacement(float step) {
        this.step = step;
    }

    private boolean assertValue(float value) {
        boolean ret = !Float.isInfinite(value) && !Float.isNaN(value);
        return ret;
    }

    @Override
    public void moveNode(VisualItem node, ForceVector forceData) {
        ForceVector displacement = forceData.normalize();
        displacement.multiply(step);

        float x = (float)node.getX() + displacement.x();
        float y = (float)node.getY() + displacement.y();

        if (assertValue(x)) {
            node.setX((double)x);
        }
        if (assertValue(y)) {
            node.setY((double)y);
        }
    }

    @Override
    public void setStep(float step) {
        this.step = step;
    }


}
