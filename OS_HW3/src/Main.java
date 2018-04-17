/**
 * Jeff Morin
 * CISC 3320
 * Fall 2017
 * OS_HW3
 *
 */

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main
{
    protected final static ProcessIDManager PIDManager = ProcessIDManager.getInstance();

    public static void main(String[] args) {
        Random rand = new Random();
        final int maxTime = 120, minTime = 5;

        Runnable PIDAllocTask = () -> {
            // each process acquiring a valid PID, sleeps for a random amount of time
            int randSec = rand.nextInt((maxTime - minTime) + 1) + minTime;
            System.out.println("Process " + Thread.currentThread().getId() + " set to sleep for " + randSec + " seconds." +
                    "\n"+PIDManager.availabilityCount().get()+" PID numbers remaining for use.\n");
            try {
                TimeUnit.SECONDS.sleep(randSec);
            } catch (InterruptedException Iex) {
                System.out.println("*Process " + Thread.currentThread().getId() + " has stopped.");
            } finally {
                //  when sleep time is up, the thread must return the PID, and terminate
                long pid = Thread.currentThread().getId();
                Thread.currentThread().interrupt();
                PIDManager.releasePID(pid);
                PIDManager.printProcessStatus(pid);
                PIDManager.printAvailabilityStatus();
            }
        };

        // initialize a list of 4000 threads, each carrying a "PID allocation" task to run.
        List<Process> pList = new ArrayList<>();
        for (int i = 0 ; i < 4000 ; i++) {
            pList.add(new Process(PIDAllocTask));
        }

        // start the process of running each thread's task.
        pList.forEach((Process process) -> {
            try {
                process.setProcessID(PIDManager.allocatePID());
                process.start();
                PIDManager.printProcessStatus((int) process.getId());
            } catch (InterruptedException ex) {
                System.err.println("unexpected interruption on PID# "+process.getId());
            }
        });
    }
}