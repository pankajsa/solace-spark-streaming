package events;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.solacesystems.jcsmp.BytesMessage;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.XMLMessageListener;

import lombok.extern.slf4j.Slf4j;

import events.AppSingleton;


@Slf4j
public class EventListener implements XMLMessageListener{
    private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

    public void debugIt() {
        System.out.println("========debugIt==========");
    }


    public String poll() {
        String d = queue.poll();
        System.out.println("========poll==========" + d);
        return d;
    }



    @Override
    public void onReceive(BytesXMLMessage msg) {
        System.out.println("MMMMessage received. ......");
        msg.ackMessage();

//        byte[] bytes = ((BytesMessage)msg).getData();


//        StringBuilder sb = new StringBuilder();
//        for (byte bb : bytes) {
//            sb.append(String.format("%02X ", bb));
//        }
//        System.out.println(sb.toString());




//        try {
//            String d = new String(bytes);
//            System.out.println("====+++++====");
//            System.out.println(d);
//            AppSingleton.getInstance().debits.add(d);
//
//            log.info(AppSingleton.getInstance().debits.toString());
//
//            System.out.println("====+++++====");
//
//        }
//        catch (Exception ex) {
//            log.error("Exception in deserializing: " + ex.getMessage());
//        }

    }

    @Override
    public void onException(JCSMPException e) {
        System.out.printf("Consumer received exception: %s%n", e);
    }



}
