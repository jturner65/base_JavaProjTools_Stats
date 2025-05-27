package base_StatsTools.visualization.visObj;

import base_Render_Interface.IRenderInterface;
import base_StatsTools.visualization.myDistFuncHistVisMgr;
import base_StatsTools.visualization.visObj.base.baseDistVisObj;

 
/**
 * visualize a functional object evaluation - draws a line
 * @author John Turner
 *
 */
public class myFuncVisObj extends baseDistVisObj{
	public myFuncVisObj(myDistFuncHistVisMgr _owner, int[][] _clrs) {
		super(_owner, _clrs);
	}
	
	@Override
	protected void _drawCurve(IRenderInterface ri, float offset) {
		ri.drawEllipse2D(dispVals[0][0], dispVals[0][1], 5.0f,5.0f);
		for (int idx = 1; idx <dispVals.length;++idx) {	
			//draw point 			
			ri.drawEllipse2D(dispVals[idx][0], dispVals[idx][1], 5.0f,5.0f);
			//draw line between points
			ri.drawLine(dispVals[idx-1][0], dispVals[idx-1][1], 0, dispVals[idx][0], dispVals[idx][1], 0);
		}		
		drawAxes(ri, 0);
	}//_drawCurve

}//myFuncVisObj