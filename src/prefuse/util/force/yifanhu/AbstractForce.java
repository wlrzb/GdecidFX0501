package prefuse.util.force.yifanhu;

import prefuse.visual.VisualItem;

public abstract class AbstractForce {

    public ForceVector calculateForce(VisualItem node1, VisualItem node2) {
        return calculateForce(node1, node2,
                ForceVectorUtils.distance(node1, node2));
    }

    public abstract ForceVector calculateForce(VisualItem node1, VisualItem node2,
            float distance);
}
