/**
 * Jeff Morin
 * CISC 3320
 * Fall 2017
 * OS_HW1
 */


import java.util.Map;
import java.time.LocalTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  This class houses a hash map of integer keys ranging from 300 to 1000, representing IDs to
 *  be allocated to processes, and boolean values indicating whether or not an ID is currently in use.
 *  This class is made a singleton to prevent multiple instances, which can lead to undesirable
 *  effects (i.e. duplicate IDs).
 */
public class ProcessIDManager
{
    private final long MIN_PID = 300, MAX_PID = 1300;
    private Semaphore PID_allocationSemaphore;

    /** A ConcurrentHashMap of integer and AtomicBoolean key-value pairs is used for retrieval
     *  of process IDs, and rapid updates of their statuses. It also ensures thread safe operations
     *  on the map, and allows for non-blocking synchronized updates of PID statuses
     *  (true for in use, false otherwise).
     */
    private ConcurrentHashMap<Long, AtomicBoolean> processID_map;
    final private String managerLogMsg = "-- PIDManager log:";

    private ProcessIDManager() {
        if ((allocateMap() == 1)) {
            PID_allocationSemaphore = new Semaphore((int) (MAX_PID - MIN_PID));
            System.out.println(managerLogMsg+" "+PID_allocationSemaphore.availablePermits()
                    +" IDs available.\n");
        } else {
            System.err.println(managerLogMsg + " error on map allocation.");
        }
    }

    private static class PIDManagerSingleton {
        private static final ProcessIDManager singleInstance = new ProcessIDManager();
    }

    public static ProcessIDManager getInstance() {
        return PIDManagerSingleton.singleInstance;
    }

    /** Creates and initializes a data structure for representing process IDs
     * @return -1 if unsuccessful and 1 if successful
     */
    private int allocateMap() {
        try {
            processID_map = new ConcurrentHashMap<>((int) (MAX_PID - MIN_PID));
            for (long i = MIN_PID ; i < MAX_PID ; i++) {
                processID_map.put(i, new AtomicBoolean(false));
            }
        } catch (Exception ex) {
            return -1;
        }
        return 1;
    }

    /** Allocates and returns a process ID to a process. A semaphore is used to prevent access
     *  to the map when there are no IDs available for use. This prevents allocating an invalid
     *  PID of -1 to any requesting process, when there aren't PIDs available.
     *
     * @return -1 if unable to allocate a process ID (meaning all PIDs are in use)
     */
    public long allocatePID() throws InterruptedException {
        PID_allocationSemaphore.acquire(1);
        for (Map.Entry<Long, AtomicBoolean> entry : processID_map.entrySet()) {
            if (entry.getValue().compareAndSet(false, true)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /** Releases a process id, and sets its in-use status to false. An ID released by a process causes
     *  the semaphore to in turn release a permit; allowing the next process to allocate an ID for use.
     *
     * @param pid - the number of the process ID to release.
     */
    public void releasePID(long pid) {
        AtomicBoolean value = isInUse(pid);
        if (value == null) {
            System.err.println(managerLogMsg+" PID# "+pid+" not found.");
        } else if (value.compareAndSet(true, false)) {
            System.out.println("\n"+managerLogMsg+" PID# "+pid+" returned.");
            PID_allocationSemaphore.release();
        } else {
            System.err.println(managerLogMsg+" PID# "+pid+" was not assigned.\n");
        }
    }

    /** prints the status of a given process ID .
     * @param pid - the requested ID to print status for. */
    public void printProcessStatus(long pid) {
        AtomicBoolean value = isInUse(pid);
        System.out.println(
                value != null ? (value.get() ? managerLogMsg+" PID# "+pid+" is now in use @ "+ LocalTime.now()+"\n"
                        : managerLogMsg+" PID# "+pid+" is not in use")
                        : managerLogMsg+" Invalid PID# provided for status.\n");
    }

    /** prints a status of the number PIDs currently available for use. */
    public void printAvailabilityStatus() {
        System.out.println(managerLogMsg+" "+getAvailablePIDs().get()+" PID numbers available for use.\n");
    }

    /** @return the number of PIDs currently available for use by a process. */
    public AtomicInteger getAvailablePIDs() {
        return new AtomicInteger(PID_allocationSemaphore.availablePermits());
    }

    /**
     * @param pid - the process id number to query for usage status.
     * @return a reference to the AtomicBoolean value of a specified PID key.
     */
    public AtomicBoolean isInUse(long pid) {
        return processID_map.getOrDefault(pid, null);
    }
}