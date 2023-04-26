# TcpReader

Programming task for a recruitmet process. Reads output from TCPDump line-by-line and outputs statistics for ten-second intervals: 
bytes received, bytes sent, top ten most common IPs, total number of IPs.

Run in terminal using:

```
sudo tcpdump -nli [interface] | java TcpReader
```
