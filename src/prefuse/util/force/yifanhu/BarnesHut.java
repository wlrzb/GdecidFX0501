package prefuse.util.force.yifanhu;

import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableVisualItem;

public class BarnesHut {

    /* theta is the parameter for Barnes-Hut opening criteria
     */
    private float theta = (float) 1.2;
    private AbstractForce force;

    public BarnesHut(AbstractForce force) {
        this.force = force;
    }

    /* Calculates the ForceVector on node against every other node represented
     * in the tree with respect to force.
     */
    public ForceVector calculateForce(TableVisualItem node, QuadTree tree) {
        if (tree.mass() <= 0) {
            return null;
        }

        float distance = ForceVectorUtils.distance(node, tree);

        if (tree.isIsLeaf() || tree.mass() == 1) {
            // this is probably the case where tree has only the node.
            if (distance < 1e-8) {
                return null;
            }
            return force.calculateForce(node, tree);
        }

        if (distance * theta > tree.size()) {
            ForceVector f = force.calculateForce(node, tree, distance);
            f.multiply(tree.mass());
            return f;
        }

        ForceVector f = new ForceVector();
        for (QuadTree child : tree.getChildren()) {
            f.add(calculateForce(node, child));
        }
        return f;
    }

    public void setTheta(float theta) {
        this.theta = theta;
    }

    public float getTheta() {
        return theta;
    }
}
