# Single Process Web Server Scheduling Simulation 

19SPWZ_CPS_4410_W02_SYSTEMS_SIMULATION

## Introduction

This is a Jave implementation of the single process web server with high flexiablity. The interarrival times are independent and identically distributed (IID) exponential random variables with a user-inputed mean. The file sizes for requests are IID Pareto random variables with Pareto index α = 1.2 and x<sub>min</sub> = 3KB. The server process can deliever 30KB/second. This implementation also allows the choices between scheduling policies [First-In-First-Out (FIFO) & Short-Job-First (SJF)] and request number The following part is the outputs with different parameters. After plenty of attempts, I found **1,000,000,000** is an ideal value for ***number of trails*** to generate a relatively accurate and stable result. Thus, for setup[A-D] below, ***number of request*** is set to **10,000**, and ***number of trails*** is set to **1,000,000,000**. Since the trails number is huge, a progress bar is added to indicate the overall progress. Besides, in order to verify the result, a seed (123456) is assigned for setup[E-H] to generate a same list of random number 

## Data Collection

### Average output with a huge number of trails (Number of request: 10,000; Number of trails: 1,000,000,000)

#### Setup A: FIFO; interarrival time = 1

```
Enter the seed for random function (0: randomly generate): 0
Enter the scheduling policy (0: FIFO; 1: SJF): 0
Enter the mean interarrival time: 1
Enter the number of requests: 10000
Enter the number of trails: 1000000000


THE AVERAGE RESULT FOR 1000000000 TRAILS
-------------------------------------------------------------------------------------
Mean response time:    5.021673105653275
Maximum response time: 120.61908065977474

Fraction of requests delayed in q > 10s: (1138.0)	0.11380000081304088
Fraction of requests delayed in q > 1m:  (215.0)	0.02149999997926971

Mean response time for small file (<=10KB):          (7599.0)	5.060857772433411
Mean response time for medium file (>10KB, <1000KB): (2394.0)	5.6525932716095175
Mean response time for large file (>=1000KB):        (7.0)	70.92182968419763

Time simulation ended (10000.0 requests): 9954.492022964465

Server utilization:      0.47253362737045296
Max number in queue:     118.0
Average delay in queue:  4.778225046882849
Average number in queue: 5.032303709574209
```

#### Setup B: SJF; interarrival time = 1

```
Enter the seed for random function (0: randomly generate): 0
Enter the scheduling policy (0: FIFO; 1: SJF): 1
Enter the mean interarrival time: 1
Enter the number of requests: 10000
Enter the number of trails: 1000000000


THE AVERAGE RESULT FOR 1000000000 TRAILS
-------------------------------------------------------------------------------------
Mean response time:    3.0052062364043164
Maximum response time: 117.39749039093354

Fraction of requests delayed in q > 10s: (739.0)	0.07389999936034627
Fraction of requests delayed in q > 1m:  (81.0)		0.008100000150708688

Mean response time for small file (<=10KB):          (7639.0)	2.7502493974347852
Mean response time for medium file (>10KB, <1000KB): (2353.0)	4.700547114610173
Mean response time for large file (>=1000KB):        (8.0)	62.052980601070196

Time simulation ended (10000.0 requests): 10092.587840128617

Server utilization:      0.462153985056085
Max number in queue:     87.0
Average delay in queue:  2.7901426852569764
Average number in queue: 2.7645463970633304
```

#### Setup C: FIFO; interarrival time = 5

```
Enter the seed for random function (0: randomly generate): 0
Enter the scheduling policy (0: FIFO; 1: SJF): 0
Enter the mean interarrival time: 5
Enter the number of requests: 10000
Enter the number of trails: 1000000000


THE AVERAGE RESULT FOR 1000000000 TRAILS
-------------------------------------------------------------------------------------
Mean response time:    1.1978566596290798
Maximum response time: 265.14770319530305

Fraction of requests delayed in q > 10s: (183.0)	0.018300000251329735
Fraction of requests delayed in q > 1m:  (54.0)		0.005400000070912969

Mean response time for small file (<=10KB):          (7610.0)	1.3361515224213125
Mean response time for medium file (>10KB, <1000KB): (2381.0)	2.304570406384122
Mean response time for large file (>=1000KB):        (9.0)	82.40164311944552

Time simulation ended (10000.0 requests): 49811.9427155203

Server utilization:      0.09856328748746154
Max number in queue:     44.0
Average delay in queue:  1.1486957020094413
Average number in queue: 0.2306064882017273
```

#### Setup D: SJF; interarrival time = 5

```
Enter the seed for random function (0: randomly generate): 0
Enter the scheduling policy (0: FIFO; 1: SJF): 1
Enter the mean interarrival time: 5
Enter the number of requests: 10000
Enter the number of trails: 1000000000

THE AVERAGE RESULT FOR 1000000000 TRAILS
-------------------------------------------------------------------------------------
Mean response time:    1.2804948298861654
Maximum response time: 214.89401257446988

Fraction of requests delayed in q > 10s: (165.0)	0.016499999917534352
Fraction of requests delayed in q > 1m:  (78.0)		0.007799999850209705

Mean response time for small file (<=10KB):          (7616.0)	1.2609299003258745
Mean response time for medium file (>10KB, <1000KB): (2378.0)	2.8576345444137377
Mean response time for large file (>=1000KB):        (6.0)	121.60674719140896

Time simulation ended (10000.0 requests): 50417.12231443367

Server utilization:      0.09313329651603616
Max number in queue:     42.0
Average delay in queue:  1.2432653130795774
Average number in queue: 0.24659585905236653
```

### Ouputs with given seed [123456] (Number of request: 10,000; Number of trails: 1)

#### Setup E: FIFO; interarrival time = 1

```
Enter the seed for random function (0: randomly generate): 123456
Enter the scheduling policy (0: FIFO; 1: SJF): 0
Enter the mean interarrival time: 1
Enter the number of requests: 10000
Enter the number of trails: 1


THE AVERAGE RESULT FOR 1 TRAIL
-------------------------------------------------------------------------------------
Mean response time:    5.705885888513227
Maximum response time: 151.42399749813202

Fraction of requests delayed in q > 10s: (1149.0)	0.1149
Fraction of requests delayed in q > 1m:  (254.0)	0.0254

Mean response time for small file (<=10KB):          (7699.0)	5.647708848067816
Mean response time for medium file (>10KB, <1000KB): (2294.0)	6.673910358273709
Mean response time for large file (>=1000KB):        (7.0)	77.48127727660695

Time simulation ended (10000.0 requests): 10104.454289365185

Server utilization:      0.45652940943445286
Max number in queue:     132.0
Average delay in queue:  5.472085615981618
Average number in queue: 5.415518205412556
```

#### Setup F: SJF; interarrival time = 1

```
Enter the seed for random function (0: randomly generate): 123456
Enter the scheduling policy (0: FIFO; 1: SJF): 1
Enter the mean interarrival time: 1
Enter the number of requests: 10000
Enter the number of trails: 1


THE AVERAGE RESULT FOR 1 TRAIL
-------------------------------------------------------------------------------------
Mean response time:    3.6367712583762724
Maximum response time: 198.8677355954356

Fraction of requests delayed in q > 10s: (697.0)	0.0697
Fraction of requests delayed in q > 1m:  (137.0)	0.0137

Mean response time for small file (<=10KB):          (7699.0)	3.307723963357547
Mean response time for medium file (>10KB, <1000KB): (2294.0)	5.4828700216322215
Mean response time for large file (>=1000KB):        (7.0)	85.57325702953104

Time simulation ended (10000.0 requests): 10104.454289365185

Server utilization:      0.45652940943445286
Max number in queue:     132.0
Average delay in queue:  3.402970985844663
Average number in queue: 3.3677929439754646
```

#### Setup G: FIFO; interarrival time = 5

```
Enter the seed for random function (0: randomly generate): 123456
Enter the scheduling policy (0: FIFO; 1: SJF): 0
Enter the mean interarrival time: 5
Enter the number of requests: 10000
Enter the number of trails: 1


THE AVERAGE RESULT FOR 1 TRAIL
-------------------------------------------------------------------------------------
Mean response time:    0.6184707978317059
Maximum response time: 151.34871353536082

Fraction of requests delayed in q > 10s: (131.0)	0.0131
Fraction of requests delayed in q > 1m:  (19.0)		0.0019

Mean response time for small file (<=10KB):          (7699.0)	0.7441096470561346
Mean response time for medium file (>10KB, <1000KB): (2294.0)	1.7614788948314088
Mean response time for large file (>=1000KB):        (7.0)	69.04775147140752

Time simulation ended (10000.0 requests): 50522.27144682565

Server utilization:      0.09130588188689345
Max number in queue:     22.0
Average delay in queue:  0.5639893453454154
Average number in queue: 0.11163182675565378
```

#### Setup H: SJF; interarrival time = 5

```
Enter the seed for random function (0: randomly generate): 123456
Enter the scheduling policy (0: FIFO; 1: SJF): 1
Enter the mean interarrival time: 5
Enter the number of requests: 10000
Enter the number of trails: 1


THE AVERAGE RESULT FOR 1 TRAIL
-------------------------------------------------------------------------------------
Mean response time:    0.5892049386122271
Maximum response time: 151.34871353536082

Fraction of requests delayed in q > 10s: (121.0)	0.0121
Fraction of requests delayed in q > 1m:  (19.0)		0.0019

Mean response time for small file (<=10KB):          (7699.0)	0.7174858535121102
Mean response time for medium file (>10KB, <1000KB): (2294.0)	1.723256573253666
Mean response time for large file (>=1000KB):        (7.0)	69.04775147140752

Time simulation ended (10000.0 requests): 50522.27144682565

Server utilization:      0.09130588188689345
Max number in queue:     22.0
Average delay in queue:  0.5347234861259367
Average number in queue: 0.10583916178209238
```

