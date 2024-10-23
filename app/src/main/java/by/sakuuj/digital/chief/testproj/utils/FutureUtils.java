package by.sakuuj.digital.chief.testproj.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@UtilityClass
public class FutureUtils {

    public <T> T waitForCompletion(Future<T> future) {
        try {
            return future.get();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        }
    }
}
