package io.keepcoding.madridshops.domain.interactors;

import android.support.annotation.NonNull;

public interface GetAllShopsFromCacheInteractor {
    void execute(@NonNull final GetAllShopsInteractorCompletion completion);
}
