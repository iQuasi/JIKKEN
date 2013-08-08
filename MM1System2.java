import java.util.*;

public class MM1System2 {
    /** number of customers in a MM1 System */
    private int mm1Queue;

    /** number of customers who would come back at random later */
    private int reQueue;
    
    public MM1System2() {
	mm1Queue = 0;
	reQueue = 0;
    }
    
    public MM1System2(int mm1queue, int requeue){
	this.mm1Queue = mm1queue;
	this.reQueue = requeue;
    }

    /** a customer arrived */
    public void inputSystem(){
	mm1Queue++;
    }

    /** a customer generate a retry */
    public void genRet(){
	reQueue++;
    }

    /** a service finished */
    public void executeServive(){
	if(mm1Queue != 0) mm1Queue--;
    }

    /** initialization of state */
    public void initialize(){
	mm1Queue = 0;
	reQueue = 0;
    }

    /** get the number of customer */
    public int getNumOfCustomer(){
	return mm1Queue;
    }

    /** get the number of retrial group */
    public int getNumOfRet(){
	return reQueue;
    }

    /** a customer leave from retrial group */
    public void arrRet(){
	if(reQueue != 0) reQueue--;
    }

    public void setMm1Queue(int mm1queue){
	this.mm1Queue = mm1queue;
    }

    public void setMm1ReQueue(int requeue){
	this.reQueue = requeue;
    }
}
