package events;

import com.solacesystems.jcsmp.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.streaming.receiver.Receiver;
import org.apache.spark.storage.StorageLevel;

import java.net.ConnectException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.spark.storage.StorageLevel;


@Slf4j
public class SolaceReceiver extends Receiver<String> implements XMLMessageListener
{
    private String uri;
    private String username;
    private String password;
    private String vpn;
    private String queueName;
    LinkedBlockingQueue<TextMessage> queue = new LinkedBlockingQueue<TextMessage>();


    private XMLMessageProducer prod;
    private JCSMPSession session;
    private Topic topic;



    public SolaceReceiver(String uri, String username, String password, String vpn, String queueName){
        super(StorageLevel.MEMORY_AND_DISK_2());
        this.uri = uri;
        this.username = username;
        this.password = password;
        this.vpn = vpn;
        this.queueName = queueName;
        log.info("SolaceReceiver");
        System.out.println("================SolaceReceiver");

    }

    public void onStart(){
        log.info("onStart");
        System.out.println("================onStart");
        try {
            setup();
            this.setReceiver();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setup(){
         log.info("setup");

        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, uri);     		// host:port
        properties.setProperty(JCSMPProperties.USERNAME, username); // client-username
        properties.setProperty(JCSMPProperties.VPN_NAME,  vpn); 	// message-vpn
        properties.setProperty(JCSMPProperties.PASSWORD, password); // client-password
        try {
            session =  JCSMPFactory.onlyInstance().createSession(properties);
            session.connect();
        } catch (JCSMPException e) {
            e.printStackTrace();
        }
    }

    private void setReceiver() throws  JCSMPException{
        ConsumerFlowProperties flow_prop = new ConsumerFlowProperties();
        Queue listenQueue = JCSMPFactory.onlyInstance().createQueue(this.queueName);

        flow_prop.setEndpoint(listenQueue);
        flow_prop.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);

        EndpointProperties endpoint_props = new EndpointProperties();
        endpoint_props.setAccessType(EndpointProperties.ACCESSTYPE_NONEXCLUSIVE);

        Context defaultContext = JCSMPFactory.onlyInstance().getDefaultContext();

        Context context = JCSMPFactory.onlyInstance().createContext(null);

        FlowReceiver cons = this.session.createFlow(this,
                flow_prop, endpoint_props);
        cons.start();
        log.info("Listening for messages: "+ this.queueName);
    }

    public void onStop(){
        log.info("onStop");
        session.closeSession();

    }


    @Override
    public void onReceive(BytesXMLMessage msg) {
        try{
            TextMessage tmsg = (TextMessage)msg;
            System.out.println("================onReceive");

            log.info("Message received. ......" + tmsg.getText() );
            ArrayList<String> messages = new ArrayList<String>();
            messages.add(tmsg.getText());
            store(messages.iterator());
            msg.ackMessage();

            // Restart in an attempt to connect again when server is active again
//            restart("Trying to connect again");
        }
        catch(Exception ex){
            log.error("onReceive Exception:" + ex.getMessage());
        }

    }

    @Override
    public void onException(JCSMPException ex) {
        log.error("Consumer received exception: %s%n", ex);
        restart("Trying to connect again");
    }

}
