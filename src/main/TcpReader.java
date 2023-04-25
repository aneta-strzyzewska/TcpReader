package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TcpReader {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        List<String> output = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            output.add(reader.readLine());
        }
        reader.close();

        output.forEach(System.out::println);
    }

}
