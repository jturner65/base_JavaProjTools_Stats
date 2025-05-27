package base_StatsTools.visualization.visObj;

import base_Render_Interface.IRenderInterface;
import base_StatsTools.visualization.myDistFuncHistVisMgr;
import base_StatsTools.visualization.visObj.base.baseDistVisObj;

//histogram evaluation of a pdf - draws buckets
public class myHistVisObj extends baseDistVisObj{
	
	public myHistVisObj(myDistFuncHistVisMgr _owner, int[][] _clrs) {
		super(_owner, _clrs);
	}
	
	protected void _drawCurve(IRenderInterface ri, float offset) {
		for (int idx = 0; idx <dispVals.length-1;++idx) {	
			ri.drawRect(dispVals[idx][0], 0, (dispVals[idx+1][0]-dispVals[idx][0]), dispVals[idx][1]);			
		}		
		drawAxes(ri, offset);
	}//_drawCurve

	
}//myHistVisObj