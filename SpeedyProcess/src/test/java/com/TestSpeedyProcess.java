package com;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TestSpeedyProcess {

    
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        SpeedyProcess<TestThread> t = new SpeedyProcess<TestThread>("Testing-SpeedyProcess", 10, 50);
        for(int i=0;i<5;i++){
            t.buildExecutable(new TestThread(i));    
        }
        t.start();
        
        for(int i =0 ;i<t.size(); i++){
            System.out.print( ((Future)t.get(i)).get());
        }
        
        //t.listDownThreadReturn();
    }
    
    
    static class TestThread implements ProcessExecuteInt<String>{
        
        private int number;
        private TestThread (int number){
            this.number=number;
        }

        @Override
        public String executeProcess() {
            System.out.println("Testing :- Thread name " + Thread.currentThread().getName());
            try {
                Thread.sleep((number % 2) * 1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("After sleeep Testing :- Thread name " + Thread.currentThread().getName());
            return "Return " + Thread.currentThread().getName();
        }

        @Override
        public int getNumber() {
            // TODO Auto-generated method stub
            return number;
        }
        
    }
    
}
