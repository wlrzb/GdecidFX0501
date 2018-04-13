package prefuse.util.force.yifanhu;

import prefuse.visual.VisualItem;

public interface Displacement {

    public void setStep(float step);

    public void moveNode(VisualItem node, ForceVector forceData);
}
