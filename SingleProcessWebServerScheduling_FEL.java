import java.util.LinkedList;

// Record for future event
class eRecord {
    double eTime; // Event time
    int eType;    // Event type: 1 Arrival event; 2 Departure Event; 3 End Event
}

public class SingleProcessWebServerScheduling_FEL {
    public static void main(String[] args) {
        SingleServer s = new SingleServer();

        s.load_data();
        System.out.println("Single-server queueing system simulation\n" +
                "Interarrival times: 3, 2, 6, 2, 4, 5\n" +
                "Service times: 2, 5, 5, 8, 4, 5\n" +
                "Simulation Ending Time: 20\n");

        // Initialize the simulation
        s.initialize();

        //print table column headings to the output file
        System.out.printf("%-10s%-10s%-10s%-10s%-10s%-10s\n", "Clock", "LQ", "LS", "B", "MQ", "FE List");

        do {
            s.print_table();

            s.timing();// Determine the next event.

            // Update time-average statistical accumulators.
            s.update_time_avg_stats();

            // Invoke the appropriate event function.
            switch (s.next_event_type) {
                case 1:
                    // Arrival Event
                    s.arrive();
                    break;
                case 2:
                    // Departure Event
                    s.depart();
                    break;
                case 3:
                    // End Event
                    break;
            }
        } while (s.next_event_type != 3);

        // Generate final report
        s.report();
    }

    public static class SingleServer {
        int next_event_type, num_custs_delayed, num_in_q, MQ, server_status;
        double area_num_in_q, area_server_status;
        double sim_time, time_last_event, total_of_delays;

        LinkedList<Double> qInterArrivalTime = new LinkedList<>();// Queue for interarrival time
        LinkedList<Double> qArrivalTime = new LinkedList<>();// Queue for Arrival time
        LinkedList<Double> qServiceTime = new LinkedList<>();// Queue for service time
        LinkedList<eRecord> FElist = new LinkedList<>();// Future event list

        private void load_data() {

            // Load the interarrival time to qInterArrivalTime
            qInterArrivalTime.add(3.0);
            qInterArrivalTime.add(2.0);
            qInterArrivalTime.add(6.0);
            qInterArrivalTime.add(2.0);
            qInterArrivalTime.add(4.0);
            qInterArrivalTime.add(5.0);

            // Load the service time to qServiceTime
            qServiceTime.add(2.0);
            qServiceTime.add(5.0);
            qServiceTime.add(5.0);
            qServiceTime.add(8.0);
            qServiceTime.add(4.0);
            qServiceTime.add(5.0);
        }

        private void initialize() {
            // Initialize the simulation clock.
            sim_time = 0.0;

            // Initialize the state variables.
            server_status = 0; // 0 for IDLE, 1 for BUSY
            num_in_q = 0;
            MQ = 0;
            time_last_event = 0.0;

            // Initialize the statistical counters.
            num_custs_delayed = 0;
            total_of_delays = 0.0;
            area_num_in_q = 0.0;
            area_server_status = 0.0;

            // Initialize event list.
            // Add first arrival event and end simulation event
            eRecord e = new eRecord();
            e.eTime = sim_time + qInterArrivalTime.peekFirst();
            qInterArrivalTime.remove();
            e.eType = 1;
            FElist.add(e);

            eRecord e2 = new eRecord();
            e2.eTime = 20;
            e2.eType = 3;
            FElist.add(e2);
        }

        private void timing() {
            next_event_type = 0;

            // Determine the event type of the next event to occur.
            if (!FElist.isEmpty()) {
                eRecord e = FElist.peekFirst();
                sim_time = e.eTime;
                next_event_type = e.eType;
                FElist.remove();
            }

            // Check to see whether the event list is empty.
            if (next_event_type == 0) {
                // The event list is empty, so stop the simulation.
                System.out.printf("\nEvent list empty at time %f", sim_time);
                System.exit(1);
            }
        }

        private void arrive() {
            double delay;

            // Schedule next arrival event
            if (qInterArrivalTime.size() > 0) {
                //insert into Future Event list
                eRecord e = new eRecord();
                e.eTime = sim_time + qInterArrivalTime.peekFirst();
                qInterArrivalTime.remove();
                e.eType = 1;
                insert_future_event_list(e);
            }

            // Check to see whether server is busy.
            if (server_status == 1) {
                // Server is busy, so increment number of customers in queue.
                ++num_in_q;
                if (MQ < num_in_q) {
                    MQ = num_in_q;
                }
                // store the time of arrival of the arriving customer at the (new) end.
                qArrivalTime.add(sim_time);
            } else {
                // Server is idle, so arriving customer has a delay of zero.
                delay = 0.0;
                total_of_delays += delay;

                // Increment the number of customers delayed, and make server busy.
                ++num_custs_delayed;
                server_status = 1;

                // Schedule a departure (service completion).
                if (qServiceTime.size() > 0) {
                    //insert departure event into Future Event list
                    eRecord e = new eRecord();
                    e.eTime = sim_time + qServiceTime.peekFirst();
                    qServiceTime.remove();
                    e.eType = 2;
                    insert_future_event_list(e);
                }
            }
        }

        private void depart() {
            double delay;
            // Check to see whether the queue is empty.
            if (num_in_q == 0) {
                // The queue is empty so make the server idle
                server_status = 0;
            } else {
                // The queue is nonempty, so decrement the number of customers in queue.
                --num_in_q;

                // Compute the delay of the customer who is beginning service
                delay = sim_time - qArrivalTime.peekFirst();
                if (delay < 0)
                    delay = 0;

                //update the total delay accumulator.
                total_of_delays += delay;

                // Increment the number of customers delayed
                ++num_custs_delayed;

                // Move each customer in queue (if any) up one place.
                qArrivalTime.remove();

                //schedule departure.
                if (qServiceTime.size() > 0) {
                    //insert departure event into Future Event list
                    eRecord e = new eRecord();
                    e.eTime = sim_time + qServiceTime.peekFirst();
                    qServiceTime.remove();
                    e.eType = 2;
                    insert_future_event_list(e);
                }
            }
        }

        private void update_time_avg_stats() {
            // Compute time since last event, and update last-event-time marker.
            double time_since_last_event = sim_time - time_last_event;
            if (time_since_last_event < 0) {
                time_since_last_event = 0;
            }

            time_last_event = sim_time;

            // Update area under number-in-queue function.
            area_num_in_q += num_in_q * time_since_last_event;

            // Update area under server-busy indicator function.
            area_server_status += server_status * time_since_last_event;
        }

        private void report() {
            System.out.printf("\nAverage delay in queue%11.2f minutes\n", total_of_delays / num_custs_delayed);
            System.out.printf("Average number in queue%10.2f\n", area_num_in_q / sim_time);
            System.out.printf("Server utilization%15.2f\n", area_server_status / sim_time);
            System.out.printf("Number of delays completed%7d\n", num_custs_delayed);
        }

        private void print_table() {
            System.out.printf("%-10.0f%-10d%-10d%-10.01f%-10d", sim_time, num_in_q, server_status, area_server_status, MQ);
            for (eRecord event : FElist) {
                System.out.printf("(%d,%.0f) ", event.eType, event.eTime);
            }
            System.out.println();
        }

        private void insert_future_event_list(eRecord e) {
            int index = 0;
            for (eRecord event : FElist) {
                if (event.eTime >= e.eTime) {
                    FElist.add(index, e);
                    break;
                }
                index++;
            }
        }
    }
}