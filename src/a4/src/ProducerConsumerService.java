package a4.src;

import java.util.ArrayList;

public class ProducerConsumerService {
	/**
	 * @author Bradley Chow
	 * 
	 *         ProducerConsumerService contains the main function which
	 *         initializes both Producer and Consumer threads. They share
	 *         ArrayList which acts as a critical section buffer for
	 *         producer/consumer queue and ProducerConsumerModule and starts the
	 *         threads. This class and implementations uses synchronization for
	 *         learning purposes and block queues are a better alternative
	 * 
	 * @see ProducerConsumerModule, Producer, Consumer
	 */

	// Main Function
	public static void main(String[] args) {
		ArrayList<Integer> messageQueue = new ArrayList<Integer>();
		ProducerConsumerModule pcm = new ProducerConsumerModule();
		Producer producer = new Producer(pcm, messageQueue);
		Consumer consumer = new Consumer(pcm, messageQueue);
		producer.start();
		consumer.start();
	}
}

/*
 * ProducerConsumerModule contains 2 methods, sendMessage and getMessage. These
 * are responsible with dealing with the adding and removing of integers within
 * the critical section which is the ArrayList. Synchronized on messageQueue,
 * which is the ArrayList
 */
class ProducerConsumerModule {

	// Synchronized Method for Getting Message
	static final int MAXBUFFERSIZE = 10;
	// State booleans for queue
	private boolean empty;
	private boolean full;

	/*
	 * @param messageQueue critical section of the producer/consumer
	 * Synchronized Method for Putting Message
	 */
	public void sendMessage(ArrayList<Integer> messageQueue) {

		int lastItemIndex;
		// Synchronized block on messageQueue critical section

		synchronized (messageQueue) {

			// check full boolean used to invoke wait, thread falls asleep
			while (full) {
				try {
					messageQueue.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// If queue is empty, producer adds a new element of 0
			// Else if the size of messageQueue is larger than 0 but smaller
			// than the bound, it will add the next incremented element to
			// messageQueue. If messageQueue is full, will change full boolean
			// to true
			lastItemIndex = messageQueue.size() - 1;
			if (messageQueue.size() == 0) {
				System.out.println("Producer Initial Item: 0");
				messageQueue.add(0);
			} else if (messageQueue.size() != MAXBUFFERSIZE && messageQueue.size() != 0) {
				System.out.print("Producer Current Item: "
						+ messageQueue.get(lastItemIndex).toString());

				messageQueue.add(lastItemIndex + 1);

				System.out.println(" Producer Next Item: "
						+ messageQueue.get(lastItemIndex + 1));

			} else {
				full = true;
			}
			empty = false;
			// wakes up any threads waiting for messageQueue
			messageQueue.notifyAll();
		}

	}

	/*
	 * @param messageQueue critical section of the producer/consumer
	 * Synchronized Method for Getting Message
	 */
	public void getMessage(ArrayList<Integer> messageQueue) {

		int lastItemIndex;
		synchronized (messageQueue) {
			// check empty boolean used to invoke wait, thread falls asleep
			while (empty) {
				try {
					messageQueue.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			lastItemIndex = messageQueue.size() - 1;

			// if the size of messageQueue is larger than 0 it will remove the
			// last element per lastItemIndex from messageQueue, changing the
			// true boolean to false, and empty to true if the size of
			// messageQueue is 0

			if (messageQueue.size() != 0) {
				System.out.println("Consumer: Removed Item: "
						+ messageQueue.get(lastItemIndex));
				messageQueue.remove(lastItemIndex);
				full = false;
			} else {
				empty = true;
			}
			// wakes up any threads waiting for messageQueue
			messageQueue.notifyAll();
		}

	}
}

/*
 * Consumer Thread Class
 */
class Consumer extends Thread {

	ProducerConsumerModule pcm;
	ArrayList<Integer> messageQueue;

	/*
	 * @param pcm ProducerConsumerModule object used to access sendMessage,
	 * getMessage
	 * 
	 * @param messageQueue ArrayList<Integer> critical section for
	 * consumer/producer queue
	 * 
	 * @return void
	 * 
	 * Constructor for Consumer
	 */
	public Consumer(ProducerConsumerModule pcm, ArrayList<Integer> messageQueue) {
		this.pcm = pcm;
		this.messageQueue = messageQueue;
	}

	/*
	 * Thread Run Method, runs the getMessage Method every 1000ms
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		System.out.println("Starting Consumer Thread");
		while (true) {
			try {

				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pcm.getMessage(messageQueue);
		}
	}
}

/*
 * Producer Thread Class
 */
class Producer extends Thread {
	ProducerConsumerModule pcm;
	ArrayList<Integer> messageQueue;

	/*
	 * @param pcm ProducerConsumerModule object used to access sendMessage,
	 * getMessage
	 * 
	 * @param messageQueue ArrayList<Integer> critical section for
	 * consumer/producer queue
	 * 
	 * @return void
	 * 
	 * Constructor for Producer
	 */
	public Producer(ProducerConsumerModule pcm, ArrayList<Integer> messageQueue) {
		this.pcm = pcm;
		this.messageQueue = messageQueue;
	}

	/*
	 * Thread Run Method, runs the sendMessage Method every 500ms
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		System.out.println("Starting Producer Thread");
		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pcm.sendMessage(messageQueue);
		}
	}
}