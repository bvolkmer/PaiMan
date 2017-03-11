package util

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers

/**
 * Mockito calls that return null tricked to be non null for non null parameter
 * @author x4fyr
 * Created on 3/5/17.
 */


@Suppress("USELESS_CAST")
fun <T> anyNonNull(clazz: Class<T>): T = ArgumentMatchers.any(clazz) as T

@Suppress("USELESS_CAST")
fun <T> eqNonNull(value: T): T = ArgumentMatchers.eq(value) as T

@Suppress("USELESS_CAST")
fun <T> argThatNonNull(argumentMatcher: ArgumentMatcher<T>): T = ArgumentMatchers.argThat(argumentMatcher) as T
