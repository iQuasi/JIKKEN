import java.util.*;
import java.io.*;
import static java.lang.Math.*;
//
public class FokkerPlanck {
    protected double lambda;
    protected double mu;
    protected double t;
    protected double e;

    protected double deltaPoint;

    private double endTime;
    private int segment;
    private double observe;

    protected double dx;
    protected double dt;

    protected double[] p;
    protected double[] F;
    protected double[] D;
    protected double[][] result;
    
    protected int observeCount;
    protected int border;

    /** Constructor */
    public FokkerPlanck(){
	loadParams("params/parameters.properties");
	
	border = (int)(mu/dx) - 1;

	p = new double[border + 1];
	F = new double[border + 1];
	D = new double[border + 1];
	
	result = new double[(int)(endTime/observe) + 1][];
	observeCount = 0;
    }
    /** main method  */
    public static void main(String[] args){

	FokkerPlanck fp = new FokkerPlanck();
	
	fp.calculateDist();
	fp.outputResult();
    }

    //---------------- FP -------------------//
    private void calculateDist(){	
	double time = 0.0;
	double outTime = 0.0;	

	setInit();
	setFunction();

	while(time < endTime){

	    time += dt;
	    //Set p to result
	    if(outTime <= time){
		setResult(); 
		System.out.println(outTime);
		outTime += observe;
	    }
	    //Calculate distribution at next time
	    calculate();
	}
	System.out.println("FP is OK!");
    }

    private void setResult(){
	result[observeCount] = p.clone();
	observeCount++;
    }

    /** calculate distribution for next time  */
    private void calculate(){
	for(int i = 0; i < border; i++){
	    p[i] += dt * (-drift(i) + diffusion(i));
	}
    }
    /** Drift  */
    private double drift(int x){
	double dri = 0.0;
	if (x != 0 && F[x-1] > 0) dri -= F[x-1] * p[x-1]/dx;	
	if (F[x] > 0) dri += F[x]*p[x]/dx;
		
	if (F[x] < 0) dri -= F[x] * p[x]/dx;
	if (F[x+1] < 0) dri += F[x+1] * p[x+1]/dx;
		
	return dri;
    }
	
    /** Diffusion  */
    private double diffusion(int x){
	double diff = 0.0;
	if (x == 0){
	    diff = 0.5*( D[1]*p[1] - D[0]*p[0] )/dx/dx;
	}else{
	    diff = 0.5*((D[x+1]*p[x+1]-D[x]*p[x])-(D[x]*p[x]-D[x-1]*p[x-1]))/dx/dx;
	}
	return diff;
    }
	
    /** initial condition is Gauss distribution  */
    private void setInit(){
	//Gauss
	
	for(int i = 0; i < border; i++){
	    p[i] = exp(-(i*dx - lambda)
		       *(i*dx - lambda)/(2.0*lambda))
		/(sqrt(2.0 * PI * lambda));
	}
	
	//p[(int)(deltaPoint * dx)] = 1.0;
    }

    /** set F[x] and D[x] */
    private void setFunction(){
	for(int i = 0; i < border; i++){
	    F[i] = lambda - (i*dx)/t + e * (i*dx)/(mu*t-i*dx);
	    //F[i] = lambda - (i*dx) + (e/t) * (i*dx)/(mu - i*dx);
	    D[i] = lambda + (i*dx)/t + e * (i*dx)/(mu*t-i*dx);
	    //D[i] = lambda + (i*dx) + (e/t) * (i*dx)/(mu - i*dx);
	}
    }
    
    private void outputResult(){
	StringBuffer sb = new StringBuffer(); 
	sb.append("fp_" + lambda).append("_")
	    .append(mu).append("_")
	    .append(t).append("_")
	    .append(e).append("_");

	String filename = sb.toString();
	filename = filename.replaceAll("\\.", "d");
	String label = "#LAMBDA=" + lambda
	    + "%n#MU" + mu
	    + "%n#T" + t
	    + "%n#EPSOLON" + e + "%n";

	printDist(filename, label, false);
	printDist(filename + "c", label, true);
    }    


    private void printDist(String filename, String label, boolean cFlag){

	try{
	    FileWriter fw 
		= new FileWriter("fp_w/" + filename + ".dat", false);
	    PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
	    
	    pw.printf(label);

	    pw.printf("#Arrival\t");
	    for(int i = 0; i <= (int)(endTime/observe);i++){ 
		pw.printf("time = %f\t", (double)(observe * i));
	    }
	    pw.printf("\n");

	    if(cFlag){

		double[] tmp = new double[(int)(endTime / observe) + 1];
		
		for(int i = 0; i < border; i++){
		    pw.printf("%f\t", i*dx);
		    for(int j = 0; j <= (int)(endTime / observe); j++){
			tmp[j] += result[j][i]*dx;
			pw.printf("%f\t", tmp[j]);
		    }
		    pw.println();
		}
		System.out.println("CDF OK!");

	    }else{

		for(int i = 0; i < border; i++){
		    pw.printf("%f\t", i*dx);
		    for(int j = 0; j <= (int)(endTime / observe); j++){
			pw.printf("%f\t",result[j][i]*dx);
		    }
		    pw.println();
		}
		System.out.println("DF OK!");
	    }
	    pw.close();
			
	}catch(IOException e){
	    e.printStackTrace();
	}
    }

    /** load parameters from property file */
    private void loadParams(String filename){
	Properties prop = new Properties();
	try {
	    prop.load(new FileInputStream(filename));
	} catch(IOException e) { e.printStackTrace(); System.exit(-1);}
    
	lambda = Double.parseDouble(prop.getProperty("lambda"));
	mu = Double.parseDouble(prop.getProperty("mu"));
	t = Double.parseDouble(prop.getProperty("t"));
	e = Double.parseDouble(prop.getProperty("e"));
	observe = Double.parseDouble(prop.getProperty("observe"));
	segment = Integer.parseInt(prop.getProperty("segment"));
	endTime = Double.parseDouble(prop.getProperty("endTime"));
	deltaPoint = Double.parseDouble(prop.getProperty("deltaPoint"));

	dx = Double.parseDouble(prop.getProperty("dx"));
	dt = Double.parseDouble(prop.getProperty("dt"));
    }
}
