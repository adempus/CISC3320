# CISC3320 Homework2
### Programming Problem 4.20
Modify HW1. Write a multithreaded program that tests your solution to HW1. You will create several threads – for example, 100 – and each thread will request a pid, sleep for a random period of time, and then release the pid. 

Sleeping for a random period approximates the typical pid usage in which a pid is assigned to a new process, the process executes and terminates, and the pid is released on the process’ termination). 
On UNIX and Linux systems, sleeping is accomplished through the ```sleep()``` function, which is passed an integer value representing the number of seconds to sleep. 
