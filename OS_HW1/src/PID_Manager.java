import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Jeff Morin
 * CISC 3320
 * Fall 2017
 * OS_HW1
 */


/** The PID_Manager class is made a singleton to prevent multiple instantiations, which can cause errors,
 *  duplicating IDs, ext.
 * */
public class PID_Manager {
    private final int MIN_PID = 300;
    private final int MAX_PID = 5000;

    /** A ConcurrentHashMap is used along with an integer and atomic boolean, key value pair.
     * This ensures thread safe operations on the map, and allows for non-blocking synchronization
     * on updates of PID status (true for in use, false otherwise).
     * **/
    private ConcurrentHashMap<Integer, AtomicBoolean> processID_map;

    private PID_Manager() {
        allocateMap();
    }

    private static class SingletonHelper {
        private static final PID_Manager singleInstance = new PID_Manager();
    }

    public static PID_Manager getInstance() {
        return SingletonHelper.singleInstance;
    }

    /** Creates and initializes a data structure for representing pids;
     *  @return -1 if unsuccessful and 1 if successful
     **/
    private int allocateMap() {
        processID_map = new ConcurrentHashMap<>(4700);
        for (int i = MIN_PID ; i < MAX_PID; i++) {
            processID_map.put(i , new AtomicBoolean(false));
        }
        return 1;
    }


    /** Allocates and returns a pid;
     * @return -1 if unable to allocate a pid (all pids are in use)
     **/
    public int allocatePID() {
        for(Map.Entry<Integer, AtomicBoolean> entry : processID_map.entrySet()) {
            if (entry.getValue().compareAndSet(false, true)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /** Releases a process id, and sets that pid's status to false (not in use).
     * @param pid - the number of the process ID to release. */
    public void releasePID(int pid) {
        AtomicBoolean value = isInUse(pid);
        if (value.get())
            value.set(false);
    }

    public void printProcessStatus(int pid) {
        if(isInUse(pid).get()) {
            System.out.println("process "+pid+" is in use.");
        } else {
            System.out.println("process "+ pid +" is not in use.");
        }
    }

    /** returns whether a process id is in use or not.
     * @return true if in use, false otherwise. */
    public AtomicBoolean isInUse(int pid) {
        for (Map.Entry<Integer, AtomicBoolean> entry : processID_map.entrySet()) {
            if (entry.getKey() == pid) {
                return entry.getValue();
            }
        }
        return new AtomicBoolean(false);
    }
}
