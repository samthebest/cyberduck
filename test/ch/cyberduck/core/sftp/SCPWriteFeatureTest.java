package ch.cyberduck.core.sftp;

import ch.cyberduck.core.AbstractTestCase;
import ch.cyberduck.core.Credentials;
import ch.cyberduck.core.DefaultHostKeyController;
import ch.cyberduck.core.DisabledListProgressListener;
import ch.cyberduck.core.DisabledLoginController;
import ch.cyberduck.core.DisabledPasswordStore;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.transfer.TransferStatus;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @version $Id$
 */
public class SCPWriteFeatureTest extends AbstractTestCase {

    @Test
    public void testWrite() throws Exception {
        final Host host = new Host(new SFTPProtocol(), "test.cyberduck.ch", new Credentials(
                properties.getProperty("sftp.user"), properties.getProperty("sftp.password")
        ));
        final SFTPSession session = new SFTPSession(host);
        session.open(new DefaultHostKeyController());
        session.login(new DisabledPasswordStore(), new DisabledLoginController());
        final TransferStatus status = new TransferStatus();
        final byte[] content = "test".getBytes("UTF-8");
        status.setLength(content.length);
        final Path test = new Path(session.workdir(), UUID.randomUUID().toString(), Path.FILE_TYPE);
        final OutputStream out = new SCPWriteFeature(session).write(test, status);
        assertNotNull(out);
        IOUtils.write(content, out);
        IOUtils.closeQuietly(out);
        assertTrue(new SFTPFindFeature(session).find(test));
        assertEquals(content.length, session.list(test.getParent(), new DisabledListProgressListener()).get(test.getReference()).attributes().getSize());
        final byte[] buffer = new byte[content.length];
        final InputStream in = new SCPReadFeature(session).read(test, new TransferStatus());
        IOUtils.readFully(in, buffer);
        IOUtils.closeQuietly(in);
        assertArrayEquals(content, buffer);
        new SFTPDeleteFeature(session).delete(Collections.<Path>singletonList(test), new DisabledLoginController());
        session.close();
    }

    @Test
    public void testAppend() throws Exception {
        assertFalse(new SCPWriteFeature(null).append(new Path("/p", Path.FILE_TYPE), 0L, null).append);
    }
}
