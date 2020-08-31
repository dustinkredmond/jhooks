package dustinredmond.com.jhook;
/*
 *  Copyright (C) 2020 Dustin K. Redmond
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

import org.junit.Test;

import java.util.HashMap;

/**
 * @author Dustin K. Redmond
 * @since 08/31/2020 11:53
 */
public class TestJHook {

    @Test
    public void testA() {
        JHook.createHook("group1", () -> System.out.println("Entry in group1"));
        JHook.createHook("group1", () -> System.out.println("Also entry in group1"));
        assert JHook.size() == 2;
        JHook.clear();
    }

    @Test
    public void testB() {
        int start = JHook.size();
        JHook.removeAllByGroup("group1");
        assert JHook.size() == 0;
    }

    @Test
    public void testC() {
        JHook.clear();
        JHook.createHooks(hookMap());
        assert JHook.size() == 4;
        JHook.executeGroup("group1");
        JHook.executeGroup("group2");
        JHook.executeAll();
    }

    @Test
    public void testD() {
        assert JHook.getHooks().size() > 0;
    }

    @Test
    public void testE() {
        assert JHook.getHooksByGroup("group1").get(0).getGroup().equals("group1");
    }

    @Test
    public void testF() {
        int start = JHook.size();
        JHook.removeHook(1);
        assert JHook.size() < start;
    }

    @Test
    public void testG() {
        JHook.removeHook(JHook.getHooks().get(0));
    }

    @Test
    public void testH() {
        JHook.clear();
        JHook.createHooks(hookMap());
        JHook.getGroupNames().forEach(groupName -> {
            assert JHook.getHooksByGroup(groupName).size() > 0;
        });
    }

    @Test
    public void testI() {
        addConcurrentTestData();
        System.out.printf("Test Thread: %s\n", Thread.currentThread().getName());
        JHook.executeGroupInNewThread("group1");

        System.out.println("\nExecuting each in different thread\n");
        JHook.executeEachInGroupInNewThread("group1");
    }

    @Test
    public void testJ() {
        addConcurrentTestData();
        JHook.executeInNewThread(JHook.getHooks().get(0));
    }

    private static HashMap<String,Runnable> hookMap() {
        HashMap<String,Runnable> map = new HashMap<>();
        map.put("group1", () -> System.out.println("Runnable in group1"));
        map.put("group2", () -> System.out.println("Runnable in group2"));
        map.put("group3", () -> System.out.println("Runnable in group3"));
        map.put("group4", () -> System.out.println("Runnable in group4"));
        return map;
    }

    private void addConcurrentTestData() {
        JHook.clear();
        JHook.createHook("group1", () -> {
            System.out.printf("group1 - entry1 | Thread: %s\n", Thread.currentThread().getName());
        });
        JHook.createHook("group1", () -> {
            System.out.printf("group1 - entry2 | Thread: %s\n", Thread.currentThread().getName());
        });
    }
}
