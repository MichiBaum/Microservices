# Java Tuning

With the resources described in [Server](Server.md#resources) Java autoconfigures following values:

- InitialHeapSize: 197132288 (~ 197Mb)
- G1HeapRegionSize: 2097152 (~ 2Mb)
- MaxHeapSize: 3135242240 (~ 3.1Gb)
- MinHeapSize: 8388608 (~ 8Mb)
- SoftMaxHeapSize: 3135242240 (~ 3.1Gb)
- UseContainerSupport: true
- UseG1GC: true
- UseParallelGC: false
- UseSerialGC: false
- UseShenandoahGC: false
- UseZGC: false

My recommendation (with the specified resources) is to configure following:

- -Xmx1g
- -Xms512m
- -XX:G1HeapRegionSize=16M
- -XX:+UseG1GC
- -XX:+UseContainerSupport
