/**
 * Jeff Morin
 * CISC 3320
 * Fall 2017
 * OS_HW1
 */

public class Main {
    static PID_Manager pid_manager = PID_Manager.getInstance();

    public static void main(String[] args) {
        int pid = 512;
        System.out.println("Testing status of a process id that is not in use: pid "+pid);
        pid_manager.printProcessStatus(pid);
        getPID();
    }

    public static void getPID() {
        System.out.println("\nTesting process id retrieval");
        int process = pid_manager.allocatePID();
        System.out.println("got process number: " + process);
        pid_manager.printProcessStatus(process);
        releasePID(process);
    }

    public static void releasePID(int pid) {
        System.out.println("\nReleasing process number "+ pid);
        pid_manager.releasePID(pid);
        pid_manager.printProcessStatus(pid);
    }
}
