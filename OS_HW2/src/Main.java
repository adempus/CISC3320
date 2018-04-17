/**
 * Jeff Morin
 * CISC 3320
 * Fall 2017
 * OS_HW2
 *
 */

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main
{
    protected final static ProcessIDManager PIDManager = ProcessIDManager.getInstance();

    public static void main(String[] args) {
        Random rand = new Random();
        final int maxTime = 90, minTime = 5;   // range of sleep time for threads, in seconds

        // this runnable task is passed to each thread with a random number of seconds to sleep for.
        Runnable task = () -> {
            int randSeconds = rand.nextInt((maxTime - minTime) + 1) + minTime;
            System.out.println("PID# " + Thread.currentThread().getId() +": "+
                    "\nProcess " + Thread.currentThread().getId() + " set to sleep for " + randSeconds + " seconds." +
                    "\n"+PIDManager.getAvailablePIDs().get()+" PID numbers remaining for use.\n");
            try {
                TimeUnit.SECONDS.sleep(randSeconds);
            } catch (InterruptedException Iex) {
                System.out.println("Process " + Thread.currentThread().getId() + " has stopped.");
            } finally {
                long pid = Thread.currentThread().getId();
                Thread.currentThread().interrupt();
                PIDManager.releasePID(pid);
                PIDManager.printProcessStatus(pid);    // the status of IDs are printed post-process execution.
                PIDManager.printAvailabilityStatus();
            }
        };

        // allocate a list of 3000 threads for each process.
        List<Process> pList = new ArrayList<>();
        for (int i = 0 ; i < 3000 ; i++)
            pList.add(new Process(task));

        // each thread's task is started, and the ProcessIDManager prints the status of each.
        pList.forEach((Process p) -> {
            try {
                p.setProcessID(PIDManager.allocatePID());
                p.start();
                PIDManager.printProcessStatus((int) p.getId());
            } catch (InterruptedException ex) {
                System.err.println("Unexpected interruption on process # "+p.getId());
            }
        });
    }
}