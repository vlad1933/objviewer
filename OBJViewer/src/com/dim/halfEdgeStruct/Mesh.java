package com.dim.halfEdgeStruct;

import java.util.ArrayList;

import com.dim.objviewer.Model;
import com.dim.objviewer.ObjViewer;
import com.dim.objviewer.Vert3;

public class Mesh implements Runnable{

	private int[][] faceList;
	//Edges
	private ArrayList<HE_edge> edgeList = new ArrayList<HE_edge>();
	//Points
	private ArrayList<pList> PList = new ArrayList<pList>();
	//
	private ArrayList<Integer> ptrPList = new ArrayList<Integer>();
	
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
	 * Builds the Half-Edge Mesh
	 */
	public void run() {
		// TODO Auto-generated method stub

		/*
		 * Mesh nur für 3eckige Faces definiert
		 */
		
		int i,j,l  = 0;
		i = 0;
		j = 0;
		
						
		for(l = 0; l < faceList.length; l++ ){ //index des Face
			/*
			 * wir gehen davon aus dass die Kante a die erste b die zweite und c die 3. HK sind. 
			 * Auf diesem prinzip beruht folgende berechnung.
			 */			
			HE_edge hk_a = new HE_edge(faceList[l][j], (HE_edge)null, (HE_edge)null, new HE_face(l));			
			HE_edge hk_b = new HE_edge(faceList[l][(j+1)%3], (HE_edge)null, (HE_edge)null, new HE_face(l));
			hk_a.setNext(hk_b);			
			HE_edge hk_c = new HE_edge(faceList[l][(j+2)%3], (HE_edge)null, (HE_edge)null, new HE_face(l));
			hk_b.setNext(hk_c);
			hk_c.setNext(hk_a);			
			
			/* Zufügen zu edgeList */
			edgeList.add(hk_a);
			edgeList.add(hk_b);
			edgeList.add(hk_c);
			
			
			for(i = 0; i < 3; i++){ // i ist index vom punkt
								
				/*
				 * Vorgänger
				 */
				j = (i + 2) % 3; //j ist vorgänger-punkt von i								
				if(faceList[l][j] > faceList[l][i]){ 	//ist der Punkt größer als der PList-Wert(int Indize v. pList) wird er rausgeschmissen 
					pList pl = new pList(faceList[l][j], new HE_face(l));
					//auf vorhandene gegenkante prüfen
					if(!plCompare(pl,faceList[l][i]))
					{
						//System.out.println("pl: " + pl);
						PList.add(pl);	//speichern des pList Objekts in einer Liste				
						ptrPList.add(faceList[l][i]);	//speichern des Indizes - quasi Startpunkt
																		
					}
					else{
						/*
						 * Da hier eine innere Kante gefunden wurde, ist dies eine Gegenkante einer in der
						 * edgeList bereits vorhandenen kannte mit parameter pair_edge == null
						 * In der for Schleife sucht man die Gegenkante die den Endpunkt v. pl (faceList[l][j]) hat
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
				 * Nachfolger
				 */
				j = (i + 4) % 3; //j ist nachfolger-punkt von i								
				if(faceList[l][j] > faceList[l][i]){
					pList pl_ = new pList(faceList[l][j], new HE_face(l));
					if(!plCompare(pl_,faceList[l][i]))
					{
						//System.out.println("pl: " + pl_);
						PList.add(pl_);	//speichern des pList Objekts in einer Liste				
						ptrPList.add(faceList[l][i]);	//speichern des Indizes
						
					}
					else{
						/*
						 * Da hier eine innere Kante gefunden wurde, ist dies eine Gegenkante einer in der
						 * edgeList bereits vorhandenen kannte mit parameter nextedge = null
						 */
						
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
		
		//indeces of points that are adjacent
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

