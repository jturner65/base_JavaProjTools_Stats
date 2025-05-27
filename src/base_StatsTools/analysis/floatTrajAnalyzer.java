package base_StatsTools.analysis;

import java.util.ArrayList;
import java.util.TreeMap;

import base_Render_Interface.IRenderInterface;
import base_StatsTools.analysis.base.baseAnalyzer;
import base_StatsTools.summary.myProbSummary_Flts;
import base_StatsTools.summary.base.baseProbSummary;

public class floatTrajAnalyzer extends baseAnalyzer {
	/**
	 * all trajectory values : per stat, per t value list of point and vector trajectories
	 */
	public float[][] trajVals;
	/**
	 * calculate these after trajectory values are calculated, only once, to display for graph
	 * 1st idx is summary, 2nd idx is trajectory idx
	 */
	protected float[][] perSummaryScaledValTrajs;
	/**
	 * build this after trajectories are summarized.  
	 * 1st idx is summary, 2nd idx is min or max
	 */
	protected float[][] perSummaryMinMax;
	

	public floatTrajAnalyzer() {
		super();
		summaries = new myProbSummary_Flts[numStatsToMeasure];	
		perSummaryScaledValTrajs = new float[summaries.length][];
		perSummaryMinMax = new float[summaries.length][];
	}

	/**
	 * find the average value, ROC, rate of ROC, etc of the passed trajectory of areas of morph maps
	 * assumes each area sample is uniformly spaced in time
	 * @param pts
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override	
	public void analyzeTrajectory(ArrayList areas, String name) {analyzeAreaTrajectory((ArrayList<Float>)areas, name, debug);}
	private void analyzeAreaTrajectory(ArrayList<Float> areas, String name, boolean _dbg) {
		if((null==areas) || (areas.size() < 4)) {return;}
		if(_dbg) {System.out.println("Float Analyzer " + ID+ " for : " + name  + " # float vals : " +areas.size() +" floatTrajAnalyzer::analyzeTrajectory : ");}

		trajVals = buildFloatTrajVals(areas);
		for(int i=0;i<summaries.length;++i) {
			summaries[i]=new myProbSummary_Flts(name +" " +statNames[i], trajVals[i]);
			if(_dbg) {System.out.println("\nSummary : " +summaries[i].toString());}
			perSummaryScaledValTrajs[i] = ((myProbSummary_Flts) summaries[i]).getScaledVals();
			perSummaryMinMax[i] = ((myProbSummary_Flts) summaries[i]).getMinMax();			
		}		
		if(_dbg) {System.out.println("Analyzer " + ID+ " for : " + name +" Done ");}
	}//analyzeAreaTrajectory

	@Override
	protected void drawSingleSummary(IRenderInterface ri, String[] mmntDispLabels, baseProbSummary smryRaw, float txtLineYDisp, float ltrMult) {
		myProbSummary_Flts smry = ((myProbSummary_Flts)smryRaw);
		TreeMap<String,String> smryStrings = smry.summaryStringAra("A");
		ri.pushMatState();
		showOffsetText_RightSideMenu(ri,ri.getClr(IRenderInterface.gui_Black, 255), ltrMult*.3f, smryStrings.get("summaryName"));
		for(int i=0;i<mmntDispLabels.length;++i) {
			showOffsetText_RightSideMenuAbs(ri, ri.getClr(IRenderInterface.gui_DarkBlue, 255), ltrMult*3.5f, smryStrings.get(mmntDispLabels[i]));
		}			
		ri.popMatState();
		ri.translate(0.0f,txtLineYDisp,0.0f);	
		
	}//drawSingleSummary
	protected int[] trajClr = new int[] {255,255,255,255};
	@Override
	protected final void drawSingleSmryGraph(IRenderInterface ri, String[] mmntDispLabels, int smryIdx, float[] graphRect, float ltrMult)  {
		float[] perSmry_Traj = perSummaryScaledValTrajs[smryIdx], perSmry_MinMax = perSummaryMinMax[smryIdx];
		float widthPerElem = graphRect[2]/(1.0f*perSmry_Traj.length);
		
		ri.pushMatState();			
		//drawSingleTraj(IRenderInterface ri, int[] clr, float[] trajRect, float[] minMax, float[] trajElems, float widthPerElem)
		drawSingleTraj(ri, trajClr, graphRect, perSmry_MinMax, perSmry_Traj,widthPerElem);
		ri.popMatState();
		
		
		ri.translate(0.0f,graphRect[3],0.0f);				//draw all these lines on each other
	}
	@Override
	protected final void drawSingleSmryGraphMinMaxLbls(IRenderInterface ri, int smryIdx, float ltrMult) {
		float[] perSmry_MinMax = perSummaryMinMax[smryIdx];
		//(IRenderInterface ri, int clrLabel, String txt, float ltrMult)
		drawSingleMinMaxTxt(ri, IRenderInterface.gui_Black, "Min/Max ["+ String.format(frmtStr,perSmry_MinMax[0])+", " + String.format(frmtStr,perSmry_MinMax[1])+"]",ltrMult);
		
	}


}//class morphAreaTrajAnalyzer
