import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class TcpReader {

    public static void main(String[] args) {
        TcpReader tcpReader = new TcpReader();
        tcpReader.readInput();
    }

    public void readInput() {
        List<String> output = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            long latestPrintTime = System.currentTimeMillis();
            while ((line = reader.readLine()) != null) {
                long currentTime = System.currentTimeMillis();
                output.add(line);
                if(currentTime - latestPrintTime > 10000) {
                    latestPrintTime = System.currentTimeMillis();
                    printStatistics(output);
                    output.clear();
                }
            }
        } catch (IOException e) {
            System.err.println("Error while reading TCPDump input.");
            e.printStackTrace();
        }
    }

    private void printStatistics(List<String> data) {
        long bytesSent = 0;
        long bytesReceived = 0;
        Map<String, Integer> ipAddresses = new LinkedHashMap<>();

        for (var line : data) {
            LineData parsedLine = parseTcpDumpInputLine(line);
            if (parsedLine.isOutgoing()) {
                bytesSent += parsedLine.bytes();
            } else {
                bytesReceived += parsedLine.bytes();
            }
            ipAddresses.merge(parsedLine.ipAddress, 1, (key, value) -> value + 1);
        }

        System.out.println(bytesSent + " bytes sent.");
        System.out.println(bytesReceived + " bytes received.");
        System.out.println("Top ten IPs:");
        ipAddresses.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(ip ->
                            System.out.println(ip.getKey() + ": " + ip.getValue())
                        );
        //TODO: can be less than ten
        System.out.println("+ " + (ipAddresses.size() - 10) + " other IPs");
    }

    private LineData parseTcpDumpInputLine(String line) {
        try {
            String localhost = InetAddress.getLocalHost().getHostAddress();

            //TODO: check if this is an IP, check length to avoid array out of bounds
            String[] parts = line.split(" ");
            String senderIP = parseIP(parts[2].split("\\."));
            String receiverIP = parseIP(parts[4].split("\\."));
            //TODO: check for NumberFormatException
            long bytes = Long.parseLong(parts[parts.length-1]);

            if(senderIP.equals(localhost)) {
                return new LineData(bytes, receiverIP, true);
            }
            return new LineData(bytes, senderIP, false);
        } catch (UnknownHostException e) {
            System.err.println("Could not obtain localhost address");
            throw new RuntimeException(e);
        }
    }

    private String parseIP(String[] parts) {
        return String.join(".", Arrays.copyOfRange(parts, 0, 4)).trim();
    }

    record LineData (long bytes, String ipAddress, boolean isOutgoing) { }
}
