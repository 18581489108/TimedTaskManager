# TimedTaskManager

定时任务管理器

### 为什么我要写这个呢
年纪大了，总是健忘，于是想着写个提醒工具来提醒自己。嘛，因为自己才开始学习多线程，想练练手，就考虑做一个定时任务管理器。提醒也是一种定时任务嘛。

### 一点都不详细的说明
整体结构由YTask + TaskManager + TaskExecutor组成
* YTask 任务的基础接口，定义了获取任务编号`getTaskNo()`、获取任务执行时间`getExecutionTime()`、任务的执行`executeTask()`以及执行完任务后的回调方法`afterExecuteTask(TaskManager taskManager)`
* TaskManager 任务管理器的基础接口，定义了添加任务`addTask(YTask task)`、移除任务`removeTask(YTask task)`以及根据任务编号获取任务`getTask(Integer taskNo)`
* TaskExecutor组成 任务执行器的基础接口，定义了添加任务`addTask(YTask task)`、添加多个任务`addTasks(Collection<? extends YTask> tasks)`以及正式启动执行器`start()`

### 可能没有什么用的测试代码
```
TaskManager taskManager = new TimedTaskManager();

OnlyOnceTimePeriod onlyOnceTimePeriod = new OnlyOnceTimePeriod();
// 延迟1s执行，且执行一次
// XxxTask需要实现Ytask接口或者继承AbstractTimedTask
YTask task = new XxxTask(1, System.currentTimeMillis() + 1000, onlyOnceTimePeriod);
taskManager.addTask(task);
```
