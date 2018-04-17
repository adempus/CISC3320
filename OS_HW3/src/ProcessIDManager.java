import java.util.Map;
import java.time.LocalTime;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  Jeff Morin
 *  CISC 3320
 *  Fall 2017
 *  OS_HW3
 *
 *  This class houses a hash map of long keys ranging from 300 to 1300, representing 1000 IDs to be
 *  allocated to processes, and boolean values indicating the current usage status of each ID. This class is
 *  made a singleton to prevent multiple instances which can lead to undesirable effects (i.e. duplicate IDs).
 */

public class ProcessIDManager
{
    final private long MIN_PID = 300, MAX_PID = 1300;
    final private String managerLogMsg = "--PIDManager log: ";

    /** ConcurrentHashMap of long,boolean k,v pairs for thread-safe rapid access of keys & updates to their statuses.
     *  (true for in use, false otherwise).
     */
    final private ConcurrentHashMap<Long, AtomicBoolean> PID_map;

    /** A counting semaphore to suspend any process requesting an ID, when none are available.
     *  Prevents allocating an invalid ID of -1 to any process.
     */
    final private Semaphore PID_allocSemaphore;
    final private AtomicInteger PID_count;

    private ProcessIDManager()
    {
        int size = (int) (MAX_PID - MIN_PID);
        PID_map = new ConcurrentHashMap<>(size);
        PID_allocSemaphore = new Semaphore(size, true);
        PID_count = new AtomicInteger(size);

        if (allocateMap() == 1)
             System.out.println(managerLogMsg+" "+ PID_count.get()+" IDs available.\n");
        else
            System.err.println(managerLogMsg+" error on map allocation.");
    }

    /** Singleton helper. */
    private static class PIDManagerSingleton {
        private static final ProcessIDManager singleInstance = new ProcessIDManager();
    }

    /** @return a singleton instance of this class.*/
    public static ProcessIDManager getInstance() {
        return PIDManagerSingleton.singleInstance;
    }

    /** initializes the hash map for representing process IDs
     *  @return -1 if unsuccessful and 1 if successful
     */
    private int allocateMap() {
        try {
            for (long i = MIN_PID ; i < MAX_PID ; i++)
                PID_map.put(i, new AtomicBoolean(false));
        } catch (Exception ex) {
            return -1;
        }
        return 1;
    }

    /** Current in-use state of the PID returned from this method will be set to true.
     *  If no PID is available, all processes invoking this method are set to wait() until one is.
     *  @return a Process ID long value if any are available. Catastrophic errors return -1.
     */
    public long allocatePID() throws InterruptedException {
        PID_allocSemaphore.acquire(1);
        for (Map.Entry<Long, AtomicBoolean> entry : PID_map.entrySet()) {
            if (entry.getValue().compareAndSet(false, true)) {
                PID_count.getAndDecrement();
                return entry.getKey();
            }
        }
        return -1;
    }

    /** Releases a process id, and sets its in-use state to false. An ID released by a process causes
     *  a semaphore to increase by 1 permit; allowing the next process to allocate an ID for use.
     *  @param pid - the process ID number to release, and change its in-use state to false.
     */
    public void releasePID(long pid) {
        AtomicBoolean value = isInUse(pid);
        if (value == null) {
            System.err.println(managerLogMsg+" PID# "+pid+" not found.");
        } else if (value.compareAndSet(true, false)) {
            System.out.println("\n"+managerLogMsg+" PID# "+pid+" was returned.");
            PID_count.getAndIncrement();
            PID_allocSemaphore.release();
        } else {
            System.err.println(managerLogMsg+" PID# "+pid+" was not assigned.\n");
        }
    }

    /** prints the current usage status of a given process ID number.
     * @param pid - the requested ID to print status for.
     */
    public void printProcessStatus(long pid) {
        AtomicBoolean value = isInUse(pid);
        System.out.println(
                value != null ? (value.get() ? managerLogMsg+" PID# "+pid+" is now in use @ "+ LocalTime.now()+"\n"
                        : managerLogMsg+" PID# "+pid+" is now available.")
                        : managerLogMsg+" Invalid PID# provided for status.\n");
    }

    /** prints a status of the number PIDs currently available for use. */
    public void printAvailabilityStatus() {
        AtomicInteger availableIDs = availabilityCount();
        final String text = " PID number available for use @ ";
        System.out.println(
                availableIDs.get() == 1 ? managerLogMsg + " "+availableIDs.get()+text + LocalTime.now()+"\n" :
                        managerLogMsg+" "+availableIDs.get()+text.replace("number", "numbers")
                                + LocalTime.now()+"\n");
    }

    /** @return the number of PIDs currently available for use by a process. */
    public AtomicInteger availabilityCount() {
        return PID_count;
    }

    /** @param pid - the process id number to query for a usage status.
     *  @return a reference to the AtomicBoolean value of a specified PID key. A value of true indicates
     *  the PID is currently in use, otherwise false.
     */
    public AtomicBoolean isInUse(long pid) {
        return PID_map.getOrDefault(pid, null);
    }
}