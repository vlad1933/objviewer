package com.dim.sceneGraph;

import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;

import com.dim.objviewer.Model;
import com.dim.objviewer.ObjViewer;

public class SceneGraph {
	private ArrayList<SgNode> nodeList = new ArrayList<SgNode>();
	private ArrayList<String> initList = new ArrayList<String>();
	
	public SgNode rootNode = new SgNode(null);
	private ObjViewer objViewerRef = null;
	private int currentPickedModelId = -1;
	public boolean wireframeMode = false;
	
	private static SceneGraph sgRef = null;

	public static SceneGraph getSceneGraphRef() {		
		return sgRef;
	}
	
	public SceneGraph(ObjViewer ov){
		SceneGraph.sgRef = this;
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
	 * Set everything up for drawing model in emphasized mode (because model is picked)
	 * @param modelID
	 */
	public void setUpPickedModel(int modelID){
		SgNode node = this.getNodeByModelId(modelID);
		
		//Background was picked or model specified by id wasn't found
		if(node == null){
			this.currentPickedModelId = -1;
			return;
		}
		
		System.out.println(node.model.fileName + " was picked!");
		this.currentPickedModelId = modelID;
		objViewerRef.triggerCanvasDisplay();		
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
	
	public SgNode getNodeByModelId(int modelId){
		for(int i = 1; i < nodeList.size(); i++){
			if(modelId == nodeList.get(i).model.id){
				return nodeList.get(i);
			}
		}
		return null;
	}
	
	public SgNode getRootNode(){
		return nodeList.get(0);
	}
	
	public void draw(GL gl, boolean solidColorMode){
		for(SgNode node : nodeList){
			//check for rootNode which ID is always 0
			if(node.nodeId == 0)
				continue;			
						
			//draw the model 
			//	in solidColors or normal mode
			//	in picked (emphasized) mode
			boolean foo = modelIsPicked(node);
			node.model.draw(gl,solidColorMode,foo);
			
			//if highlightHoles flag is true the holes in the mesh are shown
			if(this.objViewerRef.highlightMeshHoles)
				node.model.highLightHoles(gl);
		}
		
		//if(currentPickedModelId != -1)
			//getNodeByModelId(currentPickedModelId).model.draw(gl, false, true);
	}
	
	public boolean modelIsPicked(SgNode node){
		System.out.println("picked: " + currentPickedModelId);
		
		for(int i = 1; i < nodeList.size(); i++){
			if(node.model.id == this.currentPickedModelId)
				return true;
		}
		
		return false;
	}
	
	public void smoothModel(){
		if(currentPickedModelId == -1){
			System.out.println("No Model is picked, can't smooth");
			return;
		}
			
		SgNode node = getNodeByModelId(currentPickedModelId);
		node.model.smoothVerts();
		
	}
	
	/**
	 * find a Model inside the SceneGraph by its unique solid color value 
	 * (which is also used for drawing in picking mode) 
	 * @param colorValue
	 * @return
	 */
	public String findModelByColor(float[] colorValue){
		for(SgNode n : nodeList){
			float[] currentColor = n.model.mapIdToColor(n.model.id);
			if(currentColor == colorValue)
				return n.model.getFileName();
		}
		
		return "Nothing found!";  	
	}
	
	public void toggleWireframe(){
		if(this.wireframeMode)
			this.wireframeMode = false;
		else
			this.wireframeMode = true;
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