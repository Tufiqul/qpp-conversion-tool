package gov.cms.qpp.conversion.model;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;

/**
 * Represents a node of data that should be converted. Consists of a key/value
 * Map that holds the data gleaned from an input file.
 * 
 * Nodes can contain other nodes as children to create a hierarchy.
 * @author David Uselmann
 * 
 */
public class Node implements Serializable {

	private static final long serialVersionUID = 4602134063479322076L;
	private NodeType type;
	private Map<String, String> data = new HashMap<>();

	private List<Node> childNodes;

	private Node parent;

	private boolean validated;
	private String internalId;

	public Node() {
		this.childNodes = new ArrayList<>();
	}

	public Node(String id) {
		this();
		setId(id);
		this.type = NodeType.getTypeById(id);
	}

	public Node(Node parentNode) {
		this();
		this.parent = parentNode;
	}

	public Node(Node parentNode, String id) {
		this(parentNode);
		setId( id );
	}

	public String getValue(String name) {
		return data.get(name);
	}

	public void putValue(String name, String value) {
		data.put(name, value);
	}

	public void setId(String templateId) {
		this.type = NodeType.getTypeById( templateId );
		this.internalId = templateId;
	}

	public String getId() {
		return internalId;
	}

	public List<Node> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(List<Node> childNodes) {
		this.childNodes = childNodes;
	}

	/**
	 * convenience for adding multiple child nodes
	 *
	 * @param childNodes
	 */
	public void addChildNodes(Node... childNodes) {
		this.setChildNodes( Arrays.asList( childNodes ) );
	}

	public void addChildNode(Node childNode) {
		if (childNode == null || childNode == this) {
			return;
		}
		this.childNodes.add(childNode);
	}

	@Override
	public String toString() {
		return toString("");// no tabs to start
	}

	protected String toString(String tabs) {
		return tabs + selfToString() + "\n" + childrenToString(tabs + "\t");
	}

	protected String selfToString() {
		return "Node: templateId: " + getId() + ", data: " + data;
	}

	protected String childrenToString(String tabs) {
		StringJoiner children = new StringJoiner("\n");
		if (childNodes.isEmpty()) {
			children.add(" -> (none)");
		} else {
			children.add(": ");
			for (Node child : childNodes) {
				children.add(child.toString(tabs));
			}
		}
		return tabs + "childNodes of " + getId() + children;
	}

	public Set<String> getKeys() {
		return data.keySet();
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	/**
	 * Search this and child nodes for first node with matching id
	 *
	 * @param id templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @return the first {@link gov.cms.qpp.conversion.model.Node} in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id or null
	 * if no matches are found
	 */
	public Node findFirstNode(String id) {
		List<Node> nodes = this.findNode(id, Node::foundNode);
		return nodes.isEmpty() ? null : nodes.get(0);
	}

	private static Boolean foundNode(List<?> nodes) {
		return !nodes.isEmpty();
	}

	/**
	 * Search of this and child nodes for matching ids
	 *
	 * @param id templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @return a list of {@link gov.cms.qpp.conversion.model.Node}s in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id
	 */
	public List<Node> findNode(String id) {
		return findNode( id, null );
	}

	/**
	 * Search of this and child nodes for matching ids
	 *
	 * @param id templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @param bail lambda that consumes a list and returns a boolean that governs early exit
	 * @return a list of {@link gov.cms.qpp.conversion.model.Node}s in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id
	 */
	public List<Node> findNode(String id, Predicate<List<?>> bail) {
		List<Node> foundNodes = new ArrayList<>();

		if (id.equals( getId() )) {
			foundNodes.add(this);
		}

		for (Node childNode : childNodes) {
			if (bail != null && bail.test(foundNodes)) {
				break;
			}
			List<Node> matches = childNode.findNode(id, bail);
			foundNodes.addAll( matches );
		}

		return foundNodes;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public NodeType getType() {
		return type;
	}
}
