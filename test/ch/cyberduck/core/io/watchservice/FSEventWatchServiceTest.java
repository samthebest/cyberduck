package ch.cyberduck.core.io.watchservice;

/*
 * Copyright (c) 2002-2014 David Kocher. All rights reserved.
 * http://cyberduck.ch/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Bug fixes, suggestions and comments should be sent to feedback@cyberduck.ch
 */

import ch.cyberduck.core.AbstractTestCase;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.local.DisabledFileWatcherListener;
import ch.cyberduck.core.local.FileWatcher;
import ch.cyberduck.core.local.FileWatcherListener;
import ch.cyberduck.core.local.FinderLocal;
import ch.cyberduck.core.local.LocalTouchFactory;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.Watchable;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.nio.file.StandardWatchEventKinds.*;
import static org.junit.Assert.*;

public class FSEventWatchServiceTest extends AbstractTestCase {

    @Test(expected = IOException.class)
    public void testNotfound() throws Exception {
        final FileWatcher watcher = new FileWatcher(new FSEventWatchService());
        final Local file = new FinderLocal(System.getProperty("java.io.tmpdir") + "/notfound", UUID.randomUUID().toString());
        assertFalse(file.exists());
        watcher.register(file, new DisabledFileWatcherListener());
    }

    @Test
    public void testRegister() throws Exception {
        final FSEventWatchService fs = new FSEventWatchService();
        final Watchable file = Paths.get(
                File.createTempFile(UUID.randomUUID().toString(), "t").getAbsolutePath());
        final WatchKey key = fs.register(file, new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY});
        while(!key.isValid()) {
            Thread.sleep(1000);
        }
        assertTrue(key.isValid());
        fs.close();
        assertFalse(key.isValid());
    }

    @Test
    public void testListenerFSEventWatchService() throws Exception {
        final FileWatcher watcher = new FileWatcher(new FSEventWatchService());
//        final Local file = new FinderLocal(System.getProperty("java.io.tmpdir") + "/f", UUID.randomUUID().toString());
        final Local file = new FinderLocal(System.getProperty("java.io.tmpdir") + "/fé", UUID.randomUUID().toString());
        final CyclicBarrier update = new CyclicBarrier(2);
        final CyclicBarrier delete = new CyclicBarrier(2);
        final FileWatcherListener listener = new DisabledFileWatcherListener() {
            @Override
            public void fileWritten(final Local file) {
                try {
                    assertEquals(new File(file.getAbsolute()).getCanonicalPath(), new File(file.getAbsolute()).getCanonicalPath());
                }
                catch(IOException e) {
                    fail();
                }
                try {
                    update.await(1L, TimeUnit.SECONDS);
                }
                catch(InterruptedException e) {
                    fail();
                }
                catch(BrokenBarrierException e) {
                    fail();
                }
                catch(TimeoutException e) {
                    fail();
                }
            }

            @Override
            public void fileDeleted(final Local file) {
                try {
                    assertEquals(new File(file.getAbsolute()).getCanonicalPath(), new File(file.getAbsolute()).getCanonicalPath());
                }
                catch(IOException e) {
                    fail();
                }
                try {
                    delete.await(1L, TimeUnit.SECONDS);
                }
                catch(InterruptedException e) {
                    fail();
                }
                catch(BrokenBarrierException e) {
                    fail();
                }
                catch(TimeoutException e) {
                    fail();
                }
            }
        };
        LocalTouchFactory.get().touch(file);
        watcher.register(file, listener).await(1, TimeUnit.SECONDS);
        file.getOutputStream(false).write("Test".getBytes());
        update.await(1L, TimeUnit.SECONDS);
        file.delete();
        delete.await(1L, TimeUnit.SECONDS);
        watcher.close();
    }
}