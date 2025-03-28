package guru.qa.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.Nonnull;

public interface SuiteExtension extends BeforeAllCallback {

    @Override
    default void beforeAll(@Nonnull ExtensionContext context) throws Exception {
        final ExtensionContext rootContext = context.getRoot();
        rootContext.getStore(ExtensionContext.Namespace.GLOBAL)
                .getOrComputeIfAbsent(
                        this.getClass(),
                        key -> {
                            beforeSuite(rootContext);
                            return new ExtensionContext.Store.CloseableResource() {
                                @Override
                                public void close() throws Throwable {
                                    afterSuite();
                                }
                            };
                        }
                );
    }

    default void beforeSuite(ExtensionContext context) {
    }

    default void afterSuite() {
    }
}
