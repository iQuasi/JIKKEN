import java.io.*;
import java.util.Properties;
import params.Event;
import static params.Event.*;

public class RetrialMonteCalro {

    public double lambda;
    public double mu;
    public double t;
    public double e;

    public int simTimes;
    public double observe;
    public int segment;
    public double endTime;
    public double deltaPoint;

    public int[][] dist;

    public int observeCount = 0;

    /** System Object */
    protected MM1System2 retrialMM1;

    /** Default constructor */
    public RetrialMonteCalro(){
	loadParams("params/parameters.properties");	
	dist = new int[(int)(endTime/observe)][segment + 1];
	retrialMM1 = new MM1System2();
    }

    /** Main method */
    public static void main(String[] args){

	RetrialMonteCalro retrialSim = new RetrialMonteCalro();
	retrialSim.start();
	retrialSim.outputResult();
    }

    /** loop in simTimes */
    private void start(){
	for(int i = 0; i < simTimes; i++){
	    if(i % 100 == 0) System.out.println(i);
	    init();
	    doSimulation();
	}
    }

    /** initialize all parameter */
    private void init(){
	observeCount = 0;
	retrialMM1.initialize();

    }

    /** a simulation by endTime */
    private void doSimulation(){
	double time = 0.0;
	double arrRate = deltaPoint;

	//double tTime = t;
	double tTime = 1.0;
	double aTime;
	double sTime;
	double gTime;
	double rTime;
	double setTimer = observe;

	retrialMM1.setMm1Queue((int)((lambda/mu)/(1-(lambda/mu))));
	//retrialMM1.setMm1Queue((int)((deltaPoint/mu)/(1-(deltaPoint/mu))));
	//retrialMM1.setMm1ReQueue((int)(deltaPoint - lambda));

	//simulation
	while(time <= endTime){

	    if(isDiverse(arrRate + 1)) break;

	    //get each event time
	    aTime = time + expTime(lambda);
	    sTime = time + expTime(mu);	    
	    if(retrialMM1.getNumOfCustomer() == 0){
		gTime = endTime + 1; 
	    }else{
		//gTime = time + expTime(retrialMM1.getNumOfCustomer() * e / t);
		gTime = time + expTime(retrialMM1.getNumOfCustomer() * e / t);
	    }
	    if(retrialMM1.getNumOfRet() == 0){
		rTime = endTime + 1;
	    }else{
		rTime = time + expTime(retrialMM1.getNumOfRet()/t);
		//rTime = time + expTime(retrialMM1.getNumOfRet() * t);
	    }
	    
	    //an event occur
	    switch(nextEvent(tTime, aTime, sTime, gTime, rTime)){
	      case TIME_SCALE :
		  time = tTime;
		  arrRate = primary(lambda + retrialMM1.getNumOfRet());
		 
		  if(setTimer <= tTime) {
		      setDist(arrRate);
		      setTimer += observe;
		  }
		  //tTime += t;
		  tTime += 1.0;
		  break;
		  
	      case ARRIVAL :
		  time = aTime;
		  arrival(time);
		  break;

	      case SERVICE :
		  time = sTime;
		  retrialMM1.executeServive();
		  break;

	      case GENERATE :
		  time = gTime;
		  retrialMM1.genRet();
		  break;

	      case RETRIAL :
		  time = rTime;
		  retrialMM1.arrRet();
		  arrival(time);
		  break;
	    }
	    arrRate = lambda + retrialMM1.getNumOfRet();
	}
    }
    
    //*****************************/
    
    /** make Poisson distrubution  */
    private int primary(double lam){
	double tmp1 = 0.0;
	int tmp2 = 0;

	while(tmp1 < 1.0){
	    tmp1 += expTime(lam);
	    tmp2++;
	}
	/*
	while(tmp1 < t){
	    tmp1 += expTime(lam);
	    tmp2++;
	}
	*/
	return tmp2;
    }
    
    /** A customer is arrived  */
    private void arrival(double now){
	retrialMM1.inputSystem();	
    }

    /** Set arrival count to dist[][]  */
    private void setDist(double arrRate){
	if((int)(arrRate) <= segment){
	    dist[observeCount][(int)(arrRate)]++;
	}else{
	    dist[observeCount][segment]++;
	}
	observeCount++;
    }

    /** judge diversion */
    private boolean isDiverse(double arrrate){
	if(mu < arrrate){
	    System.out.println("Diverse!");

	    while(observeCount < (int)(endTime/observe)){
		dist[observeCount][segment]++;
		observeCount++;
	    }
	    return true;
	}
	return false;
    }


    private void outputResult(){
	StringBuffer sb = new StringBuffer(); 
	sb.append(lambda).append("_")
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
		= new FileWriter("simulation_h3/" + filename + ".dat", false);
	    PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
	    
	    pw.printf(label);

	    pw.printf("#Arrival\t");
	    for(int i = 1; i <= (int)(endTime/observe);i++){ 
		pw.printf("time = %f\t", (double)(observe * i));
	    }
	    pw.printf("\n");

	    if(cFlag){
		double[] tmp = new double[(int)(endTime / observe)];

		for(int i = 0; i <= segment; i++){
		    pw.printf("%d\t", i);
		    for(int j = 0; j < (int)(endTime / observe); j++){
			tmp[j] += (double)dist[j][i] / simTimes;
			pw.printf("%f\t", tmp[j]);
		    }
		    pw.printf("\n");
		}
		System.out.println("CDF OK!");

	    }else{

		for(int i = 0; i <= segment; i++){
		    pw.printf("%d\t", i);
		    for(int j = 0; j < (int)(endTime / observe); j++){
			pw.printf("%f\t",(double)dist[j][i]/simTimes);
		    }
		    pw.printf("\n");
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
       	simTimes = Integer.parseInt(prop.getProperty("simTimes"));;
	observe = Double.parseDouble(prop.getProperty("observe"));
	segment = Integer.parseInt(prop.getProperty("segment"));
	endTime = Double.parseDouble(prop.getProperty("endTime"));
	deltaPoint = Double.parseDouble(prop.getProperty("deltaPoint"));	
    }

    /**
     * @return the event occur the next.
     */
    private Event nextEvent(double tt,double at,double st,double gt,double rt){
	double min = tt;
	Event event = TIME_SCALE;
	if(min > at){ min = at; event = ARRIVAL;}
	if(min > st){ min = st; event = SERVICE;}
	if(min > gt){ min = gt; event = GENERATE;}
	if(min > rt){ min = rt; event = RETRIAL;}
	return event;
    }

    /** 
     * @return exponential time with parameter p
     */
    private double expTime(double p){
	if(p > 0){
	    return (-1.0/p)*Math.log(Math.random());
	}else{
	    return 0;
	}
    }
}
