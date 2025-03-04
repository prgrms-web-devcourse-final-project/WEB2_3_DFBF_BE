package org.dfbf.soundlink;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class CustomLogger implements TestWatcher {
    @Override
    public void testSuccessful(ExtensionContext context) {
        System.out.println("통과 되었습니다! -> " + context.getDisplayName() + " checked!");
    }
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        System.out.println("불확실한 테스트 입니다 -> " + context.getDisplayName() + " failed...");
    }
}
