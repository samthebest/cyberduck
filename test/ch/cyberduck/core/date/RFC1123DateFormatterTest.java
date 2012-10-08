package ch.cyberduck.core.date;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @version $Id:$
 */
public class RFC1123DateFormatterTest {

    @Test
    public void testParse() throws Exception {
        assertEquals(7.862976E11, new RFC1123DateFormatter().parse("Thu, 01 Dec 1994 16:00:00 GMT").getTime(), 0L);
    }

    @Test
    public void testPrint() throws Exception {
        assertEquals("Thu, 01 Dec 1994 17:00:00 CET", new RFC1123DateFormatter().format((long) 7.862976E11));
    }
}