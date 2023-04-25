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
        Map<String, Integer> ipAddresses = new HashMap<>();
        List<LineData> validData = data.stream()
                .map(this::parseTcpDumpInputLine)
                .filter(Objects::nonNull)
                .toList();

        for (var line : validData) {
            if (line.isOutgoing()) {
                bytesSent += line.bytes();
            } else {
                bytesReceived += line.bytes();
            }
            ipAddresses.merge(line.ipAddress, 1, Integer::sum);
        }

        System.out.println("\n");
        System.out.println(bytesSent + " bytes sent");
        System.out.println(bytesReceived + " bytes received");
        System.out.println("Top ten IPs:");
        ipAddresses.entrySet()
                .stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()) )
                .limit(10)
                .forEach(ip ->
                            System.out.println(ip.getKey() + "\t" + ip.getValue() + " connections")
                        );
        if(ipAddresses.size() > 10) {
            System.out.println("+ " + (ipAddresses.size() - 10) + " other IPs");
        }
    }

    private LineData parseTcpDumpInputLine(String line) {
        try {
            String localhost = InetAddress.getLocalHost().getHostAddress();

            String[] parts = line.split(" ");
            // Skipping over lines which don't fit the most common format for simplicity
            if(parts.length < 5 || !"IP".equals(parts[1]) || !isNumeric(parts[parts.length-1])) {
                return null;
            }
            String senderIP = parseIP(parts[2].split("\\."));
            String receiverIP = parseIP(parts[4].split("\\."));
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

    private boolean isNumeric(String number) {
        try {
            Long.parseLong(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    record LineData (long bytes, String ipAddress, boolean isOutgoing) { }
}
