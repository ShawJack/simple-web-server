package org.smart4j.threadpool;

/**
 * Created by ithink on 17-8-4.
 */
public interface ThreadPool<Job extends Runnable> {

    //提交并任务执行
    void execute(Job job);

    //增加工作线程数目
    void addWorkers(int num);

    //减少工作线程数目
    void removeWorker(int num);

    //关闭线程池
    void shutdown();

    //正在等待执行的任务数量
    int getJobSize();

}
