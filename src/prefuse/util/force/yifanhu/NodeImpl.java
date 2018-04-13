package prefuse.util.force.yifanhu;

import prefuse.visual.VisualItem;

public class NodeImpl implements Node {

	VisualItem  item;
	
    @Override
    public float x() {
        return (float)item.getX();
    }

    @Override
    public float y() {
        return (float)item.getY();
    }
}
