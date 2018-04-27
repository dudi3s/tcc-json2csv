/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.json2csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static org.json.simple.JSONValue.parse;
import org.json.simple.parser.ParseException;

/**
 *
 * @author eduar
 */
public class MainApp {

    public static void main(String[] args) throws ParseException, FileNotFoundException, IOException {
        // The name of the file to open.
        String readFile = "buscaMundial.json";
        String writeFile = "buscaMundial.csv";

        FileWriter fileWriter = new FileWriter(new File(writeFile));
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write("ID_TWEET;IDIOMA;TIMEZONE;USER;TEXTO_TWEET;TEXTO_RT;TEXTO_QUOTE;TEXTO_QUOTE_RT\n");

        Object obj = parse(new FileReader(readFile));

        JSONObject jsonObject = (JSONObject) obj;
        JSONArray tweets = (JSONArray) jsonObject.get("tweets");

        System.out.println(tweets.size());

        for (Object temp : tweets) {
            JSONObject tweet = (JSONObject) temp;

            //Informações gerais sobre o Tweet (Nível raiz)
            String idTweet = "";
            String lang = "";
            String createdAt = "";

            idTweet = String.valueOf(tweet.get("id"));
            lang = tweet.get("lang").toString();
            createdAt = tweet.get("created_at").toString();

            String timezone = "";
            String username = "";

            //Informações sobre o Usuário (Nível user)
            JSONObject tweetUser = (JSONObject) tweet.get("user");
            username = tweetUser.get("screen_name").toString();
            timezone = (String) tweetUser.get("time_zone");

            if (timezone == null) {
                timezone = "Não Especificado";
            }

            //Informações sobre o Texto do tweet (Nível retweeted_status)
            //É necessário verificar se o tweet possui RT dentro dele, se não tiver, então verifica se o texto excede o limte,
            //Tweet contém um RT dentro do corpo do texto
            String textOriginal = "";
            String textQuoted = "";
            String textRT = "";

            String textRTwithQuote = "";

            //Caso em que o tweet original contém um retweet:
            if (tweet.containsKey("retweeted_status")) {
                textRT = textRT(tweet);

                //Verificar se o retweet possui alguma citação:
                JSONObject retweet = (JSONObject) tweet.get("retweeted_status");
                boolean retweetHasQuote = (boolean) retweet.get("is_quote_status");
                if (retweetHasQuote) {
                    textRTwithQuote = textQuote(retweet);
                }
            } else {
                textOriginal = textOriginal(tweet);
            }

            //Caso em que o tweet original contém uma citação
            boolean tweetHasQuote = (boolean) tweet.get("is_quote_status");
            if (tweetHasQuote) {
                textQuoted = textQuote(tweet);
            }

//            textOriginal = textOriginal.replace("\n", " ");
//            textRT = textRT.replace("\n", " ");
//            textQuoted = textQuoted.replace("\n", " ");
//            textRTwithQuote = textRTwithQuote.replace("\n", " ");
            textOriginal = StringUtils.normalizeSpace(textOriginal);
            textRT = StringUtils.normalizeSpace(textRT);
            textQuoted = StringUtils.normalizeSpace(textQuoted);
            textRTwithQuote = StringUtils.normalizeSpace(textRTwithQuote);

//            System.out.println("==================================================");
//            System.out.println("ID: " + idTweet);
//            System.out.println("LANG: " + lang);
//            System.out.println("CREATED: " + createdAt);
//            System.out.println("TIMEZONE: " + timezone);
//            System.out.println("USER:" + username);
//            System.out.println("TEXTO TWEET: " + textOriginal);
//            System.out.println("TEXTO RETWEET: " + textRT);
//            System.out.println("TEXTO QUOTE: " + textQuoted);
//            System.out.println("TEXTO QUOTE DO RETWEET: " + textRTwithQuote);
//            System.out.println("==================================================");
            System.out.println(idTweet + ";" + lang + ";" + timezone + ";" + username + ";" + textOriginal + ";" + textRT + ";" + textQuoted + ";" + textRTwithQuote + "\n");
            //System.out.println(idTweet + lang + timezone + username + textOriginal + textRT + textQuoted + textRTwithQuote + "\n");
            bufferedWriter.write(idTweet + ";" + lang + ";" + timezone + ";" + username + ";" + textOriginal + ";" + textRT + ";" + textQuoted + ";" + textRTwithQuote + "\n");
//
//            bufferedWriter.write("===============================================");
//            bufferedWriter.write(idTweet);
//            bufferedWriter.write(lang);
//            bufferedWriter.write(createdAt);
//            bufferedWriter.write(timezone);
//            bufferedWriter.write(username);
//            bufferedWriter.write(textOriginal);
//            bufferedWriter.write(textRT);
//            bufferedWriter.write(textQuoted);
//            bufferedWriter.write(textRTwithQuote);
//            bufferedWriter.write("===============================================");
//            bufferedWriter.write("\n");
        }
        bufferedWriter.close();
    }

    private static String textQuote(JSONObject obj) {
        JSONObject tweetQuote = (JSONObject) obj.get("quoted_status");
        boolean truncado = (boolean) tweetQuote.get("truncated");

        if (truncado) {
            JSONObject tweetQuoteExtended = (JSONObject) tweetQuote.get("extended_tweet");
            return tweetQuoteExtended.get("full_text").toString();
        } else {
            return tweetQuote.get("text").toString();
        }
    }

    private static String textRT(JSONObject obj) {
        JSONObject tweetRT = (JSONObject) obj.get("retweeted_status");
        boolean truncado = (boolean) tweetRT.get("truncated");

        if (truncado) {
            JSONObject tweetRTExtended = (JSONObject) tweetRT.get("extended_tweet");
            return tweetRTExtended.get("full_text").toString();
        } else {
            return tweetRT.get("text").toString();
        }

    }

    private static String textOriginal(JSONObject obj) {
        boolean truncado = (boolean) obj.get("truncated");
        if (truncado) {
            JSONObject tweetOriginalExtended = (JSONObject) obj.get("extended_tweet");
            return tweetOriginalExtended.get("full_text").toString();
        } else {
            return obj.get("text").toString();
        }

    }

    // This will reference one line at a time
//        try {
//            // FileReader reads text files in the default encoding.
//            FileReader fileReader = new FileReader(readFile);
//
//            // Always wrap FileReader in BufferedReader.
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//            // Assume default encoding.
//            FileWriter fileWriter = new FileWriter(new File(writeFile));
//            
////            FileOutputStream fileStream = new FileOutputStream(new File("writeFile"));
////            OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");
//
//            // Always wrap FileWriter in BufferedWriter.
//            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//
//            String line = bufferedReader.readLine();
//            bufferedWriter.write("USUÁRIO;LOCALIZAÇÃO;CRIADO_EM;CONTEÚDO\n");
//
//            while (line != null) {
//                JSONObject obj = new JSONObject();
//                JSONParser parser = new JSONParser();
//
//                String viewUserName, timezone, content, created;
//                obj = (JSONObject) parser.parse(line);
//
//                viewUserName = (String) obj.get("userscreen_name");
//                timezone = (String) obj.get("usertimezone");
//                content = (String) obj.get("text");
//                created = (String) obj.get("created_at");
//
//                if (timezone == null) {
//                    timezone = "Não Especificado";
//                }
//
//                content = content.replace("\n", " ");
//
//                bufferedWriter.write(viewUserName + ";" + timezone + ";" + created + ";\"" + content + "\"\n");
//                line = bufferedReader.readLine();
//
//            }
//
//            // Always close files.
//            bufferedReader.close();
//            bufferedWriter.close();
//        } catch (FileNotFoundException ex) {
//            System.out.println(
//                    "Unable to open file '"
//                    + readFile + "'");
//
//        } catch (IOException ex) {
//            System.out.println(
//                    "Error reading file '"
//                    + readFile + "'");
//
//            System.out.println(
//                    "Error writing to file '"
//                    + writeFile + "'");
    // Or we could just do this: 
    // ex.printStackTrace();
}
