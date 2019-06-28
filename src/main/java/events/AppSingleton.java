package events;

import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppSingleton {
    static private AppSingleton instance = null;
    private EventListener listener = null;

    public ConcurrentLinkedQueue<String> debits = null;

    private static Broker broker = null;
    private static EventListener eventListener = null;



    private AppSingleton() {
        this.debits = new ConcurrentLinkedQueue<String>();
        log.info("AppSingleton CREATED!!!");

    }
    public static AppSingleton getInstance() {
        if (instance == null) {

            instance = new AppSingleton();


            log.info("Creating connection");
            eventListener = new EventListener();
            instance.setCallback(eventListener);
            try {
                broker = new Broker();
                broker.setReceiver(eventListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info(instance.debits.toString());


        }
        return instance;
    }

    public void setCallback(EventListener listener){
        this.listener = listener;

    }
    public EventListener getCallback(){
        return this.listener;
    }

}
