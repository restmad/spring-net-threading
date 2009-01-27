/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

import junit.framework.*;
import edu.emory.mathcs.backport.java.util.*;
import edu.emory.mathcs.backport.java.util.concurrent.*;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public class TreeSubSetTest extends JSR166TestCase {
    public static void main(String[] args) {
	junit.textui.TestRunner.run (suite());
    }
    public static Test suite() {
	return new TestSuite(TreeSubSetTest.class);
    }

    static class MyReverseComparator implements Comparator {
        public int compare(Object x, Object y) {
            int i = ((Integer)x).intValue();
            int j = ((Integer)y).intValue();
            if (i < j) return 1;
            if (i > j) return -1;
            return 0;
        }
    }

    /**
     * Create a set of given size containing consecutive
     * Integers 0 ... n.
     */
    private NavigableSet populatedSet(int n) {
        TreeSet q = new TreeSet();
        assertTrue(q.isEmpty());

	for(int i = n-1; i >= 0; i-=2)
	    assertTrue(q.add(new Integer(i)));
	for(int i = (n & 1); i < n; i+=2)
	    assertTrue(q.add(new Integer(i)));
        assertTrue(q.add(new Integer(-n)));
        assertTrue(q.add(new Integer(n)));
        NavigableSet s = q.navigableSubSet(new Integer(0), new Integer(n));
        assertFalse(s.isEmpty());
	assertEquals(n, s.size());
        return s;
    }

    /**
     * Create set of first 5 ints
     */
    private NavigableSet set5() {
        TreeSet q = new TreeSet();
        assertTrue(q.isEmpty());
        q.add(one);
        q.add(two);
        q.add(three);
        q.add(four);
        q.add(five);
        q.add(zero);
        q.add(seven);
        NavigableSet s = q.navigableSubSet(one, seven);
	assertEquals(5, s.size());
        return s;
    }

    private static NavigableSet set0() {
	TreeSet set = new TreeSet();
        assertTrue(set.isEmpty());
        return set.navigableTailSet(m1);
    }

    /**
     * A new set has unbounded capacity
     */
    public void testConstructor1() {
        assertEquals(0, set0().size());
    }


    /**
     * isEmpty is true before add, false after
     */
    public void testEmpty() {
        NavigableSet q = set0();
        assertTrue(q.isEmpty());
        q.add(new Integer(1));
        assertFalse(q.isEmpty());
        q.add(new Integer(2));
        q.pollFirst();
        q.pollFirst();
        assertTrue(q.isEmpty());
    }

    /**
     * size changes when elements added and removed
     */
    public void testSize() {
        NavigableSet q = populatedSet(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(SIZE-i, q.size());
            q.pollFirst();
        }
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.size());
            q.add(new Integer(i));
        }
    }

    /**
     * add(null) throws NPE
     */
    public void testAddNull() {
	try {
            NavigableSet q = set0();
            q.add(null);
            shouldThrow();
        } catch (NullPointerException success) { }
    }

    /**
     * Add of comparable element succeeds
     */
    public void testAdd() {
        NavigableSet q = set0();
        assertTrue(q.add(six));
    }

    /**
     * Add of duplicate element fails
     */
    public void testAddDup() {
        NavigableSet q = set0();
        assertTrue(q.add(six));
        assertFalse(q.add(six));
    }

    /**
     * Add of non-Comparable throws CCE
     */
    public void testAddNonComparable() {
        try {
            NavigableSet q = set0();
            q.add(new Object());
            q.add(new Object());
            q.add(new Object());
            shouldThrow();
        }
        catch(ClassCastException success) {}
    }


    /**
     * addAll(null) throws NPE
     */
    public void testAddAll1() {
        try {
            NavigableSet q = set0();
            q.addAll(null);
            shouldThrow();
        }
        catch (NullPointerException success) {}
    }
    /**
     * addAll of a collection with null elements throws NPE
     */
    public void testAddAll2() {
        try {
            NavigableSet q = set0();
            Integer[] ints = new Integer[SIZE];
            q.addAll(Arrays.asList(ints));
            shouldThrow();
        }
        catch (NullPointerException success) {}
    }
    /**
     * addAll of a collection with any null elements throws NPE after
     * possibly adding some elements
     */
    public void testAddAll3() {
        try {
            NavigableSet q = set0();
            Integer[] ints = new Integer[SIZE];
            for (int i = 0; i < SIZE-1; ++i)
                ints[i] = new Integer(i+SIZE);
            q.addAll(Arrays.asList(ints));
            shouldThrow();
        }
        catch (NullPointerException success) {}
    }

    /**
     * Set contains all elements of successful addAll
     */
    public void testAddAll5() {
        try {
            Integer[] empty = new Integer[0];
            Integer[] ints = new Integer[SIZE];
            for (int i = 0; i < SIZE; ++i)
                ints[i] = new Integer(SIZE-1- i);
            NavigableSet q = set0();
            assertFalse(q.addAll(Arrays.asList(empty)));
            assertTrue(q.addAll(Arrays.asList(ints)));
            for (int i = 0; i < SIZE; ++i)
                assertEquals(new Integer(i), q.pollFirst());
        }
        finally {}
    }

    /**
     * poll succeeds unless empty
     */
    public void testPoll() {
        NavigableSet q = populatedSet(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.pollFirst()).intValue());
        }
	assertNull(q.pollFirst());
    }

    /**
     * remove(x) removes x and returns true if present
     */
    public void testRemoveElement() {
        NavigableSet q = populatedSet(SIZE);
        for (int i = 1; i < SIZE; i+=2) {
            assertTrue(q.remove(new Integer(i)));
        }
        for (int i = 0; i < SIZE; i+=2) {
            assertTrue(q.remove(new Integer(i)));
            assertFalse(q.remove(new Integer(i+1)));
        }
        assertTrue(q.isEmpty());
    }

    /**
     * contains(x) reports true when elements added but not yet removed
     */
    public void testContains() {
        NavigableSet q = populatedSet(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.contains(new Integer(i)));
            q.pollFirst();
            assertFalse(q.contains(new Integer(i)));
        }
    }

    /**
     * clear removes all elements
     */
    public void testClear() {
        NavigableSet q = populatedSet(SIZE);
        q.clear();
        assertTrue(q.isEmpty());
        assertEquals(0, q.size());
        q.add(new Integer(1));
        assertFalse(q.isEmpty());
        q.clear();
        assertTrue(q.isEmpty());
    }

    /**
     * containsAll(c) is true when c contains a subset of elements
     */
    public void testContainsAll() {
        NavigableSet q = populatedSet(SIZE);
        NavigableSet p = set0();
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.containsAll(p));
            assertFalse(p.containsAll(q));
            p.add(new Integer(i));
        }
        assertTrue(p.containsAll(q));
    }

    /**
     * retainAll(c) retains only those elements of c and reports true if changed
     */
    public void testRetainAll() {
        NavigableSet q = populatedSet(SIZE);
        NavigableSet p = populatedSet(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            boolean changed = q.retainAll(p);
            if (i == 0)
                assertFalse(changed);
            else
                assertTrue(changed);

            assertTrue(q.containsAll(p));
            assertEquals(SIZE-i, q.size());
            p.pollFirst();
        }
    }

    /**
     * removeAll(c) removes only those elements of c and reports true if changed
     */
    public void testRemoveAll() {
        for (int i = 1; i < SIZE; ++i) {
            NavigableSet q = populatedSet(SIZE);
            NavigableSet p = populatedSet(i);
            assertTrue(q.removeAll(p));
            assertEquals(SIZE-i, q.size());
            for (int j = 0; j < i; ++j) {
                Integer I = (Integer)(p.pollFirst());
                assertFalse(q.contains(I));
            }
        }
    }



    /**
     * lower returns preceding element
     */
    public void testLower() {
        NavigableSet q = set5();
        Object e1 = q.lower(three);
        assertEquals(two, e1);

        Object e2 = q.lower(six);
        assertEquals(five, e2);

        Object e3 = q.lower(one);
        assertNull(e3);

        Object e4 = q.lower(zero);
        assertNull(e4);

    }

    /**
     * higher returns next element
     */
    public void testHigher() {
        NavigableSet q = set5();
        Object e1 = q.higher(three);
        assertEquals(four, e1);

        Object e2 = q.higher(zero);
        assertEquals(one, e2);

        Object e3 = q.higher(five);
        assertNull(e3);

        Object e4 = q.higher(six);
        assertNull(e4);

    }

    /**
     * floor returns preceding element
     */
    public void testFloor() {
        NavigableSet q = set5();
        Object e1 = q.floor(three);
        assertEquals(three, e1);

        Object e2 = q.floor(six);
        assertEquals(five, e2);

        Object e3 = q.floor(one);
        assertEquals(one, e3);

        Object e4 = q.floor(zero);
        assertNull(e4);

    }

    /**
     * ceiling returns next element
     */
    public void testCeiling() {
        NavigableSet q = set5();
        Object e1 = q.ceiling(three);
        assertEquals(three, e1);

        Object e2 = q.ceiling(zero);
        assertEquals(one, e2);

        Object e3 = q.ceiling(five);
        assertEquals(five, e3);

        Object e4 = q.ceiling(six);
        assertNull(e4);

    }

    /**
     * toArray contains all elements
     */
    public void testToArray() {
        NavigableSet q = populatedSet(SIZE);
	Object[] o = q.toArray();
        Arrays.sort(o);
	for(int i = 0; i < o.length; i++)
	    assertEquals(o[i], q.pollFirst());
    }

    /**
     * toArray(a) contains all elements
     */
    public void testToArray2() {
        NavigableSet q = populatedSet(SIZE);
	Integer[] ints = new Integer[SIZE];
	ints = (Integer[])q.toArray(ints);
        Arrays.sort(ints);
        for(int i = 0; i < ints.length; i++)
            assertEquals(ints[i], q.pollFirst());
    }

    /**
     * iterator iterates through all elements
     */
    public void testIterator() {
        NavigableSet q = populatedSet(SIZE);
        int i = 0;
	Iterator it = q.iterator();
        while(it.hasNext()) {
            assertTrue(q.contains(it.next()));
            ++i;
        }
        assertEquals(i, SIZE);
    }

    /**
     * iterator of empty set has no elements
     */
    public void testEmptyIterator() {
        NavigableSet q = set0();
        int i = 0;
	Iterator it = q.iterator();
        while(it.hasNext()) {
            assertTrue(q.contains(it.next()));
            ++i;
        }
        assertEquals(i, 0);
    }

    /**
     * iterator.remove removes current element
     */
    public void testIteratorRemove () {
        final NavigableSet q = set0();
        q.add(new Integer(2));
        q.add(new Integer(1));
        q.add(new Integer(3));

        Iterator it = q.iterator();
        it.next();
        it.remove();

        it = q.iterator();
        assertEquals(it.next(), new Integer(2));
        assertEquals(it.next(), new Integer(3));
        assertFalse(it.hasNext());
    }


    /**
     * toString contains toStrings of elements
     */
    public void testToString() {
        NavigableSet q = populatedSet(SIZE);
        String s = q.toString();
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(s.indexOf(String.valueOf(i)) >= 0);
        }
    }

    /**
     * A deserialized serialized set has same elements
     */
    public void testSerialization() {
        NavigableSet q = populatedSet(SIZE);
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(10000);
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
            out.writeObject(q);
            out.close();

            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(bin));
            NavigableSet r = (NavigableSet)in.readObject();
            assertEquals(q.size(), r.size());
            while (!q.isEmpty())
                assertEquals(q.pollFirst(), r.pollFirst());
        } catch(Exception e){
            e.printStackTrace();
            unexpectedException();
        }
    }

    /**
     * subSet returns set with keys in requested range
     */
    public void testSubSetContents() {
        NavigableSet set = set5();
        SortedSet sm = set.subSet(two, four);
        assertEquals(two, sm.first());
        assertEquals(three, sm.last());
        assertEquals(2, sm.size());
        assertFalse(sm.contains(one));
        assertTrue(sm.contains(two));
        assertTrue(sm.contains(three));
        assertFalse(sm.contains(four));
        assertFalse(sm.contains(five));
        Iterator i = sm.iterator();
        Object k;
        k = (Integer)(i.next());
        assertEquals(two, k);
        k = (Integer)(i.next());
        assertEquals(three, k);
        assertFalse(i.hasNext());
        Iterator j = sm.iterator();
        j.next();
        j.remove();
        assertFalse(set.contains(two));
        assertEquals(4, set.size());
        assertEquals(1, sm.size());
        assertEquals(three, sm.first());
        assertEquals(three, sm.last());
        assertTrue(sm.remove(three));
        assertTrue(sm.isEmpty());
        assertEquals(3, set.size());
    }

    public void testSubSetContents2() {
        NavigableSet set = set5();
        SortedSet sm = set.subSet(two, three);
        assertEquals(1, sm.size());
        assertEquals(two, sm.first());
        assertEquals(two, sm.last());
        assertFalse(sm.contains(one));
        assertTrue(sm.contains(two));
        assertFalse(sm.contains(three));
        assertFalse(sm.contains(four));
        assertFalse(sm.contains(five));
        Iterator i = sm.iterator();
        Object k;
        k = (Integer)(i.next());
        assertEquals(two, k);
        assertFalse(i.hasNext());
        Iterator j = sm.iterator();
        j.next();
        j.remove();
        assertFalse(set.contains(two));
        assertEquals(4, set.size());
        assertEquals(0, sm.size());
        assertTrue(sm.isEmpty());
        assertFalse(sm.remove(three));
        assertEquals(4, set.size());
    }

    /**
     * headSet returns set with keys in requested range
     */
    public void testHeadSetContents() {
        NavigableSet set = set5();
        SortedSet sm = set.headSet(four);
        assertTrue(sm.contains(one));
        assertTrue(sm.contains(two));
        assertTrue(sm.contains(three));
        assertFalse(sm.contains(four));
        assertFalse(sm.contains(five));
        Iterator i = sm.iterator();
        Object k;
        k = (Integer)(i.next());
        assertEquals(one, k);
        k = (Integer)(i.next());
        assertEquals(two, k);
        k = (Integer)(i.next());
        assertEquals(three, k);
        assertFalse(i.hasNext());
        sm.clear();
        assertTrue(sm.isEmpty());
        assertEquals(2, set.size());
        assertEquals(four, set.first());
    }

    /**
     * tailSet returns set with keys in requested range
     */
    public void testTailSetContents() {
        NavigableSet set = set5();
        SortedSet sm = set.tailSet(two);
        assertFalse(sm.contains(one));
        assertTrue(sm.contains(two));
        assertTrue(sm.contains(three));
        assertTrue(sm.contains(four));
        assertTrue(sm.contains(five));
        Iterator i = sm.iterator();
        Object k;
        k = (Integer)(i.next());
        assertEquals(two, k);
        k = (Integer)(i.next());
        assertEquals(three, k);
        k = (Integer)(i.next());
        assertEquals(four, k);
        k = (Integer)(i.next());
        assertEquals(five, k);
        assertFalse(i.hasNext());

        SortedSet ssm = sm.tailSet(four);
        assertEquals(four, ssm.first());
        assertEquals(five, ssm.last());
        assertTrue(ssm.remove(four));
        assertEquals(1, ssm.size());
        assertEquals(3, sm.size());
        assertEquals(4, set.size());
    }

}