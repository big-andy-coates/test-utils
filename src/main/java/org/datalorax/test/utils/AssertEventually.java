/*
 * Copyright (c) 2015 Andrew Coates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.datalorax.test.utils;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Hamcrest style assert that will assert that a condition is true eventually, within some timeout.
 *
 * This is very useful in multi-threaded code e.g.
 *
 * <pre>
 *     {@code
 *     assertThatEventually(() -> getResult(), is(4L));
 *     }
 * </pre>
 *
 * @author andy coates
 *         created 01/04/2014.
 */
public final class AssertEventually {
    private static final long DEFAULT_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(30);
    private static final long MAX_PAUSE_PERIOD_MS = TimeUnit.SECONDS.toMillis(1);

    public static <T> T assertThatEventually(final Supplier<? extends T> actualSupplier,
                                             final Matcher<? super T> expected) throws InterruptedException {
        return assertThatEventually("", actualSupplier, expected);
    }

    public static <T> T assertThatEventually(final String message,
                                             final Supplier<? extends T> actualSupplier,
                                             final Matcher<? super T> expected) throws InterruptedException {
        return assertThatEventually(message, actualSupplier, expected, DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    public static <T> T assertThatEventually(final String message,
                                             final Supplier<? extends T> actualSupplier,
                                             final Matcher<? super T> expected,
                                             final long timeout,
                                             final TimeUnit unit) throws InterruptedException {

        final long end = System.currentTimeMillis() + unit.toMillis(timeout);

        long period = 1;
        while (System.currentTimeMillis() < end) {
            final T actual = actualSupplier.get();
            if (expected.matches(actual)) {
                return actual;
            }

            Thread.sleep(period);
            period = Math.min(period * 2, MAX_PAUSE_PERIOD_MS);
        }

        final T actual = actualSupplier.get();
        assertThat(message, actual, expected);
        return actual;
    }

    private AssertEventually() {
    }
}
