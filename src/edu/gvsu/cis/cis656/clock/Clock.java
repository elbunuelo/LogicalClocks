package edu.gvsu.cis.cis656.clock;

public interface Clock{

	/**
	 * Update the current clock with a new one, taking into
	 * account the values of the incoming clock.
	 *
	 * E.g. for vector clocks, c1 = [2 1 0], c2 = [1 2 0],
	 * the c1.update(c2) will lead to [2 2 0].
	 * @param other
	 */
	void update(Clock other);

	/**
	 * Change the current clock with a new one, overwriting the
	 * old values.
	 * @param other
	 */
	void setClock(Clock other);

	/**
	 * Tick a clock given the process id.
	 *
	 * For Lamport timestamps, since there is only one logical time,
	 * the method can be called with the "null" parameter. (e.g.
	 * clock.tick(null).
	 * @param pid
	 */
	void tick(Integer pid);

	/**
	 * Check whether a clock has happened before another one.
	 *
	 * @param other
	 * @return True if a clock has happened before, false otherwise.
	 */
	boolean happenedBefore(Clock other);

	/**
	 * toString
	 *
	 * @return String representation of the clock.
	 */
	String toString();

	/**
	 * Set a clock given it's string representation.
	 *
	 * @param clock
	 */
	void setClockFromString(String clock);

	/**
	 *
	 * Get the time for process p
	 *
	 * @param p the process id.
	 * @return
	 */
	int getTime(int p);

	/**
	 * Add a time stamp c for process p.
	 *
	 * @param p the process id
	 * @param c the timestamp
	 */
	void addProcess(int p, int c);

}
