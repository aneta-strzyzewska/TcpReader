import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsPrinterThread extends Thread {

    private final List<String> data;

    public StatisticsPrinterThread(List<String> data) {
        this.data = data;
    }

    public void run() {
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

        String statisticsOutput =
                "\n" +
                bytesSent + " bytes sent\n" +
                bytesReceived + " bytes received\n" +
                "Top ten IPs:\n";

        statisticsOutput += ipAddresses.entrySet()
                .stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()) )
                .limit(10)
                .map(ip ->
                        ip.getKey() + "\t" + ip.getValue() + " connections\n"
                ).collect(Collectors.joining());

        if(ipAddresses.size() > 10) {
            statisticsOutput +=
                    "+ " + (ipAddresses.size() - 10) + " other IPs";
        }

        System.out.println(statisticsOutput);
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
