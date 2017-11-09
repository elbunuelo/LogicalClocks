package edu.gvsu.cis.cis656.clock;

import org.json.JSONObject;

import java.util.*;

public class VectorClock implements Clock {

    // suggested data structure ...
    private Map<String, Integer> clock = new Hashtable<>();


    @Override
    public void update(Clock other) {
        HashSet<String> allPids = new HashSet<>(this.getKnownPids());
        allPids.addAll(((VectorClock) other).getKnownPids());

        for (String pid : allPids) {
            int integerPid = Integer.parseInt(pid);
            int max = Integer.max(this.getTime(integerPid), other.getTime(integerPid));
            clock.put(pid, max);
        }
    }

    @Override
    public void setClock(Clock other) {
        this.setClockFromString(other.toString());
    }

    @Override
    public void tick(Integer pid) {
        if (pid == null) {
            return;
        }

        int currentValue = getTime(pid);
        int newValue = currentValue + 1;

        String pidString = pid.toString();
        clock.put(pidString, newValue);
    }

    @Override
    public boolean happenedBefore(Clock other) {
        boolean happenedBefore = true;
        for (Map.Entry<String, Integer> entry : clock.entrySet()) {
            if (entry.getValue() > other.getTime(Integer.parseInt(entry.getKey()))) {
                happenedBefore = false;
                break;
            }
        }
        return happenedBefore;
    }

    public String toString() {
        JSONObject json = new JSONObject();
        for (Map.Entry<String, Integer> entry : clock.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json.toString();
    }

    @Override
    public void setClockFromString(String clock) {
        JSONObject map = new JSONObject(clock);
        Iterator<?> keys = map.keys();
        Hashtable<String, Integer> newClock = new Hashtable<>();
        while (keys.hasNext()) {
            String key = (String) keys.next();

            try {
                int value = Integer.parseInt(map.get(key).toString());
                newClock.put(key, value);
            } catch (Exception e) {
                return;
            }
        }
        this.clock = newClock;
    }

    @Override
    public int getTime(int p) {
        String key = Integer.toString(p);

        if (!clock.containsKey(key)) {
            clock.put(key, 0);
        }

        return clock.get(key);

    }

    @Override
    public void addProcess(int p, int c) {
        clock.put(Integer.toString(p), c);
    }

    public Set<String> getKnownPids() {
        return clock.keySet();
    }
}
