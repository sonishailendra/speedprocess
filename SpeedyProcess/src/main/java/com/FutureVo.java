package com;

/**
 * This class hold the instance of ProcessExecuteInt which would be client object and Future Object after executing ProcessExecuteInt .
 * 
 * I used default modifier of this class because it should not be available outside of this class.
 * @author ssoni27
 *
 */
final class FutureVo<T> {
    private final ProcessExecuteInt processExecuteInt;
    private T futureObject;
    
    FutureVo(ProcessExecuteInt processExecuteInt){
        this.processExecuteInt=processExecuteInt;
    }
    
    void setFutureObject(T futureObject){
        this.futureObject=futureObject;
    }
    
    ProcessExecuteInt getProcessExecuteInt(){
        return this.processExecuteInt;
    }
    
    Object getFutureObject(){
        return futureObject;
    }
}
