package events;



import com.solacesystems.jcsmp.BytesMessage;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.ConsumerFlowProperties;
import com.solacesystems.jcsmp.Context;
import com.solacesystems.jcsmp.EndpointProperties;
import com.solacesystems.jcsmp.FlowReceiver;
import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.Queue;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageListener;
import com.solacesystems.jcsmp.XMLMessageProducer;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Broker {
    private XMLMessageProducer prod;
    private JCSMPSession session;
    private Topic topic;
    String queueName = "payment/card";


    public Broker() throws Exception {
        // log.info("Broker");
        // Create a JCSMP Session
        String uri = "tcp://vmr-mr3e5sq7dacxp.messaging.solace.cloud:20480";
        String username = "solace-cloud-client";
        String password = "cge4fi7lj67ms6mnn2b4pe76g2";
        String vpn = "msgvpn-8ksiwsp0mtv";

        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, uri);     		// host:port
        properties.setProperty(JCSMPProperties.USERNAME, username); // client-username
        properties.setProperty(JCSMPProperties.VPN_NAME,  vpn); 	// message-vpn
        properties.setProperty(JCSMPProperties.PASSWORD, password); // client-password
        session =  JCSMPFactory.onlyInstance().createSession(properties);
        session.connect();
    }


    public void setReceiver(EventListener eventListener) throws Exception {

        ConsumerFlowProperties flow_prop = new ConsumerFlowProperties();
        Queue listenQueue = JCSMPFactory.onlyInstance().createQueue(this.queueName);

        flow_prop.setEndpoint(listenQueue);
        flow_prop.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);

        EndpointProperties endpoint_props = new EndpointProperties();
        endpoint_props.setAccessType(EndpointProperties.ACCESSTYPE_NONEXCLUSIVE);

        Context defaultContext = JCSMPFactory.onlyInstance().getDefaultContext();

        Context context = JCSMPFactory.onlyInstance().createContext(null);

        FlowReceiver cons = this.session.createFlow(eventListener,
                flow_prop, endpoint_props);

        cons.start();
        log.info("Listening for messages: "+ this.queueName);
    }


    @Override
    protected void finalize() throws Throwable {
        session.closeSession();
    }


}