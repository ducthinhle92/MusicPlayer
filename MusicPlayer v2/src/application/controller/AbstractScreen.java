package application.controller;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class AbstractScreen {
	private Parent root;
	private boolean initialized = false;	
	protected boolean paused = false;

	public AbstractScreen(Stage primaryStage) {
		root = (Parent) primaryStage.getScene().getRoot();
	}
	
	/**
	 * Start the controller
	 */
	public void start() {
		if(!initialized) {
			initialize();
			initialized = true;
		}
		
		paused = false;
	}
	
	public void pause() {
		paused = true;
	}
	
	public void resume() {
		paused = false;
	}
	
	/**
	 * Where to initialize components
	 */
	protected void initialize() {
		
	}
	
	public Node findNodeById(String id) {
		return traverseParent(root, id);
	}

	private Node traverseParent(Node _root, String id) {
		SplitPane splitPane = null;
		Pane pane = null;
		Group group = null;
		ToolBar toolBar = null;
		
		if(_root.getId() != null && _root.getId().equals(id))
			return _root;
		
		try {
			pane = (Pane) _root;
			return searchParent(pane, id);
		} catch(ClassCastException e) {
//			System.out.println(e.getMessage());
		}
		
		try {
			splitPane = (SplitPane) _root;
			return searchParent(splitPane, id);			
		} catch(ClassCastException e) {
//			System.out.println(e.getMessage());
		}
		
		try {
			group = (Group) _root;
			return searchParent(group, id);
		} catch(ClassCastException e) {
//			System.out.println(e.getMessage());
		}
		
		try {
			toolBar = (ToolBar) _root;
			return searchParent(toolBar, id);
		} catch(ClassCastException e) {
//			System.out.println(e.getMessage());
		}
		
		// this is a leaf node
		if(_root.getId() != null)
			System.out.println("Leaf node: " + _root.getId());
		return null;
	}
	
	private Node searchParent(ToolBar _root, String id) {
		for(Node node : _root.getItems()) {
			if(node.getId() != null && node.getId().equals(id)) {
				System.out.println("found!");
				return node;
			}
			else {
				Node temp = traverseParent(node, id);
				if(temp != null)
					return temp;
			}
		}
		return null;
	}

	private Node searchParent(Group _root, String id) {
		for(Node node : _root.getChildren()) {
			if(node.getId() != null && node.getId().equals(id)) {
				System.out.println("found!");
				return node;
			}
			else {
				Node temp = traverseParent(node, id);
				if(temp != null)
					return temp;
			}
		}
		return null;
	}

	private Node searchParent(SplitPane _root, String id) {
		System.out.println(_root + " - children: " 
				+ _root.getChildrenUnmodifiable().size());
		for(Node node : _root.getItems()) {
			if(node.getId() != null && node.getId().equals(id)) {
				System.out.println("found!");
				return node;
			}
			else {
				Node temp = traverseParent(node, id);
				if(temp != null)
					return temp;
			}
		}
		return null;
	}

	private Node searchParent(Pane _root, String id) {
		for(Node node : _root.getChildren()) {
			if(node.getId() != null && node.getId().equals(id)) {
				System.out.println("found!");
				return node;
			}
			else {
				Node temp = traverseParent(node, id);
				if(temp != null)
					return temp;
			}
		}
		return null;
	}
}
