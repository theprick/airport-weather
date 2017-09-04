package com.crossover.trial.weather;

import com.crossover.trial.weather.utils.StringUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.StringTokenizer;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 *
 * @author code test administrator
 */
public class AirportLoader {

    /** end point for read queries */
    private WebTarget query;

    /** end point to supply updates */
    private WebTarget collect;

    private static int IATA_IDX = 4;
    private static int LATITUDE_IDX = 6;
    private static int LONGITUDE_IDX = 7;
    private static String addAirportURI = "airport/%s/%s/%s";

    public AirportLoader() {
        Client client = ClientBuilder.newClient();
        query = client.target("http://localhost:9090/query");
        collect = client.target("http://localhost:9090/collect");
    }

    public void upload(InputStream airportDataStream) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(airportDataStream));
        String line;
        // use StringTokenizer because is faster the split
        StringTokenizer tokenizer;
        String[] tokens;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            tokenizer = new StringTokenizer(line, ",");
            tokens = new String[11];
            while(tokenizer.hasMoreTokens()) {
                tokens[i++] = tokenizer.nextToken();
            }
            doPost(tokens);

            i = 0;
        }
    }

    private void doPost(String[] input) {
        String iata = StringUtils.removeQuotes(input[IATA_IDX]);
        String latitude = StringUtils.removeQuotes(input[LATITUDE_IDX]);
        String longitude = StringUtils.removeQuotes(input[LONGITUDE_IDX]);

        Response response = collect.path(String.format(addAirportURI, iata, latitude, longitude))
                .request()
                .post(null);

        if(response.getStatus() != 201) {
            System.err.println("Failed to create airport data, status code " + response.getStatus());
        }
    }

    public static void main(String args[]) throws IOException {
        if(args.length != 1) {
            System.err.println("Usage: java AirportLoader <path-to-file>");
            System.exit(1);
        }
        File airportDataFile = new File(args[0]);
        if (!airportDataFile.exists() || airportDataFile.length() == 0) {
            System.err.println(airportDataFile + " is not a valid input");
            System.exit(1);
        }

        AirportLoader al = new AirportLoader();
        al.upload(new FileInputStream(airportDataFile));
        System.exit(0);
    }
}
