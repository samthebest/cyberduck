package ch.cyberduck.ui.cocoa;

/*
 *  Copyright (c) 2004 David Kocher. All rights reserved.
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

import com.apple.cocoa.application.NSApplication;

import ch.cyberduck.core.Validator;
import ch.cyberduck.core.DownloadValidator;
import ch.cyberduck.core.Path;

/**
* @version $Id$
 */
public class CDDownloadValidatorController extends CDValidatorController implements Validator {

    public CDDownloadValidatorController(CDController windowController, boolean resume) {
        super(windowController);
        if (false == NSApplication.loadNibNamed("Validator", this)) {
            log.fatal("Couldn't load Validator.nib");
        }
		this.validator = new DownloadValidator(resume);
    }
}