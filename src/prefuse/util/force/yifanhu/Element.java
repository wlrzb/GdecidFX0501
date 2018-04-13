package prefuse.util.force.yifanhu;

public interface Element extends ElementProperties {

    /**
     * Returns the identifier.
     *
     * @return identifier
     */
    public Object getId();

    /**
     * Returns the label.
     *
     * @return label
     */
    public String getLabel();

    /**
     * Returns the location of this element in the store.
     *
     * @return store id
     */
    public int getStoreId();


    /**
     * Sets the label.
     *
     * @param label label
     */
    public void setLabel(String label);


    /**
     * Adds a timestamp.
     *
     * @param timestamp timestamp to add
     * @return true if the timestamp has been added, false if it existed already
     */
    public boolean addTimestamp(double timestamp);

    /**
     * Removes a timestamp.
     *
     * @param timestamp timestamp to remove
     * @return true if the timestamp has been removed, false if it didn't exist
     */
    public boolean removeTimestamp(double timestamp);

    /**
     * Returns true if this element has the given timestamp.
     *
     * @param timestamp timestamp
     * @return true if this element has the timestamp, false otherwise
     */
    public boolean hasTimestamp(double timestamp);

    /**
     * Returns all the timestamps this element belong to.
     *
     * @return timestamp array
     */
    public double[] getTimestamps();

}