package edu.gvsu.cis.cis656.chat;

import edu.gvsu.cis.cis656.clock.VectorClock;
import edu.gvsu.cis.cis656.clock.VectorClockComparator;
import edu.gvsu.cis.cis656.queue.PriorityQueue;
import org.junit.Assert;
import org.junit.Test;

public class VectorClockTests {
	private static final int[] testTimes = { 71, 70, 1 };

	@Test
	public void setTimes() {
		VectorClock testClock = testClock();
		for (int i = 0; i < testTimes.length; i++) {
			Assert.assertEquals(testTimes[i], testClock.getTime(i));
		}
	}

	public static VectorClock testClock() {
		VectorClock testClock = new VectorClock();
		for (int i = 0; i < testTimes.length; i++) {
			testClock.addProcess(i, testTimes[i]);
		}
		return testClock;
	}

	@Test
	public void setClock() {
		int[] newTimes = { 80, 50, 4 };
		VectorClock testClock = testClock();
		VectorClock newClock = new VectorClock();
		for (int i = 0; i < newTimes.length; i++) {
			newClock.addProcess(i, newTimes[i]);
		}
		testClock.setClock(newClock);
		for (int i = 0; i < newTimes.length; i++) {
			Assert.assertEquals(newTimes[i], testClock.getTime(i));
		}
	}

	@Test
	public void tick() {
		VectorClock testClock = testClock();
		int testPid = 0;
		testClock.tick(testPid);
		Assert.assertEquals("Time at pid " + testPid + " not ticked.",
				testTimes[testPid] + 1, testClock.getTime(testPid));
		for (int i = 1; i < testTimes.length; i++) {
			Assert.assertEquals("Time not at pid " + testPid
					+ " should be preserved.", testTimes[i],
					testClock.getTime(i));
		}
	}

	@Test
	public void updateClockSamePids() {
		int[] refTimes = { 71, 70, 1 };
		int[] newTimes = { 80, 50, 1 };
		VectorClock refClock = new VectorClock();
		VectorClock newClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
			newClock.addProcess(i, newTimes[i]);
		}
		refClock.update(newClock);
		Assert.assertEquals("Time for pid 0 should be taken from new vector.",
				newTimes[0], refClock.getTime(0));
		Assert.assertEquals("Time for pid 1 should be taken from own vector.",
				refTimes[1], refClock.getTime(1));
		Assert.assertEquals(
				"Time for pid 2 is equal in both vectors, so it should not change.",
				refTimes[2], refClock.getTime(2));
	}

	@Test
	public void updateClockNoCommonPids() {
		int[] refTimes = { 71, 70, 1 };
		int[] newTimes = { 80, 50, 1 };
		VectorClock refClock = new VectorClock();
		VectorClock newClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
			newClock.addProcess(i + 3, newTimes[i]);
		}
		refClock.update(newClock);
		for (int i = 0; i < refTimes.length; i++) {
			Assert.assertEquals("Time for pid " + i
					+ " should be taken from own vector.", refTimes[i],
					refClock.getTime(i));
		}
		for (int i = 0; i < refTimes.length; i++) {
			int newPid = i + 3;
			Assert.assertEquals("Time for pid " + newPid
					+ " should be taken from new vector.", newTimes[i],
					refClock.getTime(newPid));
		}
	}
	
	@Test
	public void compareWithLaterEventSamePids() {
		int[] refTimes = { 71, 70, 1 };
		int[] incomingTimes = { 71, 71, 1 };
		VectorClock refClock = new VectorClock();
		VectorClock incomingClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
			incomingClock.addProcess(i, incomingTimes[i]);
		}
		Assert.assertFalse(incomingClock.happenedBefore(refClock));
	}
	
	@Test
	public void compareWithParallelEventSamePids() {
		int[] refTimes = { 71, 70, 1 };
		int[] incomingTimes = { 70, 71, 1 };
		VectorClock refClock = new VectorClock();
		VectorClock incomingClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
			incomingClock.addProcess(i, incomingTimes[i]);
		}
		Assert.assertFalse(incomingClock.happenedBefore(refClock));
	}
	
	@Test
	public void compareWithEarlierEventSamePids() {
		int[] refTimes = { 71, 70, 1 };
		int[] incomingTimes = { 71, 69, 1 };
		VectorClock refClock = new VectorClock();
		VectorClock incomingClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
			incomingClock.addProcess(i, incomingTimes[i]);
		}
		Assert.assertTrue(incomingClock.happenedBefore(refClock));
	}
	
	@Test
	public void compareWithLaterEventMissingPids() {
		int[] refTimes = { 71, 70, 1 };
		int[] incomingTimes = { 71, 71 };
		VectorClock refClock = new VectorClock();
		VectorClock incomingClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
			if (i < incomingTimes.length)
				incomingClock.addProcess(i, incomingTimes[i]);
		}
		Assert.assertFalse(incomingClock.happenedBefore(refClock));
	}
	
	@Test
	public void compareWithParallelEventMissingPids() {
		int[] refTimes = { 71, 70, 1 };
		int[] incomingTimes = { 70, 71 };
		VectorClock refClock = new VectorClock();
		VectorClock incomingClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
			if (i < incomingTimes.length)
				incomingClock.addProcess(i, incomingTimes[i]);
		}
		Assert.assertFalse(incomingClock.happenedBefore(refClock));
	}
	
	@Test
	public void compareWithEarlierEventMissingPids() {
		int[] refTimes = { 71, 70, 1 };
		int[] incomingTimes = { 71, 69 };
		VectorClock refClock = new VectorClock();
		VectorClock incomingClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
			if (i < incomingTimes.length)
				incomingClock.addProcess(i, incomingTimes[i]);
		}
		Assert.assertTrue(incomingClock.happenedBefore(refClock));
	}
	
	@Test
	public void compareWithLaterEventMissingEvents() {
		int[] refTimes = { 71, 70, 5 };
		int[] incomingTimes = { 71, 71, 2 };
		VectorClock refClock = new VectorClock();
		VectorClock incomingClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
			incomingClock.addProcess(i, incomingTimes[i]);
		}
		Assert.assertFalse(incomingClock.happenedBefore(refClock));
	}
	
	@Test
	public void compareWithParallelEventMissingEvents() {
		int[] refTimes = { 71, 70, 5 };
		int[] incomingTimes = { 70, 71, 2 };
		VectorClock refClock = new VectorClock();
		VectorClock incomingClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
			incomingClock.addProcess(i, incomingTimes[i]);
		}
		Assert.assertFalse(incomingClock.happenedBefore(refClock));
	}
	
	@Test
	public void compareWithEarlierEventMissingEvents() {
		int[] refTimes = { 71, 70, 5 };
		int[] incomingTimes = { 71, 69, 2 };
		VectorClock refClock = new VectorClock();
		VectorClock incomingClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
			incomingClock.addProcess(i, incomingTimes[i]);
		}
		Assert.assertTrue(incomingClock.happenedBefore(refClock));
	}
	
	@Test
	public void convertEmptyClockToString() {
		VectorClock refClock = new VectorClock();
		Assert.assertEquals(refClock.toString(), "{}");
	}
	
	@Test
	public void convertClockToString() {
		int[] refTimes = { 71, 70, 5 };
		VectorClock refClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
		}
		Assert.assertEquals(refClock.toString(), "{\"0\":71,\"1\":70,\"2\":5}");
	}
	
	@Test
	public void convertEmptyStringToClock() {
		int[] refTimes = { 71, 70, 5 };
		VectorClock refClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
		}
		refClock.setClockFromString("{}");
		Assert.assertEquals(refClock.toString(), "{}");
	}
	
	@Test
	public void convertCorrectStringToClock() {
		int[] refTimes = { 71, 70, 5 };
		VectorClock refClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
		}	
		refClock.setClockFromString("{\"0\":1,\"1\":2,\"2\":3}");
		Assert.assertEquals(refClock.toString(), "{\"0\":1,\"1\":2,\"2\":3}");
	}
	
	@Test
	public void convertIncorrectStringToClock() {
		int[] refTimes = { 71, 70, 5 };
		VectorClock refClock = new VectorClock();
		for (int i = 0; i < refTimes.length; i++) {
			refClock.addProcess(i, refTimes[i]);
		}		
		refClock.setClockFromString("{\"0\":1,\"1\":TT,\"2\":3}");
		Assert.assertEquals(refClock.toString(), "{\"0\":71,\"1\":70,\"2\":5}");
	}

	@Test
	public void testClockSortingOne() {
		VectorClock a = new VectorClock();
		a.addProcess(0, 1);
		a.addProcess(1, 0);

		VectorClock b = new VectorClock();
		b.addProcess(0, 2);
		b.addProcess(1, 0);

		VectorClock c = new VectorClock();
		c.addProcess(0, 0);
		c.addProcess(1, 1);

		VectorClock d = new VectorClock();
		d.addProcess(0, 0);
		d.addProcess(1, 2);

		PriorityQueue<VectorClock> priorityQueue = new PriorityQueue<VectorClock>(new VectorClockComparator());
		priorityQueue.add(d);
		priorityQueue.add(a);
		priorityQueue.add(b);
		priorityQueue.add(c);

        Assert.assertEquals(priorityQueue.toString(), "[{\"0\":0,\"1\":1}, {\"0\":0,\"1\":2}, {\"0\":1,\"1\":0}, {\"0\":2,\"1\":0}]");

	}

    @Test
    public void testClockSortingTwo() {
        VectorClock a1 = new VectorClock();
        a1.addProcess(0, 1);
        a1.addProcess(1, 0);
        a1.addProcess(2, 0);

        VectorClock a2 = new VectorClock();
        a2.addProcess(0, 1);
        a2.addProcess(1, 1);
        a2.addProcess(2, 0);

        VectorClock a3 = new VectorClock();
        a3.addProcess(0, 3);
        a3.addProcess(1, 2);
        a3.addProcess(2, 0);

        VectorClock b1 = new VectorClock();
        b1.addProcess(0, 1);
        b1.addProcess(1, 2);
        b1.addProcess(2, 0);

        VectorClock b2 = new VectorClock();
        b2.addProcess(0, 4);
        b2.addProcess(1, 2);
        b2.addProcess(2, 0);

        VectorClock c1 = new VectorClock();
        c1.addProcess(0, 2);
        c1.addProcess(1, 0);
        c1.addProcess(2, 0);

        VectorClock c2 = new VectorClock();
        c2.addProcess(0, 2);
        c2.addProcess(1, 3);
        c2.addProcess(2, 0);

        PriorityQueue<VectorClock> priorityQueue = new PriorityQueue<VectorClock>(new VectorClockComparator());
        priorityQueue.add(a1);
        priorityQueue.add(a2);
        priorityQueue.add(a3);
        priorityQueue.add(b1);
        priorityQueue.add(b2);
        priorityQueue.add(c1);
        priorityQueue.add(c2);

        Assert.assertEquals(priorityQueue.toString(), "[{\"0\":1,\"1\":0,\"2\":0}, {\"0\":1,\"1\":1,\"2\":0}, {\"0\":1,\"1\":2,\"2\":0}, " +
                "{\"0\":2,\"1\":0,\"2\":0}, {\"0\":3,\"1\":2,\"2\":0}, {\"0\":4,\"1\":2,\"2\":0}, {\"0\":2,\"1\":3,\"2\":0}]");

    }

}
