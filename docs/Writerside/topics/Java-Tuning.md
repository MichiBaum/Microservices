# Java Tuning

## Default JVM Configuration

With the resources described in [Server](Server.md#resources) (12GB Memory, 8 Core), Java autoconfigures the following values:

- InitialHeapSize: 197132288 (~ 197MB)
- G1HeapRegionSize: 2097152 (~ 2MB)
- MaxHeapSize: 3135242240 (~ 3.1GB)
- MinHeapSize: 8388608 (~ 8MB)
- SoftMaxHeapSize: 3135242240 (~ 3.1GB)
- UseContainerSupport: true
- UseG1GC: true
- UseParallelGC: false
- UseSerialGC: false
- UseShenandoahGC: false
- UseZGC: false

## Recommended JVM Arguments

For microservices running in Docker containers with the specified server resources, the following JVM arguments are recommended:

```
-Xmx1g
-Xms256m
-XX:G1HeapRegionSize=8M
-XX:+UseG1GC
-XX:+UseContainerSupport
-XX:MaxGCPauseMillis=50
-XX:+UseStringDeduplication
-XX:MinHeapFreeRatio=20
-XX:MaxHeapFreeRatio=35
-XX:+ParallelRefProcEnabled
```

## Explanation of Arguments

| Argument | Description |
|----------|-------------|
| `-Xmx1g` | Sets the maximum heap size to 1GB. This is a conservative setting that allows multiple services to run on the same host without exhausting memory. |
| `-Xms256m` | Sets the initial heap size to 256MB. Starting with a smaller heap reduces startup time and memory usage when the service is lightly loaded. |
| `-XX:G1HeapRegionSize=8M` | Sets the G1 garbage collector region size to 8MB. Larger regions can improve performance for services with larger heaps. |
| `-XX:+UseG1GC` | Enables the G1 garbage collector, which is designed for applications with large heaps and predictable pause time requirements. |
| `-XX:+UseContainerSupport` | Enables container awareness, allowing the JVM to detect container memory limits and CPU resources. |
| `-XX:MaxGCPauseMillis=50` | Sets a target for maximum GC pause time of 50ms. This helps maintain responsiveness in microservices. |
| `-XX:+UseStringDeduplication` | Enables string deduplication, which reduces memory usage by sharing identical String objects. |
| `-XX:MinHeapFreeRatio=20` | Sets the minimum percentage of free heap space after a GC cycle. |
| `-XX:MaxHeapFreeRatio=35` | Sets the maximum percentage of free heap space after a GC cycle. |
| `-XX:+ParallelRefProcEnabled` | Enables parallel reference processing, which can reduce GC pause times. |

