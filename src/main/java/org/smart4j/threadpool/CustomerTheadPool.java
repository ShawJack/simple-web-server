package org.smart4j.threadpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ithink on 17-8-4.
 */
public class CustomerTheadPool<Job extends Runnable> implements ThreadPool<Job> {

    //线程池最大工作线程数目
    private final static int MAX_WORKER_NUMBERS = 10;
    //线程池默认工作线程数目
    private final static int DEFAULT_WORKER_NUMBERS = 5;
    //线程池最小工作线程数目
    private final static int MIN_WORKER_NUMBERS = 1;
    //任务列表
    private List<Job> jobs = new LinkedList<Job>();
    //工作线程列表
    private List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());
    //工作线程数量
    private int workerNumbers = DEFAULT_WORKER_NUMBERS;
    //工作线程编号生成器
    private AtomicLong threadNo = new AtomicLong();

    public CustomerTheadPool(){
        initWorkers(workerNumbers);
    }

    public void initWorkers(int num){
        for(int i=0; i<num; i++){
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "threadpool-worker-"+threadNo.incrementAndGet());
            thread.start();
        }
    }

    public void execute(Job job) {
        if(job != null){
            synchronized (jobs){
                jobs.add(job);
                jobs.notify();
            }
        }
    }

    public void addWorkers(int num) {
        synchronized(jobs){
            if(num + workerNumbers > MAX_WORKER_NUMBERS){
                num = MAX_WORKER_NUMBERS - workerNumbers;
            }
            initWorkers(num);
            workerNumbers += num;
        }
    }

    public void removeWorker(int num) {
        synchronized (jobs) {
            if (num > workerNumbers) {
                num = workerNumbers - MIN_WORKER_NUMBERS;
            }

            while(num > 0){
                Worker worker = workers.remove(workers.size()-1);
                worker.shutdown();
                num--;
            }
            workerNumbers -= num;
        }
    }

    public void shutdown() {
        for(Worker worker : workers){
            worker.shutdown();
        }
    }

    public int getJobSize() {
        return jobs.size();
    }

    class Worker implements Runnable{

        private boolean running = true;

        public void run() {
            while(running){
                Job job = null;

                synchronized (jobs){
                    while(jobs.isEmpty()){
                        try{
                            System.out.println(Thread.currentThread().getName());
                            jobs.wait();
                        }catch (InterruptedException e){
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    job = jobs.remove(0);
                }

                if(job != null){
                    job.run();
                }
            }
        }

        public void shutdown(){
            running = false;
        }
    }
}
