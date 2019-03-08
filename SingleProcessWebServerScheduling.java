import java.util.Random;
import java.util.Scanner;

public class SingleProcessWebServerScheduling {

    public static void main(String[] args) {
        SingleServer s = new SingleServer();
        Scanner input = new Scanner(System.in);

        System.out.print("Enter the seed for random function (0: randomly generate): ");
        s.seed = input.nextInt();
        System.out.print("Enter the scheduling policy (0: FIFO; 1: SJF): ");
        s.scheduling_policy = input.nextInt();
        System.out.print("Enter the mean interarrival time: ");
        s.mean_interarrival = input.nextDouble();
        System.out.print("Enter the number of requests: ");
        s.num_of_requests = input.nextInt();
        System.out.print("Enter the number of trails: ");
        int num_of_trails = input.nextInt();

        double[] final_result = new double[18];

        s.initialize(); // Initialize the simulation

        int progress_indicator = num_of_trails / 100;
        for (int i = 0; i < num_of_trails; i++) {
            while (s.num_custs_delayed < s.num_of_requests) {
                s.timing(); // Determine the next event.Run the simulation while more delays are still needed.
                s.update_time_avg_stats(); // Update time-aver
                // Invoke the appropriate statistical accumulators. event function.
                switch (s.next_event_type) {
                    case 1:
                        // Arrive
                        s.arrive();
                        break;
                    case 2:
                        // Depart
                        s.depart();
                        break;
                }
            }

            // Invoke the report generator and end the simulation.
            double[] temp = s.report();

            for (int j = 0; j < temp.length; j++) {
                final_result[j] += temp[j];
            }

            // Progress bar
            if (num_of_trails >= 100 && i % progress_indicator == 0) {
                System.out.print("\rPROGRESS|");
                for (int j = 0; j < i / progress_indicator + 1; j++) {
                    System.out.print("#");
                }
                for (int j = 0; j < 99 - i / progress_indicator; j++) {
                    System.out.print("-");
                }
                System.out.print("|" + (i / progress_indicator + 1) + "%");
            }
        }

        for (int i = 0; i < final_result.length; i++) {
            final_result[i] /= num_of_trails;
        }

        // Print final average result
        System.out.println("\n");
        System.out.println("THE AVERAGE RESULT FOR " + num_of_trails + ((num_of_trails == 1) ? " TRAIL" : " TRAILS"));
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("Mean response time:    " + final_result[0]);
        System.out.println("Maximum response time: " + final_result[1]);
        System.out.println();

        System.out.println("Fraction of requests delayed in q > 10s: (" + final_result[2] + ")\t\t"
                + final_result[3]);
        System.out.println("Fraction of requests delayed in q > 1m:  (" + final_result[4] + ")\t\t"
                + final_result[5]);
        System.out.println();

        System.out.println("Mean response time for small file (<=10KB):          (" + final_result[6] + ")\t\t"
                + final_result[7]);
        System.out.println("Mean response time for medium file (>10KB, <1000KB): (" + final_result[8] + ")\t\t"
                + final_result[9]);
        System.out.println("Mean response time for large file (>=1000KB):        (" + final_result[10] + ")\t\t"
                + final_result[11]);
        System.out.println();

        System.out.println("Time simulation ended (" + final_result[12] + " requests): " + final_result[13]);
        System.out.println();

        System.out.println("Server utilization:      " + final_result[14]);
        System.out.println("Max number in queue:     " + final_result[15]);
        System.out.println("Average delay in queue:  " + final_result[16]);
        System.out.println("Average number in queue: " + final_result[17]);
    }

    public static class SingleServer {
        Random geneator;
        final int QUEUE_SIZE = 10000;
        final double PARETO_INDEX = 1.2;
        final double SMALLEST_FILE_SIZE = 3.0;
        final double DELIEVER_SPEED = 30.0;
        int seed;
        int scheduling_policy;
        int next_event_type;
        int num_events;
        int num_in_q;
        int server_status;
        int num_of_requests;
        int max_num_in_q;

        double area_num_in_q;
        double area_server_status;
        double sim_time;
        double time_last_event;
        double mean_interarrival;

        double max_response_time;
        double total_of_delays;
        double total_of_service;
        int num_custs_delayed;
        int num_custs_delayed_10s;
        int num_custs_delayed_1m;

        int num_files_small;
        int num_files_medium;
        int num_files_large;
        double total_response_time_small;
        double total_response_time_medium;
        double total_response_time_large;

        double[] time_arrival = new double[QUEUE_SIZE];
        double[] time_next_event = new double[3];
        double[] service_time_in_q = new double[QUEUE_SIZE];

        private void initialize() {
            if (seed == 0) {
                geneator = new Random();
            } else {
                geneator = new Random(seed);
            }

            // Initialize the simulation clock.
            sim_time = 0.0;
            num_events = 2;

            // Initialize the state variables.
            server_status = 0;
            time_last_event = 0.0;

            // Initialize the statistical counters.
            num_in_q = 0;
            num_custs_delayed = 0;
            total_of_delays = 0.0;
            total_of_service = 0.0;
            max_response_time = 0.0;
            area_num_in_q = 0.0;
            area_server_status = 0.0;
            max_num_in_q = 0;
            num_files_small = 0;
            num_files_medium = 0;
            num_files_large = 0;
            total_response_time_small = 0.0;
            total_response_time_medium = 0.0;
            total_response_time_large = 0.0;

            // Initialize event list. Since no customers are present, the departure (service completion) event is
            // eliminated from consideration.
            time_next_event[1] = sim_time + expon(mean_interarrival);
            time_next_event[2] = 1.0e+30;
        }

        private void timing() {
            int i;
            double min_time_next_event = 1.0e+30;
            next_event_type = 0;

            // Determine the event type of the next event to occur.
            for (i = 1; i <= num_events; ++i) {
                if (time_next_event[i] < min_time_next_event) {
                    min_time_next_event = time_next_event[i];
                    next_event_type = i;
                }
            }

            // Check to see whether the event list is empty.
            if (next_event_type == 0) {
                // The event list is empty, so stop the simulation.
                System.out.println("Event List is Empty");
                System.exit(1);
            }

            // The event list is not empty, so advance the simulation clock.
            sim_time = min_time_next_event;
        }

        private void arrive() {
            double delay;
            double service;

            // Schedule next arrival.
            time_next_event[1] = sim_time + expon(mean_interarrival);

            // Check to see whether server is busy.
            if (server_status == 1) {
                // Server is busy, so increment number of customers in queue.
                ++num_in_q;

                // Update max number in q
                if (num_in_q >= max_num_in_q) {
                    max_num_in_q = num_in_q;
                }

                // Check to see whether an overflow condition exists.
                if (num_in_q >= QUEUE_SIZE) {
                    // The queue has overflowed, so stop the simulation.
                    System.err.println("Overflow of the queue time arrival at " + sim_time);
                    System.exit(0);
                }

                // There is still room in the queue, so store the time of arrival of the arriving customer at the (new)
                // end of time_arrival.
                time_arrival[num_in_q] = sim_time;

                service = pareto() / DELIEVER_SPEED;
                service_time_in_q[num_in_q] = service;
            } else {
                // Server is idle, so arriving customer has a delay of zero. (The following two statements are for
                // program clarity and do not affect the results of the simulation.)
                delay = 0;
                total_of_delays += delay;

                // Increment the number of customers delayed, and make server busy.
                ++num_custs_delayed;
                server_status = 1;

                // Schedule a departure (service completion).
                service = pareto() / DELIEVER_SPEED;
                time_next_event[2] = sim_time + service;

                // Compare this response time with max response time
                if (delay + service >= max_response_time) {
                    max_response_time = delay + service;
                }

                // Update the total response time accumulator for different file size.
                // Small file size: <= 10KB
                // Medium file size: >10KB, <= 1000KB
                // Large file size: > 1000KB
                if (service * DELIEVER_SPEED <= 10) {
                    total_response_time_small += delay + service;
                    num_files_small++;
                }
                if (service * DELIEVER_SPEED <= 1000 && service * DELIEVER_SPEED > 10) {
                    total_response_time_medium += delay + service;
                    num_files_medium++;
                }
                if (service * DELIEVER_SPEED > 1000) {
                    total_response_time_large += delay + service;
                    num_files_large++;
                }
            }
        }

        private void depart() {
            int i;
            double delay;
            double service;

            // Check to see whether the queue is empty.
            if (num_in_q == 0) {
                // The queue is empty so make the server idle and eliminate the departure (service completion) event
                // from consideration.
                server_status = 0;
                time_next_event[2] = 1.0e+30;
            } else {
                // The queue is nonempty, so decrement the number of customers in queue.
                --num_in_q;

                // Based on scheduling policy to calculate service time
                if (scheduling_policy == 0) {
                    // FIFO
                    service = service_time_in_q[1];
                } else {
                    // SJF
                    // Find the smallest service time in queue and exchange it with the first one in queue
                    int smallest_index = get_min_index(service_time_in_q);
                    double temp;
                    temp = service_time_in_q[1];
                    service_time_in_q[1] = service_time_in_q[smallest_index];
                    service_time_in_q[smallest_index] = temp;
                    temp = time_arrival[1];
                    time_arrival[1] = time_arrival[smallest_index];
                    time_arrival[smallest_index] = temp;

                    service = service_time_in_q[1];
                }

                // Compute the delay of the customer who is beginning service and update the total delay accumulator.
                delay = sim_time - time_arrival[1];
//                System.out.println("time arrical: " + time_arrival[1]);
                total_of_delays += delay;

                // Increment the number of customers delayed, and schedule departure.
                ++num_custs_delayed;

                time_next_event[2] = sim_time + service;
                total_of_service += service;

                // Move each customer in queue (if any) up one place.
                for (i = 1; i <= num_in_q + 1; ++i) {
                    time_arrival[i] = time_arrival[i + 1];
                    service_time_in_q[i] = service_time_in_q[i + 1];
                }

                // Compare this response time with max response time
                if (delay + service >= max_response_time) {
                    max_response_time = delay + service;
                }

                // Delay counter
                if (delay > 10) {
                    num_custs_delayed_10s++;
                    if (delay > 60) {
                        num_custs_delayed_1m++;
                    }
                }

                // Update the total response time accumulator for different file size.
                // Small file size: <= 10KB
                // Medium file size: <= 1000KB
                // Large file size: > 1000KB
                if (service * DELIEVER_SPEED <= 10) {
                    total_response_time_small += delay + service;
                    num_files_small++;
                }
                if (service * DELIEVER_SPEED <= 1000 && service * DELIEVER_SPEED > 10) {
                    total_response_time_medium += delay + service;
                    num_files_medium++;
                }
                if (service * DELIEVER_SPEED > 1000) {
                    total_response_time_large += delay + service;
                    num_files_large++;
                }
            }
        }

        private double[] report() {
//            System.out.println("-----------------------------------------------------------------------------------");
//            System.out.println("Mean response time:    " + (total_of_service + total_of_delays) / num_of_requests);
//            System.out.println("Maximum response time: " + max_response_time);
//            System.out.println();
//
//            System.out.println("Fraction of requests delayed in q > 10s: (" + num_custs_delayed_10s + ")\t"
//                    + num_custs_delayed_10s * 1.0 / num_custs_delayed);
//            System.out.println("Fraction of requests delayed in q > 1m:  (" + num_custs_delayed_1m + ")\t"
//                    + num_custs_delayed_1m * 1.0 / num_custs_delayed);
//            System.out.println();
//
//            System.out.println("Mean response time for small file (<=10KB):          (" + num_files_small + ")\t"
//                    + total_response_time_small / num_files_small);
//            System.out.println("Mean response time for medium file (>10KB, <1000KB): (" + num_files_medium + ")\t"
//                    + total_response_time_medium / num_files_medium);
//            System.out.println("Mean response time for large file (>=1000KB):        (" + num_files_large + ")\t"
//                    + total_response_time_large / num_files_large);
//            System.out.println();
//
//            System.out.println("Time simulation ended (" + num_of_requests + " requests): " + sim_time);
//            System.out.println();
//
//            System.out.println("Server utilization:      " + area_server_status / sim_time);
//            System.out.println("Max number in queue:     " + max_num_in_q);
//            System.out.println("Average delay in queue:  " + total_of_delays / num_custs_delayed);
//            System.out.println("Average number in queue: " + area_num_in_q / sim_time);

            return new double[]{
                    (total_of_service + total_of_delays) / num_of_requests,
                    max_response_time,
                    num_custs_delayed_10s, num_custs_delayed_10s * 1.0 / num_custs_delayed,
                    num_custs_delayed_1m, num_custs_delayed_1m * 1.0 / num_custs_delayed,
                    num_files_small, total_response_time_small / num_files_small,
                    num_files_medium, total_response_time_medium / num_files_medium,
                    num_files_large, total_response_time_large / num_files_large,
                    num_of_requests, sim_time,
                    area_server_status / sim_time,
                    max_num_in_q,
                    total_of_delays / num_custs_delayed,
                    area_num_in_q / sim_time
            };
        }

        private void update_time_avg_stats() {
            double time_since_last_event;

            // Compute time since last event, and update last-event-time marker.
            time_since_last_event = sim_time - time_last_event;
            time_last_event = sim_time;

            // Update area under number-in-queue function.
            area_num_in_q += num_in_q * time_since_last_event;

            // Update area under server-busy indicator function.
            area_server_status += server_status * time_since_last_event;
        }

        private static int get_min_index(double[] Array) {
            int min_index = 1;
            double min_value = Array[1];
            for (int i = 1; i < Array.length; i++) {
                if (Array[i] <= min_value && Array[i] != 0) {
                    min_index = i;
                    min_value = Array[i];
                }
            }
            return min_index;
        }


        private double expon(double mean) {
            // Return an exponential random variate with mean "mean".
//            return (-mean * Math.log(geneator.nextDouble()));
            return (-mean * Math.log(geneator.nextDouble()));
        }

        private double pareto() {
            // Return an Pareto random variate as file size with α and Xmin
//            return (SMALLEST_FILE_SIZE / (Math.pow(Math.random(), (1 / PARETO_INDEX))));
            return (SMALLEST_FILE_SIZE / (Math.pow(geneator.nextDouble(), (1 / PARETO_INDEX))));
        }
    }
}
