# 自定义线程池，和基于该线程池的Web服务器

### 1.简介
- 自定义一个简单的线程池，线程池中通过数量相对固定的工作线程执行任务，向线程池中提交任务，工作线程取出任务执行。
- 基于该线程池，实现一个简单的Web服务器。将每个Socket连接中的请求与响应封装为一个任务提交到线程池中。

### 2.自定义线程池
- 线程池中默认工作线程数目为5，最大可增加到10，不显示修改线程池大小，线程池大小固定。
- 提交工作任务给线程池，工作任务必须实现Runnable接口，加入任务队列中
- 工作线程从任务队列获取工作任务执行，当没有任务可以执行，线程进入等待状态

### 3.基于线程池的Web服务器
- 定义内部类HttpRequestHandler，实现Runnable接口，封装为一个任务处理socket连接的请求与响应。
- 可处理的请求对象包括图片(\*.jpg, \*png, \*.ico)，和html文件
