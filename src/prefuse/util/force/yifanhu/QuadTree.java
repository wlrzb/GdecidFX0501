package prefuse.util.force.yifanhu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.scene.paint.Color;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableVisualItem;

public class QuadTree extends TableVisualItem {
	

    private final float posX;
    private final float posY;
    private final float size;
    private float centerMassX;  // X and Y position of the center of mass
    private float centerMassY;
    private int mass;  // Mass of this tree (the number of nodes it contains)
    private final int maxLevel;
    private AddBehaviour add;
    private List<QuadTree> children;
    private boolean isLeaf;
    public static final float eps = (float) 1e-6;

    public static QuadTree buildTree(TupleSet ts, int maxLevel) {
        float minX = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        
        Iterator items;
        
        items = ts.tuples();

        for (int i=0; items.hasNext(); i++) {
        	TableVisualItem node = (TableVisualItem)items.next();
            minX = Math.min(minX, (float)node.getX());
            maxX = Math.max(maxX, (float)node.getX());
            minY = Math.min(minY, (float)node.getY());
            maxY = Math.max(maxY, (float)node.getY());
        }

        float size = Math.max(maxY - minY, maxX - minX);
        QuadTree tree = new QuadTree(minX, minY, size, maxLevel);
        
        items = ts.tuples();
        for (int i=0; items.hasNext(); i++) {
        	TableVisualItem node = (TableVisualItem)items.next();
            tree.addNode(node);
        }

        return tree;
    }

    public QuadTree(float posX, float posY, float size, int maxLevel) {
        this.posX = posX;
        this.posY = posY;
        this.size = size;
        this.maxLevel = maxLevel;
        this.isLeaf = true;
        mass = 0;
        add = new FirstAdd();
    }

    public float size() {
        return size;
    }

    private void divideTree() {
        float childSize = size / 2;

        children = new ArrayList<>();
        children.add(new QuadTree(posX + childSize, posY + childSize,
                childSize, maxLevel - 1));
        children.add(new QuadTree(posX, posY + childSize,
                childSize, maxLevel - 1));
        children.add(new QuadTree(posX, posY, childSize, maxLevel - 1));
        children.add(new QuadTree(posX + childSize, posY,
                childSize, maxLevel - 1));

        isLeaf = false;
    }

    private boolean addToChildren(TableVisualItem node) {
        for (QuadTree q : children) {
            if (q.addNode(node)) {
                return true;
            }
        }
        return false;
    }

    private void assimilateNode(TableVisualItem node) {
        centerMassX = (mass * centerMassX + (float)node.getX()) / (mass + 1);
        centerMassY = (mass * centerMassY + (float)node.getY()) / (mass + 1);
        mass++;
    }

    @Override
    public double getX() {
        return (double)centerMassX;
    }

    @Override
    public double getY() {
        return (double)centerMassY;
    }
    
    
    public Iterable<QuadTree> getChildren() {
        return children;
    }


    public int mass() {
        return mass;
    }

    public boolean addNode(TableVisualItem node) {
        if (posX <= (float)node.getX() && (float)node.getX() <= posX + size
                && posY <= (float)node.getY() && (float)node.getY() <= posY + size) {
            return add.addNode(node);
        } else {
            return false;
        }
    }

    /**
     * @return the isLeaf
     */
    public boolean isIsLeaf() {
        return isLeaf;
    }

    class FirstAdd implements AddBehaviour {

        @Override
        public boolean addNode(TableVisualItem node) {
            mass = 1;
            centerMassX = (float)node.getX();
            centerMassY = (float)node.getY();

            if (maxLevel == 0) {
                add = new LeafAdd();
            } else {
                add = new SecondAdd();
            }

            return true;
        }
    }

    class SecondAdd implements AddBehaviour {

        @Override
        public boolean addNode(TableVisualItem node) {
            divideTree();
            add = new RootAdd();
            /* This QuadTree represents one node, add it to a child accordingly
             */
            addToChildren(QuadTree.this);
            return add.addNode(node);
        }
    }

    class LeafAdd implements AddBehaviour {

        @Override
        public boolean addNode(TableVisualItem node) {
            assimilateNode(node);
            return true;
        }
    }

    class RootAdd implements AddBehaviour {

        @Override
        public boolean addNode(TableVisualItem node) {
            assimilateNode(node);
            return addToChildren(node);
        }
    }

}

interface AddBehaviour {

    public boolean addNode(TableVisualItem node);
}