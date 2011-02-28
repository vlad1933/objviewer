package com.dim.halfEdgeStruct;

import java.util.ArrayList;

import com.dim.objviewer.Model;
import com.dim.objviewer.ObjViewer;
import com.dim.objviewer.Vert3;
/**
 * Computes a mesh out of triangular faces (triangular polygons).
 * Provides methods for getting adjacence data.
 * @author K. Wengrzinek & D. Martens
 *
 */
public class Mesh implements Runnable{

	//2-dimensional array of faces taken as parameter e.g. {{1,2,3},{3,4,5},..}
	private int[][] faceList;
	//Edges defined by the faces
	private ArrayList<HE_edge> edgeList = new ArrayList<HE_edge>();
	//List of pList elements; a pList Object consists of a point (int) and a face (HE_Face) 
	private ArrayList<pList> PList = new ArrayList<pList>();
	//Contains begin-points of edges.From the index of the begin point in ptrPList one can imply to a pList object in in PList
	private ArrayList<Integer> ptrPList = new ArrayList<Integer>();
	
	//is set true when mesh is computed
	private boolean dataStructureReady = false;
		

	public Mesh(int[][] fl){
		
		this.faceList = fl;
		//System.out.println("in mesh constructor");
		
	}
	
	public boolean isReady(){
		return dataStructureReady;
	}
	
	public void printPList(){
		for(pList p : PList)
			System.out.println(p);
	}
	
	@Override
	/**
	 * Builds the Half-Edge mesh
	 */
	public void run() {
		// TODO Auto-generated method stub
		
		//Indicates the face 
		int l  = 0; 
		//Indicates the point in face[l]; Can only be 0,1 or 2
		int i = 0;
		//Indicates the successor and predecessor of point i
		int j = 0;
		
						
		for(l = 0; l < faceList.length; l++ ){ //index of face
			/*
			 * Wir gehen davon aus, dass die Kante A die erste B die zweite und C die 3. HK sind. 
			 * Auf diesem Prinzip beruht die folgende Operation.
			 */			
			HE_edge hk_a = new HE_edge(faceList[l][j], (HE_edge)null, (HE_edge)null, new HE_face(l));			
			HE_edge hk_b = new HE_edge(faceList[l][(j+1)%3], (HE_edge)null, (HE_edge)null, new HE_face(l));
			hk_a.setNext(hk_b);			
			HE_edge hk_c = new HE_edge(faceList[l][(j+2)%3], (HE_edge)null, (HE_edge)null, new HE_face(l));
			hk_b.setNext(hk_c);
			hk_c.setNext(hk_a);			
			
			//Add edges to edgeList
			edgeList.add(hk_a);
			edgeList.add(hk_b);
			edgeList.add(hk_c);
			
			
			for(i = 0; i < 3; i++){ // i indicates a point in face[l]
								
				/*
				 * Predecessor
				 */
				j = (i + 2) % 3; //j is predecessor point of i
				//ist der Punkt größer als der PList-Wert(int Indize v. pList) wird er rausgeschmissen
				//if the point at face[l][j] (pred) is bigger than the point at face[l][i] it is not considered
				if(faceList[l][j] > faceList[l][i]){ 	 
					pList pl = new pList(faceList[l][j], new HE_face(l));
					//look for pair-edge
					if(!plCompare(pl,faceList[l][i]))
					{
						//System.out.println("pl: " + pl);
						PList.add(pl);	//save the pList Object				
						ptrPList.add(faceList[l][i]);	//Save the starting point																		
					}
					else{
						/*
						 * Da hier eine innere Kante gefunden wurde, ist dies eine Gegenkante einer in der
						 * edgeList bereits vorhandenen Kante mit Parameter pair_edge == null
						 * In der for-Schleife sucht man die Gegenkante die den Endpunkt v. pl (faceList[l][j]) hat
						 */						
						HE_edge pair_edge = null;
						for(int z = edgeList.size()-1; z > (edgeList.size()-4); z--){
							if(edgeList.get(z).getVert() == faceList[l][i])
								pair_edge = edgeList.get(z);
						}
						
						findAndSetPairEdge(faceList[l][j], pair_edge);
						
						
					}
					
				}
				
				
				/*
				 * Successor
				 */
				j = (i + 4) % 3; //j is the successor point of i in face l (Remember: a face consists of 3 points)								
				if(faceList[l][j] > faceList[l][i]){
					pList pl_ = new pList(faceList[l][j], new HE_face(l));
					if(!plCompare(pl_,faceList[l][i]))
					{
						//System.out.println("pl: " + pl_);
						PList.add(pl_);	//save the pList Object					
						ptrPList.add(faceList[l][i]);	//Save the starting point
						
					}
					else{
												
						HE_edge pair_edge = null;
						for(int z = edgeList.size()-1; z > (edgeList.size()-4); z--){
							if(edgeList.get(z).getVert() == faceList[l][j])
								pair_edge = edgeList.get(z);								
						}
												
							
						findAndSetPairEdge(faceList[l][i], pair_edge);
						
						
					}
				}
						
			}//inner for			
			
			
		}

		
				
		/**
		 * Gibt geordnete Liste der pList Elemente aus
		 */
		/*
		for(int x = 0; x < PList.size(); x++){			
			for(int y = 0; y < ptrPList.size(); y++){
				if(ptrPList.get(y) == x ){
					System.out.print("p[" + ptrPList.get(y) + "] = " + " pList: " + PList.get(y));
					System.out.println("");
				}
			}			
		}
		
		*/
		
		
		this.dataStructureReady = true;
		
	}	
	
	/**
	 * Find and set PairEdge from edgeList
	 * @param begin_point faceList[l][i]
	 * @param edge	Pair Edge
	 * @return
	 */
	public boolean findAndSetPairEdge(int begin_point, HE_edge edge){
		for(HE_edge e: edgeList){
			if(e.getVert() == begin_point){
				if(e.getNext().getNext().getVert() == edge.getVert()){							
					
					e.setPair(edge);
					edge.setPair(e);
										
					return true;
				}				
			}
		}
				
		return false;
	}

	/**
	 * Searching for PairEdge in PList list. If found it returning true 
	 * @param pl
	 * @param index starting point in Face 
	 * @return true if found 
	 */
	public boolean plCompare(pList pl, int index){
		int i = 0;		
		
		for(i = 0; i < PList.size(); i++){			
			if(PList.get(i).getPoint() == pl.getPoint() && index == ptrPList.get(i))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Gibt alle Punkte von Außenkanten zurück. Erst Anfangspunkt der Edge dann Endpunkt usw.
	 * @return
	 */
	public int[] getBorderEdgePoints(){
		ArrayList<Integer> points = new ArrayList<Integer>();
		for(HE_edge e : edgeList){
			if(e.getPair() == null){
				points.add(e.getNext().getNext().getVert());
				points.add(e.getVert());
			}				
		}
		
		int[] pointsArr = new int[points.size()];
		int i = 0;
		for(int p: points){
			pointsArr[i] = p;
			i++;
			//System.out.print(p+",");
		}
		
		
		return pointsArr;
	}
	
	/**
	 * Returns all neighbour points of the by "vert" specified point 
	 * @param vert index of vertex that will be smoothed
	 * @return vertex indices (points) or null if nothing found
	 */
	public int[] getAdjacentVertIndeces(int vert){		
		//all adges pointing to the specified point
		ArrayList<HE_edge> adjEdges = new ArrayList<HE_edge>();
		
		//search for an edge pointing to vert
		for(HE_edge e : edgeList){
			if(e.getVert() == vert){
				adjEdges.add(e);
				break;
			}				
		}
					
		//Point wasnt found
		if(adjEdges.size() == 0)
			return null;
							
		
		//get all adjacent edges
		HE_edge startingEdge = adjEdges.get(0);	
		HE_edge iteratingEdge = startingEdge.getNext().getPair();
		System.out.println(iteratingEdge);
		
		while(!startingEdge.equals(iteratingEdge)){
			//is vert on border?
			if(iteratingEdge == null){
				System.out.println("has no pair edge -> must be on border");
				edgeList.clear();
				return null;
			}
			
			adjEdges.add(iteratingEdge);
			iteratingEdge = iteratingEdge.getNext().getPair();			
		}
		
		//Indices of points that are adjacent
		int[] points = new int[adjEdges.size()];
		int i = 0;
		for(HE_edge e : adjEdges){
			if(e.getPair() == null) //not needed
				return null;
			points[i] = e.getPair().getVert();			
			i++;
		}
		
		return points;
	}


		
}

