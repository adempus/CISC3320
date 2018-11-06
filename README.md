# CISC3320 Intro to Operating Systems 

## Final Project

This project consisted of writing a program that simulates the process of job scheduling in an OS kernel, in FCFS fashion. 
It involves using multithreading in Java to employ the use of threads as tasks, and synchronization using a semaphore
to safeguard against race conditions when assigning/unassigning unique Process Identifiers (from a shared resource),
to a new task. 

The example below was set to run 50 threads acting as tasks, each being assigned a unique ID from a limited resource of IDs,
(25 to be exact). What follows is each thread executing their task (simulated by putting the thread to sleep for 5 to 30 secs.),
before returning their respective ID for use by another awaiting task.



![alt text](https://github.com/adempus/CISC3320/blob/master/OS_HW3/ProcessIDScheduler.gif)
