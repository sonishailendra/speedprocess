 package com;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SpeedyProcess<T extends ProcessExecuteInt> {

    private ThreadPoolExecutor threadPoolExecutor = null;
    private Map<Integer, FutureVo> executableMap = null;
    private Integer counter = 0;
    private String processName;
    private AtomicInteger threadNameCounter;
    private List<FutureVo> tempList = new ArrayList<FutureVo>();

    /**
     * 
     * @param totalThreadsInvokeAtOneTime
     *            - Total Threads are running at giving time.
     * @param maxPoolSize
     *            :- Maximum total threads hold.
     */
    public SpeedyProcess(String processName, int totalThreadsInvokeAtOneTime, int maxPoolSize) {
        this.threadPoolExecutor = new ThreadPoolExecutor(totalThreadsInvokeAtOneTime, maxPoolSize, Long.MAX_VALUE, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(totalThreadsInvokeAtOneTime * maxPoolSize), new BasicThreadFactory());

        this.executableMap = new LinkedHashMap<Integer, FutureVo>(maxPoolSize);
        this.processName = processName;
        this.threadNameCounter = new AtomicInteger(0);
    }

    /**
     * Need to add each chunk of List object into finalList
     * 
     * @param processList
     */
    public void buildExecutable(T processIntObject) {
        this.executableMap.put(counter++, new FutureVo(processIntObject));
    }

    @SuppressWarnings("unchecked")
    public void start() {
        Future<?> futureReturn = null;
        try {
            for (FutureVo processIntObject : this.executableMap.values()) {
                futureReturn = this.threadPoolExecutor.submit(new WorkerThread(processIntObject.getProcessExecuteInt()));
                processIntObject.setFutureObject(futureReturn);
                tempList.add(processIntObject);
            }
            // this.threadPoolExecutor.invokeAll(listOfWorkerThread);
            threadPoolExecutor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            while (!threadPoolExecutor.isTerminated()) {
                // do nothing
            }
            threadPoolExecutor.shutdownNow();
        }

    }

    public int size() {
        if (this.executableMap == null) {
            return 0;
        }
        return this.executableMap.size();
    }

    /**
     * Return FutureObject of the Thread.
     * 
     * @param counter
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Object get(Integer counter) throws InterruptedException, ExecutionException {
        System.out.println("Main Object " + this.executableMap.get(counter).getProcessExecuteInt().getNumber());
        return this.executableMap.get(counter).getFutureObject();
    }

    public void listDownThreadReturn () {
        for(FutureVo futureVo : tempList){
            System.out.println("From tempList Main Object " + futureVo.getProcessExecuteInt().getNumber());
        }
    }
    
    /**
     * Thread related operation- it is inner class because I do not want expose
     * outside of this class.
     * 
     * @author ssoni27
     *
     */
    private final class WorkerThread implements Callable {

        private final ProcessExecuteInt processIntObject;

        private WorkerThread(ProcessExecuteInt processExecuteInt) {
            this.processIntObject = processExecuteInt;
        }

        public Object call() throws Exception {
            try {
                setName();
                return processIntObject.executeProcess();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
            
        }

        public void setName() {
            String currentThread = Thread.currentThread().getName();
            System.out.println("currentThread :- " + currentThread);
            if (currentThread.split("-").length == 3) {
                Thread.currentThread().setName(currentThread + "-" + threadNameCounter.getAndIncrement());
            } else {
                Thread.currentThread().setName(currentThread.substring(0, currentThread.lastIndexOf("-") + 1) + threadNameCounter.getAndIncrement());
            }
        }
    }

    private final class BasicThreadFactory implements ThreadFactory {

        private ThreadGroup threadGroup;
        private AtomicInteger counter;

        private BasicThreadFactory() {
            threadGroup = new ThreadGroup("SpeedyProcess");
            counter = new AtomicInteger(0);
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread t = new Thread(threadGroup, runnable);
            t.setName(SpeedyProcess.this.processName + "-" + counter.incrementAndGet());
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }

    }

}
