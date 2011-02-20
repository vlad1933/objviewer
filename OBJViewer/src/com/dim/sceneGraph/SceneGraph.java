package com.dim.sceneGraph;

import java.util.ArrayList;

import javax.media.opengl.GL;

import com.dim.objviewer.Model;
import com.dim.objviewer.ObjViewer;

public class SceneGraph {
	private ArrayList<SgNode> nodeList = new ArrayList<SgNode>();
	private ArrayList<String> initList = new ArrayList<String>();
	
	public SgNode rootNode = new SgNode(null);
	private ObjViewer objViewerRef = null;
	
	public SceneGraph(ObjViewer ov){
		this.objViewerRef = ov;
		nodeList.add(rootNode);
	}
	
	/**
	 * Because there is no GL context outside of display() or init(), in display() 
	 * the initList is checked for new "to-load" Models. Because triggered from display() GL context is available.
	 * @param model_path Path of the Model to be loaded
	 */
	public void pushIntoInitList(String model_path){
		initList.add(model_path);
		objViewerRef.triggerCanvasDisplay();
	}
	
	/**
	 * Is there a Model to init?
	 * @return true if there is something to init, false if there is nothing to init. 
	 */
	public boolean checkForInitList(){
		if(initList.size() == 0){
			return false;
		}
				
		return true;
				
	}
	
	/**
	 * Returns Value of InitList and purges the list afterwards
	 * @return Path_String of the to initialize Model 
	 */
	public String popFromInitList(){
		String str = initList.get(0);
		initList.clear();
		return str;
	}
	
	/**
	 * Attaches a Node to another Node which is specified by its ID
	 * @param node Node (child) that will be attached
	 * @param nodeID ID of Node (parent) to which a Node (child) will be attached
	 */
	public boolean addNode(Model model, int nodeID){
		SgNode parent = getNodeById(nodeID);
		SgNode child = new SgNode(model);
		
		if(parent == null){
			System.out.println("could not find Node with specified ID");
			return false;
		}
		
		parent.attachNode(child);
		nodeList.add(child);
		
		return true;		
		
	}
	
	/**
	 * Searches for Node in ArrayList specified by nodeID. Returns the Node object if found, if not it returns null;
	 * @param int nodeId
	 * @return Node Object
	 */
	public SgNode getNodeById(int nodeId){
		for(SgNode n: nodeList){
			if(nodeId == n.nodeId){
				return n;
			}
		}
		return null;
	}
	
	public SgNode getRootNode(){
		return nodeList.get(0);
	}
	
	public void draw(GL gl, boolean pickingMode){
		for(SgNode node : nodeList){
			//check for rootNode which ID is always 0
			if(node.nodeId == 0)
				continue;
			
			//draw the model in picking or normal mode
			node.model.draw(gl,pickingMode);
			
			//if highlightHoles flag is true the holes in the mesh are shown
			if(this.objViewerRef.highlightMeshHoles)
				node.model.showHoles(gl);
		}
	}
}


class SgNode{
	private boolean isRootNode = false;
	public Model model = null;
	private boolean isLeaf = true;
		
	private ArrayList<Integer> succList = new ArrayList<Integer>();
	public SgNode pred = null;
	public int nodeId;
	
	private static int _nodeId = 0;
	
	public SgNode(Model model){
		this.nodeId = SgNode._nodeId++;
		
		if(model == null)
			this.isRootNode = true;
		
		this.model = model;
	}	
	
	public void attachNode(SgNode node){
		if(this.equals(node)){
			System.out.println("Attaching node as succ of itself -> error");
			return;
		}
		
		this.isLeaf = false;
		this.succList.add(node.nodeId);
	}
}