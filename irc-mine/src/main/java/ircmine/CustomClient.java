package ircmine;

import java.net.*;
import java.io.*;
import java.util.*;

class IrcSenderSimple {
    static void sendString(BufferedWriter bw, String str) {
        try {
            bw.write(str + "\r\n");
            bw.flush();
        }
        catch (Exception e) {
            System.out.println("Exception: "+e);
        }
    }
    public static void main(String args[]) {
        try {

            String server   = "chat1.ustream.tv";
            int port        = 6667;
            String nickname = "dsafewfdsvasdvadw";
            String channel  = "#bot-test-ch";
            String message  = "hi, all";

            Socket socket = new Socket(server,port);
            System.out.println("*** Connected to server.");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            System.out.println("*** Opened OutputStreamWriter.");
            BufferedWriter bwriter = new BufferedWriter(outputStreamWriter);
            System.out.println("*** Opened BufferedWriter.");

            sendString(bwriter,"NICK "+nickname);
            sendString(bwriter,"USER chatterBot  8 * :chatterBot 0.0.1 Java IRC Bot - www.chat.org");
            sendString(bwriter,"JOIN "+channel);

      // サーバーからの応答確認
      InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
      BufferedReader breader = new BufferedReader(inputStreamReader);
      String line = null;
      int tries = 1;
      while ((line = breader.readLine()) != null) {
        System.out.println(">>> "+line);

        int firstSpace = line.indexOf(" ");
        int secondSpace = line.indexOf(" ", firstSpace + 1);
        if (secondSpace >= 0) {
          String code = line.substring(firstSpace+1, secondSpace);
          if (code.equals("004")) {
            break;
          }
        }
      }
            sendString(bwriter,"PRIVMSG "+channel+" :"+message);

            bwriter.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
