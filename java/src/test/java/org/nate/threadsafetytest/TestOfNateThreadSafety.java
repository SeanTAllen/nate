package org.nate.threadsafetytest;

import static java.util.Collections.singletonMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.nate.Engine;
import org.nate.Nate;
import org.nate.testutil.XmlFragmentAssert;


public class TestOfNateThreadSafety {

	private static final int NUM_THREADS = 20;
	private static final long DURATION_IN_MILLIS = 20 * 1000;
	private static final CyclicBarrier START_BARRIER = new CyclicBarrier(NUM_THREADS);
	
	private static final Engine ENGINE = Nate.newWith(
			"<div><p>apple</p><section>banana</section></div>",
			Nate.encoders().encoderFor("JSOUPF"));
	
	private static final List<Throwable> errors = Collections.synchronizedList(new ArrayList<Throwable>());
	
	@Test
	public void shouldBeThreadSafe() throws Throwable {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		long finishTime = System.currentTimeMillis() + DURATION_IN_MILLIS;
		for(int i = 0; i < NUM_THREADS; i++) {
			Thread thread = new Thread(new ThreadSafetyTask(finishTime));
			thread.start();
			threads.add(thread);
		}
		for (Thread thread : threads) {
			thread.join(2 * DURATION_IN_MILLIS);
		}
		if (!errors.isEmpty()) {
			throw errors.get(0);
		}
	}
	
	private static class ThreadSafetyTask implements Runnable {

		private final long finishTime;

		public ThreadSafetyTask(long finishTime) {
			this.finishTime = finishTime;
		}

		@Override
		public void run() {
			try {
				START_BARRIER.await(2, TimeUnit.SECONDS);
				while(System.currentTimeMillis() < finishTime) {
					for(int i = 0; i < 10; i++) {
						executeTask();
					}
				}
			} catch (Throwable e) {
				errors.add(e);
				e.printStackTrace();
			}
		}

		private void executeTask() throws Exception {
			Engine result1 = ENGINE.inject(singletonMap("section", ENGINE.select("p")));
			Engine result2 = ENGINE.inject(singletonMap("section", ENGINE.select("##p")));
			XmlFragmentAssert.assertXmlFragmentsIgnoringWhiteSpaceEqual(" <div> <p>apple</p> <section> <p>apple</p> </section> </div> ", result1.render());
			XmlFragmentAssert.assertXmlFragmentsIgnoringWhiteSpaceEqual(" <div> <p>apple</p> <section> apple </section> </div> ",  result2.render());
		}
		
	}
}
