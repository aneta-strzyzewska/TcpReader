import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class TcpReader {

    public static void main(String[] args) {
        TcpReader tcpReader = new TcpReader();
        tcpReader.readInput();
    }

    public void readInput() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            List<String> output = new ArrayList<>();
            String line;
            long latestPrintTime = System.currentTimeMillis();

            while ((line = reader.readLine()) != null) {
                long currentTime = System.currentTimeMillis();
                output.add(line);
                if(currentTime - latestPrintTime > 10000) {
                    latestPrintTime = System.currentTimeMillis();
                    new StatisticsPrinterThread(output).start();
                    output = new ArrayList<>();
                }
            }
        } catch (IOException e) {
            System.err.println("Error while reading TCPDump input.");
            e.printStackTrace();
        }
    }
}
