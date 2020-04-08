package io.tanker.identity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IdentityTest {
    @Test
    public void getOne() {
            assertEquals(Identity.getOne(), 1);
    }
}
