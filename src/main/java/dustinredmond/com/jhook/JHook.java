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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Dustin K. Redmond
 * @since 08/31/2020 11:47
 */
public class JHook {

    public static void createHook(String group, Runnable r) {
        hooks.add(new Hook(group, r));
    }

    public static void createHooks(HashMap<String,Runnable> map) {
        map.forEach((g,r) -> hooks.add(new Hook(g,r)));
    }

    public static List<Hook> getHooksByGroup(String group) {
        List<Hook> hooks = new ArrayList<>(JHook.hooks);
        hooks.removeIf(hook -> !hook.group.equals(group));
        return hooks;
    }

    public static Set<String> getGroupNames() {
        HashSet<String> groups = new HashSet<>();
        hooks.forEach(h -> groups.add(h.group));
        return groups;
    }

    public static int size() {
        return hooks.size();
    }

    public static List<Hook> getHooks() {
        return JHook.hooks;
    }

    public static void removeHook(int id) {
        AtomicReference<Hook> hook = new AtomicReference<>();
        hooks.forEach(existingHook -> {
            if (existingHook.id == id) {
                hook.set(existingHook);
            }
        });
       hooks.remove(hook.get());
    }

    public static void removeHook(Hook hook) {
        if (hooks.remove(hook)) {
            count.decrementAndGet();
        }
    }

    public static void removeAllByGroup(String group) {
        List<Hook> toRemove = new ArrayList<>();
        hooks.forEach(hook -> {
            if (hook.group.equals(group)) {
                toRemove.add(hook);
                count.decrementAndGet();
            }
        });
        hooks.removeAll(toRemove);
    }

    public static void clear() {
        hooks.clear();
        count.set(1);
    }

    public static void executeAll() {
        hooks.forEach(Hook::run);
    }

    public static void executeGroup(String group) {
        hooks.stream().filter(hook -> hook.group.equals(group)).forEach(Hook::run);
    }

    public static void executeGroupInNewThread(String group) {
        new Thread(() ->
            JHook.getHooksByGroup(group).forEach(hook -> hook.runnable.run())
        ).start();
    }

    public static void executeInNewThread(Hook hook) {
        new Thread(hook.runnable).start();
    }

    public static void executeEachInGroupInNewThread(String group) {
        JHook.getHooksByGroup(group).forEach(hook ->
            new Thread(hook.runnable).start()
        );
    }

    public static class Hook {
        private final int id;
        private final String group;
        private final Runnable runnable;
        private Hook(String group, Runnable r) {
            this.id = count.getAndIncrement();
            this.group = group;
            this.runnable = r;
        }
        @SuppressWarnings("unused")
        public int getId() { return this.id; }
        public String getGroup() { return this.group; }
        public void run() { this.runnable.run(); }
    }

    private static final List<Hook> hooks = new ArrayList<>();
    private static final AtomicInteger count = new AtomicInteger(1);
}
