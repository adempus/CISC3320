/**
 * Jeff Morin
 * CISC 3320
 * Fall 2017
 * OS_HW2
 *
 */


/** This class extends the Thread class to further customize the properties of Processes as we learned
 *  about in chapter 4. This includes a processID attribute to override the default ID assigned to
 *  a thread by the JVM.
 */
public class Process extends Thread
{
    private long processID;      // this will substitute the ID provided by the JVM

    public Process(String name, Runnable task) {
        super(task, name);
    }

    public Process(long processID, Runnable task) {
        this(Long.toString(processID), task);
        this.processID = processID;
    }

    public Process(Runnable task) {
        super(task);
    }

    /** Overrode the interrupt method to properly notify the user when the process has stopped,
     *  and to provide easy output as per the requirements of the assignment.
     */
    @Override
    public void interrupt() {
        System.out.println("Process "+this.processID+" has stopped. \n" +
                "Returning PID# " +this.processID+" ...");
        super.interrupt();
    }

    /** overridden to return the new process id allocated from the PIDManager class used by
     * this custom thread.
     */
    @Override
    public long getId() {
        return this.processID;
    }

    /** overridden to make sure that no process is started without a valid process ID. */
    @Override
    public void start() {
        if (processID > 0)
            super.start();
        else
            System.err.println("Invalid process ID assigned. Cannot start!");
    }

    public void setProcessID(long processID) {
        this.processID = processID;
    }
}