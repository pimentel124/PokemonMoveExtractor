import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Set up the URL connection for the first 50 moves
            URL url = new URL("https://pokeapi.co/api/v2/move/?limit=621");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Read the response into a String
            StringBuilder response = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Parse the JSON response to get the list of moves
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");

            // Create a JSON object to hold all the move data
            JSONObject allMoves = new JSONObject();

            FileWriter fileWriter = new FileWriter("moves.csv");
            fileWriter.append("Id,Nombre,Poder,Precision,Movement_type,Damage_type,PP,Generacion,Descripcion");
            fileWriter.append("\n");

            // Loop through each move and get its data
            for (int i = 0; i < results.length(); i++) {
                JSONObject move = results.getJSONObject(i);
                String moveUrl = move.getString("url");

                // Set up the URL connection for the move
                URL moveUrlObj = new URL(moveUrl);
                HttpURLConnection moveConn = (HttpURLConnection) moveUrlObj.openConnection();
                moveConn.setRequestMethod("GET");
                moveConn.connect();

                // Read the response into a String
                StringBuilder moveResponse = new StringBuilder();
                Scanner moveScanner = new Scanner(moveUrlObj.openStream());
                while (moveScanner.hasNext()) {
                    moveResponse.append(moveScanner.nextLine());
                }
                moveScanner.close();

                // Parse the JSON response to get the move data
                JSONObject json = new JSONObject(moveResponse.toString());
                int id = json.getInt("id");
                String name_es = json.getJSONArray("names").getJSONObject(5).getString("name");
                String generation = json.getJSONObject("generation").getString("name");
                String damageClass = json.getJSONObject("damage_class").getString("name");
                String type = json.getJSONObject("type").getString("name");
                int pp = json.getInt("pp");
                String description = "";
                JSONArray entries = json.getJSONArray("flavor_text_entries");
                for (int j = 0; j < entries.length(); j++) {
                    JSONObject entry = entries.getJSONObject(j);
                    if (entry.getJSONObject("language").getString("name").equals("es")) {
                        description = entry.getString("flavor_text");
                        break;
                    }
                }
                int power;
                try {
                    power = json.getInt("power");
                } catch (Exception e) {
                    power = 0;
                }
                int accuracy;
                try {
                    accuracy = json.getInt("accuracy");
                } catch (Exception e) {
                    accuracy = -1;
                }

                String romanNumeral = generation.substring(11); // Extract the roman numeral part
                int gen_result = convertRomanNumeralToInteger(romanNumeral); // Convert the roman numeral to integer

                // Write the move data to the CSV file
                fileWriter.append(Integer.toString(id));
                fileWriter.append(",");
                fileWriter.append(name_es);
                fileWriter.append(",");
                fileWriter.append(Integer.toString(power));
                fileWriter.append(",");
                fileWriter.append(Integer.toString(accuracy));
                fileWriter.append(",");
                fileWriter.append(type);
                fileWriter.append(",");
                fileWriter.append(damageClass);
                fileWriter.append(",");
                fileWriter.append(Integer.toString(pp));
                fileWriter.append(",");
                fileWriter.append(Integer.toString(gen_result));
                fileWriter.append(",");
                fileWriter.append(description.replace("\n", " "));
                fileWriter.append("\n");
                System.out.println(i);
            }

            fileWriter.close();

        } catch (Exception e) {
            System.out.println(e);
            }
        }

    public static int convertRomanNumeralToInteger(String romanNumeral) {
        return switch (romanNumeral) {
            case "i" -> 1;
            case "ii" -> 2;
            case "iii" -> 3;
            case "iv" -> 4;
            case "v" -> 5;
            case "vi" -> 6;
            case "vii" -> 7;
            case "viii" -> 8;
            default -> throw new IllegalArgumentException("Invalid Roman numeral: " + romanNumeral);
        };
    }

}

