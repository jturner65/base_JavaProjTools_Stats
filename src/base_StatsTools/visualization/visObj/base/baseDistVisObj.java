package base_StatsTools.visualization.visObj.base;

import base_JavaProjTools_IRender.base_Render_Interface.IRenderInterface;
import base_StatsTools.visualization.myDistFuncHistVisMgr;

/**
 * manage the visualization of a single distribution evaluation, either a histogram or a functional evaluation
 * @author 7strb
 *
 */
public abstract class baseDistVisObj{
	protected myDistFuncHistVisMgr owner;
	//location of axis ticks
	private float[][] axisVals;
	//axis tick value to display
	private double[][] axisDispVals;
	//x,y vals for calculation - n x 2 array, n points of x=0 idx, y=1 idx values; min, max, diff values of func eval (in x=idx 0 and in y = idx 1)
	private double[][] vals, minMaxDiffVals;
	//x,y vals for display - n x 2 array, n points of x=0 idx, y=1 idx values - x=0 -> dispWidth; y=0->dispHeight; axis values, to be displayed at equally space intervals along axis
	protected float[][] dispVals;
	//y and x values for display of x and y "0" axes, respectively
	protected float[] zeroAxisVals;	
	//whether or not to draw special axes (if shown in graph)
	private boolean[] drawZeroAxes;
	//graph frame dims
	private float[] frameDims = new float[4];
	//format strings for x and y values to display on graphs
	private final String fmtXStr = "%3.4f", fmtYStr = "%3.4f";
	//axis tick dim on either side of axis
	private static final float tic = 5.0f;
	//# of values to display on axis
	protected static final int numAxisVals = 21;
	
	//colors for display
	protected int[] fillClr, strkClr;

	
	public baseDistVisObj(myDistFuncHistVisMgr _owner, int[][] _clrs) {
		owner=_owner;
		clearEvalVals();
		setFrameDims(owner.getFrameDims());
		fillClr = _clrs[0];
		strkClr = _clrs[1];
	}//ctor

	public void setFrameDims(float[] _fd) {frameDims = _fd;rescaleDispValues(minMaxDiffVals);}
	
	public void setVals(double[][] _Vals, double[][] _minMaxDiffVals) {
		vals = _Vals;
		minMaxDiffVals = _minMaxDiffVals;
		rescaleDispValues(minMaxDiffVals);		
	}
	
	public void setFillColor(int[] _clr) {fillClr =_clr;}
	public void setStrkColor(int[] _clr) {strkClr =_clr;}
	
	private float _calcScale(double x, double min, double diff) {return	(float)((x-min)/diff);}
	
	public void clearEvalVals() {
		vals = new double[0][0];
		dispVals = new float[0][0];
		axisVals = new float[0][0];
		axisDispVals = new double[0][0]; 
		minMaxDiffVals = new double[2][3];
	}//clearVals
	
	public void rescaleDispValues(double[][] _minMaxDiffVals) {	
		dispVals = new float[vals.length][2];//x,y values for each point
		drawZeroAxes = new boolean[2];
		for(int i=0;i<vals.length;++i) {	
			//float scaleX = (float) ((funcVals[i][0] - minMaxDiffFuncVals[0][0])/minMaxDiffFuncVals[0][2]);
			float scaleX = _calcScale(vals[i][0], _minMaxDiffVals[0][0],_minMaxDiffVals[0][2]);
			dispVals[i][0] = scaleX*frameDims[2];
			//set y values to be negative so will display properly (up instead of down)
			//how much to scale height
			//float scaleY = -(float) ((funcVals[i][1] - minMaxDiffFuncVals[1][0])/minMaxDiffFuncVals[1][2]);
			float scaleY = -_calcScale(vals[i][1], _minMaxDiffVals[1][0],_minMaxDiffVals[1][2]);
			dispVals[i][1] =  scaleY*frameDims[3]*.95f;		
		}	
		//check whether or not we will build display axes
		for (int i=0;i<drawZeroAxes.length;++i) {		drawZeroAxes[(i+1)%2] = ((_minMaxDiffVals[i][0] < 0) && (_minMaxDiffVals[i][1] > 0));		}
		//build values for axes display - location and value - now that 
		buildAxisVals(_minMaxDiffVals);
	}//rescaleDispValues
	
	//build axis values to display along axes - min/max/diff vals need to be built
	private void buildAxisVals(double[][] _minMaxDiffVals) {
		axisVals = new float[numAxisVals][2];
		axisDispVals = new double[numAxisVals][2]; 
		zeroAxisVals = new float[2];
		float _denom = (1.0f*numAxisVals-1);
		//width between ticks for x and y
		float[] rawDimAra = new float[] {frameDims[2], -(frameDims[3]*.95f)};
		float[] denomAra = new float[] {rawDimAra[0]/_denom, rawDimAra[1]/_denom};
		for(int i=0;i<axisVals.length;++i) {
			float iterDenom = i/_denom;
			for (int j=0;j<2;++j) {//j == x=0,y=1 
				//location of tick line
				axisVals[i][j] = i*denomAra[j];	
				//value to display
				axisDispVals[i][j] = _minMaxDiffVals[j][0] + (iterDenom *_minMaxDiffVals[j][2]);	
			}	
		}	
		for(int i=0;i<2;++i) {
			//zeroAxisVals[(i+1)%2] = (float) ((-minMaxDiffFuncVals[i][0]/minMaxDiffFuncVals[i][2])*rawDimAra[i]);
			zeroAxisVals[(i+1)%2] = _calcScale(0, _minMaxDiffVals[i][0],_minMaxDiffVals[i][2])*rawDimAra[i];
		}
	}//buildAxisVals
	
	public double[][] getMinMaxDiffVals(){ return minMaxDiffVals;}	
	public void setMinMaxDiffVals(double[][] _minMaxDiffVals) {
		minMaxDiffVals = new double[_minMaxDiffVals.length][];
		for(int i=0;i<minMaxDiffVals.length;++i) {
			int len = _minMaxDiffVals[i].length;
			minMaxDiffVals[i] = new double[len];
			System.arraycopy(_minMaxDiffVals[i], 0, minMaxDiffVals[i], 0, len);
		}
		rescaleDispValues(minMaxDiffVals);	
	}
	
	///////////////////
	// drawing routines

	//draw axis lines through 0,0 and give tags
	private void _drawZeroLines(IRenderInterface pa) {
		pa.pushMatState();
		pa.setStrokeWt(2.0f);
		pa.setColorValFill(IRenderInterface.gui_Cyan,255);
		if (drawZeroAxes[0]) {//draw x==0 axis
			float yVal = zeroAxisVals[0];
			//draw line @ y Val
			pa.setColorValStroke(IRenderInterface.gui_White,255);
			pa.drawLine(-tic, yVal, 0, tic, yVal, 0);
			//draw line to other side
			pa.setColorValStroke(IRenderInterface.gui_Cyan,255);
			pa.drawLine(-tic, yVal, 0, frameDims[2], yVal, 0);
			//draw text for display
			pa.setColorValStroke(IRenderInterface.gui_White,255);
			pa.pushMatState();
			pa.translate(-tic-10, yVal+5.0f,0);
			pa.scale(1.4f);
			pa.showText("0", 0,0);
			pa.popMatState();
		}
		
		if (drawZeroAxes[1]) {//draw y==0 axis
			float xVal = zeroAxisVals[1];
			//draw tick line @ x Val
			pa.setColorValStroke(IRenderInterface.gui_White,255);
			pa.drawLine(xVal, -tic, 0, xVal, tic, 0);	
			//draw line to other side
			pa.setColorValStroke(IRenderInterface.gui_Cyan,255);
			pa.drawLine(xVal, -frameDims[3], 0, xVal, tic, 0);	
			//draw text for display
			pa.setColorValStroke(IRenderInterface.gui_White,255);
			pa.pushMatState();
			pa.translate( xVal - 4.0f, tic+20.0f,0);
			pa.scale(1.4f);
			pa.showText("0", 0,0);
			pa.popMatState();
		}		
		pa.popMatState();
	}//
	
	//draw x and y axis values
	//offset == 0 for axes on left, offset == frameDims[2] for offset on right
	protected void drawAxes(IRenderInterface pa, float offset) {
		pa.pushMatState();
		pa.setColorValFill(IRenderInterface.gui_White,255);
		float yAxisTxtXOffset = offset -tic-myDistFuncHistVisMgr.frmBnds[0]+10 ,
				yAxisTxtYOffset = (offset == 0.0)? 5.0f : -4.0f;
		for (int idx = 0; idx <axisVals.length;++idx) {
			float xVal = axisVals[idx][0];
			String dispX = String.format(fmtXStr, axisDispVals[idx][0]); 
			//draw tick line @ x Val
			pa.setColorValStroke(IRenderInterface.gui_White,255);
			pa.drawLine(xVal, -tic, 0, xVal, tic, 0);	
			//draw line to other side
			pa.setColorValStroke(IRenderInterface.gui_White,50);
			pa.drawLine(xVal, -frameDims[3], 0, xVal, tic, 0);	
			//draw text for display
			pa.showText(dispX, xVal - 20.0f, tic+10.0f);
			if(idx%2==0) {//only draw every other y tick
				float yVal = axisVals[idx][1];
				String dispY = String.format(fmtYStr, axisDispVals[idx][1]);
				//draw line @ y Val
				pa.setColorValStroke(IRenderInterface.gui_White,255);
				pa.drawLine(-tic + offset, yVal, 0, tic + offset, yVal, 0);
				//draw line to other side
				pa.setColorValStroke(IRenderInterface.gui_White,50);
				pa.drawLine(-tic, yVal, 0, frameDims[2], yVal, 0);
				//draw text for display
				pa.showText(dispY, yAxisTxtXOffset, yVal+yAxisTxtYOffset);
			}
		}
		_drawZeroLines(pa);
		pa.popMatState();
	}//drawAxes
	
	public final void drawMe(IRenderInterface pa, boolean isMulti) {
		pa.setFill(fillClr, fillClr[3]);
		pa.setStroke(strkClr, strkClr[3]);
		_drawCurve(pa,isMulti ? frameDims[2] : 0);
	}//drawMe

	protected abstract void _drawCurve(IRenderInterface pa, float offset);
	
}//baseDistVisObj

