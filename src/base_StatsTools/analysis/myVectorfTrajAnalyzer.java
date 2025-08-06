package base_StatsTools.analysis;

import java.util.ArrayList;

import base_Math_Objects.vectorObjs.floats.myVectorf;
import base_StatsTools.analysis.base.baseVecTrajAnalyzer;
import base_StatsTools.summary.myProbSummary_ptOrVec;

/**
 * measure and record the morph trajecto
 * @author john
 *
 */
public class myVectorfTrajAnalyzer extends baseVecTrajAnalyzer {
    /**
     * all trajectory values : per stat, per t value list of point and vector trajectories
     */
    public myVectorf[][] trajVals;

    public myVectorfTrajAnalyzer() {
        super();
    }

    /**
     * find the average position, velocity, accel, etc of the passed trajectory of points
     * assumes each position sample is uniformly spaced in time
     * @param pts
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected final void analyzeMyPtTrajectory_Indiv(ArrayList vecs, String name) {analyzeMyPtTrajectory((ArrayList<myVectorf>)vecs, name, debug);}
    protected void analyzeMyPtTrajectory(ArrayList<myVectorf> vecs, String name, boolean _dbg) {
        if((null==vecs) || (vecs.size() < 4)) {return;}
        if(_dbg) {System.out.println("Analyzer " + ID+ " for : " + name  + " # Vecs : " +vecs.size() +" myVectorfTrajAnalyzer::analyzeTrajectory : ");}

        trajVals = buildVecTrajVals(vecs);
        for(int i=0;i<summaries.length;++i) {
            summaries[i]=new myProbSummary_ptOrVec(name +" " +statNames[i], trajVals[i], 4);
            if(_dbg) {System.out.println("\nSummary : " +summaries[i].toString());}
        }        
        if(_dbg) {System.out.println("Analyzer " + ID+ " for : " + name +" Done ");}
    }
}// class myVectorfTrajAnalyzer
