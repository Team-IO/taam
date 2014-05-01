package founderio.taam.multinet;


public class AStarNode<T> implements Comparable<AStarNode<T>> {
	public final T object;
	protected Integer dist = Integer.MAX_VALUE;
	protected Double value = 0d;
	protected AStarNode<T> predecessor;
	
	public AStarNode(T object, Double value) {
		this.object = object;
		this.value = value;
	}
	
	@Override
	public int compareTo(AStarNode<T> o) {
		return value.compareTo(o.value);
	}
	
	public AStarNode<T> getPredecessor() {
		return predecessor;
	}
	
	public int getStepDistance() {
		return dist;
	}
}