package com.aman;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TaskInitiator {
    private int MINIMUM_NUMBER_TASKS = 30;
    private int MINIMUM_TIME_TO_EXECUTE = 10;
    private int counter = 0;
    private int EXECUTION_BANDWIDTH = 4;
    private Semaphore semaphore = new Semaphore(EXECUTION_BANDWIDTH);
    private CountDownLatch latch;
    static private FileHandler fileHandler;
    static private SimpleFormatter simpleFormatter;
    private final static Logger logger = Logger.getLogger(TaskInitiator.class.getName());

    public class ExecuteMultithreadedTasks extends Thread {
        private Task task;

        ExecuteMultithreadedTasks(Task task) {
            this.task = task;
        }

        @Override
        public void run() {
            try {
                logger.info("Waiting for lock => " + task.getTaskName());
                semaphore.acquire();
                logger.info("Lock acquired by " + task.getTaskName() + ". Available permits: " + semaphore.availablePermits());
                logger.info("Executing " + task.getTaskName());
                Thread.sleep(1000 * task.getTimeToComplete());
                logger.info("Finished executing " + task.getTaskName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
                logger.info("Releasing Lock by " + task.getTaskName() + ". Available permits: " + semaphore.availablePermits());
                latch.countDown();
            }
        }
    }

    public void start() {
        initLogger();
        int numberOfTasks = new Random().nextInt(10) + MINIMUM_NUMBER_TASKS;
        List<Task> taskList = generateTasks(numberOfTasks);
        executeParallelTasks(taskList);
    }

    private void initLogger() {
        logger.setLevel(Level.ALL);
        try {
            fileHandler = new FileHandler("tasks.log");
            simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Yayy! Logger initiated!");
    }

    private void executeParallelTasks(List<Task> taskList) {
        int taskSize = taskList.size();
        logger.info("Total number of tasks => " + taskSize);
        Task firstTask = taskList.get(0);
        Task finalTask = taskList.get(taskSize - 1);
        latch = new CountDownLatch(taskSize - 2);
        executeUnitTask(firstTask);
        for (int i = 1; i < taskSize - 1; i++) {
            ExecuteMultithreadedTasks executeMultithreadedTasks = new ExecuteMultithreadedTasks(taskList.get(i));
            executeMultithreadedTasks.start();
        }
        try {
            latch.await();
            logger.info("All tasks executed. Executing final task!");
            executeUnitTask(finalTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void executeUnitTask(Task task) {
        logger.info("Executing unit task => " + task.getTaskName());
        logger.info("Expected time of completion: " + task.getTimeToComplete() + "s");
        try {
            TimeUnit.SECONDS.sleep(task.getTimeToComplete());
            logger.info("Execution completed => " + task.getTaskName() + " executed successfully! Wait for 3 seconds before we move further.");
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<Task> generateTasks(int numberOfTasks) {
        logger.info("Generating tasks =>");
        List<Task> taskList = new ArrayList<Task>();

        while (counter < numberOfTasks) {
            int timeToExecute = new Random().nextInt(5) + MINIMUM_TIME_TO_EXECUTE;
            String taskName = "task#" + counter;
            Task task = new Task(taskName , timeToExecute);
            taskList.add(task);
            logger.info("Task Generated => " + taskName + " | " + "Time to Execute : " + timeToExecute + "s");
            counter++;
        }
        return taskList;
    }
}
