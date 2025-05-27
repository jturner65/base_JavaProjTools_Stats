package base_StatsTools.analysis.base;

import java.util.ArrayList;

import base_Math_Objects.vectorObjs.floats.myPointf;
import base_Math_Objects.vectorObjs.floats.myVectorf;
import base_Render_Interface.IRenderInterface;
import base_StatsTools.summary.base.baseProbSummary;

public abstract class baseAnalyzer {
	public final int ID;
	protected static int idGen = 0;
	/**
	 * current trajectory summary
	 */
	public baseProbSummary[] summaries;	
	public static final int
		posIDX		= 0,
		velIDX		= 1,
		accelIDX	= 2,
		jerkIDX		= 3;
	protected static final int numStatsToMeasure = 4;
	public static final String[] statNames = new String[]{"Position","Velocity","Acceleration","Jerk"};
	
	public static final String[] statDispLabels = new String[]{"P","V","X","J"};

	protected boolean debug = false;
	protected final String frmtStr = "%4.2f";

	public baseAnalyzer() {
		ID = idGen++;
	}
	
	public void setDebug(boolean val) {debug=val;}
	
	/**
	 * find the average value, ROC, rate of ROC, etc of the passed trajectory of areas of morph maps
	 * assumes each area sample is uniformly spaced in time
	 * @param pts
	 */
	@SuppressWarnings("rawtypes")
	public abstract void analyzeTrajectory(ArrayList pts, String name);
	

	/**
	 * this will properly format and display a string of text, and will translate the width, so multiple strings can be displayed on the same line with different colors
	 * @param tclr
	 * @param txt
	 */
	protected final void showOffsetText_RightSideMenuAbs(IRenderInterface ri, int[] tclr, float dist,  String txt) {
		ri.setFill(tclr,tclr[3]);ri.setStroke(tclr,tclr[3]);
		ri.showText(txt,0.0f,0.0f,0.0f);
		ri.translate(dist, 0.0f,0.0f);	
	}
	
	protected final void showOffsetText_RightSideMenu(IRenderInterface ri, int[] tclr, float mult,  String txt) {
		ri.setFill(tclr,tclr[3]);ri.setStroke(tclr,tclr[3]);
		ri.showText(txt,0.0f,0.0f,0.0f);
		ri.translate(txt.length()*mult, 0.0f,0.0f);		
	}
	protected void drawSingleMinMaxTxt(IRenderInterface ri, int clrLabel, String txt, float ltrMult) {
		showOffsetText_RightSideMenu(ri,ri.getClr(clrLabel, 255),1.4f* ltrMult, txt);			
	}
	
	
	
	public void drawAnalyzerData(IRenderInterface ri, String[] mmntDispLabels, float[] trajWinDims, String name) {
		float yDisp = trajWinDims[3];
		ri.pushMatState();		
		ri.translate(5.0f, yDisp, 0.0f);
			showOffsetText_RightSideMenu(ri,ri.getClr(IRenderInterface.gui_Black, 255), 6.0f, name);
		ri.popMatState();
		ri.pushMatState();
			ri.translate(5.0f, 2*yDisp, 0.0f);			
			drawAllSummaryInfo(ri,mmntDispLabels, yDisp, trajWinDims[0]);
		ri.popMatState();
		
		ri.translate(trajWinDims[0], 0.0f, 0.0f);
		ri.drawLine(0.0f,trajWinDims[2], 0.0f, 0.0f, trajWinDims[0]+ trajWinDims[2], 0.0f );
	}//_drawAnalyzerData
	
	/**
	 * 
	 * @param ri
	 * @param mmntDispLabels
	 * @param trajWinDims array of float dims - width,height of window, y value where window starts, y value to displace every line
	 * @param yDisp
	 * @param name
	 */
	public void drawAnalyzerGraphs(IRenderInterface ri, String[] mmntDispLabels, float[] trajWinDims, String name) {
		float yDisp = trajWinDims[3];
		float[] graphRect = new float[] {10.0f,10.0f,trajWinDims[0]-20.0f,(trajWinDims[1]/(1.0f*summaries.length + 1))-20.0f};
		ri.pushMatState();		
		ri.translate(5.0f, yDisp, 0.0f);
			showOffsetText_RightSideMenu(ri,ri.getClr(IRenderInterface.gui_Black, 255), 6.0f, name);
		ri.popMatState();
		ri.pushMatState();
			ri.translate(5.0f, 2*yDisp, 0.0f);			
			drawAllGraphInfo(ri,mmntDispLabels,graphRect, yDisp, trajWinDims[0]);
		ri.popMatState();
		
		ri.translate(trajWinDims[0], 0.0f, 0.0f);
		ri.drawLine(0.0f,trajWinDims[2], 0.0f, 0.0f, trajWinDims[0]+ trajWinDims[2], 0.0f );
	}//_drawAnalyzerData
	
	
	
	protected void drawAllSummaryInfo(IRenderInterface ri, String[] mmntDispLabels, float txtLineYDisp, float perDispBlockWidth) {//
		float mult = (perDispBlockWidth)/((mmntDispLabels.length + 1) * 3.1f);
		//System.out.println("perDispBlockWidth: " + perDispBlockWidth + " | mult : " + mult);
		ri.pushMatState();
		if(mult < 17) {ri.scale(1.0f,.93f,1.0f);}
		for(int i=0;i<summaries.length;++i) {		//per summary - single summary for pos, vel, accel, jerk, etc of each point
			//title of each summary 
			ri.pushMatState();
				showOffsetText_RightSideMenu(ri,ri.getClr(IRenderInterface.gui_Black, 255),1.4f* mult, statDispLabels[i]);
				for(int j=0;j<mmntDispLabels.length;++j) {		showOffsetText_RightSideMenuAbs(ri,ri.getClr(IRenderInterface.gui_DarkBlue, 255), mult*3.5f, mmntDispLabels[j]);}
			ri.popMatState();			
			ri.translate(0.0f,txtLineYDisp,0.0f);

			
			drawSingleSummary(ri,mmntDispLabels, summaries[i],txtLineYDisp,mult);
			ri.translate(0.0f,.8f*txtLineYDisp,0.0f);
		}
		ri.popMatState();
	}
	protected abstract void drawSingleSummary(IRenderInterface ri, String[] mmntDispLabels, baseProbSummary smryRaw, float txtLineYDisp, float ltrMult);
	
	/**
	 * draw stats graph info
	 * @param mmntDispLabels
	 * @param txtLineYDisp
	 * @param perDispBlockWidth
	 */
	protected final void drawAllGraphInfo(IRenderInterface ri, String[] mmntDispLabels, float[] graphRect, float txtLineYDisp, float perDispBlockWidth) {
		float mult = (perDispBlockWidth)/((mmntDispLabels.length + 1) * 3.1f);
		ri.pushMatState();
		if(mult < 17) {ri.scale(1.0f,.93f,1.0f);}
		for(int i=0;i<summaries.length;++i) {		//per summary - single summary for pos, vel, accel, jerk, etc of each point
			//title of each summary graph 
			ri.pushMatState();
				showOffsetText_RightSideMenu(ri,ri.getClr(IRenderInterface.gui_Black, 255),1.0f* mult, statDispLabels[i]);		
				drawSingleSmryGraphMinMaxLbls(ri, i, mult);
			ri.popMatState();			
			ri.translate(0.0f,txtLineYDisp,0.0f);		
			ri.pushMatState();
			ri.setFill(50,50,50,255);
			ri.drawRect(0.0f, -5.0f, graphRect[2], graphRect[3]+10.0f);
			ri.popMatState();
			drawSingleSmryGraph(ri,mmntDispLabels, i,graphRect,mult);
			ri.translate(0.0f,1.5f*txtLineYDisp,0.0f);
		}
		ri.popMatState();
	}//drawAllGraphInfo
	protected abstract void drawSingleSmryGraphMinMaxLbls(IRenderInterface ri, int smryIdx, float ltrMult);
	protected abstract void drawSingleSmryGraph(IRenderInterface ri, String[] mmntDispLabels, int smryIdx, float[] graphRect,  float ltrMult);
	
	/**
	 * draw a single trajectory within the bounds defined by trajRect
	 * @param ri
	 * @param trajRect rectangle of upper left corner x,y, width, height of drawable space for trajectory
	 * @param minMax min and max values of actual data - only for labels
	 * @param trajElems array of values 0->1 that lie between min (0) and max(1) 
	 * @param widthPerElem how far to displace between each element in trajElems
	 */
	protected void drawSingleTraj(IRenderInterface ri, int[] clr, float[] trajRect, float[] minMax, float[] trajElems, float widthPerElem) {
		ri.pushMatState();
		ri.translate(0.0f,trajRect[3],0.0f);
		ri.setStroke(clr[0],clr[1],clr[2],clr[3]);
		ri.setStrokeWt(1.0f);
		float[] oldLoc = new float[] {0.0f, 0.0f}, loc;
		for(int i=0;i<trajElems.length;++i) {
			loc = new float[] {oldLoc[0]+widthPerElem, -trajElems[i]*trajRect[3]};
			ri.drawLine(oldLoc[0],oldLoc[1],0.0f, loc[0],loc[1],0.0f);
			oldLoc=loc;
		}
		ri.popMatState();
	}//drawSingleTraj
	
	/**
	 * draw a single trajectory within the bounds defined by trajRect
	 * @param ri
	 * @param trajRect rectangle of upper left corner x,y, width, height of drawable space for trajectory
	 * @param minMax min and max values of actual data - only for labels
	 * @param trajElems array of values 0->1 that lie between min (0) and max(1) 
	 * @param widthPerElem how far to displace between each element in trajElems
	 */
	protected void drawSingleTraj(IRenderInterface ri, int[] clr, double[] trajRect, double[] minMax, double[] trajElems, double widthPerElem) {
		ri.pushMatState();
		ri.translate(0.0f,trajRect[3],0.0f);
		ri.setStroke(clr[0],clr[1],clr[2],clr[3]);
		ri.setStrokeWt(1.0f);
		double[] oldLoc = new double[] {0.0f, 0.0f}, loc;
		for(int i=0;i<trajElems.length;++i) {
			loc = new double[] {oldLoc[0]+widthPerElem, -trajElems[i]*trajRect[3]};
			ri.drawLine(oldLoc[0],oldLoc[1],0.0f, loc[0],loc[1],0.0f);
			oldLoc=loc;
		}
		ri.popMatState();
	}//drawSingleTraj
		
	protected final myPointf[][] buildPtTrajVals(ArrayList<myPointf> pts){
		int numVals = pts.size();
		myPointf[][] res = new myPointf[numStatsToMeasure][];
		res[posIDX]=new myPointf[numVals];
		res[velIDX]=new myPointf[numVals-1];
		res[accelIDX]=new myPointf[numVals-2];
		res[jerkIDX]=new myPointf[numVals-3];
		for(int i=0;i<res[posIDX].length;++i) {		res[posIDX][i] = new myPointf(pts.get(i));}
		for(int i=0;i<res[velIDX].length;++i) {		res[velIDX][i]=myVectorf._sub(res[posIDX][i+1], res[posIDX][i]);}
		for(int i=0;i<res[accelIDX].length;++i) {	res[accelIDX][i]=myVectorf._sub(res[velIDX][i+1], res[velIDX][i]);}
		for(int i=0;i<res[jerkIDX].length;++i) {	res[jerkIDX][i]=myVectorf._sub(res[accelIDX][i+1], res[accelIDX][i]);}		
		return res;
	}
	
	protected final myVectorf[][] buildVecTrajVals(ArrayList<myVectorf> vecs){
		int numVals = vecs.size();
		myVectorf[][] res = new myVectorf[numStatsToMeasure][];
		res[posIDX]=new myVectorf[numVals];
		res[velIDX]=new myVectorf[numVals-1];
		res[accelIDX]=new myVectorf[numVals-2];
		res[jerkIDX]=new myVectorf[numVals-3];
		for(int i=0;i<res[posIDX].length;++i) {		res[posIDX][i] = new myVectorf(vecs.get(i));}
		for(int i=0;i<res[velIDX].length;++i) {		res[velIDX][i]=myVectorf._sub(res[posIDX][i+1], res[posIDX][i]);}
		for(int i=0;i<res[accelIDX].length;++i) {	res[accelIDX][i]=myVectorf._sub(res[velIDX][i+1], res[velIDX][i]);}
		for(int i=0;i<res[jerkIDX].length;++i) {	res[jerkIDX][i]=myVectorf._sub(res[accelIDX][i+1], res[accelIDX][i]);}		
		return res;
	}
	
	protected final float[][] buildFloatTrajVals(ArrayList<Float> vals){
		int numVals = vals.size();
		float[][] res = new float[numStatsToMeasure][];
		res[posIDX]=new float[numVals];
		res[velIDX]=new float[numVals-1];
		res[accelIDX]=new float[numVals-2];
		res[jerkIDX]=new float[numVals-3];
		for(int i=0;i<res[posIDX].length;++i) {		res[posIDX][i] = vals.get(i);}
		for(int i=0;i<res[velIDX].length;++i) {		res[velIDX][i] = res[posIDX][i+1]- res[posIDX][i];}
		for(int i=0;i<res[accelIDX].length;++i) {	res[accelIDX][i] = res[velIDX][i+1]-res[velIDX][i];}
		for(int i=0;i<res[jerkIDX].length;++i) {	res[jerkIDX][i] = res[accelIDX][i+1]- res[accelIDX][i];}		
		return res;
	}
	
	protected final double[][] buildDoubleTrajVals(ArrayList<Double> vals){
		int numVals = vals.size();
		double[][] res = new double[numStatsToMeasure][];
		res[posIDX]=new double[numVals];
		res[velIDX]=new double[numVals-1];
		res[accelIDX]=new double[numVals-2];
		res[jerkIDX]=new double[numVals-3];
		for(int i=0;i<res[posIDX].length;++i) {		res[posIDX][i] = vals.get(i);}
		for(int i=0;i<res[velIDX].length;++i) {		res[velIDX][i] = res[posIDX][i+1]- res[posIDX][i];}
		for(int i=0;i<res[accelIDX].length;++i) {	res[accelIDX][i] = res[velIDX][i+1]-res[velIDX][i];}
		for(int i=0;i<res[jerkIDX].length;++i) {	res[jerkIDX][i] = res[accelIDX][i+1]- res[accelIDX][i];}		
		return res;
	}

}//class baseAnalyzer
