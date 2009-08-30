package ch.cyberduck.ui.cocoa;

/*
 *  Copyright (c) 2005 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import ch.cyberduck.core.Preferences;
import ch.cyberduck.core.threading.MainAction;
import ch.cyberduck.ui.cocoa.application.*;
import ch.cyberduck.ui.cocoa.foundation.*;
import ch.cyberduck.ui.cocoa.threading.WindowMainAction;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.rococoa.Foundation;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSUInteger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @version $Id$
 */
public abstract class CDWindowController extends CDBundleController implements NSWindow.Delegate {
    private static Logger log = Logger.getLogger(CDWindowController.class);

    protected static final String DEFAULT = "Default";

    public CDWindowController() {
        super();
    }

    @Override
    protected void invalidate() {
        listeners.clear();
        if(window != null) {
            window.setDelegate(null);
        }
        super.invalidate();
    }

    /**
     * The window this controller is owner of
     */
    @Outlet
    protected NSWindow window;

    private Set<CDWindowListener> listeners
            = Collections.synchronizedSet(new HashSet<CDWindowListener>());

    /**
     * @param listener
     */
    public void addListener(CDWindowListener listener) {
        listeners.add(listener);
    }

    /**
     * @param listener
     */
    public void removeListener(CDWindowListener listener) {
        listeners.remove(listener);
    }

    public void setWindow(NSWindow window) {
        this.window = window;
        NSNotificationCenter.defaultCenter().addObserver(this.id(),
                Foundation.selector("windowWillClose:"),
                NSWindow.WindowWillCloseNotification,
                this.window);
        this.window.setReleasedWhenClosed(true);
    }

    public NSWindow window() {
        return this.window;
    }

    /**
     * @return True if the controller window is on screen.
     */
    public boolean isVisible() {
        return this.window().isVisible();
    }

    /**
     * @see ch.cyberduck.ui.cocoa.application.NSWindow.Delegate
     */
    public boolean windowShouldClose(NSWindow sender) {
        return true;
    }

    /**
     * Override this method if the controller should not be invalidated after its window closes
     *
     * @param notification
     */
    public void windowWillClose(NSNotification notification) {
        log.debug("windowWillClose:" + notification);
        for(CDWindowListener listener : listeners) {
            listener.windowWillClose();
        }
        //If the window is closed it is assumed the controller object is no longer used
        this.invalidate();
    }

    /**
     * Position this controller's window relative to other open windows
     */
    protected void cascade() {
        NSArray windows = NSApplication.sharedApplication().windows();
        int count = windows.count().intValue();
        if(count != 0) {
            NSWindow window = Rococoa.cast(windows.objectAtIndex(new NSUInteger(count - 1)), NSWindow.class);
            NSPoint origin = window.frame().origin;
            origin = new NSPoint(origin.x.doubleValue(), origin.y.doubleValue() + window.frame().size.height.doubleValue());
            this.window.setFrameTopLeftPoint(this.window.cascadeTopLeftFromPoint(origin));
        }
    }

    /**
     * @param toggle
     * @param open
     */
    protected void setState(NSButton toggle, boolean open) {
        if(open) {
            toggle.performClick(null);
        }
        toggle.setState(open ? NSCell.NSOnState : NSCell.NSOffState);
    }

    @Override
    public void invoke(final MainAction runnable, final boolean wait) {
        super.invoke(new WindowMainAction(this) {
            public void run() {
                runnable.run();
            }
        }, wait);
    }

    /**
     * @return True if this window has a sheet attached
     */
    public boolean hasSheet() {
        if(null == this.window) {
            return false;
        }
        return this.window.attachedSheet() != null;
    }

    /**
     * @param alert
     */
    protected void alert(final NSAlert alert) {
        this.alert(alert, new CDSheetCallback() {
            public void callback(final int returncode) {
                ;
            }
        });
    }

    private CDAlertController alert;

    /**
     * @param alert
     * @param callback
     */
    protected void alert(final NSAlert alert, final CDSheetCallback callback) {
        this.alert = new CDAlertController(this, alert) {
            public void callback(final int returncode) {
                callback.callback(returncode);
                CDWindowController.this.alert = null;
            }
        };
        this.alert.beginSheet();
    }

    /**
     * Attach a sheet to this window
     *
     * @param sheet The sheet to be attached to this window
     * @see ch.cyberduck.ui.cocoa.CDSheetController#beginSheet()
     */
    protected void alert(final NSWindow sheet) {
        this.alert(sheet, new CDSheetCallback() {
            public void callback(final int returncode) {
                ;
            }
        });
    }

    private CDSheetController sheet;

    /**
     * Attach a sheet to this window
     *
     * @param sheet    The sheet to be attached to this window
     * @param callback The callback to call after the sheet is dismissed
     * @see ch.cyberduck.ui.cocoa.CDSheetController#beginSheet()
     */
    protected void alert(final NSWindow sheet, final CDSheetCallback callback) {
        this.sheet = new CDSheetController(this, sheet) {
            public void callback(final int returncode) {
                callback.callback(returncode);
                CDWindowController.this.sheet = null;
            }
        };
        this.sheet.beginSheet();
    }

    protected void updateField(final NSTextView f, final String value) {
        f.setString(StringUtils.isNotBlank(value) ? value : "");
    }

    protected void updateField(final NSTextField f, final String value) {
        f.setStringValue(StringUtils.isNotBlank(value) ? value : "");
    }

    public void helpButtonClicked(final NSButton sender) {
        NSWorkspace.sharedWorkspace().openURL(
                NSURL.URLWithString(Preferences.instance().getProperty("website.help"))
        );
    }
}