import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    private static void printStatistics(List<String> data) {

    }

}
