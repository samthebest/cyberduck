﻿// 
// Copyright (c) 2010-2016 Yves Langisch. All rights reserved.
// http://cyberduck.io/
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
// 
// Bug fixes, suggestions and comments should be sent to:
// feedback@cyberduck.io
// 

using System;
using System.IO;
using System.Windows.Forms;
using ch.cyberduck.core;
using ch.cyberduck.core.local;
using ch.cyberduck.core.preferences;
using Application = ch.cyberduck.core.local.Application;
using Path = ch.cyberduck.core.Path;

namespace Ch.Cyberduck.Core
{
    public class SshTerminalService : TerminalService
    {
        public void open(Host host, Path workdir)
        {
            String sshCommand = PreferencesFactory.get().getProperty("terminal.command.ssh");
            if (!File.Exists(sshCommand))
            {
                OpenFileDialog selectDialog = new OpenFileDialog();
                selectDialog.Filter = "PuTTY executable (.exe)|*.exe";
                selectDialog.FilterIndex = 1;
                if (selectDialog.ShowDialog() == DialogResult.OK)
                {
                    PreferencesFactory.get().setProperty("terminal.command.ssh", selectDialog.FileName);
                }
                else
                {
                    return;
                }
            }
            string tempFile = System.IO.Path.GetTempFileName();
            bool identity = host.getCredentials().isPublicKeyAuthentication();
            TextWriter tw = new StreamWriter(tempFile);
            tw.WriteLine("cd {0} && exec $SHELL", workdir.getAbsolute());
            tw.Close();
            String ssh = String.Format(PreferencesFactory.get().getProperty("terminal.command.ssh.args"),
                identity
                    ? string.Format("-i \"{0}\"", host.getCredentials().getIdentity().getAbsolute())
                    : String.Empty, host.getCredentials().getUsername(), host.getHostname(),
                Convert.ToString(host.getPort()), tempFile);
            ApplicationLauncherFactory.get()
                .open(
                    new Application(sshCommand, null), ssh);
        }
    }
}